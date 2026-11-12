package com.HuertoHogar.Usuarios.Servicio;

import com.HuertoHogar.Usuarios.Modelo.ModeloUsuario;
import com.HuertoHogar.Usuarios.Repositorio.RepositorioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioUsuario {

    private final RepositorioUsuario repositorioUsuario;

    public List<ModeloUsuario> listarTodos() {
        return repositorioUsuario.findAll();
    }

    public ModeloUsuario buscarPorId(Long id) {
        return repositorioUsuario.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
    }

    @Transactional
    public ModeloUsuario registrar(ModeloUsuario usuario) {
        String emailNorm = usuario.getEmail().toLowerCase().trim();
        if (repositorioUsuario.existsByEmail(emailNorm)) {
            throw new IllegalArgumentException("El correo electronico ya esta registrado.");
        }
        if (usuario.getRut() != null && !usuario.getRut().isBlank() && repositorioUsuario.existsByRut(usuario.getRut())) {
            throw new IllegalArgumentException("El RUT ingresado ya esta registrado.");
        }
        usuario.setEmail(emailNorm);
        if (usuario.getRole() == null || usuario.getRole().isBlank()) {
            usuario.setRole("cliente");
        }
        return repositorioUsuario.save(usuario);
    }

    public ModeloUsuario login(String email, String password) {
        ModeloUsuario usuario = repositorioUsuario.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales invalidas."));
        if (!usuario.getPassword().equals(password)) {
            throw new IllegalArgumentException("Credenciales invalidas.");
        }
        return usuario;
    }

    @Transactional
    public ModeloUsuario actualizar(Long id, ModeloUsuario datos) {
        ModeloUsuario existente = buscarPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setRegion(datos.getRegion());
        existente.setComuna(datos.getComuna());
        if (datos.getRole() != null) {
            existente.setRole(datos.getRole());
        }
        return repositorioUsuario.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        ModeloUsuario existente = buscarPorId(id);
        repositorioUsuario.delete(existente);
    }
}
