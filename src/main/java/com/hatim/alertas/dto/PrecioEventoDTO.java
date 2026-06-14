package com.hatim.alertas.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrecioEventoDTO {
    private Long productoId;
    private String productoNombre;
    private BigDecimal precioAnterior;
    private BigDecimal precioActual;
}