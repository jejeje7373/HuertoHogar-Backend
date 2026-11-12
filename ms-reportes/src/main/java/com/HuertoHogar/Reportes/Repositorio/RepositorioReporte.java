package com.HuertoHogar.Reportes.Repositorio;

import com.HuertoHogar.Reportes.Modelo.ModeloReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositorioReporte extends JpaRepository<ModeloReporte, Long> {
    List<ModeloReporte> findAllByOrderByFechaGeneracionDesc();
}
