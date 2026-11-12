package com.HuertoHogar.Ventas.Repositorio;

import com.HuertoHogar.Ventas.Modelo.ModeloVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositorioVenta extends JpaRepository<ModeloVenta, Long> {
    List<ModeloVenta> findByEmailClienteOrderByFechaDesc(String emailCliente);
}
