package com.HuertoHogar.Productos.Controlador;

import com.HuertoHogar.Productos.Config.DataInitializer;
import com.HuertoHogar.Productos.Modelo.ModeloProducto;
import com.HuertoHogar.Productos.Servicio.ServicioProducto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
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
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ModeloProducto producto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(servicioProducto.crearProducto(producto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<?> actualizar(@PathVariable String codigo, @Valid @RequestBody ModeloProducto producto) {
        try {
            return ResponseEntity.ok(servicioProducto.actualizarProducto(codigo, producto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{codigo}/descontar-stock")
    public ResponseEntity<?> descontarStock(@PathVariable String codigo, @RequestBody Map<String, Integer> body) {
        try {
            Integer cantidad = body.getOrDefault("cantidad", 1);
            return ResponseEntity.ok(servicioProducto.descontarStock(codigo, cantidad));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> eliminar(@PathVariable String codigo) {
        try {
            servicioProducto.eliminarProducto(codigo);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<List<ModeloProducto>> restaurarCatalogo() {
        return ResponseEntity.ok(dataInitializer.restaurarCatalogoOriginal());
    }
}
