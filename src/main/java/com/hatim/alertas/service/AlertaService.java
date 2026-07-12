package com.hatim.alertas.service;

import com.hatim.alertas.dto.AlertaDTO;
import com.hatim.alertas.exception.ResourceNotFoundException;
import com.hatim.alertas.model.Alerta;
import com.hatim.alertas.model.Producto;
import com.hatim.alertas.model.Usuario;
import com.hatim.alertas.repository.AlertaRepository;
import com.hatim.alertas.repository.ProductoRepository;
import com.hatim.alertas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertaService {

    // Inyeccion de dependencias
    private final AlertaRepository alertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public AlertaDTO crearAlerta(AlertaDTO dto) {

        // Comprobar que el usuario existe. si no existe, lanzar excepcion
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId()).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Comprobar que el producto existe. si no existe, lanzar excepcion
        Producto producto = productoRepository.findById(dto.getProductoId()).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        List<Alerta> alertasActivas = alertaRepository.findByProductoAndActivaTrue(producto);
        if (!alertasActivas.isEmpty()) {
            throw new IllegalStateException("Ya tienes una alerta activa para este producto");
        }

        // Crear entidad alerta con los datos recibidos
        Alerta alerta = new Alerta();
        alerta.setUsuario(usuario);
        alerta.setProducto(producto);
        alerta.setPrecioObjetivo(dto.getPrecioObjetivo());
        alerta.setActiva(true); // Empieza la alerta estando activa

        // Guardar en la BD y devolver la entidad con el id generado
        Alerta guardada = alertaRepository.save(alerta);

        // Convertir entidad a DTO para devolver, alternativa a new AlertaDTO
        return toDTO(guardada);
    }

    public List<AlertaDTO> obtenerAlertasUsuario(Long usuarioId) {
        return alertaRepository.findByUsuarioId(usuarioId)
                .stream() // Convertir la lista en stream para procesar
                .map(this::toDTO) // Convertir cada Alerta en AlertaDTO
                .collect(Collectors.toList()); // Guardar resultado en una lista
    }

    public void desactivarAlerta(Long alertaId, Long autenticadoId) {
        // Busca si la alerta existe. si no existe, lanza excepcion
        Alerta alerta = alertaRepository.findById(alertaId).orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada"));

        if (!alerta.getUsuario().getId().equals(autenticadoId)) {
            throw new AccessDeniedException("No tienes permisos para modificar esta alerta");
        }
        // Establecer estado de la alerta a desactivada
        alerta.setActiva(!alerta.getActiva());
        alertaRepository.save(alerta);
    }

    public void eliminarAlerta(Long alertaId, Long autenticadoId) {
        Alerta alerta = alertaRepository.findById(alertaId).orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada"));

        if (!alerta.getUsuario().getId().equals(autenticadoId)) {
            throw new AccessDeniedException("No tienes permisos para eliminar esta alerta");
        }
        alertaRepository.deleteById(alertaId);
    }

    // Metodo para convertir a DTO y asi ahorrar lineas de codigo
    private AlertaDTO toDTO(Alerta alerta) {
        AlertaDTO dto = new AlertaDTO();
        dto.setId(alerta.getId());
        dto.setUsuarioId(alerta.getUsuario().getId());
        dto.setProductoId(alerta.getProducto().getId());
        dto.setProductoNombre(alerta.getProducto().getNombre());
        dto.setPrecioObjetivo(alerta.getPrecioObjetivo());
        dto.setActiva(alerta.getActiva());
        return dto;
    }
}