import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { MdEco } from 'react-icons/md';
import { FaUser, FaStar } from 'react-icons/fa';

export default function Navbar({ user, logout }) {
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <nav style={{
      padding: '1rem 2rem',
      background: '#2e7d32',
      color: 'white',
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      position: 'relative',
      flexWrap: 'wrap'
    }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
        <h2 style={{ margin: 0, fontSize: 'clamp(1.2rem, 5vw, 1.5rem)', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <MdEco size={28} /> EcoRoutePlanner
        </h2>
      </div>

      {/* Mobile menu toggle */}
      <button
        onClick={() => setMenuOpen(!menuOpen)}
        style={{
          display: 'none',
          background: 'transparent',
          border: 'none',
          color: 'white',
          fontSize: '1.5rem',
          cursor: 'pointer',
          padding: '0.5rem'
        }}
        className="mobile-menu-toggle"
      >
        ☰
      </button>

      <div style={{
        display: 'flex',
        alignItems: 'center',
        gap: '1.5rem',
        flexWrap: 'wrap'
      }}
      className="nav-items">
        {user && (
          <>
            <span style={{ 
              fontSize: 'clamp(0.9rem, 3vw, 1rem)',
              whiteSpace: 'nowrap',
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem'
            }}>
              <FaUser size={14} /> {user.username}
            </span>
            <span style={{ 
              background: '#fff3', 
              padding: '0.25rem 0.75rem', 
              borderRadius: '12px',
              fontSize: 'clamp(0.85rem, 2.5vw, 0.95rem)',
              whiteSpace: 'nowrap',
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem'
            }}>
              <FaStar size={14} /> {user.ecoPoints || 0} pts
            </span>
          </>
        )}
      </div>

      <style>{`
        @media (max-width: 768px) {
          .mobile-menu-toggle {
            display: block !important;
          }

          .nav-items {
            ${menuOpen ? 'display: flex' : 'display: none'} !important;
            width: 100%;
            flex-direction: column;
            align-items: flex-start;
            margin-top: 1rem;
            padding-top: 1rem;
            border-top: 1px solid #fff3;
          }
        }

        @media (max-width: 480px) {
          nav {
            padding: 0.75rem 1rem !important;
          }
        }
      `}</style>
    </nav>
  );
}
