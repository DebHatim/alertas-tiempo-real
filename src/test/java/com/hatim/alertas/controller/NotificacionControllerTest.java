package com.hatim.alertas.controller;

import com.hatim.alertas.model.Notificacion;
import com.hatim.alertas.repository.NotificacionRepository;
import com.hatim.alertas.service.NotificacionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionControllerTest {

    @Mock private NotificacionRepository notificacionRepository;
    @Mock private NotificacionService notificacionService;
    @Mock private Authentication authentication;

    @InjectMocks
    private NotificacionController notificacionController;

    @Test
    void getByUsuarioConIdPropioDevuelveElHistorial() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(1L);
        // Cuando busque una notificacion con este usuario, devolver el historial correctamente
        when(notificacionRepository.findByUsuarioIdOrderByFechaDesc(1L))
                .thenReturn(List.of(new Notificacion()));

        // Act
        // Ejecutar el metodo de para obtener las notificaciones del usuario y guardar el resultado en un ResponseEntity
        ResponseEntity<List<Notificacion>> response = notificacionController.getByUsuario(1L, authentication);

        // Assert
        // Verificar que el codigo de respuesta sea succesful
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        // Verificar que el cuerpo tenga una longitud de 1 (1 notificacion)
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getByUsuarioConIdAjenoLanzaAccessDenied() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(1L);

        // Act
        // Verificar que al querer obtener las notificaciones de otra persona, este lance una excepcion AccessDenied
        assertThrows(AccessDeniedException.class,
                () -> notificacionController.getByUsuario(999L, authentication));

        // Assert
        // Verificar que no se haya ejecutado el metodo findByUsuarioIdOrderByFechaDesc
        verify(notificacionRepository, never()).findByUsuarioIdOrderByFechaDesc(any());
    }

    @Test
    void marcarLeidaDelegaEnElServicioConElUsuarioAutenticado() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(1L);
        // Cuando se llame al metodo de marcar como leida, devolver la notificacion correctamente
        when(notificacionService.marcarComoLeida(10L, 1L)).thenReturn(new Notificacion());

        // Act
        // Guardar la respuesta del metodo marcarLeida del controller en un objeto ResponseEntity
        ResponseEntity<Notificacion> response = notificacionController.marcarLeida(10L, authentication);

        // Assert
        // Verificar que el codigo de respuesta del servidor sea Successful
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        // Verificar que el servicio haya ejecutado el metodo con los valores correctos
        verify(notificacionService).marcarComoLeida(10L, 1L);
    }
}