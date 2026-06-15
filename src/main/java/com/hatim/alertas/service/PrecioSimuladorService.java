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

    // Inyeccion de dependencias
    private final ProductoRepository productoRepository;

    // Objeto usado para publicar mensajes en Kafka - String = KEY, PrecioEventoDTO = VALUE type
    private final KafkaTemplate<String, PrecioEventoDTO> kafkaTemplate;

    // Generador de objetos aleatorios de Java
    private final Random random = new Random();

    @Scheduled(fixedDelay = 5000) // Scheduled se ejecuta cada 5 segundos (5000ms) - fixedDelay espera 5 segundos despues de terminar
    // Metodo para simular cambios de precios
    public void simularCambioPrecio() {

        // Guarda todos los productos de la BD en una lista
        List<Producto> productos = productoRepository.findAll();

        // Si no hay productos, sale del metodo
        if (productos.isEmpty()) {
            log.info("No hay productos para simular precios");
            return;
        }

        // Elige un producto aleatorio de la lista - .nextInt(productos.size()) devuelve un numero entre 0 y el tamano menos 1
        Producto producto = productos.get(random.nextInt(productos.size()));

        // Guarda el valor actual antes de cambiarlo
        BigDecimal precioAnterior = producto.getPrecioActual();

        // Calcula una variación aleatoria entre -10% y +10%
        double variacion = (random.nextDouble() * 20 - 10) / 100;

        // Calcula el nuevo precio y redondea
        BigDecimal nuevoPrecio = precioAnterior.multiply(BigDecimal.valueOf(1 + variacion)).setScale(2, RoundingMode.HALF_UP);

        // Actualizacion del precio del objeto y guardado en BD
        producto.setPrecioActual(nuevoPrecio);
        productoRepository.save(producto);

        // Creacion del mensaje que viajara por Kafka con los 4 datos relevantes
        PrecioEventoDTO evento = new PrecioEventoDTO(
                producto.getId(),
                producto.getNombre(),
                precioAnterior,
                nuevoPrecio
        );

        // Publica el evento en el topic "price-events" de Kafka, cualquier usuario suscrito a este topic lo recibira
        kafkaTemplate.send("price-events", evento);

        // Log test
        log.info("Precio simulado: {} → {} → {}", producto.getNombre(), precioAnterior, nuevoPrecio);
    }
}