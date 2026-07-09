package com.hatim.alertas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    // Clave secreta harcodeada para desarrollo
    private static final String SECRET_KEY_STRING = "esta_es_una_clave_secreta_muy_larga_y_segura_para_el_algoritmo_hs256";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));

    // Expiracion de token en 24 horas
    private static final long EXPIRATION_TIME = 86400000;

    // Metodo para generar el token guardando el email como principal y mete usuarioId dentro del "payload"
    public String generarToken(String email, Long usuarioId) {
        return Jwts.builder().subject(email).claim("usuarioId", usuarioId)
                .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY).compact();
    }

    // Metodo para abrir el token y extraer el propietario
    public String extraerEmail(String token) {
        return extraerClaims(token).getSubject();
    }

    // Metodo para recuperar el id del usuario que se guarda en el login
    public Long extraerUsuarioId(String token) {
        return extraerClaims(token).get("usuarioId", Long.class);
    }

    // Metodo para comprobar que el token no haya expirado y que la estructura sea valida
    public boolean validarToken(String token) {
        try {
            return extraerClaims(token).getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // Metodo que usa la clave secreta para verificar la firma
    private Claims extraerClaims(String token) {
        return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
    }
}