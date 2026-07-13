package com.hatim.alertas.controller;

import com.hatim.alertas.dto.AlertaDTO;
import com.hatim.alertas.service.AlertaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertaControllerTest {

    @Mock private AlertaService alertaService;
    @Mock private Authentication authentication;

    @InjectMocks
    private AlertaController alertaController;

    @Test
    void crearAsignaElUsuarioAutenticadoAlDTOAntesDeGuardar() {
        // Arrange
        AlertaDTO dto = new AlertaDTO();
        dto.setProductoId(2L);
        dto.setPrecioObjetivo(new BigDecimal("500.00"));

        // Cuando la autenticacion busque el dueno, devolver usuario correcto
        when(authentication.getPrincipal()).thenReturn(1L);
        // Cuando el service cree la alerta, responder con la misma por parametro
        when(alertaService.crearAlerta(any(AlertaDTO.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        // Ejecutar metodo crear con los datos correctos
        ResponseEntity<?> response = alertaController.crear(dto, authentication);

        // Assert
        // Verificar que el codigo de respuesta del servidor sea 201 Created
        assertThat(response.getStatusCode().value()).isEqualTo(201);
        // Verificar que el usuario del dto sea el mismo del authentication
        assertThat(dto.getUsuarioId()).isEqualTo(1L);
    }

    @Test
    void getByUsuarioConIdPropioDevuelveLasAlertas() {
        // Arrange

        // Cuando la autenticacion busque el dueno, devolver usuario correcto
        when(authentication.getPrincipal()).thenReturn(1L);
        // Cuando se busquen las alertas de este usuario, devolverle correctamente
        when(alertaService.obtenerAlertasUsuario(1L)).thenReturn(List.of(new AlertaDTO()));

        // Act
        // Llamar al metodo getByUsuario del controller para recibir sus alertas
        ResponseEntity<?> response = alertaController.getByUsuario(1L, authentication);

        // Assert
        // Verificar que el codigo de respuesta del servidor sea "successful"
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        // Verificar que se haya llamado al servicio con el metodo y con el id principal
        verify(alertaService).obtenerAlertasUsuario(1L);
    }

    @Test
    void getByUsuarioConIdAjenoDevuelve403SinLlamarAlServicio() {
        // Arrange
        // Cuando la autenticacion busque el dueno, devolver usuario correcto
        when(authentication.getPrincipal()).thenReturn(1L);

        // Act
        // Llamar al metodo de recuperar alertas de usuario con id incorrecto
        ResponseEntity<?> response = alertaController.getByUsuario(999L, authentication);

        // Assert
        // Verificar que el codigo de respuesta del servidor sea el 403 Forbidden
        assertThat(response.getStatusCode().value()).isEqualTo(403);
        // Verificar que no se haya ejecutado el metodo de obtener alertas del servicio de alertas
        verify(alertaService, never()).obtenerAlertasUsuario(any());
    }

    @Test
    void eliminarDelegaEnElServicioConElUsuarioAutenticado() {
        // Arrange
        // Cuando la autenticacion busque el dueno, devolver usuario correcto
        when(authentication.getPrincipal()).thenReturn(1L);

        // Act
        // Llamar al metodo de eliminar
        ResponseEntity<?> response =alertaController.eliminar(10L, authentication);

        // Assert
        // Asegurar que el codigo de respuesta del servidor sea Successful
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        // Verificar que se haya llamado al metodo de eliminar con los valores correctos
        verify(alertaService).eliminarAlerta(10L, 1L);
    }

    @Test
    void cambiarEstadoDelegaEnElServicioConElUsuarioAutenticado() {
        // Arrange
        // Cuando la autenticacion busque el dueno, devolver usuario correcto
        when(authentication.getPrincipal()).thenReturn(1L);

        // Act
        // Llamar al metodo para cambiar el estado de la alerta
        alertaController.cambiarEstado(10L, authentication);

        // Assert
        // verificar que se haya llamado al metodo del servicio correctamente
        verify(alertaService).desactivarAlerta(10L, 1L);
    }
}