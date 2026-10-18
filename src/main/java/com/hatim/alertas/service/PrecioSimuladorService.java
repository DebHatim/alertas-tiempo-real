package com.hatim.alertas.service;

import com.hatim.alertas.dto.PrecioEventoDTO;
import com.hatim.alertas.model.Producto;
import com.hatim.alertas.repository.ProductoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrecioSimuladorService {

    private final ProductoRepository productoRepository;
    private final KafkaTemplate<String, PrecioEventoDTO> kafkaTemplate;
    private final Random random = new Random();

    // Mapa en memoria para recordar el precio base de referencia de cada producto por su nombre (sin tocar la BD)
    private final Map<String, BigDecimal> preciosBaseReferencia = new HashMap<>();

    @PostConstruct
    public void initPreciosBase() {
        // Definimos los precios de referencia iniciales de forma estática
        preciosBaseReferencia.put("PlayStation 5 Pro", new BigDecimal("799.99"));
        preciosBaseReferencia.put("iPhone 15 Pro Max", new BigDecimal("1219.00"));
        preciosBaseReferencia.put("Nintendo Switch 2", new BigDecimal("449.50"));
    }

    @Scheduled(fixedDelay = 5000)
    public void simularCambioPrecio() {
        List<Producto> productos = productoRepository.findAll();

        if (productos.isEmpty()) {
            log.info("No hay productos para simular precios");
            return;
        }

        // Seleccionar un producto al azar
        Producto producto = productos.get(random.nextInt(productos.size()));
        BigDecimal precioAnterior = producto.getPrecioActual();

        // Obtener el precio base de referencia del mapa (si no existe, usamos el actual como fallback)
        BigDecimal precioBase = preciosBaseReferencia.getOrDefault(producto.getNombre(), precioAnterior);

        // Variacion aleatoria suave entre -4% y +4%
        double variacion = (random.nextDouble() * 0.08) - 0.04;

        BigDecimal nuevoPrecio = precioAnterior.multiply(BigDecimal.valueOf(1 + variacion))
                .setScale(2, RoundingMode.HALF_UP);

        // Calcular limites estrictos basados en el precio base de referencia (Minimo 70%, Maximo 130%)
        BigDecimal precioMinimo = precioBase.multiply(BigDecimal.valueOf(0.70)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal precioMaximo = precioBase.multiply(BigDecimal.valueOf(1.30)).setScale(2, RoundingMode.HALF_UP);

        // Si se sale del rango permitido, corregir y forzar un rebote seguro
        if (nuevoPrecio.compareTo(precioMinimo) < 0) {
            nuevoPrecio = precioMinimo.multiply(BigDecimal.valueOf(1.02)).setScale(2, RoundingMode.HALF_UP);
        } else if (nuevoPrecio.compareTo(precioMaximo) > 0) {
            nuevoPrecio = precioMaximo.multiply(BigDecimal.valueOf(0.98)).setScale(2, RoundingMode.HALF_UP);
        }

        // Guardar el nuevo precio en la base de datos
        producto.setPrecioActual(nuevoPrecio);
        productoRepository.save(producto);

        // Enviar el evento del cambio a Kafka
        PrecioEventoDTO evento = new PrecioEventoDTO(
                producto.getId(),
                producto.getNombre(),
                precioAnterior,
                nuevoPrecio
        );

        kafkaTemplate.send("price-events", evento);

        log.info("Precio simulado: {} -> {} -> {}", producto.getNombre(), precioAnterior, nuevoPrecio);
    }
}