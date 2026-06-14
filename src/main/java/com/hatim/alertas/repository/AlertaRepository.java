package com.hatim.alertas.repository;

import com.hatim.alertas.model.Alerta;
import com.hatim.alertas.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    List<Alerta> findByProductoAndActivaTrue(Producto producto);
    List<Alerta> findByUsuarioId(Long usuarioId);
}