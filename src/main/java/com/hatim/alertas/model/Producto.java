package com.hatim.alertas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private BigDecimal precioActual;
}