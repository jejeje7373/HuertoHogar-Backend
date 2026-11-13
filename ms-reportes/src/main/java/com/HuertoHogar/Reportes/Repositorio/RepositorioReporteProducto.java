package com.HuertoHogar.Reportes.Repositorio;

import com.HuertoHogar.Reportes.Modelo.ModeloReporteProducto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepositorioReporteProducto extends JpaRepository<ModeloReporteProducto, Long> {
    List<ModeloReporteProducto> findAllByOrderByFechaReporteDesc();
}
