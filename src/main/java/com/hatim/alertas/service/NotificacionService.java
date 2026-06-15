package com.hatim.alertas.service;

import com.hatim.alertas.dto.NotificacionDTO;
import com.hatim.alertas.dto.PrecioEventoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionService {

    // Objeto de Spring que permite enviar mensajes por WebSocket a usuarios conectados
    private final SimpMessagingTemplate messagingTemplate;

    public void enviarNotificacion(
            Long usuarioId, // Recibe el id del usuario
            PrecioEventoDTO evento, // Datos del cambio de precio
            BigDecimal precioObjetivo) { // Precio objetivo que configuro el usuario
        NotificacionDTO dto = new NotificacionDTO(
                usuarioId,
                String.format("¡%s ha bajado de tu precio objetivo!", evento.getProductoNombre()),
                evento.getProductoNombre(),
                precioObjetivo,
                evento.getPrecioActual(),
                LocalDateTime.now() // Guardar momento en el que se notifica el nuevo precio
        );

        // Metodo para enviar la notificacion al usuario pasandole el dto
        messagingTemplate.convertAndSend("/topic/notificaciones/" + usuarioId, dto);

        // Log test
        log.info("Notificación enviada por WebSocket al usuario {}", usuarioId);
    }
}