import React, {useContext} from 'react'
import {BrowserRouter as Router, Routes, Route, Navigate} from 'react-router-dom'
import {AuthContext} from './context/AuthContext'
import Auth from './pages/Auth.jsx'
import DashBoard from './pages/DashBoard'
import Alertas from './pages/Alertas'
import Navbar from "./components/Navbar.jsx";
import Landing from "./pages/Landing.jsx";

function App() {
    const {user} = useContext(AuthContext)

    return (
        <Router>
            <Navbar />
            <Routes>
                {/* Ruta Landing publica */}
                <Route
                    path="/"
                    element={!user ? <Landing/> : <Navigate to="/dashboard"/>}
                />

                {/* Ruta Login/Registro */}
                <Route
                    path="/login"
                    element={!user ? <Auth defaultLogin={true}/> : <Navigate to="/dashboard"/>}
                />
                <Route
                    path="/registro"
                    element={!user ? <Auth defaultLogin={false}/> : <Navigate to="/dashboard"/>}
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
                    element={<Navigate to={user ? "/dashboard" : "/"}/>}
                />
            </Routes>
        </Router>
    )
}

export default App