package com.HuertoHogar.Reportes.Controlador;

import com.HuertoHogar.Reportes.Modelo.ModeloReporte;
import com.HuertoHogar.Reportes.Modelo.ModeloReporteProducto;
import com.HuertoHogar.Reportes.Servicio.ServicioReporte;
import com.HuertoHogar.Reportes.Servicio.ServicioReporteProducto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ControladorReporte {

    private final ServicioReporte servicioReporte;
    private final ServicioReporteProducto servicioReporteProducto;

    @PostMapping("/productos")
    public ResponseEntity<ModeloReporteProducto> reportarProductoMalEstado(
            @Valid @RequestBody ModeloReporteProducto solicitud,
            Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && (solicitud.getReportadoPor() == null || solicitud.getReportadoPor().isBlank())) {
            solicitud.setReportadoPor(authentication.getName());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioReporteProducto.crearReporte(solicitud));
    }

    @GetMapping("/productos")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<List<ModeloReporteProducto>> listarReportesProductos() {
        return ResponseEntity.ok(servicioReporteProducto.listarReportes());
    }

    @PatchMapping("/productos/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarEstadoReporteProducto(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(servicioReporteProducto.actualizarEstado(id, body.get("estado")));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/productos/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarReporteProducto(@PathVariable Long id) {
        try {
            servicioReporteProducto.eliminarReporte(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<Map<String, Object>> obtenerDashboardGraficos(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(servicioReporte.obtenerDatosGraficosDashboard(authHeader));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<List<ModeloReporte>> listarReportes() {
        return ResponseEntity.ok(servicioReporte.listarReportes());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(servicioReporte.buscarPorId(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<ModeloReporte> crearReporte(
            @Valid @RequestBody ModeloReporte solicitud,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioReporte.generarYGuardarReporte(solicitud, authHeader));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarReporte(@PathVariable Long id, @RequestBody ModeloReporte datos) {
        try {
            return ResponseEntity.ok(servicioReporte.actualizarReporte(id, datos));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarReporte(@PathVariable Long id) {
        try {
            servicioReporte.eliminarReporte(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
