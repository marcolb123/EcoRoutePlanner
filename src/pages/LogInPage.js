import React, { useState } from "react";
import axios from "axios";
import "../App.css"; // optional styling

function LoginPage({ setUser }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // Handle login
  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const res = await axios.post("http://localhost:8080/api/users/login", {
        username,
        password,
      });

      // Store user data locally
      localStorage.setItem("user", JSON.stringify(res.data));
      setUser(res.data);
    } catch (err) {
      setError("Invalid username or password.");
    } finally {
      setLoading(false);
    }
  };

  // Handle guest login
  const handleGuest = async () => {
    try {
      const res = await axios.post("http://localhost:8080/api/users/guest");
      localStorage.setItem("user", JSON.stringify(res.data));
      setUser(res.data);
    } catch (err) {
      setError("Guest login failed. Please try again.");
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

        <button className="guest-btn" onClick={handleGuest}>
          Continue as Guest
        </button>

        {error && <p className="error">{error}</p>}
      </div>
    </div>
  );
}

export default LoginPage;
