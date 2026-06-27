import React, {useContext, useState} from 'react';
import {AuthContext} from "../context/AuthContext.jsx";
import {authService} from "../services/api.js";

const Auth = () => {
    // Usar el contexto para autenticar
    const {login} = useContext(AuthContext)

    // Definir estados locales para capturar datos del formulario
    const [nombre, setNombre] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    // Metodo para controlar el formulario
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        try {
            // Llamar al endpoint de Spring Boot /api/auth/registro
            const response = await authService.registro({nombre, email, password});

            // Guardar datos de Usuario si el backend responde 201 Created
            const usuarioGuardado = response.data;

            // Pasar el usuario al contexto global
            login(usuarioGuardado);

        } catch (err) {
            console.error(err);
            // Control de errores
            setError(err.response?.data || 'Hubo un error en el registro');
        }
    };

    return (
        <div>
            <h2>Aplicación de Alertas - Acceso al Sistema</h2>

            {/* Mostrar erroes */}
            {error && <p style={{ color: 'red' }}>{error}</p>}

            <form onSubmit={handleSubmit}>
                <input type="text" value={nombre} placeholder="Nombre" onChange={(e) => setNombre(e.target.value)} required/>
                <input type="email" value={email} placeholder="Email" onChange={(e) => setEmail(e.target.value)} required/>
                <input type="password" value={password} placeholder="Contraseña" onChange={(e) => setPassword(e.target.value)} required/>
                <button type="submit">Acceder</button>
            </form>
        </div>
    );
};

export default Auth;