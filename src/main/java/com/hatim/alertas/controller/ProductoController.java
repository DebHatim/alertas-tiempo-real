package com.hatim.alertas.controller;

import com.hatim.alertas.dto.ProductoDTO;
import com.hatim.alertas.model.Producto;
import com.hatim.alertas.repository.ProductoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Marca la clase como controlador REST - todo devuelve JSON automaticamente
@RestController

// Prefijo es "/api/productos"
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Catálogo de productos e inyección de precios")
public class ProductoController {

    private final ProductoRepository productoRepository;

    // Metodo principal que devuelve todos los productos
    @GetMapping
    @Operation(summary = "Listar todos los productos", description = "Devuelve el catálogo completo de productos en el sistema.")
    public List<Producto> getAll() {
        return productoRepository.findAll();
    }

    // Metodo que devuelve un producto por id
    @GetMapping("/{id}")
    @Operation(summary = "Buscar producto por ID", description = "Devuelve los detalles de un producto o un 404 si no existe.")
    public ResponseEntity<Producto> getById(@PathVariable Long id) {
        // ResponseEntity para controlar el codigo HTTP de respuesta
        return productoRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Metodo POST para crear un producto nuevo
    @PostMapping
    @Operation(summary = "Registrar un nuevo producto", description = "Introduce un artículo nuevo con su precio inicial.")
    // @RequestBody lee el JSON del cuerpo de la peticion y lo convierte a objeto Producto
    public ResponseEntity<Producto> crear(@Valid @RequestBody ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setPrecioActual(dto.getPrecio());

        Producto guardado = productoRepository.save(producto);

        return ResponseEntity.ok().body(guardado);
    }
}