package com.hatim.alertas.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario; // Usuario al que se le notifica

    @Column(nullable = false)
    private String mensaje; // Mensaje mostrando el cambio del precio del producto

    @Column(nullable = false)
    private Boolean leida = false; // Usuario puede marcar notificación como leida o como no leida

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now(); // Fecha en la que se notifica el cambio
}