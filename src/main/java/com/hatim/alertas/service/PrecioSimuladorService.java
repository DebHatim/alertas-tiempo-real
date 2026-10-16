package com.hatim.alertas.service;

import com.hatim.alertas.dto.PrecioEventoDTO;
import com.hatim.alertas.model.Producto;
import com.hatim.alertas.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrecioSimuladorService {

    // Inyección de dependencias
    private final ProductoRepository productoRepository;

    // Objeto usado para publicar mensajes en Kafka - String = KEY, PrecioEventoDTO = VALUE type
    private final KafkaTemplate<String, PrecioEventoDTO> kafkaTemplate;

    // Generador de objetos aleatorios de Java
    private final Random random = new Random();

    // Límite mínimo de seguridad para evitar que los precios bajen de cierto valor
    private static final BigDecimal PRECIO_MINIMO = BigDecimal.valueOf(0.50);

    @Scheduled(fixedDelay = 5000) // Se ejecuta cada 5 segundos (5000ms) después de completar la última ejecución
    public void simularCambioPrecio() {

        // Guarda todos los productos de la BD en una lista
        List<Producto> productos = productoRepository.findAll();

        // Si no hay productos, sale del método
        if (productos.isEmpty()) {
            log.info("No hay productos para simular precios");
            return;
        }

        // Elige un producto aleatorio de la lista
        Producto producto = productos.get(random.nextInt(productos.size()));

        // Guarda el valor actual antes de cambiarlo
        BigDecimal precioAnterior = producto.getPrecioActual();

        // Calcula una variación aleatoria entre -10% y +10%
        double variacion = (random.nextDouble() * 20 - 10) / 100;

        // Calcula el nuevo precio aplicando la variación y redondeando a 2 decimales
        BigDecimal nuevoPrecio = precioAnterior.multiply(BigDecimal.valueOf(1 + variacion))
                .setScale(2, RoundingMode.HALF_UP);

        // Validación contra el límite mínimo. Si el cálculo redondeado baja de 0.50€,
        // lo forzamos a 0.50€ para evitar que el redondeo 'HALF_UP' atrape el precio en 0.05€.
        if (nuevoPrecio.compareTo(PRECIO_MINIMO) < 0) {
            nuevoPrecio = PRECIO_MINIMO;
        }

        // Actualización del precio del objeto y guardado en BD
        producto.setPrecioActual(nuevoPrecio);
        productoRepository.save(producto);

        // Creación del mensaje que viajará por Kafka con los datos relevantes
        PrecioEventoDTO evento = new PrecioEventoDTO(
                producto.getId(),
                producto.getNombre(),
                precioAnterior,
                nuevoPrecio
        );

        // Publica el evento en el topic "price-events" de Kafka
        kafkaTemplate.send("price-events", evento);

        // Log informativo
        log.info("Precio simulado: {} → {} → {}", producto.getNombre(), precioAnterior, nuevoPrecio);
    }
}