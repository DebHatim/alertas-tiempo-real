import React from 'react';

const Login = () => {
    return (
        <div>
            <h2>Acceso / Registro</h2>
            <form onSubmit={(e) => e.preventDefault()}>
                <input type="email" placeholder="Email" />
                <input type="password" placeholder="Contraseña" />
                <button type="submit">Entrar</button>
            </form>
        </div>
    );
};

export default Login;