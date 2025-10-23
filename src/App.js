import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LogInPage";
import HomePage from "./pages/HomePage";
import RewardsPage from "./pages/RewardsPage";
import RoutePlannerPage from "./pages/RoutePlannerPage";
import GuestGuard from "./components/GuestGard";

function App() {
  const [user, setUser] = useState(null);

  // Load user from localStorage on refresh
  useEffect(() => {
    const storedUser = localStorage.getItem("user");
    if (storedUser) setUser(JSON.parse(storedUser));
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("user");
    setUser(null);
  };

  return (
    <Router>
      <Routes>
        {/* Login Route */}
        <Route path="/login" element={<LoginPage setUser={setUser} />} />

        {/* Protected routes */}
        <Route
          path="/home"
          element={
            user ? <HomePage user={user} logout={handleLogout} /> : <Navigate to="/login" />
          }
        />

        {/* Rewards: allow only non-guest users; if guest show modal */}
        <Route
          path="/rewards"
          element={
            <GuestGuard user={user}>
              <RewardsPage user={user} />
            </GuestGuard>
          }
        />

        <Route
          path="/routes"
          element={
            user ? <RoutePlannerPage user={user} /> : <Navigate to="/login" />
          }
        />

        {/* Default redirect */}
        <Route path="*" element={<Navigate to={user ? "/home" : "/login"} />} />
      </Routes>
    </Router>
  );
}

export default App;