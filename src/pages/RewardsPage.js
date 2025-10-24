import React from "react";
import { Link } from "react-router-dom";
import "../App.css";

function RewardsPage({ user }) {
  // placeholder rewards list
  const rewards = [
    { id: 1, title: "5% Off EcoStore", cost: 50 },
    { id: 2, title: "Tree planted in your name", cost: 100 },
    { id: 3, title: "Free Transit Day Pass", cost: 200 }
  ];

  const points = user?.ecoPoints ?? 0;

  return (
    <div className="login-container">
      <div className="login-box rewards-box">
        <h2>Rewards</h2>
        <p className="muted">You have <strong>{points}</strong> Eco Points</p>

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
                  disabled={points < r.cost}
                  title={points < r.cost ? "Not enough points" : "Redeem"}
                >
                  {points >= r.cost ? "Redeem" : "Insufficient"}
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