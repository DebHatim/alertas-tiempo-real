import {useEffect, useState} from 'react';
import SockJS from 'sockjs-client';
import {Client} from '@stomp/stompjs';

export const useWebSocket = (usuarioId) => {
    // Guardar todas las alertas que nos lleguen
    const [notificaciones, setNotificaciones] = useState([]);

    const [conectado, setConectado] = useState(false);

    useEffect(() => {
        if (!usuarioId) return;

        // Inicializar el cliente STOMP para comunicar con el endpoint de Spring Boot
        const stompClient = new Client({
            // Se usa SockJS
            webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
            // Reconectar cada 5s si cae backend
            reconnectDelay: 5000,
            // Imprimir trafico en consola
            debug: (str) => console.log('STOMP Debug:', str),
        });

        // onConnect = callback
        stompClient.onConnect = (frame) => {
            // Log comprobacion conexion
            setConectado(true);
            console.log('Conectado a WebSocket con éxito!');

            // Indicar canal de suscripcion
            stompClient.subscribe(`/topic/notificaciones/${usuarioId}`, (message) => {
                if (message.body) {
                    const nuevaNotificacion = JSON.parse(message.body);

                    // Añadimos la nueva notificacion en el estado
                    setNotificaciones((prev) => [nuevaNotificacion, ...prev]);
                }
            });
        };

        // Manejo de errores
        stompClient.onStompError = (frame) => {
            console.error('Error en el protocolo STOMP:', frame.headers['message']);
        };

        stompClient.onDisconnect = () => {
            setConectado(false);
        };

        // Activar la conexion
        stompClient.activate();

        // Limpieza al desmontar el componente - cerrar conexion
        return () => {
            if (stompClient) {
                stompClient.deactivate();
            }
        };
    }, [usuarioId]);

    return {notificaciones, setNotificaciones, conectado};
};