import React, { useState, useEffect } from "react";
import { Link, Navigate } from "react-router-dom";
import { FaUsers, FaGift, FaChartBar, FaUserPlus, FaEdit, FaTrash, FaUserShield, FaUserTimes, FaUserCheck, FaBoxOpen, FaStar } from 'react-icons/fa';
import AdminNavbar from "../components/AdminNavbar";
import api from "../services/api";
import "../App.css";

function AdminDashboardPage({ user, logout }) {
  const [activeTab, setActiveTab] = useState("users");
  const [users, setUsers] = useState([]);
  const [staff, setStaff] = useState([]);
  const [rewards, setRewards] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [systemStats, setSystemStats] = useState(null);
  
  // Form states for creating new entries
  const [showUserForm, setShowUserForm] = useState(false);
  const [showRewardForm, setShowRewardForm] = useState(false);
  const [editingUser, setEditingUser] = useState(null);
  const [editingReward, setEditingReward] = useState(null);
  
  const [newUser, setNewUser] = useState({
    username: "",
    email: "",
    password: "",
    role: "USER",
    ecoPoints: 0
  });
  
  const [newReward, setNewReward] = useState({
    rewardName: "",
    description: "",
    rewardCost: 0,
    stock: 0
  });

  useEffect(() => {
    if (user?.role === "ADMIN") {
      fetchData();
    }
  }, [activeTab, user]);

  if (!user || user.role !== "ADMIN") {
    return <Navigate to="/home" />;
  }

  const fetchData = async () => {
    setLoading(true);
    setError("");
    try {
      if (activeTab === "users") {
        const response = await fetch("http://localhost:8080/api/admin/users");
        const data = await response.json();
        setUsers(data);
      } else if (activeTab === "rewards") {
        const response = await fetch("http://localhost:8080/api/rewards/all");
        const data = await response.json();
        setRewards(data);
      } else if (activeTab === "statistics") {
        const response = await fetch("http://localhost:8080/api/admin/statistics/system");
        const data = await response.json();
        setSystemStats(data);
      }
    } catch (err) {
      setError("Failed to load data: " + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteUser = async (userId) => {
    if (!window.confirm("Are you sure you want to delete this user?")) return;
    
    try {
      const response = await fetch(`http://localhost:8080/api/admin/users/${userId}`, {
        method: "DELETE",
      });
      if (response.ok) {
        alert("User deleted successfully");
        fetchData();
      } else {
        const data = await response.json();
        alert(data.error || "Failed to delete user");
      }
    } catch (err) {
      alert("Error: " + err.message);
    }
  };

  const handleSuspendUser = async (userId, suspend) => {
    try {
      const response = await fetch(`http://localhost:8080/api/admin/users/${userId}/suspend`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ suspend }),
      });
      if (response.ok) {
        alert(suspend ? "User suspended" : "User unsuspended");
        fetchData();
      } else {
        alert("Failed to update user");
      }
    } catch (err) {
      alert("Error: " + err.message);
    }
  };

  const handleChangeRole = async (userId, newRole) => {
    try {
      const response = await fetch(`http://localhost:8080/api/admin/users/${userId}/role`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ role: newRole }),
      });
      if (response.ok) {
        alert("User role updated successfully");
        fetchData();
      } else {
        alert("Failed to update role");
      }
    } catch (err) {
      alert("Error: " + err.message);
    }
  };

  const handleDeleteReward = async (rewardId) => {
    if (!window.confirm("Are you sure you want to delete this reward?")) return;
    
    try {
      const response = await fetch(`http://localhost:8080/api/rewards/delete/${rewardId}`, {
        method: "DELETE",
      });
      if (response.ok) {
        alert("Reward deleted successfully");
        fetchData();
      } else {
        alert("Failed to delete reward");
      }
    } catch (err) {
      alert("Error: " + err.message);
    }
  };

  const handleCreateUser = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch("http://localhost:8080/api/admin/users", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(newUser),
      });
      if (response.ok) {
        alert("User created successfully");
        setShowUserForm(false);
        setNewUser({ username: "", email: "", password: "", role: "USER", ecoPoints: 0 });
        fetchData();
      } else {
        const data = await response.json();
        alert(data.error || "Failed to create user");
      }
    } catch (err) {
      alert("Error: " + err.message);
    }
  };

  const handleUpdateUser = async (e) => {
    e.preventDefault();
    if (!editingUser) return;
    
    try {
      const response = await fetch(`http://localhost:8080/api/admin/users/${editingUser.id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(editingUser),
      });
      if (response.ok) {
        alert("User updated successfully");
        setEditingUser(null);
        fetchData();
      } else {
        const data = await response.json();
        alert(data.error || "Failed to update user");
      }
    } catch (err) {
      alert("Error: " + err.message);
    }
  };

  const handleCreateReward = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch("http://localhost:8080/api/rewards/create", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(newReward),
      });
      if (response.ok) {
        alert("Reward created successfully");
        setShowRewardForm(false);
        setNewReward({ rewardName: "", description: "", rewardCost: 0, stock: 0 });
        fetchData();
      } else {
        const data = await response.json();
        alert(data.error || "Failed to create reward");
      }
    } catch (err) {
      alert("Error: " + err.message);
    }
  };

  const handleUpdateReward = async (e) => {
    e.preventDefault();
    if (!editingReward) return;
    
    try {
      const response = await fetch(`http://localhost:8080/api/rewards/update/${editingReward.rewardId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(editingReward),
      });
      if (response.ok) {
        alert("Reward updated successfully");
        setEditingReward(null);
        fetchData();
      } else {
        const data = await response.json();
        alert(data.error || "Failed to update reward");
      }
    } catch (err) {
      alert("Error: " + err.message);
    }
  };

  return (
    <div>
      <AdminNavbar user={user} logout={logout} />
      <div className="login-container">
        <div className="login-box" style={{ maxWidth: "1200px" }}>
          <img src="/logo.png" alt="ECO Route Logo" className="page-logo" />
          <h2><FaUserShield style={{ marginRight: '0.5rem', verticalAlign: 'middle' }} />Admin Dashboard</h2>
          <p className="muted">Manage users, rewards, and system settings</p>

          <div style={{ display: "flex", gap: "10px", marginTop: "20px", marginBottom: "20px" }}>
            <button onClick={() => setActiveTab("users")} style={{
                flex: 1, padding: "10px",
                background: activeTab === "users" ? "#6c5ce7" : "#dfe6e9",
                color: activeTab === "users" ? "white" : "#2d3436",
                border: "none", borderRadius: "5px", cursor: "pointer", fontWeight: "bold"
              }}>
              <FaUsers style={{ marginRight: '0.5rem' }} />Users
            </button>
            <button onClick={() => setActiveTab("rewards")} style={{
                flex: 1, padding: "10px",
                background: activeTab === "rewards" ? "#6c5ce7" : "#dfe6e9",
                color: activeTab === "rewards" ? "white" : "#2d3436",
                border: "none", borderRadius: "5px", cursor: "pointer", fontWeight: "bold"
              }}>
              <FaGift style={{ marginRight: '0.5rem' }} />Rewards
            </button>
            <button onClick={() => setActiveTab("statistics")} style={{
                flex: 1, padding: "10px",
                background: activeTab === "statistics" ? "#6c5ce7" : "#dfe6e9",
                color: activeTab === "statistics" ? "white" : "#2d3436",
                border: "none", borderRadius: "5px", cursor: "pointer", fontWeight: "bold"
              }}>
              <FaChartBar style={{ marginRight: '0.5rem' }} />Statistics
            </button>
          </div>

          {error && <div className="error">{error}</div>}

          {loading ? (
            <div style={{ textAlign: "center", padding: "2rem" }}>Loading...</div>
          ) : (
            <>
              {/* Users Tab */}
              {activeTab === "users" && (
                <div className="crud-section">
                  <div className="crud-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <h3><FaUsers style={{ marginRight: '0.5rem' }} />Manage Users</h3>
                    <button 
                      onClick={() => setShowUserForm(true)} 
                      style={{
                        background: '#10ac84',
                        color: 'white',
                        border: 'none',
                        padding: '0.6rem 1.2rem',
                        borderRadius: '5px',
                        cursor: 'pointer',
                        fontWeight: 'bold'
                      }}
                    >
                      <FaUserPlus style={{ marginRight: '0.5rem' }} />Add New User
                    </button>
                  </div>
                  
                  {/* Create User Form Modal */}
                  {showUserForm && (
                    <div style={{
                      position: 'fixed',
                      top: 0,
                      left: 0,
                      right: 0,
                      bottom: 0,
                      background: 'rgba(0,0,0,0.5)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      zIndex: 1000
                    }}>
                      <div style={{
                        background: 'white',
                        padding: '2rem',
                        borderRadius: '10px',
                        maxWidth: '500px',
                        width: '90%',
                        maxHeight: '90vh',
                        overflow: 'auto'
                      }}>
                        <h3>Create New User</h3>
                        <form onSubmit={handleCreateUser}>
                          <div className="form-group">
                            <label>Username:</label>
                            <input
                              type="text"
                              value={newUser.username}
                              onChange={(e) => setNewUser({ ...newUser, username: e.target.value })}
                              required
                              style={{ width: '100%', padding: '0.5rem', marginTop: '0.5rem', border: '2px solid #e0e0e0', borderRadius: '6px' }}
                            />
                          </div>
                          <div className="form-group" style={{ marginTop: '1rem' }}>
                            <label>Email:</label>
                            <input
                              type="email"
                              value={newUser.email}
                              onChange={(e) => setNewUser({ ...newUser, email: e.target.value })}
                              required
                              style={{ width: '100%', padding: '0.5rem', marginTop: '0.5rem', border: '2px solid #e0e0e0', borderRadius: '6px' }}
                            />
                          </div>
                          <div className="form-group" style={{ marginTop: '1rem' }}>
                            <label>Password:</label>
                            <input
                              type="password"
                              value={newUser.password}
                              onChange={(e) => setNewUser({ ...newUser, password: e.target.value })}
                              required
                              style={{ width: '100%', padding: '0.5rem', marginTop: '0.5rem', border: '2px solid #e0e0e0', borderRadius: '6px' }}
                            />
                          </div>
                          <div className="form-group" style={{ marginTop: '1rem' }}>
                            <label>Role:</label>
                            <select
                              value={newUser.role}
                              onChange={(e) => setNewUser({ ...newUser, role: e.target.value })}
                              style={{ width: '100%', padding: '0.5rem', marginTop: '0.5rem', border: '2px solid #e0e0e0', borderRadius: '6px' }}
                            >
                              <option value="USER">USER</option>
                              <option value="ADMIN">ADMIN</option>
                              <option value="STAFF">STAFF</option>
                              <option value="GUEST">GUEST</option>
                            </select>
                          </div>
                          <div className="form-group" style={{ marginTop: '1rem' }}>
                            <label>Eco Points:</label>
                            <input
                              type="number"
                              value={newUser.ecoPoints}
                              onChange={(e) => setNewUser({ ...newUser, ecoPoints: parseInt(e.target.value) || 0 })}
                              style={{ width: '100%', padding: '0.5rem', marginTop: '0.5rem', border: '2px solid #e0e0e0', borderRadius: '6px' }}
                            />
                          </div>
                          <div style={{ marginTop: '1.5rem', display: 'flex', gap: '1rem' }}>
                            <button type="submit" style={{
                              flex: 1,
                              background: '#10ac84',
                              color: 'white',
                              border: 'none',
                              padding: '0.7rem',
                              borderRadius: '5px',
                              cursor: 'pointer',
                              fontWeight: 'bold'
                            }}>
                              Create User
                            </button>
                            <button type="button" onClick={() => {
                              setShowUserForm(false);
                              setNewUser({ username: "", email: "", password: "", role: "USER", ecoPoints: 0 });
                            }} style={{
                              flex: 1,
                              background: '#636e72',
                              color: 'white',
                              border: 'none',
                              padding: '0.7rem',
                              borderRadius: '5px',
                              cursor: 'pointer',
                              fontWeight: 'bold'
                            }}>
                              Cancel
                            </button>
                          </div>
                        </form>
                      </div>
                    </div>
                  )}

                  {/* Edit User Form Modal */}
                  {editingUser && (
                    <div style={{
                      position: 'fixed',
                      top: 0,
                      left: 0,
                      right: 0,
                      bottom: 0,
                      background: 'rgba(0,0,0,0.5)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      zIndex: 1000
                    }}>
                      <div style={{
                        background: 'white',
                        padding: '2rem',
                        borderRadius: '10px',
                        maxWidth: '500px',
                        width: '90%',
                        maxHeight: '90vh',
                        overflow: 'auto'
                      }}>
                        <h3>Edit User</h3>
                        <form onSubmit={handleUpdateUser}>
                          <div className="form-group">
                            <label>Username:</label>
                            <input
                              type="text"
                              value={editingUser.username}
                              onChange={(e) => setEditingUser({ ...editingUser, username: e.target.value })}
                              required
                              style={{ width: '100%', padding: '0.5rem', marginTop: '0.5rem', border: '2px solid #e0e0e0', borderRadius: '6px' }}
                            />
                          </div>
                          <div className="form-group" style={{ marginTop: '1rem' }}>
                            <label>Email:</label>
                            <input
                              type="email"
                              value={editingUser.email}
                              onChange={(e) => setEditingUser({ ...editingUser, email: e.target.value })}
                              required
                              style={{ width: '100%', padding: '0.5rem', marginTop: '0.5rem', border: '2px solid #e0e0e0', borderRadius: '6px' }}
                            />
                          </div>
                          <div className="form-group" style={{ marginTop: '1rem' }}>
                            <label>Eco Points:</label>
                            <input
                              type="number"
                              value={editingUser.ecoPoints || 0}
                              onChange={(e) => setEditingUser({ ...editingUser, ecoPoints: parseInt(e.target.value) || 0 })}
                              style={{ width: '100%', padding: '0.5rem', marginTop: '0.5rem', border: '2px solid #e0e0e0', borderRadius: '6px' }}
                            />
                          </div>
                          <div style={{ marginTop: '1.5rem', display: 'flex', gap: '1rem' }}>
                            <button type="submit" style={{
                              flex: 1,
                              background: '#0984e3',
                              color: 'white',
                              border: 'none',
                              padding: '0.7rem',
                              borderRadius: '5px',
                              cursor: 'pointer',
                              fontWeight: 'bold'
                            }}>
                              Update User
                            </button>
                            <button type="button" onClick={() => setEditingUser(null)} style={{
                              flex: 1,
                              background: '#636e72',
                              color: 'white',
                              border: 'none',
                              padding: '0.7rem',
                              borderRadius: '5px',
                              cursor: 'pointer',
                              fontWeight: 'bold'
                            }}>
                              Cancel
                            </button>
                          </div>
                        </form>
                      </div>
                    </div>
                  )}
                  {users.length === 0 ? (
                    <div className="empty-state">
                      <FaUsers size={48} style={{ marginBottom: '1rem', color: '#ccc' }} />
                      <p>No users found</p>
                    </div>
                  ) : (
                    <table className="crud-table">
                      <thead>
                        <tr>
                          <th>ID</th>
                          <th>Username</th>
                          <th>Email</th>
                          <th>Role</th>
                          <th>Eco Points</th>
                          <th>Status</th>
                          <th>Actions</th>
                        </tr>
                      </thead>
                      <tbody>
                        {users.map((u) => (
                          <tr key={u.id}>
                            <td>{u.id}</td>
                            <td>{u.username}</td>
                            <td>{u.email}</td>
                            <td>
                              <select
                                value={u.role}
                                onChange={(e) => handleChangeRole(u.id, e.target.value)}
                                className="form-group"
                                style={{ padding: "0.5rem", border: "2px solid #e0e0e0", borderRadius: "6px" }}
                              >
                                <option value="USER">USER</option>
                                <option value="ADMIN">ADMIN</option>
                                <option value="STAFF">STAFF</option>
                                <option value="GUEST">GUEST</option>
                              </select>
                            </td>
                            <td><FaStar style={{ marginRight: '0.25rem', color: '#f39c12' }} />{u.ecoPoints || 0}</td>
                            <td>
                              <span className={`badge ${u.suspended ? "staff" : "user"}`}>
                                {u.suspended ? "Suspended" : "Active"}
                              </span>
                            </td>
                            <td>
                              <div className="action-buttons">
                                <button
                                  onClick={() => setEditingUser(u)}
                                  className="edit-btn"
                                  style={{ background: '#0984e3', marginRight: '0.5rem' }}
                                >
                                  <FaEdit style={{ marginRight: '0.25rem' }} />Edit
                                </button>
                                <button
                                  onClick={() => handleSuspendUser(u.id, !u.suspended)}
                                  className="edit-btn"
                                  style={{ background: u.suspended ? '#00b894' : '#fdcb6e' }}
                                >
                                  {u.suspended ? <><FaUserCheck style={{ marginRight: '0.25rem' }} />Unsuspend</> : <><FaUserTimes style={{ marginRight: '0.25rem' }} />Suspend</>}
                                </button>
                                {u.role !== "ADMIN" && (
                                  <button
                                    onClick={() => handleDeleteUser(u.id)}
                                    className="delete-btn"
                                  >
                                    <FaTrash style={{ marginRight: '0.25rem' }} />Delete
                                  </button>
                                )}
                              </div>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  )}
                </div>
              )}

              {/* Rewards Tab */}
              {activeTab === "rewards" && (
                <div className="crud-section">
                  <div className="crud-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <h3><FaGift style={{ marginRight: '0.5rem' }} />Manage Rewards</h3>
                    <button 
                      onClick={() => setShowRewardForm(true)} 
                      style={{
                        background: '#10ac84',
                        color: 'white',
                        border: 'none',
                        padding: '0.6rem 1.2rem',
                        borderRadius: '5px',
                        cursor: 'pointer',
                        fontWeight: 'bold'
                      }}
                    >
                      <FaGift style={{ marginRight: '0.5rem' }} />Add New Reward
                    </button>
                  </div>

                  {/* Create Reward Form Modal */}
                  {showRewardForm && (
                    <div style={{
                      position: 'fixed',
                      top: 0,
                      left: 0,
                      right: 0,
                      bottom: 0,
                      background: 'rgba(0,0,0,0.5)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      zIndex: 1000
                    }}>
                      <div style={{
                        background: 'white',
                        padding: '2rem',
                        borderRadius: '10px',
                        maxWidth: '500px',
                        width: '90%',
                        maxHeight: '90vh',
                        overflow: 'auto'
                      }}>
                        <h3>Create New Reward</h3>
                        <form onSubmit={handleCreateReward}>
                          <div className="form-group">
                            <label>Reward Name:</label>
                            <input
                              type="text"
                              value={newReward.rewardName}
                              onChange={(e) => setNewReward({ ...newReward, rewardName: e.target.value })}
                              required
                              style={{ width: '100%', padding: '0.5rem', marginTop: '0.5rem', border: '2px solid #e0e0e0', borderRadius: '6px' }}
                            />
                          </div>
                          <div className="form-group" style={{ marginTop: '1rem' }}>
                            <label>Description:</label>
                            <textarea
                              value={newReward.description}
                              onChange={(e) => setNewReward({ ...newReward, description: e.target.value })}
                              required
                              rows={3}
                              style={{ width: '100%', padding: '0.5rem', marginTop: '0.5rem', border: '2px solid #e0e0e0', borderRadius: '6px' }}
                            />
                          </div>
                          <div className="form-group" style={{ marginTop: '1rem' }}>
                            <label>Cost (Points):</label>
                            <input
                              type="number"
                              value={newReward.rewardCost}
                              onChange={(e) => setNewReward({ ...newReward, rewardCost: parseInt(e.target.value) || 0 })}
                              required
                              style={{ width: '100%', padding: '0.5rem', marginTop: '0.5rem', border: '2px solid #e0e0e0', borderRadius: '6px' }}
                            />
                          </div>
                          <div className="form-group" style={{ marginTop: '1rem' }}>
                            <label>Stock:</label>
                            <input
                              type="number"
                              value={newReward.stock}
                              onChange={(e) => setNewReward({ ...newReward, stock: parseInt(e.target.value) || 0 })}
                              required
                              style={{ width: '100%', padding: '0.5rem', marginTop: '0.5rem', border: '2px solid #e0e0e0', borderRadius: '6px' }}
                            />
                          </div>
                          <div style={{ marginTop: '1.5rem', display: 'flex', gap: '1rem' }}>
                            <button type="submit" style={{
                              flex: 1,
                              background: '#10ac84',
                              color: 'white',
                              border: 'none',
                              padding: '0.7rem',
                              borderRadius: '5px',
                              cursor: 'pointer',
                              fontWeight: 'bold'
                            }}>
                              Create Reward
                            </button>
                            <button type="button" onClick={() => {
                              setShowRewardForm(false);
                              setNewReward({ rewardName: "", description: "", rewardCost: 0, stock: 0 });
                            }} style={{
                              flex: 1,
                              background: '#636e72',
                              color: 'white',
                              border: 'none',
                              padding: '0.7rem',
                              borderRadius: '5px',
                              cursor: 'pointer',
                              fontWeight: 'bold'
                            }}>
                              Cancel
                            </button>
                          </div>
                        </form>
                      </div>
                    </div>
                  )}

                  {/* Edit Reward Form Modal */}
                  {editingReward && (
                    <div style={{
                      position: 'fixed',
                      top: 0,
                      left: 0,
                      right: 0,
                      bottom: 0,
                      background: 'rgba(0,0,0,0.5)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      zIndex: 1000
                    }}>
                      <div style={{
                        background: 'white',
                        padding: '2rem',
                        borderRadius: '10px',
                        maxWidth: '500px',
                        width: '90%',
                        maxHeight: '90vh',
                        overflow: 'auto'
                      }}>
                        <h3>Edit Reward</h3>
                        <form onSubmit={handleUpdateReward}>
                          <div className="form-group">
                            <label>Reward Name:</label>
                            <input
                              type="text"
                              value={editingReward.rewardName}
                              onChange={(e) => setEditingReward({ ...editingReward, rewardName: e.target.value })}
                              required
                              style={{ width: '100%', padding: '0.5rem', marginTop: '0.5rem', border: '2px solid #e0e0e0', borderRadius: '6px' }}
                            />
                          </div>
                          <div className="form-group" style={{ marginTop: '1rem' }}>
                            <label>Description:</label>
                            <textarea
                              value={editingReward.description}
                              onChange={(e) => setEditingReward({ ...editingReward, description: e.target.value })}
                              required
                              rows={3}
                              style={{ width: '100%', padding: '0.5rem', marginTop: '0.5rem', border: '2px solid #e0e0e0', borderRadius: '6px' }}
                            />
                          </div>
                          <div className="form-group" style={{ marginTop: '1rem' }}>
                            <label>Cost (Points):</label>
                            <input
                              type="number"
                              value={editingReward.rewardCost}
                              onChange={(e) => setEditingReward({ ...editingReward, rewardCost: parseInt(e.target.value) || 0 })}
                              required
                              style={{ width: '100%', padding: '0.5rem', marginTop: '0.5rem', border: '2px solid #e0e0e0', borderRadius: '6px' }}
                            />
                          </div>
                          <div className="form-group" style={{ marginTop: '1rem' }}>
                            <label>Stock:</label>
                            <input
                              type="number"
                              value={editingReward.stock || 0}
                              onChange={(e) => setEditingReward({ ...editingReward, stock: parseInt(e.target.value) || 0 })}
                              style={{ width: '100%', padding: '0.5rem', marginTop: '0.5rem', border: '2px solid #e0e0e0', borderRadius: '6px' }}
                            />
                          </div>
                          <div style={{ marginTop: '1.5rem', display: 'flex', gap: '1rem' }}>
                            <button type="submit" style={{
                              flex: 1,
                              background: '#0984e3',
                              color: 'white',
                              border: 'none',
                              padding: '0.7rem',
                              borderRadius: '5px',
                              cursor: 'pointer',
                              fontWeight: 'bold'
                            }}>
                              Update Reward
                            </button>
                            <button type="button" onClick={() => setEditingReward(null)} style={{
                              flex: 1,
                              background: '#636e72',
                              color: 'white',
                              border: 'none',
                              padding: '0.7rem',
                              borderRadius: '5px',
                              cursor: 'pointer',
                              fontWeight: 'bold'
                            }}>
                              Cancel
                            </button>
                          </div>
                        </form>
                      </div>
                    </div>
                  )}
                  {rewards.length === 0 ? (
                    <div className="empty-state">
                      <FaGift size={48} style={{ marginBottom: '1rem', color: '#ccc' }} />
                      <p>No rewards found</p>
                    </div>
                  ) : (
                    <table className="crud-table">
                      <thead>
                        <tr>
                          <th>ID</th>
                          <th>Name</th>
                          <th>Description</th>
                          <th>Cost (Points)</th>
                          <th>Stock</th>
                          <th>Actions</th>
                        </tr>
                      </thead>
                      <tbody>
                        {rewards.map((r) => (
                          <tr key={r.rewardId}>
                            <td>{r.rewardId}</td>
                            <td><FaGift style={{ marginRight: '0.5rem', color: '#2e7d32' }} />{r.rewardName}</td>
                            <td>{r.description}</td>
                            <td><FaStar style={{ marginRight: '0.25rem', color: '#f39c12' }} />{r.rewardCost}</td>
                            <td><FaBoxOpen style={{ marginRight: '0.25rem' }} />{r.stock || 0}</td>
                            <td>
                              <div className="action-buttons">
                                <button
                                  onClick={() => setEditingReward(r)}
                                  className="edit-btn"
                                  style={{ background: '#0984e3', marginRight: '0.5rem' }}
                                >
                                  <FaEdit style={{ marginRight: '0.25rem' }} />Edit
                                </button>
                                <button
                                  onClick={() => handleDeleteReward(r.rewardId)}
                                  className="delete-btn"
                                >
                                  <FaTrash style={{ marginRight: '0.25rem' }} />Delete
                                </button>
                              </div>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  )}
                </div>
              )}

              {/* Statistics Tab */}
              {activeTab === "statistics" && systemStats && (
                <div className="crud-section">
                  <h3><FaChartBar style={{ marginRight: '0.5rem' }} />System Statistics</h3>
                  <div className="user-stats" style={{ marginTop: "2rem" }}>
                    <div className="stat">
                      <div className="stat-value"><FaUsers size={32} color="#2e7d32" />{systemStats.totalUsers}</div>
                      <div className="stat-label">Total Users</div>
                    </div>
                    <div className="stat">
                      <div className="stat-value"><FaUserCheck size={32} color="#2e7d32" />{systemStats.activeUsers}</div>
                      <div className="stat-label">Active Users</div>
                    </div>
                    <div className="stat">
                      <div className="stat-value"><FaUserTimes size={32} color="#e74c3c" />{systemStats.suspendedUsers}</div>
                      <div className="stat-label">Suspended Users</div>
                    </div>
                    <div className="stat">
                      <div className="stat-value"><FaStar size={32} color="#f39c12" />{systemStats.totalEcoPoints}</div>
                      <div className="stat-label">Total Eco Points</div>
                    </div>
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
}

export default AdminDashboardPage;
