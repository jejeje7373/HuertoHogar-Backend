package com.HuertoHogar.Ventas.Modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModeloVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String emailCliente;
    private String nombreCliente;
    private String regionDestino;
    private String comunaDestino;
    private LocalDateTime fecha = LocalDateTime.now();

    private Integer subtotal;
    private Integer descuentoPorcentaje = 0;
    private String cuponAplicado;
    private Integer total;
    private String estado = "CONFIRMADA";

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "venta_id")
    private List<ModeloVentaDetalle> detalles;
}
