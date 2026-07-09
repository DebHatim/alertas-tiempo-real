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
                                <li key={index} className="notification-item">
                                    <span className="notification-message">{notif.mensaje}</span>
                                    <div className="notification-details">
                                        Actual: <strong>{notif.precioActual}€</strong> |
                                        Objetivo: <strong>{notif.precioObjetivo}€</strong>
                                    </div>
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