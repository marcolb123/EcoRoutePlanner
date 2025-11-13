import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { FaCar, FaRoute, FaCalendar, FaTrash, FaArrowLeft, FaLeaf, FaRuler, FaMapMarkerAlt } from 'react-icons/fa';
import Navbar from "../components/Navbar";
import api from "../services/api";
import "../App.css";

function TripHistoryPage({ user, logout }) {
  const [journeys, setJourneys] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (user && user.id) {
      fetchJourneys();
    }
  }, [user]);

  const fetchJourneys = async () => {
    setLoading(true);
    setError("");
    try {
      const response = await api.get(`/api/journeys/user/${user.id}`);
      setJourneys(response.data || []);
    } catch (err) {
      console.error("Failed to fetch journeys:", err);
      setError("Failed to load trip history. Please try again later.");
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (journeyId) => {
    if (!window.confirm("Are you sure you want to delete this journey?")) return;
    
    try {
      await api.delete(`/api/journeys/${journeyId}`);
      setJourneys(journeys.filter(j => j.journeyId !== journeyId));
    } catch (err) {
      console.error("Failed to delete journey:", err);
      alert("Failed to delete journey. Please try again.");
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

  return (
    <div>
      <Navbar user={user} logout={logout} />
      <div className="login-container">
        <div className="login-box" style={{ maxWidth: "900px" }}>
          <img src="/logo.png" alt="ECO Route Logo" className="page-logo" />
          <h2><FaRoute style={{ marginRight: '0.5rem', verticalAlign: 'middle' }} />Trip History</h2>
          <p className="muted">View and manage your eco-friendly journeys</p>

          {error && <p className="error">{error}</p>}

          {loading ? (
            <div style={{ textAlign: "center", padding: "2rem" }}>
              <p>Loading trip history...</p>
            </div>
          ) : journeys.length === 0 ? (
            <div style={{ textAlign: "center", padding: "2rem", color: "#999" }}>
              <FaCar size={48} style={{ marginBottom: '1rem' }} />
              <p>No trips yet. Start planning eco-friendly routes!</p>
              <Link to="/routes" className="action-btn" style={{ marginTop: '1rem', display: 'inline-block' }}>
                <FaMapMarkerAlt style={{ marginRight: '0.5rem' }} />Plan a Route
              </Link>
            </div>
          ) : (
            <div style={{ marginTop: "1.5rem" }}>
              {journeys.map((journey) => (
                <div
                  key={journey.journeyId}
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
                  <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", flexWrap: "wrap", gap: "1rem" }}>
                    <div style={{ flex: 1, minWidth: "250px" }}>
                      <div style={{ fontSize: "1.1rem", fontWeight: "700", color: "#2e7d32", marginBottom: "0.5rem" }}>
                        <FaCar style={{ marginRight: '0.5rem', verticalAlign: 'middle' }} />
                        {journey.vehicle || "Unknown Vehicle"}
                      </div>
                      <div style={{ fontSize: "0.95rem", color: "#555", marginBottom: "0.25rem" }}>
                        <FaMapMarkerAlt style={{ marginRight: '0.5rem', color: '#2e7d32' }} />
                        <strong>From:</strong> {journey.travelingFrom || "N/A"}
                      </div>
                      <div style={{ fontSize: "0.95rem", color: "#555", marginBottom: "0.25rem" }}>
                        <FaMapMarkerAlt style={{ marginRight: '0.5rem', color: '#d32f2f' }} />
                        <strong>To:</strong> {journey.travelingTo || "N/A"}
                      </div>
                      <div style={{ fontSize: "0.9rem", color: "#777", marginTop: "0.5rem" }}>
                        <FaCalendar style={{ marginRight: '0.5rem' }} />
                        {formatDate(journey.travelDate)}
                      </div>
                    </div>

                    <div style={{ 
                      display: "flex", 
                      gap: "1rem", 
                      flexWrap: "wrap",
                      minWidth: "250px"
                    }}>
                      <div style={{ 
                        background: "#e8f5e9", 
                        padding: "0.75rem 1rem", 
                        borderRadius: "8px",
                        textAlign: "center",
                        flex: 1,
                        minWidth: "100px"
                      }}>
                        <div style={{ fontSize: "1.2rem", fontWeight: "700", color: "#2e7d32" }}>
                          <FaRuler style={{ marginRight: '0.25rem' }} />
                          {journey.distance ? Number(journey.distance).toFixed(2) : "0"} km
                        </div>
                        <div style={{ fontSize: "0.85rem", color: "#666" }}>Distance</div>
                      </div>

                      <div style={{ 
                        background: "#fff3e0", 
                        padding: "0.75rem 1rem", 
                        borderRadius: "8px",
                        textAlign: "center",
                        flex: 1,
                        minWidth: "100px"
                      }}>
                        <div style={{ fontSize: "1.2rem", fontWeight: "700", color: "#f57c00" }}>
                          <FaLeaf style={{ marginRight: '0.25rem' }} />
                          {journey.emissionsReduced ? Number(journey.emissionsReduced).toFixed(2) : "0"} kg
                        </div>
                        <div style={{ fontSize: "0.85rem", color: "#666" }}>CO₂ Saved</div>
                      </div>
                    </div>

                    <button
                      onClick={() => handleDelete(journey.journeyId)}
                      style={{
                        background: "#e57373",
                        color: "white",
                        border: "none",
                        padding: "0.65rem 1.25rem",
                        borderRadius: "8px",
                        cursor: "pointer",
                        fontWeight: "700",
                        fontSize: "0.95rem",
                        transition: "all 0.2s",
                        whiteSpace: "nowrap"
                      }}
                      onMouseEnter={(e) => {
                        e.target.style.background = "#ef5350";
                        e.target.style.transform = "translateY(-1px)";
                      }}
                      onMouseLeave={(e) => {
                        e.target.style.background = "#e57373";
                        e.target.style.transform = "translateY(0)";
                      }}
                    >
                      <FaTrash style={{ marginRight: '0.5rem' }} />Delete
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

export default TripHistoryPage;
