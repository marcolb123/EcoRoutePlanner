import React from "react";
import { Link } from "react-router-dom";
import "../App.css";

function HomePage({ user, logout }) {
  return (
    <div className="login-container">
      <div className="login-box home-box">
        <h2>Welcome, {user.username}!</h2>
        <p className="muted user-role">Role: {user.role}</p>

        <div className="home-content">
          <div className="home-cards">
            <div className="home-card">
              <h3>🗺️ Route Planner</h3>
              <p>Find sustainable routes and compare emissions.</p>
              <Link to="/routes" className="action-btn">
                Open Planner
              </Link>
            </div>

            <div className="home-card">
              <h3>🎁 Rewards</h3>
              <p>See and redeem the points you've earned.</p>
              <Link to="/rewards" className="action-btn">
                View Rewards
              </Link>
            </div>
          </div>

          <div className="user-stats">
            <div className="stat">
              <div className="stat-value">{user.ecoPoints ?? 0}</div>
              <div className="stat-label">Eco Points</div>
            </div>
            <div className="stat">
              <div className="stat-value">{user.role}</div>
              <div className="stat-label">Account Type</div>
            </div>
          </div>
        </div>

        <div className="home-footer">
          <button onClick={logout} className="action-btn logout">
            Sign in
          </button>
        </div>
      </div>
    </div>
  );
}

export default HomePage;