package com.HuertoHogar.Categorias.Repositorio;

import com.HuertoHogar.Categorias.Modelo.ModeloCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositorioCategoria extends JpaRepository<ModeloCategoria, Long> {
    Optional<ModeloCategoria> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}
