package com.HuertoHogar.Ventas.Controlador;

import com.HuertoHogar.Ventas.Modelo.ModeloVenta;
import com.HuertoHogar.Ventas.Servicio.ServicioVenta;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ControladorVenta {

    private final ServicioVenta servicioVenta;

    @GetMapping
    public ResponseEntity<List<ModeloVenta>> listar() {
        return ResponseEntity.ok(servicioVenta.listarTodas());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        return ResponseEntity.ok(servicioVenta.obtenerEstadisticasVentas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(servicioVenta.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping({"/checkout", ""})
    public ResponseEntity<?> crear(@RequestBody ModeloVenta venta) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(servicioVenta.crearVenta(venta));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String estado = body.getOrDefault("estado", "CONFIRMADA");
            return ResponseEntity.ok(servicioVenta.actualizarEstadoVenta(id, estado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            servicioVenta.eliminarVenta(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
