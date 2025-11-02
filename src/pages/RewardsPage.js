import React, { useState } from "react";
import { Link } from "react-router-dom";
import api from "../services/api";
import "../App.css";

function RewardsPage({ user, setUser }) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const rewards = [
    { id: 1, title: "5% Off EcoStore", cost: 50 },
    { id: 2, title: "Tree planted in your name", cost: 100 },
    { id: 3, title: "Free Transit Day Pass", cost: 200 },
    { id: 4, title: "new test purchase", cost: 10 }
  ];

  const points = user?.ecoPoints ?? 0;

  // Handle points deduction for a specific reward
  const handleDeductPoints = async (rewardCost, rewardTitle) => {
    // Check if user has enough points
    if (points < rewardCost) {
      setError(`Not enough points! You need at least ${rewardCost} points.`);
      setSuccess("");
      return;
    }

    setLoading(true);
    setError("");
    setSuccess("");

    try {
      // Call API to reduce eco points
      const res = await api.put(`/api/users/${user.id}/eco-points`, {
        pointsToDeduct: rewardCost
      });

      if (res.data && res.data.ecoPoints !== undefined) {
        // Update user data in localStorage
        const updatedUser = { ...user, ecoPoints: res.data.ecoPoints };
        localStorage.setItem("user", JSON.stringify(updatedUser));
        
        // Update app state
        setUser(updatedUser);
        
        setSuccess(`Successfully redeemed: ${rewardTitle}!`);
      } else {
        setError("Failed to update points. Please try again.");
      }
    } catch (err) {
      console.error("Error deducting points:", err);
      setError("Failed to deduct points. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-box rewards-box">
        <img src="/logo.png" alt="ECO Route Logo" className="page-logo" />
        <h2>Rewards</h2>
        <p className="muted">You have <strong>{points}</strong> Eco Points</p>

        {error && <p style={{ color: "red", marginBottom: "1rem" }}>{error}</p>}
        {success && <p style={{ color: "green", marginBottom: "1rem" }}>{success}</p>}

        <div className="rewards-list">
          {rewards.map(r => (
            <div key={r.id} className="reward-item">
              <div className="reward-info">
                <div className="reward-title">{r.title}</div>
                <div className="reward-cost">{r.cost} pts</div>
              </div>
              <div>
                <button
                  className="redeem-btn"
                  disabled={points < r.cost || loading}
                  title={points < r.cost ? "Not enough points" : "Redeem"}
                  onClick={() => handleDeductPoints(r.cost, r.title)}
                  style={{
                    cursor: points < r.cost || loading ? "not-allowed" : "pointer",
                    opacity: points < r.cost || loading ? 0.6 : 1
                  }}
                >
                  {loading ? "Processing..." : points >= r.cost ? "Redeem" : "Insufficient"}
                </button>
              </div>
            </div>
          ))}
        </div>

        <div style={{ marginTop: 12 }}>
          <Link to="/home" className="action-btn">
            ⬅️ Back to Home
          </Link>
        </div>
      </div>
    </div>
  );
}

export default RewardsPage;