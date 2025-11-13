import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LogInPage";
import HomePage from "./pages/HomePage";
import RewardsPage from "./pages/RewardsPage";
import RoutePlannerPage from "./pages/RoutePlannerPage";
import StatisticsPage from "./pages/StatisticsPage";
import TripHistoryPage from "./pages/TripHistoryPage";
import PurchaseHistoryPage from "./pages/PurchaseHistoryPage";
import AdminDashboardPage from "./pages/AdminDashboardPage";
import VehicleComparisonPage from "./pages/VehicleComparisonPage";
import GuestGuard from "./components/GuestGard";
import AdminGuard from "./components/AdminGuard";

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

        {/* Admin-only route */}
        <Route
          path="/admin"
          element={
            <AdminGuard user={user}>
              <AdminDashboardPage user={user} logout={handleLogout} />
            </AdminGuard>
          }
        />

        {/* Route Planner - Accessible to all logged-in users including guests */}
        <Route
          path="/routes"
          element={
            user ? <RoutePlannerPage user={user} logout={handleLogout} /> : <Navigate to="/login" />
          }
        />

        {/* Rewards - Accessible to all logged-in users including guests */}
        <Route
          path="/rewards"
          element={
            user ? <RewardsPage user={user} setUser={setUser} /> : <Navigate to="/login" />
          }
        />

        {/* Vehicle Comparison - Accessible to all logged-in users including guests */}
        <Route
          path="/vehicle-comparison"
          element={
            user ? <VehicleComparisonPage user={user} logout={handleLogout} /> : <Navigate to="/login" />
          }
        />

        {/* User-only routes - blocked for guests */}
        <Route
          path="/statistics"
          element={
            <GuestGuard user={user}>
              <StatisticsPage user={user} logout={handleLogout} />
            </GuestGuard>
          }
        />

        <Route
          path="/history"
          element={
            <GuestGuard user={user}>
              <TripHistoryPage user={user} logout={handleLogout} />
            </GuestGuard>
          }
        />

        <Route
          path="/purchases"
          element={
            <GuestGuard user={user}>
              <PurchaseHistoryPage user={user} logout={handleLogout} />
            </GuestGuard>
          }
        />

        {/* Default redirect */}
        <Route path="*" element={<Navigate to={user ? "/home" : "/login"} />} />
      </Routes>
    </Router>
  );
}

export default App;