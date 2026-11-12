package com.HuertoHogar.Productos.Controlador;

import com.HuertoHogar.Productos.Config.DataInitializer;
import com.HuertoHogar.Productos.Modelo.ModeloProducto;
import com.HuertoHogar.Productos.Servicio.ServicioProducto;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ControladorProducto {

    private final ServicioProducto servicioProducto;
    private final DataInitializer dataInitializer;

    @GetMapping
    public ResponseEntity<List<ModeloProducto>> listar(@RequestParam(required = false) String categoria) {
        return ResponseEntity.ok(servicioProducto.listarProductos(categoria));
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<?> obtenerPorCodigo(@PathVariable String codigo) {
        try {
            return ResponseEntity.ok(servicioProducto.buscarPorCodigo(codigo));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<?> crear(@Valid @RequestBody ModeloProducto producto, Authentication authentication) {
        try {
            if (authentication != null && (producto.getVendedorEmail() == null || producto.getVendedorEmail().isBlank())) {
                producto.setVendedorEmail(authentication.getName());
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(servicioProducto.crearProducto(producto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{codigo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<?> actualizar(@PathVariable String codigo, @Valid @RequestBody ModeloProducto producto) {
        try {
            return ResponseEntity.ok(servicioProducto.actualizarProducto(codigo, producto));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{codigo}/visualizacion")
    public ResponseEntity<?> registrarVisualizacion(@PathVariable String codigo) {
        try {
            return ResponseEntity.ok(servicioProducto.registrarVisualizacion(codigo));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{codigo}/descontar-stock")
    public ResponseEntity<?> descontarStock(@PathVariable String codigo, @RequestBody Map<String, Integer> body) {
        try {
            Integer cantidad = body.getOrDefault("cantidad", 1);
            return ResponseEntity.ok(servicioProducto.descontarStock(codigo, cantidad));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminar(@PathVariable String codigo) {
        try {
            servicioProducto.eliminarProducto(codigo);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ModeloProducto>> restaurarCatalogo() {
        return ResponseEntity.ok(dataInitializer.restaurarCatalogoOriginal());
    }
}
