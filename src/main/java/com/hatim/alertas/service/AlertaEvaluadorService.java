package com.hatim.alertas.service;

import com.hatim.alertas.dto.PrecioEventoDTO;
import com.hatim.alertas.model.Alerta;
import com.hatim.alertas.model.Producto;
import com.hatim.alertas.repository.AlertaRepository;
import com.hatim.alertas.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertaEvaluadorService {

    // Inyeccion de dependencias
    private final AlertaRepository alertaRepository;
    private final ProductoRepository productoRepository;
    private final NotificacionService notificacionService;

    // @KafkaListener — marca este metodo como consumidor de Kafka
    // topics = "price-events" — escucha exactamente este topic
    // groupId = "alertas-group" — identifica este consumidor dentro de Kafka
    // Si hubiera varios consumidores con el mismo groupId, Kafka repartiría los mensajes entre ellos (balanceo de carga)
    @KafkaListener(topics = "price-events", groupId = "alertas-group")
    public void evaluarAlertas(PrecioEventoDTO evento) {
        // Metodo que se ejecuta cada vez que llega un nuevo mensaje al topic "price-events"

        // Log test
        log.info("Evento recibido: {} nuevo precio {}", evento.getProductoNombre(), evento.getPrecioActual());

        // Busca el producto en BD por su id
        Optional<Producto> productoOpt = productoRepository.findById(evento.getProductoId());

        // Si el producto no existe, sale del metodo
        if (productoOpt.isEmpty()) return;

        // Busca todas las alertas activas para este producto
        List<Alerta> alertasActivas = alertaRepository.findByProductoAndActivaTrue(productoOpt.get());

        // Comprobar las alertas una por una para ver si se disparan
        for (Alerta alerta : alertasActivas) {

            // Comparacion de precios para enviar notificacion o no
            boolean seDispara = evento.getPrecioActual().compareTo(alerta.getPrecioObjetivo()) <= 0;

            if (seDispara) {

                // LLama al servicio que envia por WebSocket al frontend
                notificacionService.enviarNotificacion(alerta.getUsuario(), evento, alerta.getPrecioObjetivo());
                alerta.setActiva(false);
                alertaRepository.save(alerta);

                // Log test
                log.info("Alerta disparada para usuario {}", alerta.getUsuario().getId());
            }
        }
    }
}