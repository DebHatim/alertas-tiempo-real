package com.hatim.alertas.service;

import com.hatim.alertas.dto.PrecioEventoDTO;
import com.hatim.alertas.model.Alerta;
import com.hatim.alertas.model.Producto;
import com.hatim.alertas.model.Usuario;
import com.hatim.alertas.repository.AlertaRepository;
import com.hatim.alertas.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertaEvaluadorServiceTest {

    @Mock private AlertaRepository alertaRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private NotificacionService notificacionService;

    @InjectMocks
    private AlertaEvaluadorService alertaEvaluadorService;

    @Test
    void productoInexistenteNoHaceNada() {
        // Arrange
        // Crear el dto del cambio de precio
        PrecioEventoDTO evento = new PrecioEventoDTO(99L, "Producto Fantasma",
                new BigDecimal("100.00"), new BigDecimal("90.00"));

        // Cuando se busque el producto 99L no devolver nada
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        // Llamar al servicio para evaluar este evento
        alertaEvaluadorService.evaluarAlertas(evento);

        // Assert
        // Verificar no se haya ejecutado el metodo de buscar alertas en el repositorio
        verify(alertaRepository, never()).findByProductoAndActivaTrue(any());
        // Verificar que no se ha interactuado con el servicio de notificaciones
        verifyNoInteractions(notificacionService);
    }

    @Test
    void precioPorDebajoDelObjetivoDisparaNotificacionYDesactivaAlerta() {
        // Arrange
        // Crear producto, usuario y alerta correctos
        Producto producto = new Producto();
        producto.setId(2L);
        producto.setNombre("PlayStation 5 Pro");

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Alerta alerta = new Alerta();
        alerta.setId(10L);
        alerta.setUsuario(usuario);
        alerta.setProducto(producto);
        alerta.setPrecioObjetivo(new BigDecimal("700.00"));
        alerta.setActiva(true);

        // Crear evento con precio debajo del objetivo
        PrecioEventoDTO evento = new PrecioEventoDTO(2L, "PlayStation 5 Pro",
                new BigDecimal("799.99"), new BigDecimal("699.99"));

        // Cuando se busque el producto devolverselo
        when(productoRepository.findById(2L)).thenReturn(Optional.of(producto));

        // Cuando se busque si una alerta esta activa con tal producto devolver la alerta creada
        when(alertaRepository.findByProductoAndActivaTrue(producto)).thenReturn(List.of(alerta));

        // Act
        // Llamar al metodo para evaluar la alerta
        alertaEvaluadorService.evaluarAlertas(evento);

        // Assert
        // Verificar que se llame al metodo enviarNotificacion correctamente
        verify(notificacionService).enviarNotificacion(usuario, evento, new BigDecimal("700.00"));

        // Verificar que se guarda la alerta correctamente y que la alerta este desactivada
        verify(alertaRepository).save(alerta);
        assertThat(alerta.getActiva()).isFalse();
    }

    @Test
    void precioPorEncimaDelObjetivoNoDisparaNotificacion() {
        // Arrange
        // Crear alerta con precio por encima del objetivo
        Producto producto = new Producto();
        producto.setId(2L);

        Alerta alerta = new Alerta();
        alerta.setId(10L);
        alerta.setUsuario(new Usuario());
        alerta.setProducto(producto);
        alerta.setPrecioObjetivo(new BigDecimal("500.00"));
        alerta.setActiva(true);

        PrecioEventoDTO evento = new PrecioEventoDTO(2L, "PlayStation 5 Pro",
                new BigDecimal("799.99"), new BigDecimal("750.00")); // sigue por encima

        // Cuando se busque el producto en el repositorio devolver el creado
        when(productoRepository.findById(2L)).thenReturn(Optional.of(producto));
        // Cuando se busque si la alerta esta activa devolver esta misma
        when(alertaRepository.findByProductoAndActivaTrue(producto)).thenReturn(List.of(alerta));

        // Act
        // Llamar al metodo para evaluar alertas
        alertaEvaluadorService.evaluarAlertas(evento);

        // Assert
        // Verificar que no hubo cambios en el servicio de notificaciones
        verifyNoInteractions(notificacionService);
        // Verificar que no se ha anadido ninguna alerta al repositorio de alertas
        verify(alertaRepository, never()).save(any());
    }

    @Test
    void variasAlertasSoloDisparaLasQueCumplenElObjetivo() {
        // Arrange
        // Crear las alertas para disparar
        Producto producto = new Producto();
        producto.setId(2L);

        Usuario usuarioA = new Usuario();
        usuarioA.setId(1L);
        Usuario usuarioB = new Usuario();
        usuarioB.setId(2L);

        Alerta alertaQueCumple = new Alerta();
        alertaQueCumple.setId(10L);
        alertaQueCumple.setUsuario(usuarioA);
        alertaQueCumple.setProducto(producto);
        alertaQueCumple.setPrecioObjetivo(new BigDecimal("700.00"));
        alertaQueCumple.setActiva(true);

        Alerta alertaQueNoCumple = new Alerta();
        alertaQueNoCumple.setId(11L);
        alertaQueNoCumple.setUsuario(usuarioB);
        alertaQueNoCumple.setProducto(producto);
        alertaQueNoCumple.setPrecioObjetivo(new BigDecimal("600.00"));
        alertaQueNoCumple.setActiva(true);

        PrecioEventoDTO evento = new PrecioEventoDTO(2L, "PlayStation 5 Pro",
                new BigDecimal("799.99"), new BigDecimal("650.00"));

        when(productoRepository.findById(2L)).thenReturn(Optional.of(producto));
        // Cuando se busquen alertas activas de ese producto, devolver las dos creadas
        when(alertaRepository.findByProductoAndActivaTrue(producto))
                .thenReturn(List.of(alertaQueCumple, alertaQueNoCumple));

        // Act
        // Llamar al metodo para evaluar alertas
        alertaEvaluadorService.evaluarAlertas(evento);

        // Assert
        // Verificar que se ha enviado la notificacion al servicio con los valores de la primera alerta
        verify(notificacionService).enviarNotificacion(usuarioA, evento, new BigDecimal("700.00"));
        // Verificar que no se haya enviado una notificacion del segundo usuario ( segunda alerta )
        verify(notificacionService, never()).enviarNotificacion(eq(usuarioB), any(), any());
        // Verificar que los repositorios hayan recibido la primera alerta y no la segunda
        verify(alertaRepository).save(alertaQueCumple);
        verify(alertaRepository, never()).save(alertaQueNoCumple);
    }
}