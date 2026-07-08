package com.hatim.alertas.controller;

import com.hatim.alertas.dto.AlertaDTO;
import com.hatim.alertas.service.AlertaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AlertaController {

    private final AlertaService alertaService;

    // Metodo para crear una alerta nueva
    @PostMapping
    public ResponseEntity<AlertaDTO> crear(@RequestBody AlertaDTO dto) {
        AlertaDTO creada = alertaService.crearAlerta(dto);
        // 201 Created
        return ResponseEntity.status(201).body(creada);
    }

    // Metodo para obtener las alertas de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public List<AlertaDTO> getByUsuario(@PathVariable Long usuarioId) {
        return alertaService.obtenerAlertasUsuario(usuarioId);
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