package com.hatim.alertas.controller;

import com.hatim.alertas.model.Usuario;
import com.hatim.alertas.repository.UsuarioRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Metodo para registrar un usuario nuevo
    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody RegistroRequest request) {

        // Comprobar si el email ya existe
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email ya registrado");
        }

        // Si no existe se crea
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());

        // Cifrar contraseña con BCrypt antes de guardar a BD
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        Usuario guardado = usuarioRepository.save(usuario);
        return ResponseEntity.status(201).body(guardado);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<Usuario> usuarioOpcional = usuarioRepository.findByEmail(request.getEmail());

        if (usuarioOpcional.isEmpty()) {
            return ResponseEntity.status(401).body("Credenciales invalidas");
        }

        Usuario usuario = usuarioOpcional.get();
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(401).body("Credenciales invalidas");
        }
        return ResponseEntity.ok(usuario);
    }

    // Clase interna para leer el JSON del body del registro
    @Data
    public static class RegistroRequest {
        private String nombre;
        private String email;
        private String password;
    }

    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }
}