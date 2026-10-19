package com.hatim.alertas.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Crear Bean con Spring de esta clase
@Component
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    private static class Entrada {
        private final Bucket bucket;
        private volatile Instant ultimoAcceso;

        Entrada(Bucket bucket) {
            this.bucket = bucket;
            this.ultimoAcceso = Instant.now();
        }
    }

    // Mapa para guardar un Bucket de tokens por cada IP
    private final Map<String, Entrada> cache = new ConcurrentHashMap<>();

    // IPs sin actividad durante mas de este tiempo se eliminan de la cache
    private static final Duration TIEMPO_INACTIVIDAD = Duration.ofMinutes(30);

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
            // Buscar en la cache si existe una entrada para esa ip, si no existe se crea una nueva
            Entrada entrada = cache.computeIfAbsent(ipclient, k -> new Entrada(crearNuevoBucket()));
            entrada.ultimoAcceso = Instant.now();

            // Intentar consumir un token
            if (!entrada.bucket.tryConsume(1)) {
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

    // Tarea periodica que limpia IPs inactivas para evitar fuga de memoria
    @Scheduled(fixedDelay = 600_000) // cada 10 minutos
    public void limpiarCacheInactiva() {
        Instant limite = Instant.now().minus(TIEMPO_INACTIVIDAD);
        int antes = cache.size();
        cache.entrySet().removeIf(e -> e.getValue().ultimoAcceso.isBefore(limite));
        int eliminadas = antes - cache.size();
        if (eliminadas > 0) {
            log.debug("RateLimitingFilter: {} entradas inactivas eliminadas de la cache", eliminadas);
        }
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