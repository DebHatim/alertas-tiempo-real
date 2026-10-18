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

    private final ProductoRepository productoRepository;
    private final KafkaTemplate<String, PrecioEventoDTO> kafkaTemplate;
    private final Random random = new Random();

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

        // Variacion aleatoria suave entre -6% y +6%
        double variacion = (random.nextDouble() * 0.12) - 0.06;

        BigDecimal nuevoPrecio = precioAnterior.multiply(BigDecimal.valueOf(1 + variacion))
                .setScale(2, RoundingMode.HALF_UP);

        // Limites de seguridad para evitar que el precio baje demasiado o suba en exceso
        BigDecimal precioMinimoSeguro = BigDecimal.valueOf(10.00);
        BigDecimal precioMaximoSeguro = BigDecimal.valueOf(2000.00);

        if (nuevoPrecio.compareTo(precioMinimoSeguro) < 0) {
            // Si baja del minimo, forzar un rebote hacia arriba
            nuevoPrecio = precioAnterior.multiply(BigDecimal.valueOf(1.05)).setScale(2, RoundingMode.HALF_UP);
        } else if (nuevoPrecio.compareTo(precioMaximoSeguro) > 0) {
            nuevoPrecio = precioMaximoSeguro;
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