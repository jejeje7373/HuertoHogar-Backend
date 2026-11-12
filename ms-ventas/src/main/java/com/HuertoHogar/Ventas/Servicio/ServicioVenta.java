package com.HuertoHogar.Ventas.Servicio;

import com.HuertoHogar.Ventas.Modelo.ModeloVenta;
import com.HuertoHogar.Ventas.Modelo.ModeloVentaDetalle;
import com.HuertoHogar.Ventas.Repositorio.RepositorioVenta;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ServicioVenta {

    private final RepositorioVenta repositorioVenta;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ms.productos.url:http://localhost:8083/api/products}")
    private String msProductosUrl;

    public List<ModeloVenta> listarTodas() {
        return repositorioVenta.findAll();
    }

    public ModeloVenta buscarPorId(Long id) {
        return repositorioVenta.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada con ID: " + id));
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public ModeloVenta crearVenta(ModeloVenta venta) {
        int subtotal = 0;
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La orden debe incluir al menos un producto.");
        }

        for (ModeloVentaDetalle detalle : venta.getDetalles()) {
            String urlDescuento = msProductosUrl + "/" + detalle.getCodigoProducto() + "/descontar-stock";
            restTemplate.put(urlDescuento, Map.of("cantidad", detalle.getCantidad()));

            String urlProducto = msProductosUrl + "/" + detalle.getCodigoProducto();
            Map<String, Object> prod = restTemplate.getForObject(urlProducto, Map.class);
            if (prod != null) {
                Integer precio = (Integer) prod.get("precio");
                detalle.setNombreProducto((String) prod.get("nombre"));
                detalle.setPrecioUnitario(precio);
                detalle.setUnidad((String) prod.get("unidad"));
                subtotal += precio * detalle.getCantidad();
            }
        }

        int descPorcentaje = venta.getDescuentoPorcentaje() != null ? venta.getDescuentoPorcentaje() : 0;
        int descuentoMonto = Math.round(subtotal * (descPorcentaje / 100.0f));
        int costoEnvio = (subtotal >= 20000 || subtotal == 0) ? 0 : 2500;
        venta.setSubtotal(subtotal);
        venta.setTotal(Math.max(0, subtotal - descuentoMonto + costoEnvio));
        venta.setFecha(LocalDateTime.now());
        if (venta.getEstado() == null || venta.getEstado().isBlank()) {
            venta.setEstado("CONFIRMADA");
        }

        return repositorioVenta.save(venta);
    }

    @Transactional
    public ModeloVenta actualizarEstadoVenta(Long id, String nuevoEstado) {
        ModeloVenta existente = buscarPorId(id);
        existente.setEstado(nuevoEstado);
        return repositorioVenta.save(existente);
    }

    @Transactional
    public void eliminarVenta(Long id) {
        ModeloVenta existente = buscarPorId(id);
        repositorioVenta.delete(existente);
    }

    public Map<String, Object> obtenerEstadisticasVentas() {
        List<ModeloVenta> ventas = repositorioVenta.findAll();
        int totalIngresos = 0;
        int totalUnidades = 0;
        Map<String, Map<String, Object>> porProducto = new LinkedHashMap<>();
        Map<String, Integer> ingresosPorFecha = new TreeMap<>();

        for (ModeloVenta v : ventas) {
            int montoVenta = v.getTotal() != null ? v.getTotal() : 0;
            totalIngresos += montoVenta;

            String fechaDia = v.getFecha() != null
                    ? v.getFecha().toLocalDate().toString()
                    : LocalDate.now().toString();
            ingresosPorFecha.put(fechaDia, ingresosPorFecha.getOrDefault(fechaDia, 0) + montoVenta);

            if (v.getDetalles() != null) {
                for (ModeloVentaDetalle det : v.getDetalles()) {
                    int cant = det.getCantidad() != null ? det.getCantidad() : 0;
                    int precio = det.getPrecioUnitario() != null ? det.getPrecioUnitario() : 0;
                    totalUnidades += cant;

                    String codigo = det.getCodigoProducto();
                    Map<String, Object> itemStat = porProducto.computeIfAbsent(codigo, k -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("codigo", codigo);
                        m.put("nombre", det.getNombreProducto());
                        m.put("cantidadVendida", 0);
                        m.put("ingresos", 0);
                        return m;
                    });
                    itemStat.put("cantidadVendida", (Integer) itemStat.get("cantidadVendida") + cant);
                    itemStat.put("ingresos", (Integer) itemStat.get("ingresos") + (cant * precio));
                }
            }
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("totalIngresos", totalIngresos);
        resultado.put("totalOrdenes", ventas.size());
        resultado.put("totalUnidadesVendidas", totalUnidades);
        resultado.put("ventasPorProducto", new ArrayList<>(porProducto.values()));
        resultado.put("ingresosPorFecha", ingresosPorFecha);
        return resultado;
    }
}
