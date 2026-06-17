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

    // Metodo DELETE para desactivar una alerta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        alertaService.desactivarAlerta(id);
        // 204 No Content
        return ResponseEntity.noContent().build();
    }
}