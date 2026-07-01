import React, { useContext, useEffect, useState } from 'react';
import { AuthContext } from '../context/AuthContext';
import { useWebSocket } from '../hooks/useWebSocket';
import {notificacionService, productoService} from '../services/api';

const DashBoard = () => {
    const { user } = useContext(AuthContext);
    const [productos, setProductos] = useState([]);

    // Conectamos al WebSocket usando el ID del usuario logueado
    const { notificaciones, setNotificaciones, conectado } = useWebSocket(user?.id);

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
        <div>
            <h1>Dashboard - Bienvenido, {user?.nombre}</h1>
            <p>Estado del Servidor en Tiempo Real: {conectado ? "🟢 Conectado" : "🔴 Desconectado"}</p>

            <div style={{ display: 'flex', gap: '2rem', marginTop: '1rem' }}>

                {/* Productos */}
                <div style={{ flex: 1 }}>
                    <h3>Productos Activos</h3>
                    <ul>
                        {productos.map(prod => (
                            <li key={prod.id}>
                                <strong>{prod.nombre}</strong> — {prod.precioActual}€
                            </li>
                        ))}
                    </ul>
                </div>

                {/* Notificaciones */}
                <div style={{ flex: 1, background: '#f0f0f0', padding: '1rem' }}>
                    <h3>Alertas Disparadas (Kafka ⚡ WebSocket)</h3>
                    {notificaciones.length === 0 ? (
                        <p>Esperando a que baje algún precio...</p>
                    ) : (
                        <ul>
                            {notificaciones.map((notif, index) => (
                                <li key={index} style={{ marginBottom: '10px', color: 'darkred' }}>
                                    <strong>{notif.mensaje}</strong> <br />
                                    <small>Actual: {notif.precioActual}€ | Tu Objetivo: {notif.precioObjetivo}€</small>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>

            </div>
        </div>
    );
};

export default DashBoard;