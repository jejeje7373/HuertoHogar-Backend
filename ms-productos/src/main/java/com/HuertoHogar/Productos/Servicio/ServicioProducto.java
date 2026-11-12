package com.HuertoHogar.Productos.Servicio;

import com.HuertoHogar.Productos.Modelo.ModeloProducto;
import com.HuertoHogar.Productos.Repositorio.RepositorioProducto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ServicioProducto {

    private final RepositorioProducto repositorioProducto;

    private static final List<String> PALABRAS_QUINTA_REGION = List.of(
            "valparaiso", "vina del mar", "concon", "quintero", "puchuncavi",
            "quillota", "la calera", "calera", "la cruz", "hijuelas", "nogales",
            "limache", "olmue", "quilpue", "villa alemana",
            "casablanca", "san antonio", "cartagena", "el quisco", "el tabo",
            "algarrobo", "santo domingo",
            "san felipe", "los andes", "llay-llay", "llay llay", "llaillay",
            "putaendo", "catemu", "panquehue", "santa maria",
            "calle larga", "rinconada", "san esteban",
            "petorca", "la ligua", "cabildo", "papudo", "zapallar",
            "isla de pascua", "rapa nui", "juan fernandez",
            "aconcagua", "v region", "5 region", "5a region", "quinta region", "region de valparaiso"
    );

    public static boolean esOrigenValidoQuintaRegion(String origen) {
        if (origen == null || origen.isBlank()) return false;
        String norm = Normalizer.normalize(origen, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .replace("ª", "a")
                .trim();
        return PALABRAS_QUINTA_REGION.stream().anyMatch(norm::contains);
    }

    public List<ModeloProducto> listarProductos(String categoria) {
        if (categoria == null || categoria.isBlank() || categoria.equalsIgnoreCase("Todas")) {
            return repositorioProducto.findAll();
        }
        return repositorioProducto.findByCategoria(categoria);
    }

    public ModeloProducto buscarPorCodigo(String codigo) {
        return repositorioProducto.findByCodigo(codigo.toUpperCase())
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con código: " + codigo));
    }

    @Transactional
    public ModeloProducto crearProducto(ModeloProducto producto) {
        if (!esOrigenValidoQuintaRegion(producto.getAtributo())) {
            throw new IllegalArgumentException(
                    "Ubicación inválida: El origen debe pertenecer a la V Región de Valparaíso (ej: Quillota, Limache, Casablanca, San Felipe).");
        }
        String codigoNorm = producto.getCodigo().trim().toUpperCase();
        if (repositorioProducto.existsByCodigo(codigoNorm)) {
            throw new IllegalArgumentException("Ya existe un producto con el código: " + codigoNorm);
        }
        producto.setCodigo(codigoNorm);
        if (producto.getImagen() == null || producto.getImagen().isBlank()) {
            producto.setImagen("assets/img/manzanas.svg");
        }
        if (producto.getVisualizaciones() == null || producto.getVisualizaciones() < 0) {
            producto.setVisualizaciones(0L);
        }
        return repositorioProducto.save(producto);
    }

    @Transactional
    public ModeloProducto actualizarProducto(String codigo, ModeloProducto datos) {
        if (!esOrigenValidoQuintaRegion(datos.getAtributo())) {
            throw new IllegalArgumentException(
                    "Ubicación inválida: El origen debe pertenecer a la V Región de Valparaíso (ej: Quillota, Limache, Casablanca, San Felipe).");
        }
        ModeloProducto existente = buscarPorCodigo(codigo);
        existente.setNombre(datos.getNombre());
        existente.setCategoria(datos.getCategoria());
        existente.setPrecio(datos.getPrecio());
        existente.setUnidad(datos.getUnidad());
        existente.setStock(datos.getStock());
        existente.setAtributo(datos.getAtributo());
        existente.setDescripcion(datos.getDescripcion());
        if (datos.getImagen() != null && !datos.getImagen().isBlank()) {
            existente.setImagen(datos.getImagen());
        }
        if (datos.getVisualizaciones() != null && datos.getVisualizaciones() >= 0) {
            existente.setVisualizaciones(datos.getVisualizaciones());
        }
        return repositorioProducto.save(existente);
    }

    @Transactional
    public ModeloProducto registrarVisualizacion(String codigo) {
        ModeloProducto existente = buscarPorCodigo(codigo);
        long actuales = existente.getVisualizaciones() != null ? existente.getVisualizaciones() : 0L;
        existente.setVisualizaciones(actuales + 1L);
        return repositorioProducto.save(existente);
    }

    @Transactional
    public ModeloProducto descontarStock(String codigo, Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a descontar debe ser mayor a cero.");
        }
        ModeloProducto existente = buscarPorCodigo(codigo);
        if (existente.getStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente para " + existente.getNombre());
        }
        existente.setStock(existente.getStock() - cantidad);
        return repositorioProducto.save(existente);
    }

    @Transactional
    public void eliminarProducto(String codigo) {
        ModeloProducto existente = buscarPorCodigo(codigo);
        repositorioProducto.delete(existente);
    }
}
