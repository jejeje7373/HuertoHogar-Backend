package com.HuertoHogar.Categorias.Servicio;

import com.HuertoHogar.Categorias.Modelo.ModeloCategoria;
import com.HuertoHogar.Categorias.Repositorio.RepositorioCategoria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ServicioCategoria {

    private final RepositorioCategoria repositorioCategoria;

    public List<ModeloCategoria> listarTodas() {
        return repositorioCategoria.findAll();
    }

    public ModeloCategoria buscarPorId(Long id) {
        return repositorioCategoria.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada con ID: " + id));
    }

    @Transactional
    public ModeloCategoria crear(ModeloCategoria categoria) {
        if (repositorioCategoria.existsByNombre(categoria.getNombre())) {
            throw new IllegalArgumentException("La categoría ya existe: " + categoria.getNombre());
        }
        return repositorioCategoria.save(categoria);
    }

    @Transactional
    public ModeloCategoria actualizar(Long id, ModeloCategoria datos) {
        ModeloCategoria existente = buscarPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        return repositorioCategoria.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        ModeloCategoria existente = buscarPorId(id);
        repositorioCategoria.delete(existente);
    }
}
