import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaUserCircle, FaLock, FaEnvelope, FaUserPlus, FaSignInAlt, FaUserShield, FaArrowLeft } from 'react-icons/fa';
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
      // Distinguish network/back-end availability from invalid credentials
      if (!err.response) {
        // No response from server (timeout / network error)
        setError("Backend unavailable — try again later.");
      } else if (err.response.status === 400 && err.response.data?.error === 'invalid_credentials') {
        setError("Invalid username or password.");
      } else {
        setError("Login failed — please try again.");
      }
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
        <h3><FaSignInAlt style={{ marginRight: '0.5rem', verticalAlign: 'middle' }} />Sign in to continue or explore as guest</h3>
        

        {!showRegister ? (
          <>
            <form onSubmit={handleLogin}>
              <div style={{ position: 'relative' }}>
                <FaUserCircle style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: '#999', zIndex: 1 }} />
                <input
                  type="text"
                  placeholder="Username"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                  style={{ paddingLeft: '2.5rem' }}
                />
              </div>

              <div style={{ position: 'relative' }}>
                <FaLock style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: '#999', zIndex: 1 }} />
                <input
                  type="password"
                  placeholder="Password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  style={{ paddingLeft: '2.5rem' }}
                />
              </div>

              <button type="submit" className="primary-btn" disabled={loading}>
                <FaSignInAlt style={{ marginRight: '0.5rem' }} />
                {loading ? "Logging in..." : "Login"}
              </button>
            </form>

            <button
              className="guest-btn"
              onClick={handleGuest}
              disabled={guestLoading}
            >
              <FaUserShield style={{ marginRight: '0.5rem' }} />
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
                <FaUserPlus style={{ marginRight: '0.5rem' }} />
                Create a new account
              </button>
            </div>
          </>
        ) : (
          <>
            <form onSubmit={handleRegister}>
              <div style={{ position: 'relative' }}>
                <FaUserCircle style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: '#999', zIndex: 1 }} />
                <input
                  type="text"
                  placeholder="Choose a username"
                  value={regUsername}
                  onChange={(e) => setRegUsername(e.target.value)}
                  required
                  style={{ paddingLeft: '2.5rem' }}
                />
              </div>

              <div style={{ position: 'relative' }}>
                <FaLock style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: '#999', zIndex: 1 }} />
                <input
                  type="password"
                  placeholder="Choose a password"
                  value={regPassword}
                  onChange={(e) => setRegPassword(e.target.value)}
                  required
                  style={{ paddingLeft: '2.5rem' }}
                />
              </div>

              <div style={{ position: 'relative' }}>
                <FaLock style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: '#999', zIndex: 1 }} />
                <input
                  type="password"
                  placeholder="Confirm password"
                  value={regConfirm}
                  onChange={(e) => setRegConfirm(e.target.value)}
                  required
                  style={{ paddingLeft: '2.5rem' }}
                />
              </div>

              <div style={{ position: 'relative' }}>
                <FaEnvelope style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: '#999', zIndex: 1 }} />
                <input
                  type="email"
                  placeholder="Email (optional)"
                  value={regEmail}
                  onChange={(e) => setRegEmail(e.target.value)}
                  style={{ paddingLeft: '2.5rem' }}
                />
              </div>

              <button type="submit" className="primary-btn" disabled={registerLoading}>
                <FaUserPlus style={{ marginRight: '0.5rem' }} />
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
                <FaArrowLeft style={{ marginRight: '0.5rem' }} />
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