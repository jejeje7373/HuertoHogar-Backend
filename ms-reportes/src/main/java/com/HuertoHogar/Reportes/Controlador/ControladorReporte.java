package com.HuertoHogar.Reportes.Controlador;

import com.HuertoHogar.Reportes.Modelo.ModeloReporte;
import com.HuertoHogar.Reportes.Servicio.ServicioReporte;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ControladorReporte {

    private final ServicioReporte servicioReporte;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> obtenerDashboardGraficos() {
        return ResponseEntity.ok(servicioReporte.obtenerDatosGraficosDashboard());
    }

    @GetMapping
    public ResponseEntity<List<ModeloReporte>> listarReportes() {
        return ResponseEntity.ok(servicioReporte.listarReportes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(servicioReporte.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ModeloReporte> crearReporte(@Valid @RequestBody ModeloReporte solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioReporte.generarYGuardarReporte(solicitud));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarReporte(@PathVariable Long id, @RequestBody ModeloReporte datos) {
        try {
            return ResponseEntity.ok(servicioReporte.actualizarReporte(id, datos));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarReporte(@PathVariable Long id) {
        try {
            servicioReporte.eliminarReporte(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
