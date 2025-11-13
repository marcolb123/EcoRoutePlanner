import React from "react";
import { Link } from "react-router-dom";
import { FaMapMarkedAlt, FaArrowLeft } from 'react-icons/fa';
import Navbar from "../components/Navbar";
import MapView from "../components/MapView";
import "../App.css";

function RoutePlannerPage({ user, logout }) {
  return (
    <div>
      <Navbar user={user} logout={logout} />
      <div className="login-container">
        <div className="login-box route-box">
          <img src="/logo.png" alt="ECO Route Logo" className="page-logo" />
          <h2><FaMapMarkedAlt style={{ marginRight: '0.5rem', verticalAlign: 'middle' }} />Eco Route Planner</h2>
          <p className="muted">Plan sustainable routes and compare emissions</p>

          <div className="map-container">
            <MapView user={user} />
          </div>

          <div className="route-actions">
            <Link to="/home" className="guest-btn">
              <FaArrowLeft style={{ marginRight: '0.5rem', verticalAlign: 'middle' }} />Back to Home
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}

export default RoutePlannerPage;