import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { FaMapMarkedAlt, FaGift, FaChartLine, FaCar, FaShoppingBag, FaBalanceScale } from 'react-icons/fa';
import "../App.css";

function HomePage({ user, logout }) {
  const navigate = useNavigate();
  const role = (user?.role || "").toString().toUpperCase();
  const isGuest = role === "GUEST";
  const isAdmin = role === "ADMIN";

  // Redirect admins to admin dashboard
  React.useEffect(() => {
    if (isAdmin) {
      navigate("/admin");
    }
  }, [isAdmin, navigate]);

  const handlePrimary = () => {
    if (isGuest) {
      // For guests, take them to the login page to sign in
      navigate("/login");
    } else {
      // For regular users, perform logout
      logout();
    }
  };

  // Don't render anything for admin, they'll be redirected
  if (isAdmin) {
    return null;
  }

  return (
    <div className="login-container">
      <div className="login-box home-box">
        <img src="/logo.png" alt="ECO Route Logo" className="page-logo" />
        <h2>Welcome, {user.username}!</h2>
        <p className="muted user-role">Role: {role || "USER"}</p>

        <div className="home-content">
          <div className="home-cards">
            {/* Route Planner - Available to all users including guests */}
            <div className="home-card">
              <h3><FaMapMarkedAlt style={{ marginRight: '0.5rem', verticalAlign: 'middle' }} />Route Planner</h3>
              <p>Find sustainable routes and compare emissions.</p>
              <Link to="/routes" className="action-btn">
                Open Planner
              </Link>
            </div>

            {/* Vehicle Comparison - Available to all users */}
            <div className="home-card">
              <h3><FaBalanceScale style={{ marginRight: '0.5rem', verticalAlign: 'middle' }} />Vehicle Comparison</h3>
              <p>Compare CO₂ emissions between different vehicles.</p>
              <Link to="/vehicle-comparison" className="action-btn">
                Compare Vehicles
              </Link>
            </div>

            {/* Rewards - Available to all users including guests */}
            <div className="home-card">
              <h3><FaGift style={{ marginRight: '0.5rem', verticalAlign: 'middle' }} />Rewards</h3>
              <p>
                {isGuest 
                  ? "Browse available rewards (Sign in to redeem)." 
                  : "See and redeem the points you've earned."}
              </p>
              <Link to="/rewards" className="action-btn">
                View Rewards
              </Link>
            </div>

            {/* Hide statistics, history, and purchases for guests */}
            {!isGuest && (
              <>
                <div className="home-card">
                  <h3><FaChartLine style={{ marginRight: '0.5rem', verticalAlign: 'middle' }} />Statistics</h3>
                  <p>Track your environmental impact and savings.</p>
                  <Link to="/statistics" className="action-btn">
                    View Stats
                  </Link>
                </div>

                <div className="home-card">
                  <h3><FaCar style={{ marginRight: '0.5rem', verticalAlign: 'middle' }} />Trip History</h3>
                  <p>Review all your past eco journeys.</p>
                  <Link to="/history" className="action-btn">
                    View History
                  </Link>
                </div>

                <div className="home-card">
                  <h3><FaShoppingBag style={{ marginRight: '0.5rem', verticalAlign: 'middle' }} />Purchase History</h3>
                  <p>View your reward redemptions.</p>
                  <Link to="/purchases" className="action-btn">
                    View Purchases
                  </Link>
                </div>
              </>
            )}
          </div>

          <div className="user-stats">
            <div className="stat">
              <div className="stat-value">{isGuest ? "—" : user.ecoPoints ?? 0}</div>
              <div className="stat-label">Eco Points</div>
            </div>
            <div className="stat">
              <div className="stat-value">{user.role}</div>
              <div className="stat-label">Account Type</div>
            </div>
          </div>
        </div>

        <div className="home-footer">
          <button
            onClick={handlePrimary}
            className="action-btn logout"
          >
            {isGuest ? "Sign in" : "Logout"}
          </button>
        </div>
      </div>
    </div>
  );
}

export default HomePage;