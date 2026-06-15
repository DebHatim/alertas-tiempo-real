package com.hatim.alertas.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre; // Nombre del usuario

    @Column(nullable = false, unique = true)
    private String email; // Correo electronico del usuario

    @Column(nullable = false)
    private String password; // Contrasena del usuario
}