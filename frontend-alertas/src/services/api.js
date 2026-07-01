import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

// Inicializacion de la instancia base para siempre usar API_URL
const api = axios.create({
    baseURL: API_URL,
    headers: {'Content-Type': 'application/json',}, // Datos viajan en formato JSON
});

// Servicio para registrar usuario
export const authService = {
    registro: (userData) => api.post('/auth/registro', userData),
};

// Servicio para obtener productos dependiendo de los parametros
export const productoService = {
    getAll: () => api.get('/productos'),
    getById: (id) => api.get(`/productos/${id}`),
};

// Servicio para el manejo de las alertas dependiendo de los parametros
export const alertaService = {
    crear: (alertaData) => api.post('/alertas', alertaData),
    getByUsuario: (usuarioId) => api.get(`/alertas/usuario/${usuarioId}`),
    desactivar: (id) => api.delete(`/alertas/${id}`),
};

// Servicio para manejar las notificaciones dependiendo de los parametros
export const notificacionService = {
    getByUsuario: (usuarioId) => api.get(`/notificaciones/usuario/${usuarioId}`),
    getHistorial: (usuarioId) => api.get(`/notificaciones/usuario/${usuarioId}`),
    marcarLeida: (id) => api.patch(`/notificaciones/${id}/leer`)
};

export default api;