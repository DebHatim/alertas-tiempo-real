package com.hatim.alertas.controller;

import com.hatim.alertas.dto.AlertaDTO;
import com.hatim.alertas.service.AlertaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
@Tag(name = "Alertas", description = "Gestión de alertas de precios parametrizadas por el usuario")
public class AlertaController {

    private final AlertaService alertaService;

    // Metodo para crear una alerta nueva
    @PostMapping
    @Operation(summary = "Crear una nueva alerta", description = "Registra una alerta vinculada automáticamente al usuario autenticado.")
    public ResponseEntity<?> crear(@Valid @RequestBody AlertaDTO dto, Authentication authentication) {
        Long autenticadoId = (Long) authentication.getPrincipal(); // Extraer el ID inyectado en el filtro
        dto.setUsuarioId(autenticadoId);
        AlertaDTO creada = alertaService.crearAlerta(dto);
        // 201 Created
        return ResponseEntity.status(201).body(creada);
    }

    // Metodo para obtener las alertas de un usuario
    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener alertas de un usuario", description = "Recupera todas las alertas configuradas por el ID del usuario (requiere propiedad).")
    public ResponseEntity<?> getByUsuario(@PathVariable Long usuarioId, Authentication authentication) {
        Long autenticadoId = (Long) authentication.getPrincipal();
        if (!usuarioId.equals(autenticadoId)) {
            return ResponseEntity.status(403).body("No tienes permisos para ver las alertas de este usuario");
        }
        return ResponseEntity.ok(alertaService.obtenerAlertasUsuario(usuarioId));
    }

    // Metodo DELETE para eliminar una alerta
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una alerta", description = "Borra físicamente una alerta del sistema si pertenece al usuario.")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication authentication) {
        Long autenticadoId = (Long) authentication.getPrincipal();
        alertaService.eliminarAlerta(id, autenticadoId);
        return ResponseEntity.noContent().build();
    }

    // Metodo PUT para activar o desactivar una alerta
    @PutMapping("/{id}/toggle")
    @Operation(summary = "Alternar estado de una alerta", description = "Activa o desactiva la alerta seleccionada.")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, Authentication authentication) {
        Long autenticadoId = (Long) authentication.getPrincipal();
        alertaService.desactivarAlerta(id, autenticadoId);
        // 204 No Content
        return ResponseEntity.noContent().build();
    }
}