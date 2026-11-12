package com.HuertoHogar.Usuarios.Repositorio;

import com.HuertoHogar.Usuarios.Modelo.ModeloUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositorioUsuario extends JpaRepository<ModeloUsuario, Long> {
    Optional<ModeloUsuario> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByRut(String rut);
}
