package com.HuertoHogar.Categorias.Config;

import com.HuertoHogar.Categorias.Modelo.ModeloCategoria;
import com.HuertoHogar.Categorias.Repositorio.RepositorioCategoria;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RepositorioCategoria repositorioCategoria;

    @Override
    public void run(String... args) {
        List<String> categorias = List.of("Frutas Frescas", "Verduras Organicas", "Productos Organicos");
        for (String nombre : categorias) {
            if (!repositorioCategoria.existsByNombre(nombre)) {
                repositorioCategoria.save(new ModeloCategoria(null, nombre, "Categoria oficial HuertoHogar"));
            }
        }
    }
}
