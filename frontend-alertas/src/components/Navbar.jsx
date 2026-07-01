import React, { useContext } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const Navbar = () => {
    const { user, logout } = useContext(AuthContext);

    // Si no hay usuario con sesion, no se muestra
    if (!user) return null;

    return (
        <nav style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            background: '#2c3e50',
            color: 'white',
            padding: '10px 20px',
            marginBottom: '20px'
        }}>
            <div style={{ display: 'flex', gap: '20px', alignItems: 'center' }}>
            <span style={{ fontSize: '1.2rem', fontWeight: 'bold', marginRight: '10px' }}>
                Alertas App
            </span>
                {/* Enlaces de navegacion */}
                <Link to="/" style={{ color: 'white', textDecoration: 'none', fontWeight: 'bold' }}>Dashboard</Link>
                <Link to="/alertas" style={{ color: 'white', textDecoration: 'none', fontWeight: 'bold' }}>Mis Alertas</Link>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                <span>Hola, <strong>{user.nombre || user.email}</strong></span>
                <button
                    onClick={logout}
                    style={{
                        background: '#e74c3c',
                        color: 'white',
                        border: 'none',
                        padding: '6px 12px',
                        borderRadius: '4px',
                        cursor: 'pointer',
                        fontWeight: 'bold'
                    }}>Cerrar Sesión</button>
            </div>
        </nav>
    );
};

export default Navbar;