package com.hatim.alertas.controller;

import com.hatim.alertas.model.Usuario;
import com.hatim.alertas.repository.UsuarioRepository;
import com.hatim.alertas.security.JwtUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
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
    private final JwtUtils jwtUtils;

    // Metodo para registrar un usuario nuevo
    @PostMapping("/registro")
    public ResponseEntity<?> registro(@Valid @RequestBody RegistroRequest request) {

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
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Optional<Usuario> usuarioOpcional = usuarioRepository.findByEmail(request.getEmail());

        if (usuarioOpcional.isEmpty()) {
            return ResponseEntity.status(401).body("Credenciales invalidas");
        }

        Usuario usuario = usuarioOpcional.get();
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(401).body("Credenciales invalidas");
        }

        String token = jwtUtils.generarToken(usuario.getEmail(), usuario.getId());

        return ResponseEntity.ok(new LoginResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail(), token));
    }

    // Clases internas para leer el JSON del body del registro y el login
    @Data
    public static class RegistroRequest {
        @NotBlank(message = "El nombre es obligatorio")
        private String nombre;

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es valido")
        private String email;

        @NotBlank(message = "La contraseña es obligatoria")
        private String password;
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es valido")
        private String email;

        @NotBlank(message = "La contraseña es obligatoria")
        private String password;
    }

    // Clase interna autenticacion login con token
    @Data
    @AllArgsConstructor
    public static class LoginResponse {
        private Long id;
        private String nombre;
        private String email;
        private String token;
    }
}