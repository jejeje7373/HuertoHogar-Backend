package com.HuertoHogar.Productos.Servicio;

import com.HuertoHogar.Productos.Modelo.ModeloProducto;
import com.HuertoHogar.Productos.Repositorio.RepositorioProducto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioProducto {

    private final RepositorioProducto repositorioProducto;

    public List<ModeloProducto> listarProductos(String categoria) {
        if (categoria == null || categoria.isBlank() || categoria.equalsIgnoreCase("Todas")) {
            return repositorioProducto.findAll();
        }
        return repositorioProducto.findByCategoria(categoria);
    }

    public ModeloProducto buscarPorCodigo(String codigo) {
        return repositorioProducto.findByCodigo(codigo.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con codigo: " + codigo));
    }

    @Transactional
    public ModeloProducto crearProducto(ModeloProducto producto) {
        String codigoNorm = producto.getCodigo().trim().toUpperCase();
        if (repositorioProducto.existsByCodigo(codigoNorm)) {
            throw new IllegalArgumentException("Ya existe un producto con el codigo: " + codigoNorm);
        }
        producto.setCodigo(codigoNorm);
        if (producto.getImagen() == null || producto.getImagen().isBlank()) {
            producto.setImagen("/assets/img/manzanas-fuji.jpg");
        }
        return repositorioProducto.save(producto);
    }

    @Transactional
    public ModeloProducto actualizarProducto(String codigo, ModeloProducto datos) {
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
        return repositorioProducto.save(existente);
    }

    @Transactional
    public ModeloProducto descontarStock(String codigo, Integer cantidad) {
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
