package com.hatim.alertas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoDTO {

    @NotBlank(message = "El nombre del producto no puede estar vacio")
    private String nombre;

    @NotNull(message = "El precio no puede ser nulo")
    @Positive(message = "El precio debe ser un número positivo")
    private BigDecimal precio;
}
