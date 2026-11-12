package com.HuertoHogar.Usuarios.Config;

import com.HuertoHogar.Usuarios.Modelo.ModeloUsuario;
import com.HuertoHogar.Usuarios.Repositorio.RepositorioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RepositorioUsuario repositorioUsuario;

    @Override
    public void run(String... args) {
        if (!repositorioUsuario.existsByEmail("admin@huertohogar.cl")) {
            repositorioUsuario.save(ModeloUsuario.builder()
                    .nombre("Administrador HuertoHogar")
                    .email("admin@huertohogar.cl")
                    .password("admin")
                    .rut("11.111.111-1")
                    .region("Region Metropolitana de Santiago")
                    .comuna("Santiago")
                    .role("admin")
                    .activo(true)
                    .build());
        }
        if (!repositorioUsuario.existsByEmail("cliente@huertohogar.cl")) {
            repositorioUsuario.save(ModeloUsuario.builder()
                    .nombre("Cliente HuertoHogar")
                    .email("cliente@huertohogar.cl")
                    .password("cliente123")
                    .rut("22.222.222-2")
                    .region("Region de Valparaiso")
                    .comuna("Vina del Mar")
                    .role("cliente")
                    .activo(true)
                    .build());
        }
    }
}
