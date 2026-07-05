import React, {useContext, useEffect, useState} from 'react';
import {AuthContext} from "../context/AuthContext.jsx";
import {alertaService, productoService} from "../services/api.js";
import './Alertas.css';

const Alertas = () => {
    // Usar el contexto para autenticar
    const {user} = useContext(AuthContext)

    // Estados para los datos API
    const [productos, setProductos] = useState([])
    const [alertas, setAlertas] = useState([])

    // Estados para el formulario de nueva alerta
    const [productoSeleccionado, setProductoSeleccionado] = useState('')
    const [precioObjetivo, setPrecioObjetivo] = useState('')

    // Estados de correccion
    const [error, setError] = useState('')
    const [success, setSuccess] = useState('')

    // Metodo para recoger los productos y alertas al arrancar
    useEffect(() => {
        if (user?.id) {
            cargarProductos();
            cargarAlertasUsuario();
        }
    }, [user]);

    // Metodo para consultar los productos disponibles
    const cargarProductos = async () => {
        try {
            const res = await productoService.getAll();
            setProductos(res.data);
            if (res.data.length > 0) setProductoSeleccionado(res.data[0].id);
        } catch (error) {
            console.error('Error cargando productos', error);
        }
    };

    // Metodo para consultar las alertas del usuario
    const cargarAlertasUsuario = async () => {
        try {
            const res = await alertaService.getByUsuario(user.id);
            setAlertas(res.data);
        } catch (err) {
            console.error('Error cargando alertas', err);
        }
    };

    // Metodo para procesar la creacion de alerta
    const handleCrearAlerta = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        if (!productoSeleccionado || !precioObjetivo) {
            setError('Por favor, rellena todos los campos');
            return;
        }

        try {
            await alertaService.crear({
                usuario: { id: user.id },
                producto: { id: parseInt(productoSeleccionado) },
                precioObjetivo: parseFloat(precioObjetivo),
                activa: true
            });
            setPrecioObjetivo('');
            setSuccess('Alerta configurada correctamente');
            cargarAlertasUsuario();
        } catch (err) {
            setError('No se pudo crear la alerta');
        }
    };

    // Metodo para desactivar la alerta que seleccione el usuario
    const handleDesactivar = async (id) => {
        try {
            await alertaService.desactivar(id);
            cargarAlertasUsuario();
        } catch (err) {
            console.error('Error al desactivar', err);
        }
    };

    return (
        <div className="alerts-container">
            {/* Configurador */}
            <div className="alerts-card">
                <h3>Configurar nueva alerta</h3>
                {error && <div className="alert-msg error">{error}</div>}
                {success && <div className="alert-msg success">{success}</div>}

                <form onSubmit={handleCrearAlerta} className="alerts-form">
                    <div className="form-group">
                        <label>Selecciona un producto:</label>
                        <select value={productoSeleccionado}
                            onChange={(e) => setProductoSeleccionado(e.target.value)}
                            className="form-control">
                            {productos.map(p => (
                                <option key={p.id} value={p.id}>{p.nombre} ({p.precioActual}€)</option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group">
                        <label>Precio Objetivo (€):</label>
                        <input type="number" step="0.01" value={precioObjetivo} placeholder="Ej. 500"
                            onChange={(e) => setPrecioObjetivo(e.target.value)}
                            className="form-control" required />
                    </div>

                    <button type="submit" className="btn-submit-alert">
                        Activar Alerta
                    </button>
                </form>
            </div>

            {/* Monitoreo */}
            <div className="alerts-card">
                <h3>Mis Alertas Activas</h3>
                {alertas.length > 0 ? (
                    <div className="alerts-grid">
                        {alertas.map(alerta => (
                            <div key={alerta.id} className="alert-item-card">
                                <div className="alert-item-info">
                                    <h4>{alerta.producto?.nombre}</h4>
                                    <p>Precio Objetivo: <strong>{alerta.precioObjetivo}€</strong></p>
                                    <span className={`status-badge ${alerta.activa ? 'active' : 'inactive'}`}>
                                        {alerta.activa ? 'Monitoreando' : 'Inactiva'}
                                    </span>
                                </div>
                                <button onClick={() => handleDesactivar(alerta.id)} className="btn-delete-alert">
                                    Eliminar
                                </button>
                            </div>
                        ))}
                    </div>
                ) : (
                    <p className="no-alerts">No tienes alertas configuradas todavia.</p>
                )}
            </div>
        </div>
    );
};

export default Alertas;