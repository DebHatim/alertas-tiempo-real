import React, { useContext } from 'react';
import {Link, useLocation} from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import './Navbar.css';

const Navbar = () => {
    const { user, logout } = useContext(AuthContext);
    const location = useLocation()

    // Si no hay usuario con sesion, no se muestra
    if (!user) return null;

    return (
        <nav className="navbar">
            <div className="navbar-left">
                <span className="navbar-brand">Alertas App</span>

                {/* Enlaces de navegacion con clase activa dinamica */}
                <div className="navbar-links">
                    <Link to="/" className={`nav-link ${location.pathname === '/' || location.pathname === '/dashboard' ? 'active' : ''}`}>
                        Dashboard
                    </Link>
                    <Link to="/alertas" className={`nav-link ${location.pathname === '/alertas' ? 'active' : ''}`}>
                        Mis Alertas
                    </Link>
                </div>
            </div>

            <div className="navbar-right">
                <span className="navbar-user">
                    Hola, <strong>{user.nombre || user.email}</strong>
                </span>
                <button onClick={() => logout()} className="btn-logout">
                    Cerrar Sesion
                </button>
            </div>
        </nav>
    );
};

export default Navbar;