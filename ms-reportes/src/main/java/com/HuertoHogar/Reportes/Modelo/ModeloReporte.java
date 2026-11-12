package com.HuertoHogar.Reportes.Modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reportes_ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModeloReporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El titulo del reporte es obligatorio")
    private String titulo;

    private String periodo;
    private LocalDateTime fechaGeneracion = LocalDateTime.now();

    private Integer totalIngresos;
    private Integer totalOrdenes;
    private Integer totalUnidadesVendidas;
    private String productoMasVendido;
    private String observaciones;
}
