package com.hatim.alertas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "alertas")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private BigDecimal precioObjetivo;

    @Column(nullable = false)
    private Boolean activa = true;
}