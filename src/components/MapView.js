import React, { useState, useCallback, useEffect } from "react";
import { GoogleMap, useJsApiLoader, DirectionsService, DirectionsRenderer } from "@react-google-maps/api";

const containerStyle = { width: "100%", height: "400px" };
// default center -> Glasgow
const center = { lat: 55.8642, lng: -4.2518 };

export default function MapView({ defaultOrigin = "", defaultDestination = "" }) {
  const [origin, setOrigin] = useState(defaultOrigin);
  const [destination, setDestination] = useState(defaultDestination);
  const [directionsResponse, setDirectionsResponse] = useState(null);
  const [requestOptions, setRequestOptions] = useState(null);

  // emissions state
  const [distanceKm, setDistanceKm] = useState(0);
  const [selectedMode, setSelectedMode] = useState("WALKING"); // lower-emission mode to compare
  const [metrics, setMetrics] = useState({
    carKg: 0,
    modeKg: 0,
    savedKg: 0,
    points: 0
  });

  // Example emission factors (gCO2 / km). Adjust to your project's values.
  const emissionFactors = {
    CAR_AVERAGE: 192,   // gCO2/km (example)
    DRIVING: 192,
    TRANSIT: 41,        // train average (example)
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
      travelMode: "WALKING" // DirectionsService travelMode is for route calculation; we use selectedMode only for emissions comparison
    });
  }, [origin, destination]);

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
      <div style={{ display: "flex", gap: 8, marginBottom: 8 }}>
        <input placeholder="Origin" value={origin} onChange={(e) => setOrigin(e.target.value)} style={{flex:1}} />
        <input placeholder="Destination" value={destination} onChange={(e) => setDestination(e.target.value)} style={{flex:1}} />
        <button onClick={onCalculate}>Get Route</button>
      </div>

      <div style={{ display: "flex", gap: 8, marginBottom: 8, alignItems: "center" }}>
        <label>Compare with:</label>
        <select value={selectedMode} onChange={(e) => setSelectedMode(e.target.value)}>
          <option value="TRANSIT">Train / Public transit</option>
          <option value="BUS">Bus</option>
          <option value="BICYCLING">Bicycle/Walking</option>
        </select>
      </div>

      <GoogleMap mapContainerStyle={containerStyle} center={center} zoom={12}>
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
