package com.hatim.alertas.service;

import com.hatim.alertas.dto.NotificacionDTO;
import com.hatim.alertas.dto.PrecioEventoDTO;
import com.hatim.alertas.model.Notificacion;
import com.hatim.alertas.model.Usuario;
import com.hatim.alertas.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionService {

    // Objeto de Spring que permite enviar mensajes por WebSocket a usuarios conectados
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificacionRepository notificacionRepository;

    @Transactional // Esta bien tenerlo para lecturas y escrituras seguidas
    public void enviarNotificacion(
            Usuario usuario, // Recibe el id del usuario
            PrecioEventoDTO evento, // Datos del cambio de precio
            BigDecimal precioObjetivo) { // Precio objetivo que configuro el usuario

        // Filtrar para anti spam - comprobacion de 5 minutos
        LocalDateTime cincominutos = LocalDateTime.now().minusMinutes(5);
        boolean yaNotificado = notificacionRepository.comprobarDuplicadoReciente(usuario.getId(), evento.getProductoNombre(), cincominutos);

        // Si ya esta notificado, se sale del metodo y bloquea el WebSocket
        if (yaNotificado) {
            log.info("Alerta omitida para usuario {}", usuario.getId());
            return;
        }

        // Si se debe notificar se construye el mensaje
        String mensaje = String.format("%s ha bajado de tu precio objetivo a %s€",
                evento.getProductoNombre(),evento.getPrecioActual()
        );

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setMensaje(mensaje);
        notificacion.setPrecioActual(evento.getPrecioActual());
        notificacion.setPrecioObjetivo(precioObjetivo);
        notificacion.setFecha(LocalDateTime.now());
        notificacion.setLeida(false);
        notificacionRepository.save(notificacion);

        // Enviar el WebSocket al frontend
        NotificacionDTO dto = new NotificacionDTO(
                usuario.getId(),
                mensaje,
                evento.getProductoNombre(),
                precioObjetivo,
                evento.getPrecioActual(),
                LocalDateTime.now() // Guardar momento en el que se notifica el nuevo precio
        );

        // Metodo para enviar la notificacion al usuario pasandole el dto
        messagingTemplate.convertAndSend("/topic/notificaciones/" + usuario.getId(), dto);

        // Log test
        log.info("Notificación enviada por WebSocket al usuario {}", usuario.getId());
    }
}