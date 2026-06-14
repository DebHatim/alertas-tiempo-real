package com.hatim.alertas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String mensaje;

    @Column(nullable = false)
    private Boolean leida = false;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();
}