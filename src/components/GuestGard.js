import React from "react";
import { Navigate, useNavigate } from "react-router-dom";
import "../App.css";

function GuestGuard({ user, children }) {
  const navigate = useNavigate();
  const role = (user?.role || "").toString().toUpperCase();

  // If not logged in at all, redirect to login
  if (!user) return <Navigate to="/login" />;

  // If admin, redirect to admin dashboard
  if (role === "ADMIN") {
    return <Navigate to="/admin" />;
  }

  // If logged-in and NOT a guest, render the protected content
  if (role !== "GUEST") {
    return children;
  }

  // If logged-in but a guest, show the modal with two choices:
  // - Go to Login
  // - Close (returns to home)
  const goToLogin = () => navigate("/login");
  const closeModal = () => navigate("/home");

  return (
    <div className="modal-overlay">
      <div className="modal">
        <h3>Sign in required</h3>
        <p>Guest accounts can't access this feature. Please log in with a registered account.</p>
        <div className="modal-actions">
          <button className="modal-btn primary" onClick={goToLogin}>
            Go to Login
          </button>
          <button className="modal-btn" onClick={closeModal}>
            Close
          </button>
        </div>
      </div>
    </div>
  );
}

export default GuestGuard;