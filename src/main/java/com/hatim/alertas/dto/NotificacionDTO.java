package com.hatim.alertas.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionDTO {
    private Long id;
    private Long usuarioId;
    private String mensaje;
    private String productoNombre;
    private BigDecimal precioObjetivo;
    private BigDecimal precioActual;
    private LocalDateTime fecha;
    private Boolean leida;
}