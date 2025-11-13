import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { FaShoppingBag, FaGift, FaStar, FaCalendar, FaArrowLeft, FaReceipt } from 'react-icons/fa';
import Navbar from "../components/Navbar";
import api from "../services/api";
import "../App.css";

function PurchaseHistoryPage({ user, logout }) {
  const [purchases, setPurchases] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (user && user.id) {
      fetchPurchases();
    }
  }, [user]);

  const fetchPurchases = async () => {
    setLoading(true);
    setError("");
    try {
      const response = await api.get(`/api/users/${user.id}/purchase-history`);
      setPurchases(response.data || []);
    } catch (err) {
      console.error("Failed to fetch purchases:", err);
      setError("Failed to load purchase history. Please try again later.");
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return "N/A";
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString() + " " + date.toLocaleTimeString();
    } catch {
      return dateString;
    }
  };

  const totalPointsSpent = purchases.reduce((sum, p) => sum + (p.pointsCost || 0), 0);

  return (
    <div>
      <Navbar user={user} logout={logout} />
      <div className="login-container">
        <div className="login-box" style={{ maxWidth: "800px" }}>
          <img src="/logo.png" alt="ECO Route Logo" className="page-logo" />
          <h2><FaShoppingBag style={{ marginRight: '0.5rem', verticalAlign: 'middle' }} />Purchase History</h2>
          <p className="muted">View your reward redemptions and points spent</p>

          {error && <p className="error">{error}</p>}

          {/* Summary Card */}
          {!loading && purchases.length > 0 && (
            <div style={{
              background: "linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%)",
              border: "2px solid #ffcc80",
              padding: "1.25rem",
              borderRadius: "10px",
              marginBottom: "1.5rem",
              textAlign: "center"
            }}>
              <div style={{ display: "flex", justifyContent: "space-around", flexWrap: "wrap", gap: "1rem" }}>
                <div>
                  <div style={{ fontSize: "1.8rem", fontWeight: "700", color: "#f57c00" }}>
                    <FaReceipt style={{ marginRight: '0.5rem', verticalAlign: 'middle' }} />
                    {purchases.length}
                  </div>
                  <div style={{ fontSize: "0.9rem", color: "#666" }}>Total Purchases</div>
                </div>
                <div>
                  <div style={{ fontSize: "1.8rem", fontWeight: "700", color: "#f57c00" }}>
                    <FaStar style={{ marginRight: '0.5rem', verticalAlign: 'middle' }} />
                    {totalPointsSpent}
                  </div>
                  <div style={{ fontSize: "0.9rem", color: "#666" }}>Points Spent</div>
                </div>
              </div>
            </div>
          )}

          {loading ? (
            <div style={{ textAlign: "center", padding: "2rem" }}>
              <p>Loading purchase history...</p>
            </div>
          ) : purchases.length === 0 ? (
            <div style={{ textAlign: "center", padding: "2rem", color: "#999" }}>
              <FaShoppingBag size={48} style={{ marginBottom: '1rem' }} />
              <p>No purchases yet. Start redeeming rewards!</p>
              <Link to="/rewards" className="action-btn" style={{ marginTop: '1rem', display: 'inline-block' }}>
                <FaGift style={{ marginRight: '0.5rem' }} />View Rewards
              </Link>
            </div>
          ) : (
            <div style={{ marginTop: "1rem" }}>
              {purchases.map((purchase, index) => (
                <div
                  key={purchase.redemptionId || index}
                  style={{
                    background: "linear-gradient(135deg, #fafafa 0%, #f5f5f5 100%)",
                    border: "2px solid #e8e8e8",
                    padding: "1.25rem",
                    borderRadius: "10px",
                    marginBottom: "1rem",
                    boxShadow: "0 2px 10px rgba(0,0,0,0.04)",
                    transition: "transform 0.2s, box-shadow 0.2s"
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.transform = "translateY(-2px)";
                    e.currentTarget.style.boxShadow = "0 4px 16px rgba(0,0,0,0.08)";
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.transform = "translateY(0)";
                    e.currentTarget.style.boxShadow = "0 2px 10px rgba(0,0,0,0.04)";
                  }}
                >
                  <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: "1rem" }}>
                    <div style={{ flex: 1, minWidth: "200px" }}>
                      <div style={{ fontSize: "1.1rem", fontWeight: "700", color: "#2e7d32", marginBottom: "0.5rem" }}>
                        <FaGift style={{ marginRight: '0.5rem', color: '#2e7d32' }} />
                        {purchase.rewardName || "Unknown Reward"}
                      </div>
                      <div style={{ fontSize: "0.9rem", color: "#777" }}>
                        <FaCalendar style={{ marginRight: '0.5rem' }} />
                        {formatDate(purchase.redeemedAt)}
                      </div>
                    </div>

                    <div style={{
                      background: "#fff3e0",
                      border: "2px solid #ffcc80",
                      padding: "0.75rem 1.5rem",
                      borderRadius: "8px",
                      textAlign: "center"
                    }}>
                      <div style={{ fontSize: "1.3rem", fontWeight: "700", color: "#f57c00" }}>
                        <FaStar style={{ marginRight: '0.25rem' }} />
                        {purchase.pointsCost || 0}
                      </div>
                      <div style={{ fontSize: "0.85rem", color: "#666" }}>Points</div>
                    </div>
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

export default PurchaseHistoryPage;
