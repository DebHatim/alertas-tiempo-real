package com.hatim.alertas.config;

import com.hatim.alertas.dto.PrecioEventoDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    // Direccion donde funciona Kafka
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // Configuracion de los productores de Kafka
    @Bean
    public ProducerFactory<String, PrecioEventoDTO> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        // Establecer como atributo el servidor donde funciona Kafka
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Serializar la key como String - StringSerializer.class
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // Serializar el value como JSON - JsonSerializer.class
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    // KafkaTemplate es el objeto que se va a usar en el servicio para publicar mensajes - usa producerFactory
    public KafkaTemplate<String, PrecioEventoDTO> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // Configuracion de los consumidores de Kafka
    @Bean
    public ConsumerFactory<String, PrecioEventoDTO> consumerFactory() {
        Map<String, Object> config = new HashMap<>();

        // Establecer como atributo el servidor donde funciona Kafka
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Identificador del grupo de consumidores
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "alertas-group");

        // Deserializar la key - sera en String
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // Deserializar el value - sera en JSON
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // Paquetes de confianza para deserializar — seguridad de Kafka
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.hatim.alertas.dto");

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    // Factory que crea los listeners de @KafkaListener necesita el consumerFactory para saber como leer los mensajes
    public ConcurrentKafkaListenerContainerFactory<String, PrecioEventoDTO> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PrecioEventoDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}