import React from "react";
import { Link } from "react-router-dom";
import MapView from "../components/MapView";

function RoutePlannerPage() {
  return (
    <div style={{ textAlign: "center", marginTop: "2rem" }}>
      <h2>Eco Route Planner</h2>
      

      <div style={{ maxWidth: 900, margin: "1rem auto" }}>
        <MapView />
      </div>

      <Link to="/home">⬅️ Back to Home</Link>
    </div>
  );
}

export default RoutePlannerPage;
