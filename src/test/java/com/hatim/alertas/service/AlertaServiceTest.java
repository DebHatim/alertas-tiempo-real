package com.hatim.alertas.service;

import com.hatim.alertas.dto.AlertaDTO;
import com.hatim.alertas.exception.ResourceNotFoundException;
import com.hatim.alertas.model.Alerta;
import com.hatim.alertas.model.Producto;
import com.hatim.alertas.model.Usuario;
import com.hatim.alertas.repository.AlertaRepository;
import com.hatim.alertas.repository.ProductoRepository;
import com.hatim.alertas.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Usar chip de Mockito para este test
@ExtendWith(MockitoExtension.class)
class AlertaServiceTest {

    // @Mock para crear un doble simulado
    @Mock private AlertaRepository alertaRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ProductoRepository productoRepository;

    // @InjectMocks para crear una instancia real de la clase
    @InjectMocks
    private AlertaService alertaService;

    // @Test hace que sea un metodo de prueba ejecutable
    @Test
    void crearAlertaConUsuarioYProductoValidoDevuelveDTO() {
        // Arrange / Preparar
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Producto producto = new Producto();
        producto.setId(2L);

        // Creacion de dto con usuario y producto validos
        AlertaDTO dto = new AlertaDTO();
        dto.setUsuarioId(1L);
        dto.setProductoId(2L);
        dto.setPrecioObjetivo(new BigDecimal("100.00"));

        // Cuando se ejecute "findById(1L)" devolver el usuario creado
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Cuando se ejecute "findById(2L)" devolver el producto creado
        when(productoRepository.findById(2L)).thenReturn(Optional.of(producto));

        // Cuando acepte cualquier objeto de tipo Alerta devolver el objeto por parametro
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act / Actuar
        AlertaDTO resultado = alertaService.crearAlerta(dto);

        // Assert / Verificar

        // Verificar que la alerta se haya creado correctamente
        assertThat(resultado.getUsuarioId()).isEqualTo(1L);
        assertThat(resultado.getActiva()).isTrue();

        // Verificar de forma estricta que el servicio ha interactuado con el repositorio para guardar la alerta
        verify(alertaRepository).save(any(Alerta.class));
    }

    @Test
    void crearAlertaConUsuarioInexistenteLanzaExcepcion() {
        // Cuando se ejecute "findById(99L)" devolver un usuario vacio
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // Creacion de dto con usuario inexistente
        AlertaDTO dto = new AlertaDTO();
        dto.setUsuarioId(99L);

        // Verificar que al crear la alerta con el usuario inexistente, este devuelva una excepcion ResourceNotFound
        assertThrows(ResourceNotFoundException.class, () -> alertaService.crearAlerta(dto));
    }

    @Test
    void crearAlertaConProductoInexistenteLanzaExcepcion() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        // Cuando se ejecute "findById(1L)" devolver el usuario
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        // Cuando se ejecute "findById(88L)" devolver valor vacio
        when(productoRepository.findById(88L)).thenReturn(Optional.empty());

        AlertaDTO dto = new AlertaDTO();
        dto.setUsuarioId(1L);
        dto.setProductoId(88L); // Producto inexistente

        // Verificar que lanza la excepcion correspondiente al no encontrar el producto
        assertThrows(ResourceNotFoundException.class, () -> alertaService.crearAlerta(dto));
    }

    @Test
    void crearAlertaConAlertaYaActivaLanzaExcepcion() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Producto producto = new Producto();
        producto.setId(2L);

        // Cuando se ejecute "findById(1L)" devolver el usuario
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        // Cuando se ejecute "findById(2L)" devolver el producto
        when(productoRepository.findById(2L)).thenReturn(Optional.of(producto));

        // Simular que el repositorio encuentra una alerta ya activa para este producto
        when(alertaRepository.findByProductoAndActivaTrue(producto)).thenReturn(List.of(new Alerta()));

        AlertaDTO dto = new AlertaDTO();
        dto.setUsuarioId(1L);
        dto.setProductoId(2L);

        // Verificar que lanza IllegalStateException al intentar duplicar la alerta activa
        assertThrows(IllegalStateException.class, () -> alertaService.crearAlerta(dto));
    }

    @Test
    void obtenerAlertasUsuarioDevuelveListaDeDTOs() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Producto producto = new Producto();
        producto.setId(2L);
        producto.setNombre("Teclado Mecanico");

        Alerta alerta = new Alerta();
        alerta.setId(10L);
        alerta.setUsuario(usuario);
        alerta.setProducto(producto);
        alerta.setPrecioObjetivo(new BigDecimal("50.00"));
        alerta.setActiva(true);

        // Cuando busquen las alertas del usuario 1L, devuelve una lista simulada con nuestra alerta
        when(alertaRepository.findByUsuarioId(1L)).thenReturn(List.of(alerta));

        // Act / Actuar
        List<AlertaDTO> resultado = alertaService.obtenerAlertasUsuario(1L);

        // Assert / Verificar
        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(1);
        assertThat(resultado.getFirst().getProductoNombre()).isEqualTo("Teclado Mecanico");
    }

    @Test
    void desactivarAlertaConPropietarioValidoCambiaEstado() {
        Usuario dueno = new Usuario();
        dueno.setId(1L);
        Alerta alerta = new Alerta();
        alerta.setId(10L);
        alerta.setUsuario(dueno);
        alerta.setActiva(true); // Empieza activa

        when(alertaRepository.findById(10L)).thenReturn(Optional.of(alerta));

        // Act / Actuar (El dueño 1L intenta desactivar su alerta 10L)
        alertaService.desactivarAlerta(10L, 1L);

        // Assert / Verificar
        assertThat(alerta.getActiva()).isFalse(); // Comprobar que ha pasado de true a false
        verify(alertaRepository).save(alerta); // Verificar que se interactuo guardando el cambio
    }

    @Test
    void eliminarAlertaConPropietarioValidoEliminaCorrectamente() {
        Usuario dueno = new Usuario();
        dueno.setId(1L);
        Alerta alerta = new Alerta();
        alerta.setId(10L);
        alerta.setUsuario(dueno);

        when(alertaRepository.findById(10L)).thenReturn(Optional.of(alerta));

        // Act / Actuar (El dueno 1L elimina su alerta 10L)
        alertaService.eliminarAlerta(10L, 1L);

        // Assert / Verificar que se interactuo con el metodo deleteById del repositorio
        verify(alertaRepository).deleteById(10L);
    }

    @Test
    void eliminarAlertaConUsuarioNoPropietarioLanzaAccessDenied() {
        // Crear usuario dueno de la alerta
        Usuario dueno = new Usuario();
        dueno.setId(1L);
        // Crear alerta a eliminar
        Alerta alerta = new Alerta();
        alerta.setId(10L);
        alerta.setUsuario(dueno);

        // Cuando se ejecute "findById(10L)" devolver la alerta
        when(alertaRepository.findById(10L)).thenReturn(Optional.of(alerta));

        // Verificar que al intentar eliminar la alerta con dueno incorrecto, este devuelva una excepcion AccessDenied
        assertThrows(AccessDeniedException.class,
                () -> alertaService.eliminarAlerta(10L, 999L)); // 999L no es el dueño
    }

    @Test
    void desactivarAlertaConUsuarioNoPropietarioLanzaAccessDenied() {
        // Crear usuario dueno de la alerta
        Usuario dueno = new Usuario();
        dueno.setId(1L);
        // Crear alerta a desactivar
        Alerta alerta = new Alerta();
        alerta.setId(10L);
        alerta.setUsuario(dueno);

        // Cuando se ejecute "findById(10L)" devolver la alerta
        when(alertaRepository.findById(10L)).thenReturn(Optional.of(alerta));

        // Verificar que al intentar desactivar la alerta con dueño incorrecto, devuelva AccessDenied
        assertThrows(AccessDeniedException.class,
                () -> alertaService.desactivarAlerta(10L, 999L)); // 999L no es el dueño
    }
}
