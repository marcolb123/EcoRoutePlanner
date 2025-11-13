import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FaGift, FaStar, FaBoxOpen, FaShoppingCart, FaArrowLeft, FaSignInAlt } from 'react-icons/fa';
import Navbar from "../components/Navbar";
import api from "../services/api";
import "../App.css";

function RewardsPage({ user, setUser }) {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [rewards, setRewards] = useState([]);
  const [showLoginPrompt, setShowLoginPrompt] = useState(false);

  const points = user?.ecoPoints ?? 0;
  const isGuest = user?.role === "GUEST";

  // Fetch rewards from backend on component mount
  useEffect(() => {
    fetchRewards();
  }, []);

  const fetchRewards = async () => {
    try {
      const response = await api.get('/api/rewards/all');
      setRewards(response.data || []);
    } catch (err) {
      console.error("Failed to fetch rewards:", err);
      setError("Failed to load rewards. Please try again later.");
    }
  };

  // Handle points deduction for a specific reward
  const handleDeductPoints = async (rewardId, rewardCost, rewardTitle) => {
    // Check if user is a guest
    if (isGuest) {
      setShowLoginPrompt(true);
      return;
    }

    if (points < rewardCost) {
      setError(`Not enough points! You need at least ${rewardCost} points.`);
      setSuccess("");
      return;
    }

    setLoading(true);
    setError("");
    setSuccess("");

    try {
      // Redeem the reward (reduce stock)
      await api.post('/api/rewards/redeem', { 
        rewardId: rewardId,
        userId: user.id,
        quantity: 1
      });

      // Deduct points from user
      const pointsResponse = await api.put(`/api/users/${user.id}/eco-points`, {
        pointsToDeduct: rewardCost
      });

      if (pointsResponse.data && pointsResponse.data.ecoPoints !== undefined) {
        const updatedUser = { ...user, ecoPoints: pointsResponse.data.ecoPoints };
        localStorage.setItem("user", JSON.stringify(updatedUser));
        setUser(updatedUser);
        setSuccess(`Successfully redeemed: ${rewardTitle}!`);
        fetchRewards();
      } else {
        setError("Failed to update points. Please try again.");
      }
    } catch (err) {
      console.error("Error redeeming reward:", err.message);
      const errorMessage = err.response?.data?.error || "Failed to redeem reward. Please try again.";
      setError(errorMessage);
      fetchRewards();
    } finally {
      setLoading(false);
    }
  };

  const handleCloseLoginPrompt = () => {
    setShowLoginPrompt(false);
  };

  const handleGoToLogin = () => {
    navigate('/login');
  };

  return (
    <div>
      <Navbar user={user} />
      <div className="login-container">
        <div className="login-box rewards-box">
          <img src="/logo.png" alt="ECO Route Logo" className="page-logo" />
          <h2><FaGift style={{ marginRight: '0.5rem', verticalAlign: 'middle' }} />Rewards</h2>
          <p className="muted">
            You have <strong><FaStar style={{ verticalAlign: 'middle', color: '#f39c12' }} /> {points}</strong> Eco Points
            {isGuest && <span style={{ display: 'block', color: '#f57c00', fontSize: '0.9rem', marginTop: '0.5rem' }}>
              Sign in to redeem rewards
            </span>}
          </p>

          {/* Login Prompt Modal */}
          {showLoginPrompt && (
            <div className="modal-overlay">
              <div className="modal" style={{ maxWidth: '450px' }}>
                <FaSignInAlt style={{ fontSize: '3rem', marginBottom: '1rem', color: '#2e7d32' }} />
                <h3 style={{ color: '#2e7d32', marginBottom: '1rem' }}>Sign in Required</h3>
                
                <p style={{ color: '#666', marginBottom: '1.5rem', lineHeight: '1.6' }}>
                  To redeem rewards and earn eco points, you need to create an account or log in with an existing account.
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

          {error && <p className="error">{error}</p>}
          {success && <p style={{ color: "#2e7d32", marginBottom: "1rem", background: "#e8f5e9", padding: "0.5rem", borderRadius: "6px" }}>{success}</p>}

          {loading && rewards.length === 0 ? (
            <div style={{ textAlign: "center", padding: "2rem" }}>
              Loading rewards...
            </div>
          ) : rewards.length === 0 ? (
            <div style={{ textAlign: "center", padding: "2rem", color: "#999" }}>
              <FaBoxOpen size={48} style={{ marginBottom: '1rem' }} />
              <p>No rewards available at this time.</p>
            </div>
          ) : (
            <div className="rewards-list">
              {rewards.map(r => (
                <div key={r.rewardId} className="reward-item">
                  <div className="reward-info">
                    <div className="reward-title">
                      <FaGift style={{ marginRight: '0.5rem', color: '#2e7d32' }} />
                      {r.rewardName}
                    </div>
                    <div className="reward-cost">
                      <FaStar style={{ marginRight: '0.25rem', color: '#f39c12' }} />
                      {r.rewardCost} pts
                    </div>
                    {r.description && (
                      <div style={{ fontSize: "0.9rem", color: "#666", marginTop: "0.25rem" }}>
                        {r.description}
                      </div>
                    )}
                    <div style={{ fontSize: "0.85rem", color: "#999", marginTop: "0.25rem" }}>
                      <FaBoxOpen style={{ marginRight: '0.25rem' }} />
                      Stock: {r.stock || 0} available
                    </div>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    <button
                      className="redeem-btn"
                      disabled={(!isGuest && points < r.rewardCost) || loading || !r.stock || r.stock <= 0}
                      title={
                        isGuest
                          ? "Sign in to redeem"
                          : !r.stock || r.stock <= 0 
                          ? "Out of stock" 
                          : points < r.rewardCost 
                          ? "Not enough points" 
                          : "Redeem"
                      }
                      onClick={() => handleDeductPoints(r.rewardId, r.rewardCost, r.rewardName)}
                    >
                      <FaShoppingCart style={{ marginRight: '0.5rem' }} />
                      {loading 
                        ? "Processing..." 
                        : !r.stock || r.stock <= 0 
                        ? "Out of Stock"
                        : isGuest
                        ? "Sign in to Redeem"
                        : points >= r.rewardCost 
                        ? "Redeem" 
                        : "Insufficient"}
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}

          <div style={{ marginTop: "1.5rem", display: 'flex', justifyContent: 'center' }}>
            <Link to="/home" className="action-btn">
              <FaArrowLeft style={{ marginRight: '0.5rem' }} />Back to Home
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}

export default RewardsPage;