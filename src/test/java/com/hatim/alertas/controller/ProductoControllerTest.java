package com.hatim.alertas.controller;

import com.hatim.alertas.dto.ProductoDTO;
import com.hatim.alertas.model.Producto;
import com.hatim.alertas.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoController productoController;

    @Test
    void getAllDevuelveTodosLosProductos() {
        // Arrange
        // Cuando se busquen todos los productos del repositorio, devolver estos 2 nuevos
        when(productoRepository.findAll()).thenReturn(List.of(new Producto(), new Producto()));

        // Act
        // Llamar al metodo getAll del controlador
        List<Producto> productos = productoController.getAll();

        // Assert
        // Verificar que la respuesta tenga un tamano de 2
        assertThat(productos).hasSize(2);
    }

    @Test
    void getByIdConProductoExistenteDevuelve200() {
        // Arrange
        Producto producto = new Producto();
        producto.setId(1L);
        // Cuando se busque si existe este producto devolverlo
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act
        // Llamar al metodo getById del controlador
        ResponseEntity<Producto> response = productoController.getById(1L);

        // Assert
        // Verificar que la respuesta sea Successful
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void getByIdConProductoInexistenteDevuelve404() {
        // Arrange
        // Cuando se busque un producto con un id que no existe no devolver nada
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        // Llamar al metodo getById del controlador
        ResponseEntity<Producto> response = productoController.getById(99L);

        // Assert
        // Verificar que la respuesta sea 404 Not Found
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void crearGuardaYDevuelveElProducto() {
        // Arrange
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("Nintendo Switch 2");
        dto.setPrecio(new BigDecimal("449.50"));

        // Cuando se guarde cualquier producto, devolver este mismo por parametro
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        // Llamar al metodo crear del controlador
        ResponseEntity<Producto> response = productoController.crear(dto);

        // Assert
        // Verificar que el cuerpo de la respuesta no sea nulo
        assertThat(response.getBody()).isNotNull();
        // Verificar que el nombre del producto de la respuesta sea el mismo
        assertThat(response.getBody().getNombre()).isEqualTo("Nintendo Switch 2");
    }
}