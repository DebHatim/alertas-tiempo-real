package com.hatim.alertas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// Clase de configuracion de Spring
@Configuration
// Activa el soporte de WebSocket con broker de mensajes
// Esto hace que SimpMessagingTemplate esté disponible para inyectar
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${app.cors.allowed-origin:http://localhost:5173}")
    private String allowedOrigins;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefijo para los canales donde el servidor envia mensajes al cliente
        // React se suscribirá a /topic/notificaciones/{usuarioId}
        config.enableSimpleBroker("/topic");

        // Prefijo para los mensajes que el cliente envia al servidor
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint WebSocket al que React se conectara
        // withSockJS() anade fallback para navegadores que no soporten WebSocket
        registry.addEndpoint("/ws").setAllowedOriginPatterns(allowedOrigins).withSockJS();
    }
}