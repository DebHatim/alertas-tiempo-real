import React, {useContext, useEffect, useState} from 'react';
import {AuthContext} from '../context/AuthContext';
import {useWebSocket} from '../hooks/useWebSocket';
import {notificacionService, productoService} from '../services/api';
import './Dashboard.css';

const DashBoard = () => {
    const {user} = useContext(AuthContext);
    const [productos, setProductos] = useState([]);

    // Conectamos al WebSocket usando el ID del usuario logueado
    const {notificaciones, setNotificaciones, conectado} = useWebSocket(user?.id);

    // Cargamos los productos de la BD
    useEffect(() => {
        productoService.getAll()
            .then(response => setProductos(response.data))
            .catch(error => console.error("Error cargando productos:", error));
    }, []);

    // Segundo useEffect para cargar el historial de notificaciones
    useEffect(() => {
        if (user?.id) {
            notificacionService.getHistorial(user.id)
                .then(res => {
                    setNotificaciones(res.data)
                })
                .catch(error => console.error("Error cargando historial de notificaciones", error))
        }
    }, [user?.id, setNotificaciones]);

    // Metodo para gestionar marcar notificaciones como leidas
    const handleMarcarLeida = async (id) => {
        try {
            await notificacionService.marcarLeida(id);
            setNotificaciones(prev =>
                prev.map(n => n.id === id ? { ...n, leida: true } : n)
            );
        } catch (error) {
            console.error('Error al marcar como leída', error);
        }
    };

    const handleEliminarNotificacion = async (id, index) => {
        if (!id) {
            setNotificaciones(prev => prev.filter((_, i) => i !== index));
            return;
        }

        try {
            await notificacionService.eliminar(id);
            // Filtrar por id
            setNotificaciones(prev => prev.filter(n => n.id !== id));
        } catch (error) {
            console.error('Error al eliminar la notificacion', error);
            setNotificaciones(prev => prev.filter(n => n.id !== id));
        }
    }

    const formatearFecha = (fechaInput) => {
        if (!fechaInput) return "";
        const fecha = new Date(fechaInput);

        return fecha.toLocaleString('es-ES', {
            day: 'numeric',
            month: 'short',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    return (
        <div className="dashboard-container">
            <div className="dashboard-header">
                <h1>Dashboard - Bienvenido, {user?.nombre}</h1>
                <p>Notificaciones en vivo: {conectado ? "🟢 Activas" : "🔴 Inactivas"}</p>
            </div>

            {/* Seccion de productos */}
            <div className="dashboard-grid">
                <div className="dashboard-card">
                    <h3>Productos Activos</h3>
                    <ul className="product-list">
                        {productos.length > 0 ? (
                            productos.map((prod) => (
                                <li key={prod.id} className="product-item">
                                    <span>{prod.nombre}</span>
                                    <span className="product-price">
                                        {typeof prod.precioActual === 'number' ?
                                            prod.precioActual.toFixed(2) : Number(prod.precioActual).toFixed(2)}€
                                    </span>
                                </li>
                            ))
                        ) : (
                            <p className="no-products">Sin productos activos para mostrar en este momento.</p>
                        )}
                    </ul>
                </div>

                {/* Seccion de notificaciones */}
                <div className="dashboard-card notification-panel">
                    <h3>Notificaciones</h3>
                    {notificaciones.length === 0 ? (
                        <p className="no-alerts">Esperando a que baje algún precio...</p>
                    ) : (
                        <ul className="notification-list">
                            {notificaciones.map((notif, index) => (
                                <li key={notif.id ?? index} className={`notification-item ${notif.leida ? 'leida' : ''}`}>
                                    <div className="notification-meta">
                                        <span className="notification-date">{formatearFecha(notif.fecha)}</span>
                                        <button className="btn-eliminar-notif"
                                            onClick={() => handleEliminarNotificacion(notif.id, index)}
                                            title="Eliminar notificación">ELIMINAR</button>
                                    </div>

                                    <span className="notification-message">{notif.mensaje}</span>

                                    <div className="notification-details">
                                        Actual: <strong>{notif.precioActual}€</strong> |
                                        Objetivo: <strong>{notif.precioObjetivo}€</strong>
                                    </div>

                                    {!notif.leida && (
                                        <button className="btn-marcar-leida" onClick={() => handleMarcarLeida(notif.id)}>
                                            Marcar como leída
                                        </button>
                                    )}
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
            </div>
        </div>
    )
        ;
};

export default DashBoard;