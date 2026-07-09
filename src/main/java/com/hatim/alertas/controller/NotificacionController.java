package com.hatim.alertas.controller;

import com.hatim.alertas.model.Notificacion;
import com.hatim.alertas.repository.NotificacionRepository;
import com.hatim.alertas.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionRepository notificacionRepository;
    private final NotificacionService notificacionService;

    // Metodo que devuelve el historial de notificaciones de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Notificacion>> getByUsuario(@PathVariable Long usuarioId, Authentication authentication) {
        Long autenticadoId = (Long) authentication.getPrincipal();
        if (!usuarioId.equals(autenticadoId)) {
            throw new AccessDeniedException("No tienes permisos para ver las notificaciones de este usuario");
        }
        return ResponseEntity.ok(notificacionRepository.findByUsuarioIdOrderByFechaDesc(usuarioId));
    }

    // Metodo PATCH para marcar notificacion como leida
    @PatchMapping("/{id}/leer")
    public ResponseEntity<Notificacion> marcarLeida(@PathVariable Long id, Authentication authentication) {
        Long autenticadoId = (Long) authentication.getPrincipal();

        Notificacion actualizada = notificacionService.marcarComoLeida(id, autenticadoId);
        return ResponseEntity.ok(actualizada);
    }
}