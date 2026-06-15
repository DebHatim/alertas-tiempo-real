package com.hatim.alertas.repository;

import com.hatim.alertas.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioIdOrderByFechaDesc(Long usuarioId); // Metodo para buscar una notificacion asociada al id de un usuario
    // Se escribe OrderByFechaDesc para que Spring lo lea y añada el ORDER BY por si solo
}