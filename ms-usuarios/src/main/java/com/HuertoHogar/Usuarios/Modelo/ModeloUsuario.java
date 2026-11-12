package com.HuertoHogar.Usuarios.Modelo;

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

    @NotBlank(message = "La contrasena es obligatoria")
    private String password;

    private String rut;
    private String region;
    private String comuna;
    private String direccion;

    @Column(nullable = false)
    @Builder.Default
    private String role = "cliente";

    @Builder.Default
    private boolean activo = true;

    public String getRol() {
        return this.role;
    }

    public void setRol(String rol) {
        if (rol != null && !rol.isBlank()) {
            this.role = rol;
        }
    }
}
