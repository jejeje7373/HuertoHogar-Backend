package com.HuertoHogar.Reportes.Modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reportes_productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModeloReporteProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El codigo del producto es obligatorio")
    private String codigoProducto;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombreProducto;

    @NotBlank(message = "El motivo del reporte es obligatorio")
    private String motivo;

    @Column(length = 1000)
    private String descripcion;

    private String reportadoPor;

    @Column(nullable = false)
    @Builder.Default
    private String estado = "PENDIENTE";

    @Builder.Default
    private LocalDateTime fechaReporte = LocalDateTime.now();
}
