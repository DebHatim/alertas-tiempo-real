package com.hatim.alertas.repository;

import com.hatim.alertas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email); // Metodo para encontrar un usario usando un email
    // Se usa Optional para evitar NullPointerException en el caso de que el usuario no exista
}