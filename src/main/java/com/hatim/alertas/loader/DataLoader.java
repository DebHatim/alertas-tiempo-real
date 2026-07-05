package com.hatim.alertas.loader;

import com.hatim.alertas.model.Producto;
import com.hatim.alertas.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ProductoRepository productoRepository;

    @Override
    public void run(String... args) {

        // Solo insertar si la tabla de productos esta vacia
        if (productoRepository.count() == 0) {
            Producto p1 = new Producto();
            p1.setNombre("PlayStation 5 Pro");
            p1.setPrecioActual(new BigDecimal("799.99"));

            Producto p2 = new Producto();
            p2.setNombre("iPhone 15 Pro Max");
            p2.setPrecioActual(new BigDecimal("1219.00"));

            Producto p3 = new Producto();
            p3.setNombre("Nintendo Switch 2");
            p3.setPrecioActual(new BigDecimal("449.50"));

            productoRepository.saveAll(List.of(p1, p2, p3));

            System.out.println("DataLoader: Datos de ejemplo cargados correctamente.");
        }
    }
}
