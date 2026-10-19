import React from 'react';

class ErrorBoundary extends React.Component {
    constructor(props) {
        super(props);
        this.state = {tieneError: false};
    }

    static getDerivedStateFromError() {
        return {tieneError: true};
    }

    componentDidCatch(error, info) {
        console.error('Error capturado por ErrorBoundary:', error, info);
    }

    handleRecargar = () => {
        window.location.href = '/';
    };

    render() {
        if (this.state.tieneError) {
            return (
                <div style={{
                    minHeight: '100vh',
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    justifyContent: 'center',
                    gap: '1rem',
                    fontFamily: 'Segoe UI, Tahoma, Geneva, Verdana, sans-serif',
                    padding: '2rem',
                    textAlign: 'center'
                }}>
                    <h2>Algo ha ido mal</h2>
                    <p>Ha ocurrido un error inesperado. Intenta recargar la página.</p>
                    <button onClick={this.handleRecargar} style={{
                        backgroundColor: '#0A0F1E',
                        color: '#E4F0F6',
                        border: 'none',
                        padding: '0.75rem 1.5rem',
                        borderRadius: '8px',
                        cursor: 'pointer',
                        fontWeight: 600
                    }}>
                        Volver al inicio
                    </button>
                </div>
            );
        }

        return this.props.children;
    }
}

export default ErrorBoundary;