package com.HuertoHogar.Ventas.Controlador;

import com.HuertoHogar.Ventas.Modelo.ModeloVenta;
import com.HuertoHogar.Ventas.Servicio.ServicioVenta;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class ControladorVenta {

    private final ServicioVenta servicioVenta;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<List<ModeloVenta>> listar() {
        return ResponseEntity.ok(servicioVenta.listarTodas());
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        return ResponseEntity.ok(servicioVenta.obtenerEstadisticasVentas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(servicioVenta.buscarPorId(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping({"/checkout", ""})
    public ResponseEntity<?> crear(@RequestBody ModeloVenta venta,
                                   @RequestHeader(value = "Authorization", required = false) String authHeader,
                                   Authentication authentication) {
        try {
            if (authentication != null && (venta.getEmailCliente() == null || venta.getEmailCliente().isBlank())) {
                venta.setEmailCliente(authentication.getName());
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(servicioVenta.crearVenta(venta, authHeader));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String estado = body.getOrDefault("estado", "CONFIRMADA");
            return ResponseEntity.ok(servicioVenta.actualizarEstadoVenta(id, estado));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            servicioVenta.eliminarVenta(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
