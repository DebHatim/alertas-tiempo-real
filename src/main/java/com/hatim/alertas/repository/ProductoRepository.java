package com.hatim.alertas.repository;

import com.hatim.alertas.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // No tiene metodos ya que solo usaremos findAll() y findById()
}