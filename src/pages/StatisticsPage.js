import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { FaChartLine, FaRoute, FaRuler, FaLeaf, FaStar, FaArrowLeft, FaCar, FaChartBar } from 'react-icons/fa';
import Navbar from "../components/Navbar";
import api from "../services/api";
import "../App.css";

function StatisticsPage({ user, logout }) {
  const [stats, setStats] = useState({
    totalJourneys: 0,
    totalDistance: 0,
    averageDistance: 0,
    totalEmissions: 0,
    averageEmissions: 0,
    totalEmissionsReduced: 0,
    averageEmissionsReduced: 0,
    ecoPoints: user?.ecoPoints || 0
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    if (user && user.id) {
      fetchStatistics();
    }
  }, [user]);

  const fetchStatistics = async () => {
    setLoading(true);
    setError("");
    
    try {
      const userId = user.id;
      console.log("Fetching statistics for userId:", userId);

      // Fetch all statistics in parallel
      const [
        totalJourneysRes,
        totalDistanceRes,
        averageDistanceRes,
        totalEmissionsRes,
        averageEmissionsRes,
        totalEmissionsReducedRes,
        averageEmissionsReducedRes
      ] = await Promise.all([
        api.get(`/api/statistics/totalJourneys?userId=${userId}`),
        api.get(`/api/statistics/totalDistance?userId=${userId}`),
        api.get(`/api/statistics/averageDistance?userId=${userId}`),
        api.get(`/api/statistics/totalEmissions?userId=${userId}`),
        api.get(`/api/statistics/averageEmissions?userId=${userId}`),
        api.get(`/api/statistics/totalEmissionsReduced?userId=${userId}`),
        api.get(`/api/statistics/averageEmissionsReduced?userId=${userId}`)
      ]);

      console.log("Statistics responses:", {
        totalJourneys: totalJourneysRes.data,
        totalDistance: totalDistanceRes.data,
        averageDistance: averageDistanceRes.data
      });

      setStats({
        totalJourneys: totalJourneysRes.data || 0,
        totalDistance: totalDistanceRes.data || 0,
        averageDistance: averageDistanceRes.data || 0,
        totalEmissions: totalEmissionsRes.data || 0,
        averageEmissions: averageEmissionsRes.data || 0,
        totalEmissionsReduced: totalEmissionsReducedRes.data || 0,
        averageEmissionsReduced: averageEmissionsReducedRes.data || 0,
        ecoPoints: user.ecoPoints || 0
      });
    } catch (err) {
      console.error("Failed to fetch statistics:", err);
      setError("Failed to load statistics. Please try again later.");
    } finally {
      setLoading(false);
    }
  };

  if (!user) {
    return <div>Please log in to view statistics.</div>;
  }

  return (
    <div>
      <Navbar user={user} logout={logout} />
      <div className="login-container">
        <div className="login-box" style={{ maxWidth: "900px" }}>
          <img src="/logo.png" alt="ECO Route Logo" className="page-logo" />
          <h2><FaChartLine style={{ marginRight: '0.5rem', verticalAlign: 'middle' }} />Your Statistics</h2>
          <p className="muted">Track your environmental impact and progress</p>

          {error && <p className="error">{error}</p>}

          {loading ? (
            <div style={{ textAlign: "center", padding: "2rem" }}>
              <p>Loading statistics...</p>
            </div>
          ) : !stats ? (
            <div style={{ textAlign: "center", padding: "2rem", color: "#999" }}>
              <FaChartBar size={48} style={{ marginBottom: '1rem' }} />
              <p>No statistics available yet.</p>
            </div>
          ) : (
            <>
              {/* Main Statistics Grid */}
              <div style={{
                display: "grid",
                gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))",
                gap: "1rem",
                marginTop: "1.5rem"
              }}>
                <div className="stat" style={{ 
                  background: "linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%)",
                  border: "2px solid #90caf9"
                }}>
                  <div className="stat-value">
                    <FaRoute size={32} color="#1976d2" style={{ marginBottom: '0.5rem' }} />
                    <br />{stats.totalJourneys || 0}
                  </div>
                  <div className="stat-label">Total Journeys</div>
                </div>

                <div className="stat" style={{ 
                  background: "linear-gradient(135deg, #f3e5f5 0%, #e1bee7 100%)",
                  border: "2px solid #ce93d8"
                }}>
                  <div className="stat-value">
                    <FaRuler size={32} color="#7b1fa2" style={{ marginBottom: '0.5rem' }} />
                    <br />{stats.totalDistance ? Number(stats.totalDistance).toFixed(2) : "0"} km
                  </div>
                  <div className="stat-label">Total Distance</div>
                </div>

                <div className="stat" style={{ 
                  background: "linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%)",
                  border: "2px solid #a5d6a7"
                }}>
                  <div className="stat-value">
                    <FaLeaf size={32} color="#2e7d32" style={{ marginBottom: '0.5rem' }} />
                    <br />{stats.totalEmissionsReduced ? Number(stats.totalEmissionsReduced).toFixed(2) : "0"} kg
                  </div>
                  <div className="stat-label">CO₂ Saved</div>
                </div>

                <div className="stat" style={{ 
                  background: "linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%)",
                  border: "2px solid #ffcc80"
                }}>
                  <div className="stat-value">
                    <FaStar size={32} color="#f57c00" style={{ marginBottom: '0.5rem' }} />
                    <br />{user.ecoPoints || 0}
                  </div>
                  <div className="stat-label">Eco Points</div>
                </div>
              </div>

              {/* Average Statistics */}
              <div style={{ marginTop: "2rem" }}>
                <h3 style={{ color: "#2e7d32", marginBottom: "1rem" }}>
                  <FaChartBar style={{ marginRight: '0.5rem', verticalAlign: 'middle' }} />
                  Average per Journey
                </h3>
                <div style={{
                  display: "grid",
                  gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))",
                  gap: "1rem"
                }}>
                  <div style={{
                    background: "#fafafa",
                    border: "2px solid #e8e8e8",
                    padding: "1rem",
                    borderRadius: "10px",
                    textAlign: "center"
                  }}>
                    <div style={{ fontSize: "1.3rem", fontWeight: "700", color: "#1976d2", marginBottom: "0.25rem" }}>
                      <FaRuler style={{ marginRight: '0.25rem' }} />
                      {stats.averageDistance ? Number(stats.averageDistance).toFixed(2) : "0"} km
                    </div>
                    <div style={{ fontSize: "0.9rem", color: "#666" }}>Avg Distance</div>
                  </div>

                  <div style={{
                    background: "#fafafa",
                    border: "2px solid #e8e8e8",
                    padding: "1rem",
                    borderRadius: "10px",
                    textAlign: "center"
                  }}>
                    <div style={{ fontSize: "1.3rem", fontWeight: "700", color: "#d32f2f", marginBottom: "0.25rem" }}>
                      <FaCar style={{ marginRight: '0.25rem' }} />
                      {stats.averageEmissions ? Number(stats.averageEmissions).toFixed(2) : "0"} kg
                    </div>
                    <div style={{ fontSize: "0.9rem", color: "#666" }}>Avg Emissions</div>
                  </div>

                  <div style={{
                    background: "#fafafa",
                    border: "2px solid #e8e8e8",
                    padding: "1rem",
                    borderRadius: "10px",
                    textAlign: "center"
                  }}>
                    <div style={{ fontSize: "1.3rem", fontWeight: "700", color: "#2e7d32", marginBottom: "0.25rem" }}>
                      <FaLeaf style={{ marginRight: '0.25rem' }} />
                      {stats.averageEmissionsReduced ? Number(stats.averageEmissionsReduced).toFixed(2) : "0"} kg
                    </div>
                    <div style={{ fontSize: "0.9rem", color: "#666" }}>Avg CO₂ Saved</div>
                  </div>
                </div>
              </div>

              {/* Environmental Impact Summary */}
              <div style={{
                marginTop: "2rem",
                background: "linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%)",
                border: "2px solid #a5d6a7",
                padding: "1.5rem",
                borderRadius: "12px",
                textAlign: "center"
              }}>
                <h3 style={{ color: "#2e7d32", marginBottom: "1rem" }}>
                  <FaLeaf style={{ marginRight: '0.5rem', verticalAlign: 'middle' }} />
                  Environmental Impact
                </h3>
                <p style={{ fontSize: "1.1rem", color: "#555", lineHeight: "1.6" }}>
                  By choosing eco-friendly transportation, you've saved approximately{" "}
                  <strong style={{ color: "#2e7d32" }}>
                    {stats.totalEmissionsReduced ? Number(stats.totalEmissionsReduced).toFixed(2) : "0"} kg
                  </strong>{" "}
                  of CO₂ emissions across{" "}
                  <strong style={{ color: "#2e7d32" }}>{stats.totalJourneys || 0}</strong> journeys!
                </p>
                <p style={{ fontSize: "0.95rem", color: "#666", marginTop: "0.75rem" }}>
                  Keep up the great work and continue making sustainable choices! 🌱
                </p>
              </div>
            </>
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

export default StatisticsPage;
