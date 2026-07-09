package com.hatim.alertas.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AlertaDTO {
    private Long id;
    private Long usuarioId;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;
    private String productoNombre;

    @NotNull(message = "El precio objetivo es obligatorio")
    @Positive(message = "El precio objetivo debe ser mayor que cero")
    private BigDecimal precioObjetivo;
    private Boolean activa;
}