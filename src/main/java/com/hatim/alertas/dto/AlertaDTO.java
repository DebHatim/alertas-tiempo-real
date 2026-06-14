package com.hatim.alertas.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AlertaDTO {
    private Long id;
    private Long usuarioId;
    private Long productoId;
    private String productoNombre;
    private BigDecimal precioObjetivo;
    private Boolean activa;
}