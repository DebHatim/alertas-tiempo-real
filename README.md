# Sistema de Alertas de Precios en Tiempo Real

[![Java](https://img.shields.io/badge/Java-21-blue)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-7.5-black)](https://kafka.apache.org/)
[![React](https://img.shields.io/badge/React-18-61DAFB)](https://react.dev/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)](https://www.docker.com/)
[![Status](https://img.shields.io/badge/Status-En%20desarrollo%20activo-orange)]()

Plataforma donde los usuarios configuran alertas personalizadas sobre productos y reciben notificaciones en tiempo real cuando el precio baja de su objetivo. Arquitectura orientada a eventos con Apache Kafka como núcleo del sistema.

---

## Arquitectura

```
Simulador de precios (@Scheduled)
        ↓
  Kafka · topic: price-events
        ↓
  Consumidor · evalúa alertas activas
        ↓
  WebSocket · STOMP · notifica al usuario
        ↓
  Dashboard React · notificación en tiempo real
```

## Stack

| Capa | Tecnología |
|------|-----------|
| Backend | Java 21 · Spring Boot 3.5 |
| Mensajería | Apache Kafka · Zookeeper |
| Seguridad | Spring Security 6 · BCrypt |
| Persistencia | JPA/Hibernate · MySQL 8 |
| Tiempo real | WebSocket · STOMP |
| Frontend | React 18 |
| Infraestructura | Docker · Docker Compose |
| Build | Maven · Lombok |

## Funcionalidades

- Registro y autenticación de usuarios con Spring Security
- Gestión de alertas por producto y precio objetivo
- Simulador de cambios de precio con variación aleatoria cada 5 segundos
- Evaluación de alertas en tiempo real mediante consumidor Kafka
- Notificaciones instantáneas al dashboard via WebSocket sin recargar la página
- Historial de notificaciones por usuario

## Decisiones de diseño

**¿Por qué Kafka y no una llamada directa entre servicios?**
El desacoplamiento permite que el simulador y el evaluador evolucionen de forma independiente. Si el evaluador se cae, los eventos se acumulan en Kafka y se procesan cuando vuelve — sin perder ninguno.

**¿Por qué WebSocket y no polling?**
El polling requeriría que el cliente pregunte cada X segundos si hay notificaciones nuevas, generando carga innecesaria. WebSocket mantiene una conexión abierta y el servidor empuja las notificaciones en el momento exacto en que ocurren.

## Instalación local

**Requisitos:** Java 21, Maven, Docker Desktop, MySQL 8

```bash
# 1. Clonar el repositorio
git clone https://github.com/DebHatim/alertas-tiempo-real.git
cd alertas-tiempo-real

# 2. Levantar Kafka con Docker
docker-compose up -d

# 3. Crear base de datos MySQL
# Crear una base de datos llamada 'alertas' en tu cliente MySQL

# 4. Configurar application.properties con tus credenciales MySQL

# 5. Arrancar la aplicación
./mvnw spring-boot:run
```

La aplicación arranca en `http://localhost:8080`

## Autor

**Hatim Debboun** · [LinkedIn](https://linkedin.com/in/hatimdebboun) · [GitHub](https://github.com/DebHatim)
