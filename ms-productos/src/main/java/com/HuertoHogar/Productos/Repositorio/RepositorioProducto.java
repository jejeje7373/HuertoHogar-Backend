package com.HuertoHogar.Productos.Repositorio;

import com.HuertoHogar.Productos.Modelo.ModeloProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioProducto extends JpaRepository<ModeloProducto, Long> {
    Optional<ModeloProducto> findByCodigo(String codigo);
    List<ModeloProducto> findByCategoria(String categoria);
    boolean existsByCodigo(String codigo);
}
