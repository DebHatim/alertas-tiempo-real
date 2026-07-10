import React from 'react';
import { useNavigate } from 'react-router-dom';
import './Landing.css';

const Landing = () => {
    const navigate = useNavigate();

    return (
        <div className="landing-container">
            <div className="landing-card">
                <h1 className="landing-title">Alertas App</h1>

                <p className="landing-description">
                    Monitoriza tus productos favoritos en tiempo real y recibe notificaciones instantáneas.
                    <br />
                    No vuelvas a perderte una bajada de precio ni las mejores ofertas del mercado.
                </p>

                <button onClick={() => navigate('/login')} className="btn-enter">
                    Entrar
                </button>
            </div>
        </div>
    );
};

export default Landing;