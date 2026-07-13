package com.hatim.alertas.controller;

import com.hatim.alertas.model.Notificacion;
import com.hatim.alertas.repository.NotificacionRepository;
import com.hatim.alertas.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "Historial y estados de notificaciones disparadas por Kafka")
public class NotificacionController {

    private final NotificacionRepository notificacionRepository;
    private final NotificacionService notificacionService;

    // Metodo que devuelve el historial de notificaciones de un usuario
    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Ver historial de notificaciones", description = "Obtiene las notificaciones ordenadas por fecha de forma descendente.")
    public ResponseEntity<List<Notificacion>> getByUsuario(@PathVariable Long usuarioId, Authentication authentication) {
        Long autenticadoId = (Long) authentication.getPrincipal();
        if (!usuarioId.equals(autenticadoId)) {
            throw new AccessDeniedException("No tienes permisos para ver las notificaciones de este usuario");
        }
        return ResponseEntity.ok(notificacionRepository.findByUsuarioIdOrderByFechaDesc(usuarioId));
    }

    // Metodo PATCH para marcar notificacion como leida
    @PatchMapping("/{id}/leer")
    @Operation(summary = "Marcar notificación como leída", description = "Actualiza el estado 'leido' de una notificación específica.")
    public ResponseEntity<Notificacion> marcarLeida(@PathVariable Long id, Authentication authentication) {
        Long autenticadoId = (Long) authentication.getPrincipal();

        Notificacion actualizada = notificacionService.marcarComoLeida(id, autenticadoId);
        return ResponseEntity.ok(actualizada);
    }
}