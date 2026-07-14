import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL;

// Inicializacion de la instancia base para siempre usar API_URL
const api = axios.create({
    baseURL: API_URL,
    headers: {'Content-Type': 'application/json',}, // Datos viajan en formato JSON
});

// Interceptor para inyectar el token en las peticiones que salen
api.interceptors.request.use(
    (config) => {
        const storedUser = localStorage.getItem('usuario_alertas');

        if (storedUser) {
            try {
                const userData = JSON.parse(storedUser);

                if (userData && userData.token) {
                    config.headers.Authorization = `Bearer ${userData.token}`;
                }
            } catch (error) {
                console.error("Error parseando el usuario", error)
            }
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
)

// Interceptor para capturar respuestas de error
api.interceptors.response.use(
    (response) => {
        // Si las respuesta es correcta (codigo 2xx)
        return response;
    },
    (error) => {
        // Comprobar si el servidor ha respondido con un error
        if (error.response) {
            const status = error.response.status;

            // Si el estado es 401 o 403
            if (status === 401 || status === 403) {
                console.warn("La sesión ha expirado o el token no es válido.");

                // Eliminar la clave del usuario del almacenamiento local
                localStorage.removeItem('usuario_alertas');

                // Redirigir al login para salir
                window.location.href = "/login";
            }
        }
        // Devolver el error
        return Promise.reject(error);
    }
);

// Servicios para autenticar usuarios
export const authService = {
    registro: (userData) => api.post('/auth/registro', userData),
    login: (userData) => api.post('/auth/login', userData),
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
    desactivar: (id) => api.put(`/alertas/${id}/toggle`),
    eliminar: (id) => api.delete(`/alertas/${id}`),
};

// Servicio para manejar las notificaciones dependiendo de los parametros
export const notificacionService = {
    getByUsuario: (usuarioId) => api.get(`/notificaciones/usuario/${usuarioId}`),
    getHistorial: (usuarioId) => api.get(`/notificaciones/usuario/${usuarioId}`),
    marcarLeida: (id) => api.patch(`/notificaciones/${id}/leer`)
};

export default api;