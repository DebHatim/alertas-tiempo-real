package com.hatim.alertas.service;

import com.hatim.alertas.dto.PrecioEventoDTO;
import com.hatim.alertas.exception.ResourceNotFoundException;
import com.hatim.alertas.model.Notificacion;
import com.hatim.alertas.model.Usuario;
import com.hatim.alertas.repository.NotificacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    // Crear un doble simulado
    @Mock private NotificacionRepository notificacionRepository;
    @Mock private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificacionService notificacionService;

    @Test
    void enviarNotificacionGuardaEnBDYEnviaPorWebSocket() {
        // Arrange
        // Crear usuario a usar
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        // Crear dto a usar, generar precio cambiado y enviar a BD
        PrecioEventoDTO evento = new PrecioEventoDTO(
                2L, "PlayStation 5 Pro", new BigDecimal("799.99"), new BigDecimal("699.99")
        );

        // Cuando se guarde una notificacion en en su repositorio, responder con el objeto por parametro
        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        // Enviar notificacion con sus parametros
        notificacionService.enviarNotificacion(usuario, evento, new BigDecimal("700.00"));

        // Assert
        // Usa ArgumentCaptor para cazar los objetos tipo notificacion
        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        // Pasar capturador a repositorio para capturar lo que pase por ahi
        verify(notificacionRepository).save(captor.capture());
        // Capturar el valor usando .getValue()
        Notificacion guardada = captor.getValue();

        // Verificar que la notificacion pertenezca al usuario creado
        assertThat(guardada.getUsuario()).isEqualTo(usuario);
        // Verificar que el precio es igual al precio pasado por parametro
        assertThat(guardada.getPrecioActual()).isEqualByComparingTo("699.99");
        // Verificar que la notificacion no esta leida
        assertThat(guardada.getLeida()).isFalse();

        // Verificar que se envia por WebSocket al topic correcto del usuario
        verify(messagingTemplate).convertAndSend(anyString(), any(Object.class));
    }

    @Test
    void marcarComoLeidaConPropietarioValidoActualizaEstado() {
        // Arrange
        Usuario dueno = new Usuario();
        dueno.setId(1L);

        // Crear la notificacion estableciendole el usuario creado
        Notificacion notificacion = new Notificacion();
        notificacion.setId(10L);
        notificacion.setUsuario(dueno);
        notificacion.setLeida(false);

        // Cuando se use "findById(10L)" devolver la notificacion
        when(notificacionRepository.findById(10L)).thenReturn(Optional.of(notificacion));
        // Cuando se guarde una notificacion en su repositorio , responder con el objeto por parametro
        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        // El dueno marca la notificacion como leida
        Notificacion resultado = notificacionService.marcarComoLeida(10L, 1L);

        // Assert
        // Verificar que se haya cambiado el valor en la BD correctamente
        assertThat(resultado.getLeida()).isTrue();
        verify(notificacionRepository).save(notificacion);
    }

    @Test
    void marcarComoLeidaConUsuarioNoPropietarioLanzaAccessDenied() {
        // Arrange
        Usuario dueno = new Usuario();
        dueno.setId(1L);

        // Crear notificacion a usar
        Notificacion notificacion = new Notificacion();
        notificacion.setId(10L);
        notificacion.setUsuario(dueno);

        // Cuando se use "findById(10L)" devolver la notificacion
        when(notificacionRepository.findById(10L)).thenReturn(Optional.of(notificacion));

        // Act / Assert
        // Verificar que al marcar la notificacion como leida con el usuario incorrecto, se lance excepcion AccessDenied
        assertThrows(AccessDeniedException.class,
                () -> notificacionService.marcarComoLeida(10L, 999L)); // 999L no es el dueño
    }

    @Test
    void marcarComoLeidaConNotificacionInexistenteLanzaResourceNotFound() {
        // Arrange
        // Cuando se busque una notificacion inexistente no devolver nada
        when(notificacionRepository.findById(404L)).thenReturn(Optional.empty());

        // Act / Assert
        // Verificar que al marcar como leida una notificacion inexistente, se lance excepcion ResourceNotFound
        assertThrows(ResourceNotFoundException.class,
                () -> notificacionService.marcarComoLeida(404L, 1L));
    }
}