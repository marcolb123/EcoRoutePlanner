import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import "../App.css";

function LoginPage({ setUser }) {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [guestLoading, setGuestLoading] = useState(false);
  const [error, setError] = useState("");

  // Registration state
  const [showRegister, setShowRegister] = useState(false);
  const [regUsername, setRegUsername] = useState("");
  const [regPassword, setRegPassword] = useState("");
  const [regConfirm, setRegConfirm] = useState("");
  const [regEmail, setRegEmail] = useState("");
  const [registerLoading, setRegisterLoading] = useState(false);
  const [registerError, setRegisterError] = useState("");

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

  // Handle registration
  const handleRegister = async (e) => {
    e.preventDefault();
    setRegisterError("");
    if (!regUsername || !regPassword) {
      setRegisterError("Please fill all fields.");
      return;
    }
    if (regPassword !== regConfirm) {
      setRegisterError("Passwords must match.");
      return;
    }

    setRegisterLoading(true);
    try {
      const res = await api.post("/api/users/register", {
        username: regUsername,
        password: regPassword,
        email: regEmail || null
      });
      // auto-login on successful registration
      localStorage.setItem("user", JSON.stringify(res.data));
      setUser(res.data);
      navigate("/home");
    } catch (err) {
      const code = err?.response?.data?.error;
      if (code === "username_taken") setRegisterError("Username already taken.");
      else if (code === "missing_fields") setRegisterError("Please fill all required fields.");
      else if (code === "server_error") setRegisterError("Registration failed - server error.");
      else setRegisterError("Registration failed. Try a different username.");
    } finally {
      setRegisterLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <img src="/logo.png" alt="ECO Route Logo" className="page-logo" />
        <h3>Sign in to continue or explore as guest</h3>
        

        {!showRegister ? (
          <>
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

              <button type="submit" className="primary-btn" disabled={loading}>
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

            <div style={{ marginTop: 10 }}>
              <button
                className="link-like"
                onClick={() => {
                  setShowRegister(true);
                  setRegisterError("");
                }}
              >
                Create a new account
              </button>
            </div>
          </>
        ) : (
          <>
            <form onSubmit={handleRegister}>
              <input
                type="text"
                placeholder="Choose a username"
                value={regUsername}
                onChange={(e) => setRegUsername(e.target.value)}
                required
              />

              <input
                type="password"
                placeholder="Choose a password"
                value={regPassword}
                onChange={(e) => setRegPassword(e.target.value)}
                required
              />

              <input
                type="password"
                placeholder="Confirm password"
                value={regConfirm}
                onChange={(e) => setRegConfirm(e.target.value)}
                required
              />

              <input
                type="email"
                placeholder="Email (optional)"
                value={regEmail}
                onChange={(e) => setRegEmail(e.target.value)}
              />

              <button type="submit" className="primary-btn" disabled={registerLoading}>
                {registerLoading ? "Creating account..." : "Create account"}
              </button>
            </form>

            <div style={{ marginTop: 10 }}>
              <button
                className="link-like"
                onClick={() => {
                  setShowRegister(false);
                  setRegisterError("");
                }}
              >
                Back to Login
              </button>
            </div>

            {registerError && <p className="error">{registerError}</p>}
          </>
        )}

        {error && <p className="error">{error}</p>}
      </div>
    </div>
  );
}

export default LoginPage;