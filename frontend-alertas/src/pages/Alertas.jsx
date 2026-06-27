import React, {useContext, useEffect, useState} from 'react';
import {AuthContext} from "../context/AuthContext.jsx";
import {alertaService, productoService} from "../services/api.js";

const Alertas = () => {
    // Usar el contexto para autenticar
    const {user} = useContext(AuthContext)

    // Estados para los datos API
    const [productos, setProductos] = useState([])
    const [alertas, setAlertas] = useState([])

    // Estados para el formulario de nueva alerta
    const [productoSeleccionado, setProductoSeleccionado] = useState('')
    const [precioObjetivo, setPrecioObjetivo] = useState('')

    // Cargar productos y alertas de usuario al entrar
    useEffect(() => {
        // Cargar los productos y las alertas a sus estados
        productoService.getAll().then(resultado => setProductos(resultado.data))
            .catch(error => console.error("Error cargando productos",error))
        alertaService.getByUsuario(user.id).then(resultado => setAlertas(resultado.data))
            .catch(error => console.error("Error cargando alertas",error))
    }, [user]);

    // Controlar datos al crear alerta
    const handleCreateAlerta = async(e) => {
        e.preventDefault()
        if (!productoSeleccionado || !precioObjetivo) return;

        // Crear DTO si ha seleccionado un producto y establecido un precio objetivo
        const nuevaAlertaDTO = {
            usuarioId: user.id,
            productoId: parseInt(productoSeleccionado),
            precioObjetivo: parseFloat(precioObjetivo)
        }

        try {
            // Anadir la nueva alerta
            const res = await alertaService.crear(nuevaAlertaDTO)
            setAlertas([...alertas, res.data])

            // Limpiar inputs de formulario
            setProductoSeleccionado('')
            setPrecioObjetivo('')
        } catch (error) {
            console.error("Error al crear la alerta", error)
        }
    }

    return (
        <div>
            <h1>Mis Alertas</h1>

            <form onSubmit={handleCreateAlerta}>
                <h3>Configurar nueva alerta</h3>

                <div>
                    <label>Selecciona un producto:</label>
                    <select value={productoSeleccionado} onChange={(e) => setProductoSeleccionado(e.target.value)} required>
                        <option value=""></option>
                        {productos.map(prod => (
                            <option key={prod.id} value={prod.id}>{prod.nombre} (Actual: {prod.precioActual}€)</option>
                        ))}
                    </select>
                </div>

                <div>
                    <label>Precio Objetivo (€):</label>
                    <input type="number" step="0.01" value={precioObjetivo}
                        onChange={(e) => setPrecioObjetivo(e.target.value)}
                        placeholder="500" required />
                </div>

                <button type={"submit"}>Activar Alerta</button>
            </form>

            <h3>Mis Reglas de Monitoreo Activas</h3>
            {alertas.length === 0 ? (
                <p>No tienes alertas configuradas todavía.</p>
            ) : (
                <table border="1">
                    <thead>
                    <tr>
                        <th>Producto</th>
                        <th>Precio Límite Objetivo</th>
                        <th>Estado</th>
                    </tr>
                    </thead>
                    <tbody>
                    {alertas.map(alerta => (
                        <tr key={alerta.id}>
                            {/* Mapeamos el nombre buscando en la lista local de productos */}
                            <td>{productos.find(p => p.id === alerta.productoId)?.nombre || `Producto #${alerta.productoId}`}</td>
                            <td>{alerta.precioObjetivo}€</td>
                            <td>{alerta.activa ? '🟢 Activa (Escuchando Kafka)' : '🔴 Inactiva'}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default Alertas;