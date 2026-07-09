package com.hatim.alertas.controller;

import com.hatim.alertas.model.Notificacion;
import com.hatim.alertas.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionRepository notificacionRepository;

    // Metodo que devuelve el historial de notificaciones de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public List<Notificacion> getByUsuario(@PathVariable Long usuarioId) {
        return notificacionRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    // Metodo PATCH para marcar notificacion como leida
    @PatchMapping("/{id}/leer")
    public Notificacion marcarLeida(@PathVariable Long id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        notificacion.setLeida(true);
        return notificacionRepository.save(notificacion);
    }
}