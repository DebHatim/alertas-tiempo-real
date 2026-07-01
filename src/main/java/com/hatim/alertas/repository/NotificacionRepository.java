package com.hatim.alertas.repository;

import com.hatim.alertas.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioIdOrderByFechaDesc(Long usuarioId); // Metodo para buscar una notificacion asociada al id de un usuario
    // Se escribe OrderByFechaDesc para que Spring lo lea y añada el ORDER BY por si solo

    // Para controlar las notificaciones en masa
    @Query("SELECT COUNT(n) > 0 FROM Notificacion n WHERE n.usuario.id = :usuarioId AND n.mensaje LIKE CONCAT('%', :productoNombre, '%') AND n.fecha > :fecha")
    boolean comprobarDuplicadoReciente(
            @Param("usuarioId") Long usuarioId,
            @Param("productoNombre") String productoNombre,
            @Param("fecha") LocalDateTime fecha
    );
}