package com.hatim.alertas.controller;

import com.hatim.alertas.model.Producto;
import com.hatim.alertas.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Marca la clase como controlador REST - todo devuelve JSON automaticamente
@RestController

// Prefijo es "/api/productos"
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ProductoController {

    private final ProductoRepository productoRepository;

    // Metodo principal que devuelve todos los productos
    @GetMapping
    public List<Producto> getAll() {
        return productoRepository.findAll();
    }

    // Metodo que devuelve un producto por id
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getById(@PathVariable Long id) {
        // ResponseEntity para controlar el codigo HTTP de respuesta
        return productoRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Metodo POST para crear un producto nuevo
    @PostMapping
    // @RequestBody lee el JSON del cuerpo de la peticion y lo convierte a objeto Producto
    public Producto crear(@RequestBody Producto producto) {
        return productoRepository.save(producto);
    }
}