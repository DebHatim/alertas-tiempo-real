package com.hatim.alertas.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Crear Bean con Spring de esta clase
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    // Mapa para guardar un Bucket de tokens por cada IP
    private final Map<String, Bucket> cache =  new ConcurrentHashMap<>();

    // Crear un nuevo Bucket de tokens con la configuracion de limites que le indicamos
    private Bucket crearNuevoBucket() {
        // Crear el limite de ancho de banda usando la API de construccion Builder
        Bandwidth limit = Bandwidth.builder()
                .capacity(5)
                .refillIntervally(1, Duration.ofSeconds(12))
                .build();

        // Construir y devolver el Bucket de tokens con el limite aplicado
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // Filtrar por endpoint, en este caso el login y que el metodo sea POST
        if ("/api/auth/login".equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod())) {

            // Obtener la direccion ip del usuario que hace la peticion
            String ipclient = obtenerIpCliente(request);
            // Buscar en la cache si existe un bucket para esa ip, si no existe se crea uno nuevo
            Bucket bucket = cache.computeIfAbsent(ipclient, k -> crearNuevoBucket());

            // Intenar consumir un token?
            if (!bucket.tryConsume(1)) {
                // Bloquear la peticion devolviendo 429 Too Many Requests
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Demasiados intentos de inicio de sesion. Intentalo de nuevo mas tarde.\"}");
                return;
            }
        }
        // Continuar al siguiente filtro en la cadena de Spring Security
        filterChain.doFilter(request, response);
    }

    // Metodo para extraer la direccion ip real del usuario
    private String obtenerIpCliente(HttpServletRequest request) {
        // Recoger la ip de la cabecera
        String xfHeader = request.getHeader("X-Forwarded-For");

        // Si no hay cabecera, acceder directamente a la ip del socket de red
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }

        // Split en el caso de que haya proxys incluidos
        return xfHeader.split(",")[0];
    }
}
