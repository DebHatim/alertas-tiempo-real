import React, {useContext, useState} from 'react';
import {AuthContext} from "../context/AuthContext.jsx";
import {authService} from "../services/api.js";
import './Auth.css';

const Auth = () => {
    // Usar el contexto para autenticar
    const {login} = useContext(AuthContext)

    // Estado para alternar entre Login y Registro
    const [isLogin, setIsLogin] = useState(true);

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
            let response;

            // Llamar al endpoint de Spring Boot /api/auth/registro o /api/auth/login
            if (isLogin) {
                response = await authService.login({email, password});
            } else {
                response = await authService.registro({nombre, email, password});
            }

            // Guardar datos de Usuario si el backend responde 201 Created
            const usuarioGuardado = response.data;

            // Pasar el usuario al contexto global
            login(usuarioGuardado);
        } catch (err) {
            console.error(err);
            // Control de errores
            setError(err.response?.data || 'Hubo un error en la autenticacion');
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card">

                <div className="auth-header">
                    <h2>Aplicación de Alertas</h2>
                    <p>{isLogin ? 'Acceso al Sistema' : 'Crea tu cuenta de usuario'}</p>
                </div>

                {/* Mostrar erroes */}
                {error && <p style={{color: 'red'}}>{error}</p>}

                <form onSubmit={handleSubmit} className="auth-form">
                    {!isLogin && (
                        <div className="form-group">
                            <input type="text" value={nombre} placeholder="Nombre completo"
                                   onChange={(e) => setNombre(e.target.value)}
                                   required className="form-control"/>
                        </div>
                    )}
                    <div className="form-group">
                        <input
                            type="email"
                            value={email}
                            placeholder="Correo electronico"
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            className="form-control"
                        />
                    </div>

                    <div className="form-group">
                        <input type="password" value={password} placeholder="Contraseña"
                               onChange={(e) => setPassword(e.target.value)}
                               required className="form-control"/>
                    </div>

                    <button type="submit" className="btn-auth">
                        {isLogin ? 'Acceder' : 'Registrarse'}
                    </button>
                </form>
                <div className="auth-footer">
                    <button type="button" className="btn-switch"
                            onClick={() => {
                                setIsLogin(!isLogin);
                                setError('');
                            }}>
                        {isLogin ? '¿No tienes cuenta? Registrate aqui' : '¿Ya tienes cuenta? Inicia sesion'}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default Auth;