package com.hatim.alertas.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Busca el header Authorization en la peticion
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Verificar si el cliente ha enviado un token y empieza por "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Para quedarse con el string de JWT

            // Comprueba la firma y la fecha de caducidad
            if (jwtUtils.validarToken(token)) {
                // Extraer el email y el id del usuario
                String email = jwtUtils.extraerEmail(token);
                Long usuarioId = jwtUtils.extraerUsuarioId(token);

                // Creamos un objeto de autenticacion personalizado portando el ID del usuario como principal
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        usuarioId, // Lo ponemos en el principal para leerlo directamente en los controladores
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

                // Establecerlo en el contexto
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continuar la ruta, pasa al siguiente filtro o al controlador
        filterChain.doFilter(request, response);
    }
}