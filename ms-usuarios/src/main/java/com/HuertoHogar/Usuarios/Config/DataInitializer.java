package com.HuertoHogar.Usuarios.Config;

import com.HuertoHogar.Usuarios.Modelo.ModeloUsuario;
import com.HuertoHogar.Usuarios.Repositorio.RepositorioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RepositorioUsuario repositorioUsuario;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!repositorioUsuario.existsByEmail("admin@huertohogar.cl")) {
            repositorioUsuario.save(ModeloUsuario.builder()
                    .nombre("Administrador HuertoHogar")
                    .email("admin@huertohogar.cl")
                    .password(passwordEncoder.encode("Admin123*"))
                    .rut("11.111.111-1")
                    .region("Región Metropolitana")
                    .comuna("Santiago")
                    .direccion("Casa Matriz HuertoHogar")
                    .role("ADMIN")
                    .activo(true)
                    .build());
        }
        if (!repositorioUsuario.existsByEmail("cliente@huertohogar.cl")) {
            repositorioUsuario.save(ModeloUsuario.builder()
                    .nombre("Catalina Gómez")
                    .email("cliente@huertohogar.cl")
                    .password(passwordEncoder.encode("Password123*"))
                    .rut("22.222.222-2")
                    .region("Región Metropolitana")
                    .comuna("Santiago")
                    .direccion("Av. Providencia 1234")
                    .role("CLIENTE")
                    .activo(true)
                    .build());
        }
        if (!repositorioUsuario.existsByEmail("vendedor@huertohogar.cl")) {
            repositorioUsuario.save(ModeloUsuario.builder()
                    .nombre("Matías Rojas (Vendedor)")
                    .email("vendedor@huertohogar.cl")
                    .password(passwordEncoder.encode("Vendedor123*"))
                    .rut("33.333.333-3")
                    .region("Región de Valparaíso")
                    .comuna("Quillota")
                    .direccion("Fundo San Isidro Km 4")
                    .role("VENDEDOR")
                    .activo(true)
                    .build());
        }

        // Garantizar que ningun registro existente quede sin hash BCrypt o con rol en minusculas
        List<ModeloUsuario> todos = repositorioUsuario.findAll();
        for (ModeloUsuario u : todos) {
            boolean dirty = false;
            if (u.getPassword() != null && !u.getPassword().startsWith("$2")) {
                u.setPassword(passwordEncoder.encode(u.getPassword()));
                dirty = true;
            }
            String rolNormalizado = ModeloUsuario.normalizarRol(u.getRole());
            if (!rolNormalizado.equals(u.getRole())) {
                u.setRole(rolNormalizado);
                dirty = true;
            }
            if (dirty) {
                repositorioUsuario.save(u);
            }
        }
    }
}
