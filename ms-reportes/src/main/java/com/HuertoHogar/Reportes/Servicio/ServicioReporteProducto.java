package com.HuertoHogar.Reportes.Servicio;

import com.HuertoHogar.Reportes.Modelo.ModeloReporteProducto;
import com.HuertoHogar.Reportes.Repositorio.RepositorioReporteProducto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ServicioReporteProducto {

    private static final Set<String> ESTADOS_VALIDOS = Set.of("PENDIENTE", "EN_REVISION", "RESUELTO");

    private final RepositorioReporteProducto repositorioReporteProducto;

    @Transactional
    public ModeloReporteProducto crearReporte(ModeloReporteProducto reporte) {
        if (reporte.getReportadoPor() == null || reporte.getReportadoPor().isBlank()) {
            reporte.setReportadoPor("Cliente invitado");
        }
        reporte.setEstado("PENDIENTE");
        reporte.setFechaReporte(LocalDateTime.now());
        return repositorioReporteProducto.save(reporte);
    }

    public List<ModeloReporteProducto> listarReportes() {
        return repositorioReporteProducto.findAllByOrderByFechaReporteDesc();
    }

    @Transactional
    public ModeloReporteProducto actualizarEstado(Long id, String estado) {
        ModeloReporteProducto existente = repositorioReporteProducto.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reporte de producto no encontrado con ID: " + id));
        String normalizado = estado == null ? "" : estado.trim().toUpperCase();
        if (!ESTADOS_VALIDOS.contains(normalizado)) {
            throw new IllegalArgumentException("Estado inválido. Valores permitidos: PENDIENTE, EN_REVISION, RESUELTO.");
        }
        existente.setEstado(normalizado);
        return repositorioReporteProducto.save(existente);
    }

    @Transactional
    public void eliminarReporte(Long id) {
        ModeloReporteProducto existente = repositorioReporteProducto.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reporte de producto no encontrado con ID: " + id));
        repositorioReporteProducto.delete(existente);
    }
}
