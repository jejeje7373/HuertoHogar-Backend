package com.HuertoHogar.Usuarios.Servicio;

import com.HuertoHogar.Usuarios.Modelo.ModeloUsuario;
import com.HuertoHogar.Usuarios.Repositorio.RepositorioUsuario;
import com.HuertoHogar.Usuarios.Security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ServicioUsuario {

    private final RepositorioUsuario repositorioUsuario;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public List<ModeloUsuario> listarTodos() {
        return repositorioUsuario.findAll();
    }

    public ModeloUsuario buscarPorId(Long id) {
        return repositorioUsuario.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con ID: " + id));
    }

    public ModeloUsuario buscarPorEmail(String email) {
        return repositorioUsuario.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con email: " + email));
    }

    @Transactional
    public Map<String, Object> registrar(ModeloUsuario usuario) {
        if (usuario.getPassword() == null || usuario.getPassword().trim().length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
        String emailNorm = usuario.getEmail().toLowerCase().trim();
        if (repositorioUsuario.existsByEmail(emailNorm)) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        }
        if (usuario.getRut() != null && !usuario.getRut().isBlank() && repositorioUsuario.existsByRut(usuario.getRut().trim())) {
            throw new IllegalArgumentException("El RUT ingresado ya está registrado.");
        }

        usuario.setEmail(emailNorm);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        // El registro público solo permite CLIENTE o VENDEDOR (nunca ADMIN)
        String rolSolicitado = ModeloUsuario.normalizarRol(usuario.getRole());
        usuario.setRole(rolSolicitado.equals("VENDEDOR") ? "VENDEDOR" : "CLIENTE");
        usuario.setActivo(true);

        ModeloUsuario guardado = repositorioUsuario.save(usuario);
        String token = jwtUtil.generarToken(guardado.getId(), guardado.getEmail(), guardado.getNombre(), guardado.getRole());
        return construirRespuestaAuth(token, guardado);
    }

    @Transactional
    public ModeloUsuario crearPorAdmin(ModeloUsuario usuario) {
        if (usuario.getPassword() == null || usuario.getPassword().trim().length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
        String emailNorm = usuario.getEmail().toLowerCase().trim();
        if (repositorioUsuario.existsByEmail(emailNorm)) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        }
        if (usuario.getRut() != null && !usuario.getRut().isBlank() && repositorioUsuario.existsByRut(usuario.getRut().trim())) {
            throw new IllegalArgumentException("El RUT ingresado ya está registrado.");
        }

        usuario.setEmail(emailNorm);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRole(ModeloUsuario.normalizarRol(usuario.getRole()));
        if (usuario.getActivo() == null) {
            usuario.setActivo(true);
        }
        return repositorioUsuario.save(usuario);
    }

    public Map<String, Object> login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Debes ingresar correo y contraseña.");
        }
        String emailNorm = email.toLowerCase().trim();
        ModeloUsuario usuario = repositorioUsuario.findByEmail(emailNorm)
                .orElseThrow(() -> new SecurityException("Credenciales inválidas. Verifica tu correo y contraseña."));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new SecurityException("Credenciales inválidas. Verifica tu correo y contraseña.");
        }

        if (!usuario.isActivo()) {
            throw new IllegalStateException("Tu cuenta está desactivada. Contacta al administrador.");
        }

        String token = jwtUtil.generarToken(usuario.getId(), usuario.getEmail(), usuario.getNombre(), usuario.getRole());
        return construirRespuestaAuth(token, usuario);
    }

    @Transactional
    public ModeloUsuario actualizar(Long id, ModeloUsuario datos) {
        ModeloUsuario existente = buscarPorId(id);

        if (datos.getNombre() != null && !datos.getNombre().isBlank()) {
            existente.setNombre(datos.getNombre().trim());
        }
        if (datos.getEmail() != null && !datos.getEmail().isBlank()) {
            String nuevoEmail = datos.getEmail().toLowerCase().trim();
            if (!nuevoEmail.equals(existente.getEmail()) && repositorioUsuario.existsByEmail(nuevoEmail)) {
                throw new IllegalArgumentException("El correo electrónico ya está registrado por otro usuario.");
            }
            existente.setEmail(nuevoEmail);
        }
        if (datos.getPassword() != null && !datos.getPassword().isBlank()) {
            if (datos.getPassword().trim().length() < 6) {
                throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
            }
            existente.setPassword(passwordEncoder.encode(datos.getPassword()));
        }
        if (datos.getRut() != null) {
            existente.setRut(datos.getRut().trim());
        }
        if (datos.getRegion() != null) {
            existente.setRegion(datos.getRegion());
        }
        if (datos.getComuna() != null) {
            existente.setComuna(datos.getComuna());
        }
        if (datos.getDireccion() != null) {
            existente.setDireccion(datos.getDireccion());
        }
        if (datos.getRole() != null && !datos.getRole().isBlank()) {
            String rolActual = ModeloUsuario.normalizarRol(existente.getRole());
            String nuevoRol = ModeloUsuario.normalizarRol(datos.getRole());
            if ("VENDEDOR".equals(rolActual) && "ADMIN".equals(nuevoRol)) {
                throw new IllegalArgumentException("No se puede otorgar permiso de Administrador a un usuario Vendedor.");
            }
            existente.setRole(nuevoRol);
        }
        if (datos.getActivo() != null) {
            existente.setActivo(datos.getActivo());
        }

        return repositorioUsuario.save(existente);
    }

    @Transactional
    public ModeloUsuario cambiarEstado(Long id, boolean activo) {
        ModeloUsuario existente = buscarPorId(id);
        existente.setActivo(activo);
        return repositorioUsuario.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        ModeloUsuario existente = buscarPorId(id);
        repositorioUsuario.delete(existente);
    }

    private Map<String, Object> construirRespuestaAuth(String token, ModeloUsuario usuario) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("token", token);
        response.put("type", "Bearer");
        response.put("user", usuario);
        return response;
    }
}
