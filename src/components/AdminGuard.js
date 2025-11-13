import React from "react";
import { Navigate } from "react-router-dom";

function AdminGuard({ user, children }) {
  // If not logged in, redirect to login
  if (!user) return <Navigate to="/login" />;

  // If not admin, redirect to home
  if (user.role !== "ADMIN") {
    return <Navigate to="/home" />;
  }

  // Admin user, render the protected content
  return children;
}

export default AdminGuard;
