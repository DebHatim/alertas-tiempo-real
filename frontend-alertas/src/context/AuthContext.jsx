import React, { createContext, useState, useEffect } from 'react';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    // Al cargar la app, comprobamos si hay un usuario guardado en el navegador
    useEffect(() => {
        const storedUser = localStorage.getItem('usuario_alertas');
        if (storedUser) {
            setUser(JSON.parse(storedUser));
        }
        setLoading(false);
    }, []);

    // Funcion para simular el inicio de sesion
    const login = (userData) => {
        localStorage.setItem('usuario_alertas', JSON.stringify(userData));
        setUser(userData);
    };

    // Funcion para cerrar sesion
    const logout = () => {
        localStorage.removeItem('usuario_alertas');
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, login, logout, loading }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};