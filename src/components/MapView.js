import React, { useState, useCallback, useEffect } from "react";
import { GoogleMap, useJsApiLoader, DirectionsService, DirectionsRenderer } from "@react-google-maps/api";
import { useNavigate } from "react-router-dom";
import { FaSave, FaMapMarkedAlt, FaHome, FaCheckCircle, FaRoute, FaCar, FaRuler, FaLeaf, FaSignInAlt } from 'react-icons/fa';
import api from "../services/api";

const containerStyle = { width: "100%", height: "400px" };
// default center -> Glasgow
const center = { lat: 55.8642, lng: -4.2518 };

export default function MapView({ defaultOrigin = "", defaultDestination = "", user }) {
  const navigate = useNavigate();
  const [origin, setOrigin] = useState(defaultOrigin);
  const [destination, setDestination] = useState(defaultDestination);
  const [directionsResponse, setDirectionsResponse] = useState(null);
  const [requestOptions, setRequestOptions] = useState(null);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [journeyDetails, setJourneyDetails] = useState(null);
  const [showLoginPrompt, setShowLoginPrompt] = useState(false);

  // emissions state
  const [distanceKm, setDistanceKm] = useState(0);
  const [selectedMode, setSelectedMode] = useState("TRANSIT"); // Changed from WALKING to TRANSIT
  const [metrics, setMetrics] = useState({
    carKg: 0,
    modeKg: 0,
    savedKg: 0,
    points: 0
  });

  // Example emission factors (gCO2 / km). Adjust to your project's values.
  const emissionFactors = {
    CAR_AVERAGE: 192,
    DRIVING: 192,
    TRANSIT: 41,
    BUS: 105,
    BICYCLING: 0,
    WALKING: 0
  };

  const apiKey = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;
  const { isLoaded, loadError } = useJsApiLoader({ googleMapsApiKey: apiKey });

  const onCalculate = useCallback(() => {
    if (!origin || !destination) return;
    setRequestOptions({
      origin,
      destination,
      travelMode: "DRIVING" // Use DRIVING for route calculation to get proper directions
    });
  }, [origin, destination]);

  const handleSaveJourney = async () => {
    // Check if user is a guest
    if (!user || user.role === "GUEST") {
      setShowLoginPrompt(true);
      return;
    }

    if (!origin || !destination || !directionsResponse) {
      alert("Please calculate a route first!");
      return;
    }

    try {
      const response = await api.post("/api/journeys", {
        userId: user.id,
        vehicle: selectedMode,
        travelingFrom: origin,
        travelingTo: destination,
        distance: distanceKm,
        emissions: metrics.modeKg,
        emissionsReduced: metrics.savedKg
      });

      const pointsEarned = response.data?.pointsEarned || 0;
      
      // Store journey details for modal
      setJourneyDetails({
        pointsEarned,
        distance: distanceKm,
        emissions: metrics.modeKg,
        saved: metrics.savedKg,
        mode: selectedMode,
        from: origin,
        to: destination
      });
      
      // Show success modal
      setShowSuccessModal(true);
      
      // Update user points in localStorage
      if (pointsEarned > 0) {
        const updatedUser = { ...user, ecoPoints: (user.ecoPoints || 0) + pointsEarned };
        localStorage.setItem("user", JSON.stringify(updatedUser));
        
        // Dispatch a custom event to update navbar points
        window.dispatchEvent(new Event('userUpdated'));
      }
    } catch (error) {
      console.error("Failed to save journey:", error);
      alert("Failed to save journey. Please try again.");
    }
  };

  const handleCloseModal = () => {
    setShowSuccessModal(false);
    setJourneyDetails(null);
  };

  const handleGoHome = () => {
    navigate('/home');
  };

  const handleCloseLoginPrompt = () => {
    setShowLoginPrompt(false);
  };

  const handleGoToLogin = () => {
    navigate('/login');
  };

  // when we get directions, compute total distance and emissions
  useEffect(() => {
    if (!directionsResponse) return;

    const legs = directionsResponse.routes?.[0]?.legs || [];
    const totalMeters = legs.reduce((sum, leg) => sum + (leg.distance?.value || 0), 0);
    const km = totalMeters / 1000;
    setDistanceKm(km);

    const carFactor = emissionFactors.CAR_AVERAGE;
    // map selected mode to a factor (prefer TRANSIT -> BUS/TRain fallback)
    const modeFactor = selectedMode === "TRANSIT"
      ? emissionFactors.TRANSIT
      : (emissionFactors[selectedMode] ?? 0);

    const carKg = (km * carFactor) / 1000;   // convert g -> kg
    const modeKg = (km * modeFactor) / 1000; // convert g -> kg
    const savedKg = Math.max(0, carKg - modeKg);

    // example points calculation: 1 point per 0.1 kg saved (adjust as needed)
    const points = Math.round(savedKg * 10);

    setMetrics({
      carKg,
      modeKg,
      savedKg,
      points
    });
  }, [directionsResponse, selectedMode]);

  if (loadError) return <div>Map load error</div>;
  if (!isLoaded) return <div>Loading map…</div>;

  return (
    <div>
      {/* Login Prompt Modal for Guests */}
      {showLoginPrompt && (
        <div className="modal-overlay">
          <div className="modal" style={{ maxWidth: '450px' }}>
            <FaSignInAlt style={{ fontSize: '3rem', marginBottom: '1rem', color: '#2e7d32' }} />
            <h3 style={{ color: '#2e7d32', marginBottom: '1rem' }}>Sign in Required</h3>
            
            <p style={{ color: '#666', marginBottom: '1.5rem', lineHeight: '1.6' }}>
              To save journeys and earn eco points, you need to create an account or log in with an existing account.
            </p>

            <div className="modal-actions" style={{ flexDirection: 'column', gap: '0.75rem' }}>
              <button
                onClick={handleGoToLogin}
                className="modal-btn primary"
                style={{
                  width: '100%',
                  padding: '0.85rem',
                  fontSize: '1rem',
                  background: '#2e7d32',
                  color: 'white',
                  border: 'none',
                  borderRadius: '8px',
                  cursor: 'pointer',
                  fontWeight: 'bold',
                  transition: 'all 0.2s'
                }}
                onMouseOver={(e) => e.target.style.background = '#1b5e20'}
                onMouseOut={(e) => e.target.style.background = '#2e7d32'}
              >
                <FaSignInAlt style={{ marginRight: '0.5rem' }} />Go to Login
              </button>
              <button
                onClick={handleCloseLoginPrompt}
                className="modal-btn"
                style={{
                  width: '100%',
                  padding: '0.85rem',
                  fontSize: '1rem',
                  background: '#f5f5f5',
                  color: '#333',
                  border: '2px solid #e0e0e0',
                  borderRadius: '8px',
                  cursor: 'pointer',
                  fontWeight: 'bold',
                  transition: 'all 0.2s'
                }}
                onMouseOver={(e) => e.target.style.background = '#e8e8e8'}
                onMouseOut={(e) => e.target.style.background = '#f5f5f5'}
              >
                Continue Browsing
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Success Modal */}
      {showSuccessModal && journeyDetails && (
        <div className="modal-overlay">
          <div className="modal" style={{ maxWidth: '500px' }}>
            <FaCheckCircle style={{ fontSize: '3rem', marginBottom: '1rem', color: '#2e7d32' }} />
            <h3 style={{ color: '#2e7d32', marginBottom: '1rem' }}>Journey Saved Successfully!</h3>
            
            <div style={{
              background: '#e8f5e9',
              padding: '1.5rem',
              borderRadius: '10px',
              marginBottom: '1.5rem',
              textAlign: 'left'
            }}>
              <div style={{ marginBottom: '0.75rem' }}>
                <strong><FaRoute style={{ marginRight: '0.5rem', color: '#2e7d32' }} />From:</strong> {journeyDetails.from}
              </div>
              <div style={{ marginBottom: '0.75rem' }}>
                <strong><FaRoute style={{ marginRight: '0.5rem', color: '#2e7d32' }} />To:</strong> {journeyDetails.to}
              </div>
              <div style={{ marginBottom: '0.75rem' }}>
                <strong><FaCar style={{ marginRight: '0.5rem', color: '#2e7d32' }} />Mode:</strong> {journeyDetails.mode}
              </div>
              <div style={{ marginBottom: '0.75rem' }}>
                <strong><FaRuler style={{ marginRight: '0.5rem', color: '#2e7d32' }} />Distance:</strong> {journeyDetails.distance.toFixed(2)} km
              </div>
              <div style={{ marginBottom: '0.75rem' }}>
                <strong><FaLeaf style={{ marginRight: '0.5rem', color: '#2e7d32' }} />CO₂ Saved:</strong> {journeyDetails.saved.toFixed(2)} kg
              </div>
              <div style={{
                fontSize: '1.3rem',
                fontWeight: 'bold',
                color: '#2e7d32',
                marginTop: '1rem',
                padding: '0.75rem',
                background: '#c8e6c9',
                borderRadius: '8px',
                textAlign: 'center'
              }}>
                ⭐ +{journeyDetails.pointsEarned} Eco Points Earned!
              </div>
            </div>

            <p style={{ color: '#666', marginBottom: '1.5rem', lineHeight: '1.6' }}>
              Great job choosing sustainable transportation! Your journey has been saved to your trip history.
            </p>

            <div className="modal-actions" style={{ flexDirection: 'column', gap: '0.75rem' }}>
              <button
                onClick={handleCloseModal}
                className="modal-btn primary"
                style={{
                  width: '100%',
                  padding: '0.85rem',
                  fontSize: '1rem',
                  background: '#2e7d32',
                  color: 'white',
                  border: 'none',
                  borderRadius: '8px',
                  cursor: 'pointer',
                  fontWeight: 'bold',
                  transition: 'all 0.2s'
                }}
                onMouseOver={(e) => e.target.style.background = '#1b5e20'}
                onMouseOut={(e) => e.target.style.background = '#2e7d32'}
              >
                <FaMapMarkedAlt style={{ marginRight: '0.5rem' }} />Plan Another Route
              </button>
              <button
                onClick={handleGoHome}
                className="modal-btn"
                style={{
                  width: '100%',
                  padding: '0.85rem',
                  fontSize: '1rem',
                  background: '#f5f5f5',
                  color: '#333',
                  border: '2px solid #e0e0e0',
                  borderRadius: '8px',
                  cursor: 'pointer',
                  fontWeight: 'bold',
                  transition: 'all 0.2s'
                }}
                onMouseOver={(e) => e.target.style.background = '#e8e8e8'}
                onMouseOut={(e) => e.target.style.background = '#f5f5f5'}
              >
                <FaHome style={{ marginRight: '0.5rem' }} />Back to Home
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Map Input Section - FIXED */}
      <div style={{ 
        display: "flex", 
        flexDirection: window.innerWidth <= 600 ? 'column' : 'row',
        gap: '0.5rem', 
        marginBottom: '0.5rem' 
      }}>
        <input 
          placeholder="Origin" 
          value={origin} 
          onChange={(e) => setOrigin(e.target.value)} 
          style={{
            flex: 1,
            fontSize: '16px',
            padding: '0.75rem',
            border: '2px solid #e0e0e0',
            borderRadius: '8px'
          }} 
        />
        <input 
          placeholder="Destination" 
          value={destination} 
          onChange={(e) => setDestination(e.target.value)} 
          style={{
            flex: 1,
            fontSize: '16px',
            padding: '0.75rem',
            border: '2px solid #e0e0e0',
            borderRadius: '8px'
          }} 
        />
        <button 
          onClick={onCalculate} 
          style={{ 
            padding: "0.75rem 1.25rem", 
            cursor: "pointer",
            minWidth: window.innerWidth <= 600 ? '100%' : '120px',
            background: '#2e7d32',
            color: 'white',
            border: 'none',
            borderRadius: '8px',
            fontWeight: 'bold',
            minHeight: '44px'
          }}
        >
          Get Route
        </button>
      </div>

      {/* Save Journey Button - Show for all users */}
      {directionsResponse && (
        <div style={{ marginBottom: '0.5rem' }}>
          <button 
            onClick={handleSaveJourney}
            style={{
              width: "100%",
              padding: "12px",
              background: user?.role === "GUEST" ? "#81c784" : "#00b894",
              color: "white",
              border: "none",
              borderRadius: "8px",
              cursor: "pointer",
              fontWeight: "bold",
              minHeight: "44px",
              fontSize: '1rem'
            }}
          >
            <FaSave style={{ marginRight: '0.5rem' }} />
            {user?.role === "GUEST" ? "Sign in to Save Journey" : "Save Journey & Earn Points"}
          </button>
        </div>
      )}

      {/* Mode Selection */}
      <div style={{ 
        display: "flex", 
        flexDirection: window.innerWidth <= 600 ? 'column' : 'row',
        gap: '0.5rem', 
        marginBottom: '0.5rem', 
        alignItems: window.innerWidth <= 600 ? 'stretch' : 'center'
      }}>
        <label style={{ 
          fontSize: '0.95rem',
          fontWeight: '600',
          whiteSpace: 'nowrap'
        }}>
          Compare with:
        </label>
        <select 
          value={selectedMode} 
          onChange={(e) => setSelectedMode(e.target.value)}
          style={{
            flex: 1,
            fontSize: '16px',
            padding: '0.75rem',
            border: '2px solid #e0e0e0',
            borderRadius: '8px',
            minHeight: '44px'
          }}
        >
          <option value="TRANSIT">Train / Public transit</option>
          <option value="BUS">Bus</option>
          <option value="BICYCLING">Bicycle</option>
          <option value="WALKING">Walking</option>
        </select>
      </div>

      {/* Google Map */}
      <GoogleMap 
        mapContainerStyle={{
          width: "100%",
          height: window.innerWidth <= 768 ? '350px' : window.innerWidth <= 480 ? '300px' : '400px'
        }} 
        center={center} 
        zoom={12}
      >
        {requestOptions && (
          <DirectionsService
            options={requestOptions}
            callback={(result, status) => {
              if (status === "OK" && result) {
                setDirectionsResponse(result);
              } else {
                console.error("Directions request failed:", status);
              }
            }}
          />
        )}

        {directionsResponse && (
          <>
            <DirectionsRenderer options={{ directions: directionsResponse }} />
            <div style={{
              position: "absolute",
              left: 16,
              top: 16,
              background: "white",
              padding: "8px",
              borderRadius: 6,
              boxShadow: "0 2px 6px rgba(0,0,0,0.2)"
            }}>
              <div><strong>Distance:</strong> {distanceKm.toFixed(2)} km</div>
              <div><strong>Car emissions:</strong> {metrics.carKg.toFixed(2)} kg CO2</div>
              <div><strong>{selectedMode} emissions:</strong> {metrics.modeKg.toFixed(2)} kg CO2</div>
              <div><strong>Saved:</strong> {metrics.savedKg.toFixed(2)} kg CO2</div>
              <div><strong>Reward points:</strong> {metrics.points}</div>
            </div>
          </>
        )}
      </GoogleMap>
    </div>
  );
}
