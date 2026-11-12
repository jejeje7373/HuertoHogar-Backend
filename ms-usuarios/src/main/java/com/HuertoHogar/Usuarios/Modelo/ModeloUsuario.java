package com.HuertoHogar.Usuarios.Modelo;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModeloUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Email(message = "El formato del correo no es valido")
    @NotBlank(message = "El email es obligatorio")
    @Column(unique = true, nullable = false)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String rut;
    private String region;
    private String comuna;
    private String direccion;

    @Column(nullable = false)
    @Builder.Default
    private String role = "CLIENTE";

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    public String getRol() {
        return normalizarRol(this.role);
    }

    public void setRol(String rol) {
        if (rol != null && !rol.isBlank()) {
            this.role = normalizarRol(rol);
        }
    }

    public String getRole() {
        return normalizarRol(this.role);
    }

    public void setRole(String role) {
        if (role != null && !role.isBlank()) {
            this.role = normalizarRol(role);
        }
    }

    public boolean isActivo() {
        return this.activo == null || this.activo;
    }

    public static String normalizarRol(String r) {
        if (r == null || r.isBlank()) return "CLIENTE";
        String upper = r.trim().toUpperCase();
        if (upper.startsWith("ROLE_")) {
            upper = upper.substring(5);
        }
        if (upper.equals("ADMIN")) return "ADMIN";
        if (upper.equals("VENDEDOR")) return "VENDEDOR";
        return "CLIENTE";
    }
}
