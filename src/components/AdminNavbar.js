import React from 'react';
import { Link } from 'react-router-dom';
import '../App.css';

function AdminNavbar({ user, logout }) {
  return (
    <nav style={{
      padding: '1rem 2rem',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      color: 'white',
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      boxShadow: '0 2px 10px rgba(0,0,0,0.1)'
    }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '2rem' }}>
        <h2 style={{ margin: 0, fontSize: '1.5rem' }}>🛡️ Admin Panel</h2>
        <div style={{ display: 'flex', gap: '1rem' }}>
          <Link to="/admin" style={{
            color: 'white',
            textDecoration: 'none',
            padding: '0.5rem 1rem',
            borderRadius: '5px',
            transition: 'background 0.3s',
            background: 'rgba(255,255,255,0.1)'
          }}>
            Dashboard
          </Link>
        </div>
      </div>
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
        <span style={{ fontSize: '0.9rem', opacity: 0.9 }}>
          👤 {user?.username} (Admin)
        </span>
        <button
          onClick={logout}
          style={{
            background: 'rgba(255,255,255,0.2)',
            color: 'white',
            border: 'none',
            padding: '0.5rem 1rem',
            borderRadius: '5px',
            cursor: 'pointer',
            fontWeight: 'bold',
            transition: 'background 0.3s'
          }}
          onMouseOver={(e) => e.target.style.background = 'rgba(255,255,255,0.3)'}
          onMouseOut={(e) => e.target.style.background = 'rgba(255,255,255,0.2)'}
        >
          Logout
        </button>
      </div>
    </nav>
  );
}

export default AdminNavbar;
