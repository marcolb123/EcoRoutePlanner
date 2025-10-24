import React from "react";
import { Link } from "react-router-dom";
import MapView from "../components/MapView";
import "../App.css";

function RoutePlannerPage({ user }) {
  return (
    <div className="login-container">
      <div className="login-box route-box">
        <img src="/logo.png" alt="ECO Route Logo" className="page-logo" />
        <h2>Eco Route Planner</h2>
        <p className="muted">Plan sustainable routes and compare emissions</p>

        <div className="map-container">
          <MapView />
        </div>

        <div className="route-actions">
          <Link to="/home" className="guest-btn">
            ⬅️ Back to Home
          </Link>
        </div>
      </div>
    </div>
  );
}

export default RoutePlannerPage;