import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import "../App.css"; // optional styling

function LoginPage({ setUser }) {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [guestLoading, setGuestLoading] = useState(false);
  const [error, setError] = useState("");

  // Handle login
  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const res = await api.post("/api/users/login", { username, password });

      if (res.data && res.data.error) {
        setError("Invalid username or password.");
      } else {
        // Store user data locally and update app state
        localStorage.setItem("user", JSON.stringify(res.data));
        setUser(res.data);
        navigate("/home");
      }
    } catch (err) {
      setError("Invalid username or password.");
    } finally {
      setLoading(false);
    }
  };

  // Handle guest login
  const handleGuest = async () => {
    setError("");
    setGuestLoading(true);
    try {
      const res = await api.post("/api/users/guest");
      localStorage.setItem("user", JSON.stringify(res.data));
      setUser(res.data);
      navigate("/home");
    } catch (err) {
      setError("Guest login failed. Please try again.");
    } finally {
      setGuestLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <h2>Eco Route Planner</h2>
        <p>Sign in to continue or explore as guest</p>

        <form onSubmit={handleLogin}>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />

          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          <button type="submit" disabled={loading}>
            {loading ? "Logging in..." : "Login"}
          </button>
        </form>

        <button
          className="guest-btn"
          onClick={handleGuest}
          disabled={guestLoading}
        >
          {guestLoading ? "Continuing as Guest..." : "Continue as Guest"}
        </button>

        {error && <p className="error">{error}</p>}
      </div>
    </div>
  );
}

export default LoginPage;
