import React from "react";
import { Link } from "react-router-dom";

function RoutePlannerPage() {
  return (
    <div style={{ textAlign: "center", marginTop: "2rem" }}>
      <h2>Eco Route Planner</h2>
      <p>Here you’ll integrate Google Maps and route options later.</p>
      <Link to="/home">⬅️ Back to Home</Link>
    </div>
  );
}

export default RoutePlannerPage;
