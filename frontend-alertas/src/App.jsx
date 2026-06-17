import React, {useContext} from 'react'
import {BrowserRouter as Router, Routes, Route, Navigate} from 'react-router-dom'
import {AuthContext} from './context/AuthContext'
import Login from './pages/Login'
import DashBoard from './pages/DashBoard'
import Alertas from './pages/Alertas'

function App() {
    const {user} = useContext(AuthContext)

    return (
        <Router>
            <Routes>
                {/* Ruta Login/Registro */}
                <Route
                    path="/login"
                    element={!user ? <Login/> : <Navigate to="/dashboard"/>}
                />

                {/* Rutas privadas del usuario */}
                <Route
                    path="/dashboard"
                    element={user ? <DashBoard/> : <Navigate to="/login"/>}
                />
                <Route
                    path="/alertas"
                    element={user ? <Alertas/> : <Navigate to="/login"/>}
                />

                {/* Redireccion por defecto */}
                <Route
                    path="*"
                    element={<Navigate to={user ? "/dashboard" : "/login"}/>}
                />
            </Routes>
        </Router>
    )
}

export default App