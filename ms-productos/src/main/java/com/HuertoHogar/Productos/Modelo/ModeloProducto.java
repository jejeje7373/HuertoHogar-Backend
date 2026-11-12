package com.HuertoHogar.Productos.Modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "catalogo_productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModeloProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El codigo del producto es obligatorio")
    @Column(unique = true, nullable = false, length = 20)
    private String codigo;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "La categoria es obligatoria")
    @Column(nullable = false)
    private String categoria;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    private Integer precio;

    @NotBlank(message = "La unidad de medida es obligatoria")
    private String unidad;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    private String atributo;

    @Column(length = 1000)
    private String descripcion;

    private String imagen;

    @Column(nullable = false)
    @Builder.Default
    private Long visualizaciones = 0L;

    private String vendedorEmail;
}
