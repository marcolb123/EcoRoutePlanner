import React from "react";
import { Link } from "react-router-dom";

function HomePage({ user, logout }) {
  return (
    <div style={{ textAlign: "center", marginTop: "2rem" }}>
      <h2>Welcome, {user.username}!</h2>
      <p>Role: {user.role}</p>
      <nav>
        <Link to="/routes">🗺️ Route Planner</Link> |{" "}
        <Link to="/rewards">🎁 Rewards</Link> |{" "}
        <button onClick={logout}>Logout</button>
      </nav>
    </div>
  );
}

export default HomePage;
