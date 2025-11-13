import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { FaCar, FaLeaf, FaGasPump, FaBolt, FaHeart, FaRegHeart, FaArrowLeft, FaBalanceScale, FaChartBar } from 'react-icons/fa';
import Navbar from "../components/Navbar";
import api from "../services/api";
import "../App.css";

function VehicleComparisonPage({ user, logout }) {
  const [brands, setBrands] = useState([]);
  const [vehicles, setVehicles] = useState([]);
  const [favorites, setFavorites] = useState([]);
  
  const [vehicle1Brand, setVehicle1Brand] = useState("");
  const [vehicle1Models, setVehicle1Models] = useState([]);
  const [vehicle1, setVehicle1] = useState(null);
  
  const [vehicle2Brand, setVehicle2Brand] = useState("");
  const [vehicle2Models, setVehicle2Models] = useState([]);
  const [vehicle2, setVehicle2] = useState(null);
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchBrands();
    fetchAllVehicles();
    if (user && user.id && user.role !== "GUEST") {
      fetchFavorites();
    }
  }, [user]);

  const fetchBrands = async () => {
    try {
      const response = await api.get('/api/vehicles/brands');
      setBrands(response.data || []);
    } catch (err) {
      console.error("Failed to fetch brands:", err);
      setError("Failed to load vehicle brands");
    }
  };

  const fetchAllVehicles = async () => {
    try {
      const response = await api.get('/api/vehicles/all');
      setVehicles(response.data || []);
    } catch (err) {
      console.error("Failed to fetch vehicles:", err);
    }
  };

  const fetchFavorites = async () => {
    if (!user || !user.id || user.role === "GUEST") return;
    
    try {
      const response = await api.get(`/api/vehicles/user/${user.id}/favorites`);
      setFavorites(response.data || []);
    } catch (err) {
      console.error("Failed to fetch favorites:", err);
    }
  };

  const handleBrand1Change = async (brand) => {
    setVehicle1Brand(brand);
    setVehicle1(null);
    if (brand) {
      try {
        const response = await api.get(`/api/vehicles/brand/${brand}`);
        setVehicle1Models(response.data || []);
      } catch (err) {
        console.error("Failed to fetch models:", err);
      }
    } else {
      setVehicle1Models([]);
    }
  };

  const handleBrand2Change = async (brand) => {
    setVehicle2Brand(brand);
    setVehicle2(null);
    if (brand) {
      try {
        const response = await api.get(`/api/vehicles/brand/${brand}`);
        setVehicle2Models(response.data || []);
      } catch (err) {
        console.error("Failed to fetch models:", err);
      }
    } else {
      setVehicle2Models([]);
    }
  };

  const handleFavoriteToggle = async (vehicleId) => {
    if (!user || user.role === "GUEST") {
      alert("Please log in to save favorites!");
      return;
    }

    const isFav = favorites.some(f => f.vehicle.vehicleId === vehicleId);
    
    try {
      if (isFav) {
        await api.delete(`/api/vehicles/user/${user.id}/favorites/${vehicleId}`);
      } else {
        await api.post(`/api/vehicles/user/${user.id}/favorites`, { vehicleId });
      }
      fetchFavorites();
    } catch (err) {
      console.error("Failed to toggle favorite:", err);
      alert("Failed to update favorites");
    }
  };

  const isFavorite = (vehicleId) => {
    return favorites.some(f => f.vehicle.vehicleId === vehicleId);
  };

  const getFuelTypeColor = (fuelType) => {
    switch (fuelType) {
      case "ELECTRIC": return "#00b894";
      case "HYBRID": return "#0984e3";
      case "PLUG_IN_HYBRID": return "#6c5ce7";
      case "DIESEL": return "#636e72";
      case "PETROL": return "#d63031";
      default: return "#2d3436";
    }
  };

  const getFuelTypeIcon = (fuelType) => {
    switch (fuelType) {
      case "ELECTRIC": return <FaBolt />;
      case "HYBRID":
      case "PLUG_IN_HYBRID": return <FaLeaf />;
      default: return <FaGasPump />;
    }
  };

  const renderVehicleCard = (vehicle, position) => {
    if (!vehicle) {
      return (
        <div style={{
          flex: 1,
          background: "linear-gradient(135deg, #fafafa 0%, #f5f5f5 100%)",
          border: "2px dashed #e0e0e0",
          borderRadius: "12px",
          padding: "2rem",
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          minHeight: "400px"
        }}>
          <FaCar size={64} color="#ccc" style={{ marginBottom: "1rem" }} />
          <p style={{ color: "#999", fontSize: "1.1rem" }}>
            Select Vehicle {position}
          </p>
        </div>
      );
    }

    const isElectric = vehicle.fuelType === "ELECTRIC";
    const emissionsPerYear = (vehicle.co2EmissionsGPerKm * 15000 / 1000).toFixed(2); // Assuming 15,000 km/year

    return (
      <div style={{
        flex: 1,
        background: "linear-gradient(135deg, #ffffff 0%, #f9f9f9 100%)",
        border: `3px solid ${getFuelTypeColor(vehicle.fuelType)}`,
        borderRadius: "12px",
        padding: "2rem",
        position: "relative",
        boxShadow: "0 4px 20px rgba(0,0,0,0.1)"
      }}>
        {/* Favorite Button */}
        {user && user.role !== "GUEST" && (
          <button
            onClick={() => handleFavoriteToggle(vehicle.vehicleId)}
            style={{
              position: "absolute",
              top: "1rem",
              right: "1rem",
              background: "transparent",
              border: "none",
              cursor: "pointer",
              fontSize: "1.5rem",
              color: isFavorite(vehicle.vehicleId) ? "#e74c3c" : "#ccc",
              transition: "all 0.2s"
            }}
            onMouseOver={(e) => e.target.style.transform = "scale(1.2)"}
            onMouseOut={(e) => e.target.style.transform = "scale(1)"}
          >
            {isFavorite(vehicle.vehicleId) ? <FaHeart /> : <FaRegHeart />}
          </button>
        )}

        {/* Vehicle Header */}
        <div style={{ textAlign: "center", marginBottom: "1.5rem" }}>
          <FaCar size={48} color={getFuelTypeColor(vehicle.fuelType)} style={{ marginBottom: "0.5rem" }} />
          <h3 style={{ margin: "0.5rem 0", fontSize: "1.5rem", color: "#2e7d32" }}>
            {vehicle.brand} {vehicle.model}
          </h3>
          <div style={{
            display: "inline-flex",
            alignItems: "center",
            gap: "0.5rem",
            background: getFuelTypeColor(vehicle.fuelType),
            color: "white",
            padding: "0.4rem 1rem",
            borderRadius: "20px",
            fontSize: "0.9rem",
            fontWeight: "bold"
          }}>
            {getFuelTypeIcon(vehicle.fuelType)}
            {vehicle.fuelType.replace(/_/g, " ")}
          </div>
        </div>

        {/* Vehicle Stats */}
        <div style={{ marginTop: "1.5rem" }}>
          <div style={{
            display: "grid",
            gridTemplateColumns: "1fr 1fr",
            gap: "1rem"
          }}>
            {/* CO2 Emissions */}
            <div style={{
              background: isElectric ? "#e8f5e9" : "#fff3e0",
              padding: "1rem",
              borderRadius: "8px",
              textAlign: "center"
            }}>
              <div style={{ fontSize: "1.8rem", fontWeight: "bold", color: isElectric ? "#2e7d32" : "#f57c00" }}>
                {vehicle.co2EmissionsGPerKm.toFixed(0)}
              </div>
              <div style={{ fontSize: "0.85rem", color: "#666" }}>g CO₂/km</div>
            </div>

            {/* Fuel/Electric Consumption */}
            <div style={{
              background: "#e3f2fd",
              padding: "1rem",
              borderRadius: "8px",
              textAlign: "center"
            }}>
              <div style={{ fontSize: "1.8rem", fontWeight: "bold", color: "#1976d2" }}>
                {isElectric 
                  ? `${(vehicle.batteryCapacityKwh || 0).toFixed(1)}`
                  : `${(vehicle.fuelConsumptionLPer100Km || 0).toFixed(1)}`}
              </div>
              <div style={{ fontSize: "0.85rem", color: "#666" }}>
                {isElectric ? "kWh" : "L/100km"}
              </div>
            </div>

            {/* Range */}
            {vehicle.electricRangeKm && (
              <div style={{
                background: "#f3e5f5",
                padding: "1rem",
                borderRadius: "8px",
                textAlign: "center",
                gridColumn: "span 2"
              }}>
                <div style={{ fontSize: "1.8rem", fontWeight: "bold", color: "#7b1fa2" }}>
                  {vehicle.electricRangeKm.toFixed(0)}
                </div>
                <div style={{ fontSize: "0.85rem", color: "#666" }}>km Range</div>
              </div>
            )}
          </div>

          {/* Annual Emissions */}
          <div style={{
            marginTop: "1rem",
            padding: "1rem",
            background: isElectric ? "#c8e6c9" : "#ffccbc",
            borderRadius: "8px",
            textAlign: "center"
          }}>
            <div style={{ fontSize: "0.9rem", color: "#666", marginBottom: "0.25rem" }}>
              Annual CO₂ Emissions (15,000 km/year)
            </div>
            <div style={{ fontSize: "1.5rem", fontWeight: "bold", color: isElectric ? "#2e7d32" : "#d32f2f" }}>
              {emissionsPerYear} kg CO₂
            </div>
          </div>

          {/* Vehicle Type & Year */}
          <div style={{
            marginTop: "1rem",
            display: "flex",
            justifyContent: "space-between",
            fontSize: "0.9rem",
            color: "#666"
          }}>
            <span><strong>Type:</strong> {vehicle.vehicleType}</span>
            <span><strong>Year:</strong> {vehicle.year}</span>
          </div>
        </div>
      </div>
    );
  };

  const renderComparison = () => {
    if (!vehicle1 || !vehicle2) return null;

    const diff = vehicle1.co2EmissionsGPerKm - vehicle2.co2EmissionsGPerKm;
    const diffPerYear = (diff * 15000 / 1000).toFixed(2);
    const betterVehicle = diff > 0 ? vehicle2 : vehicle1;

    return (
      <div style={{
        marginTop: "2rem",
        padding: "2rem",
        background: "linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%)",
        border: "3px solid #66bb6a",
        borderRadius: "12px"
      }}>
        <h3 style={{ textAlign: "center", color: "#2e7d32", marginBottom: "1.5rem" }}>
          <FaBalanceScale style={{ marginRight: "0.5rem" }} />
          Comparison Results
        </h3>

        <div style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))",
          gap: "1rem",
          marginBottom: "1.5rem"
        }}>
          <div style={{
            background: "white",
            padding: "1rem",
            borderRadius: "8px",
            textAlign: "center"
          }}>
            <div style={{ fontSize: "1.5rem", fontWeight: "bold", color: "#2e7d32" }}>
              {Math.abs(diff).toFixed(1)} g
            </div>
            <div style={{ fontSize: "0.9rem", color: "#666" }}>CO₂ Difference/km</div>
          </div>

          <div style={{
            background: "white",
            padding: "1rem",
            borderRadius: "8px",
            textAlign: "center"
          }}>
            <div style={{ fontSize: "1.5rem", fontWeight: "bold", color: "#2e7d32" }}>
              {Math.abs(diffPerYear)} kg
            </div>
            <div style={{ fontSize: "0.9rem", color: "#666" }}>CO₂ Difference/year</div>
          </div>
        </div>

        <div style={{
          background: "white",
          padding: "1.5rem",
          borderRadius: "8px",
          textAlign: "center"
        }}>
          <FaLeaf size={32} color="#2e7d32" style={{ marginBottom: "0.5rem" }} />
          <p style={{ fontSize: "1.1rem", color: "#555", margin: "0.5rem 0" }}>
            <strong>{betterVehicle.brand} {betterVehicle.model}</strong> is more eco-friendly!
          </p>
          <p style={{ fontSize: "0.95rem", color: "#666" }}>
            Choosing this vehicle over the other could save approximately{" "}
            <strong style={{ color: "#2e7d32" }}>{Math.abs(diffPerYear)} kg</strong> of CO₂ emissions per year.
          </p>
        </div>
      </div>
    );
  };

  return (
    <div>
      <Navbar user={user} logout={logout} />
      <div className="login-container">
        <div className="login-box" style={{ maxWidth: "1200px" }}>
          <img src="/logo.png" alt="ECO Route Logo" className="page-logo" />
          <h2>
            <FaBalanceScale style={{ marginRight: "0.5rem", verticalAlign: "middle" }} />
            Vehicle Comparison
          </h2>
          <p className="muted">Compare CO₂ emissions and fuel efficiency between different vehicles</p>

          {error && <p className="error">{error}</p>}

          {/* Vehicle Selectors */}
          <div style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fit, minmax(300px, 1fr))",
            gap: "1.5rem",
            marginTop: "2rem"
          }}>
            {/* Vehicle 1 Selector */}
            <div>
              <label style={{ fontSize: "1rem", fontWeight: "600", marginBottom: "0.5rem", display: "block" }}>
                <FaCar style={{ marginRight: "0.5rem" }} />Vehicle 1
              </label>
              <select
                value={vehicle1Brand}
                onChange={(e) => handleBrand1Change(e.target.value)}
                style={{
                  width: "100%",
                  padding: "0.75rem",
                  fontSize: "16px",
                  border: "2px solid #e0e0e0",
                  borderRadius: "8px",
                  marginBottom: "0.5rem"
                }}
              >
                <option value="">Select Brand</option>
                {brands.map(brand => (
                  <option key={brand} value={brand}>{brand}</option>
                ))}
              </select>

              {vehicle1Brand && (
                <select
                  value={vehicle1?.vehicleId || ""}
                  onChange={(e) => {
                    const selected = vehicle1Models.find(v => v.vehicleId === parseInt(e.target.value));
                    setVehicle1(selected);
                  }}
                  style={{
                    width: "100%",
                    padding: "0.75rem",
                    fontSize: "16px",
                    border: "2px solid #e0e0e0",
                    borderRadius: "8px"
                  }}
                >
                  <option value="">Select Model</option>
                  {vehicle1Models.map(vehicle => (
                    <option key={vehicle.vehicleId} value={vehicle.vehicleId}>
                      {vehicle.model} ({vehicle.year}) - {vehicle.fuelType}
                    </option>
                  ))}
                </select>
              )}
            </div>

            {/* Vehicle 2 Selector */}
            <div>
              <label style={{ fontSize: "1rem", fontWeight: "600", marginBottom: "0.5rem", display: "block" }}>
                <FaCar style={{ marginRight: "0.5rem" }} />Vehicle 2
              </label>
              <select
                value={vehicle2Brand}
                onChange={(e) => handleBrand2Change(e.target.value)}
                style={{
                  width: "100%",
                  padding: "0.75rem",
                  fontSize: "16px",
                  border: "2px solid #e0e0e0",
                  borderRadius: "8px",
                  marginBottom: "0.5rem"
                }}
              >
                <option value="">Select Brand</option>
                {brands.map(brand => (
                  <option key={brand} value={brand}>{brand}</option>
                ))}
              </select>

              {vehicle2Brand && (
                <select
                  value={vehicle2?.vehicleId || ""}
                  onChange={(e) => {
                    const selected = vehicle2Models.find(v => v.vehicleId === parseInt(e.target.value));
                    setVehicle2(selected);
                  }}
                  style={{
                    width: "100%",
                    padding: "0.75rem",
                    fontSize: "16px",
                    border: "2px solid #e0e0e0",
                    borderRadius: "8px"
                  }}
                >
                  <option value="">Select Model</option>
                  {vehicle2Models.map(vehicle => (
                    <option key={vehicle.vehicleId} value={vehicle.vehicleId}>
                      {vehicle.model} ({vehicle.year}) - {vehicle.fuelType}
                    </option>
                  ))}
                </select>
              )}
            </div>
          </div>

          {/* Vehicle Cards */}
          <div style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fit, minmax(350px, 1fr))",
            gap: "2rem",
            marginTop: "2rem"
          }}>
            {renderVehicleCard(vehicle1, 1)}
            {renderVehicleCard(vehicle2, 2)}
          </div>

          {/* Comparison Results */}
          {renderComparison()}

          {/* Favorites Section */}
          {user && user.role !== "GUEST" && favorites.length > 0 && (
            <div style={{ marginTop: "2rem" }}>
              <h3 style={{ color: "#2e7d32", marginBottom: "1rem" }}>
                <FaHeart style={{ marginRight: "0.5rem", color: "#e74c3c" }} />
                Your Favorite Vehicles
              </h3>
              <div style={{
                display: "grid",
                gridTemplateColumns: "repeat(auto-fill, minmax(250px, 1fr))",
                gap: "1rem"
              }}>
                {favorites.map(fav => (
                  <div
                    key={fav.favorite.favoriteId}
                    style={{
                      background: "white",
                      border: "2px solid #e0e0e0",
                      borderRadius: "8px",
                      padding: "1rem",
                      cursor: "pointer",
                      transition: "all 0.2s"
                    }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.transform = "translateY(-2px)";
                      e.currentTarget.style.boxShadow = "0 4px 12px rgba(0,0,0,0.1)";
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.transform = "translateY(0)";
                      e.currentTarget.style.boxShadow = "none";
                    }}
                  >
                    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "0.5rem" }}>
                      <strong style={{ color: "#2e7d32" }}>
                        {fav.vehicle.brand} {fav.vehicle.model}
                      </strong>
                      <button
                        onClick={() => handleFavoriteToggle(fav.vehicle.vehicleId)}
                        style={{
                          background: "transparent",
                          border: "none",
                          cursor: "pointer",
                          fontSize: "1.2rem",
                          color: "#e74c3c"
                        }}
                      >
                        <FaHeart />
                      </button>
                    </div>
                    <div style={{ fontSize: "0.9rem", color: "#666" }}>
                      <div>{fav.vehicle.fuelType}</div>
                      <div>CO₂: {fav.vehicle.co2EmissionsGPerKm.toFixed(0)} g/km</div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Back Button */}
          <div style={{ marginTop: "2rem", display: "flex", justifyContent: "center" }}>
            <Link to="/home" className="action-btn">
              <FaArrowLeft style={{ marginRight: "0.5rem" }} />Back to Home
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}

export default VehicleComparisonPage;
