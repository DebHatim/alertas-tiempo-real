package com.hatim.alertas.controller;

import com.hatim.alertas.dto.AlertaDTO;
import com.hatim.alertas.service.AlertaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AlertaController {

    private final AlertaService alertaService;

    // Metodo para crear una alerta nueva
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody AlertaDTO dto, Authentication authentication) {
        Long autenticadoId = (Long) authentication.getPrincipal(); // Extraer el ID inyectado en el filtro
        dto.setUsuarioId(autenticadoId);
        AlertaDTO creada = alertaService.crearAlerta(dto);
        // 201 Created
        return ResponseEntity.status(201).body(creada);
    }

    // Metodo para obtener las alertas de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> getByUsuario(@PathVariable Long usuarioId, Authentication authentication) {
        Long autenticadoId = (Long) authentication.getPrincipal();
        if (!usuarioId.equals(autenticadoId)) {
            return ResponseEntity.status(403).body("No tienes permisos para ver las alertas de este usuario");
        }
        return ResponseEntity.ok(alertaService.obtenerAlertasUsuario(usuarioId));
    }

    // Metodo DELETE para eliminar una alerta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        alertaService.eliminarAlerta(id);
        return ResponseEntity.noContent().build();
    }

    // Metodo PUT para activar o desactivar una alerta
    @PutMapping("/{id}/toggle")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id) {
        alertaService.desactivarAlerta(id);
        // 204 No Content
        return ResponseEntity.noContent().build();
    }
}