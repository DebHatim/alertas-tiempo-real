package com.hatim.alertas.controller;

import com.hatim.alertas.model.Usuario;
import com.hatim.alertas.repository.UsuarioRepository;
import com.hatim.alertas.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtils jwtUtils;

    @InjectMocks
    private AuthController authController;

    @Test
    void registroConEmailNuevoCreaUsuarioYDevuelve201() {
        // Arrange
        // Crear una solicitud de registro con datos de usuario nuevos
        AuthController.RegistroRequest request = new AuthController.RegistroRequest();
        request.setNombre("Hatim");
        request.setEmail("hatim@test.com");
        request.setPassword("password123");

        // Asegurar que el email no exista
        when(usuarioRepository.findByEmail("hatim@test.com")).thenReturn(Optional.empty());
        // Cuando se encripte la contrasena, devolverla "encriptada"
        when(passwordEncoder.encode("password123")).thenReturn("hashhash");
        // Cuando se guarde el usuario en el repositorio, devolver con el usuario creado por parametro
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        // Act
        // Ejecutar el metodo de registro del controlador
        ResponseEntity<?> response = authController.registro(request);

        // Assert
        // Verificar que la respuesta sea 201 Created
        assertThat(response.getStatusCode().value()).isEqualTo(201);
        // Verificar que la contrasena del usuario se haya guardado con el mismo valor
        verify(usuarioRepository).save(argThat(u -> u.getPassword().equals("hashhash")));
    }

    @Test
    void registroConEmailYaExistenteDevuelve400() {
        // Arrange
        // Crear request de registro con un email ya existente
        AuthController.RegistroRequest request = new AuthController.RegistroRequest();
        request.setEmail("hatim@test.com");

        // Cuando se busque en la base de datos si existe, devolver que si
        when(usuarioRepository.findByEmail("hatim@test.com")).thenReturn(Optional.of(new Usuario()));

        // Act
        // Ejecutar el metodo de registro del controlador
        ResponseEntity<?> response = authController.registro(request);

        // Assert
        // Verificar que la respuesta sea 400 Bad Request
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        // Verificar que no se haya guardado nada en el repositorio del usuario
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void loginConCredencialesCorrectasDevuelveToken() {
        // Arrange
        // Crear un LoginRequest con datos correctos
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Hatim");
        usuario.setEmail("hatim@test.com");
        usuario.setPassword("hashhash");

        AuthController.LoginRequest request = new AuthController.LoginRequest();
        request.setEmail("hatim@test.com");
        request.setPassword("password123");

        // Cuando busque si existe este email, responder con este mismo
        when(usuarioRepository.findByEmail("hatim@test.com")).thenReturn(Optional.of(usuario));
        // Cuando el passwordEncoder compruebe si esta bien encriptada la contrasena devolver true
        when(passwordEncoder.matches("password123", "hashhash")).thenReturn(true);
        // Cuando el gestionador de tokens la genere para este correo, devolver el token
        when(jwtUtils.generarToken("hatim@test.com", 1L)).thenReturn("token-falso");

        // Act
        // Ejecutar el metodo de login del controlador
        ResponseEntity<?> response = authController.login(request);

        // Assert
        // Verificar que la respuesta sea codigo Successful
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        // Verificar que el cuerpo no este vacio
        assertThat(response.getBody()).isNotNull();
        // Verificar que el token dentro de la respuesta sea el mismo
        AuthController.LoginResponse body = (AuthController.LoginResponse) response.getBody();
        assertThat(body.getToken()).isEqualTo("token-falso");
    }

    @Test
    void loginConPasswordIncorrectaDevuelve401() {
        // Arrange
        // Crear LoginRequest con contrasena incorrecta
        Usuario usuario = new Usuario();
        usuario.setEmail("hatim@test.com");
        usuario.setPassword("hashhash");

        AuthController.LoginRequest request = new AuthController.LoginRequest();
        request.setEmail("hatim@test.com");
        request.setPassword("incorrecta");

        // Cuando se busque si existe el usuario en el repo, devolver este
        when(usuarioRepository.findByEmail("hatim@test.com")).thenReturn(Optional.of(usuario));
        // Cuando se verifique si la contrasena es correcta, devolver con un false
        when(passwordEncoder.matches("incorrecta", "hashhash")).thenReturn(false);

        // Act
        // Ejecutar el metodo de login del controlador
        ResponseEntity<?> response = authController.login(request);

        // Assert
        // Verificar que la respuesta sea 401 Unauthorized
        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }

    @Test
    void loginConEmailInexistenteDevuelve401() {
        // Arrange
        // Crear LoginRequest con email inexistente
        AuthController.LoginRequest request = new AuthController.LoginRequest();
        request.setEmail("noexiste@test.com");
        request.setPassword("cualquiera");

        // Cuando se busque si existe un usuario con este email, no devolver nada
        when(usuarioRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        // Act
        // Ejecutar el metodo de login del controlador
        ResponseEntity<?> response = authController.login(request);

        // Assert
        // Verificar que la respuesta sea 401 Unauthorized
        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }
}