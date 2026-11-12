package com.HuertoHogar.Reportes.Servicio;

import com.HuertoHogar.Reportes.Modelo.ModeloReporte;
import com.HuertoHogar.Reportes.Repositorio.RepositorioReporte;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ServicioReporte {

    private final RepositorioReporte repositorioReporte;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ms.ventas.url:http://localhost:8084/api/orders}")
    private String msVentasUrl;

    @Value("${ms.productos.url:http://localhost:8083/api/products}")
    private String msProductosUrl;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Map<String, Object> obtenerDatosGraficosDashboard(String authHeader) {
        List<Map<String, Object>> ordenes = new ArrayList<>();
        List<Map<String, Object>> productos = new ArrayList<>();

        try {
            HttpHeaders headers = new HttpHeaders();
            if (authHeader != null && !authHeader.isBlank()) {
                headers.set("Authorization", authHeader);
            }
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map[]> respOrdenes = restTemplate.exchange(msVentasUrl, HttpMethod.GET, entity, Map[].class);
            if (respOrdenes.getBody() != null) {
                ordenes = Arrays.asList(respOrdenes.getBody());
            }
        } catch (Exception ignored) {
        }

        try {
            Map<String, Object>[] respProductos = restTemplate.getForObject(msProductosUrl, Map[].class);
            if (respProductos != null) {
                productos = Arrays.asList(respProductos);
            }
        } catch (Exception ignored) {
        }

        Map<String, String> categoriaPorCodigo = new HashMap<>();
        Map<String, Integer> stockActualPorCodigo = new HashMap<>();
        long totalVisualizaciones = 0L;
        String productoMasVisto = "Sin visualizaciones";
        long maxVistas = -1L;

        for (Map<String, Object> prod : productos) {
            String cod = (String) prod.get("codigo");
            categoriaPorCodigo.put(cod, (String) prod.getOrDefault("categoria", "Sin Categoría"));
            Object stockObj = prod.get("stock");
            stockActualPorCodigo.put(cod, stockObj instanceof Number n ? n.intValue() : 0);

            Object visObj = prod.get("visualizaciones");
            long vistas = visObj instanceof Number n ? n.longValue() : 0L;
            totalVisualizaciones += vistas;
            if (vistas > maxVistas) {
                maxVistas = vistas;
                productoMasVisto = prod.getOrDefault("nombre", cod) + " (" + vistas + " vistas)";
            }
        }

        int totalIngresos = 0;
        int totalUnidadesVendidas = 0;
        Map<String, Map<String, Object>> ventasPorProducto = new LinkedHashMap<>();
        Map<String, Integer> ingresosPorCategoria = new LinkedHashMap<>();
        Map<String, Integer> ingresosPorFecha = new TreeMap<>();

        for (Map<String, Object> orden : ordenes) {
            Integer totalOrden = (Integer) orden.getOrDefault("total", 0);
            totalIngresos += totalOrden;

            String fechaRaw = String.valueOf(orden.getOrDefault("fecha", LocalDate.now().toString()));
            String fechaDia = fechaRaw.length() >= 10 ? fechaRaw.substring(0, 10) : fechaRaw;
            ingresosPorFecha.put(fechaDia, ingresosPorFecha.getOrDefault(fechaDia, 0) + totalOrden);

            List<Map<String, Object>> detalles = (List<Map<String, Object>>) orden.get("detalles");
            if (detalles != null) {
                for (Map<String, Object> det : detalles) {
                    String codigo = (String) det.get("codigoProducto");
                    String nombre = (String) det.getOrDefault("nombreProducto", codigo);
                    Integer cantidad = (Integer) det.getOrDefault("cantidad", 0);
                    Integer precio = (Integer) det.getOrDefault("precioUnitario", 0);
                    int subtotalItem = cantidad * precio;

                    totalUnidadesVendidas += cantidad;

                    String categoria = categoriaPorCodigo.getOrDefault(codigo, "Otros");
                    ingresosPorCategoria.put(categoria, ingresosPorCategoria.getOrDefault(categoria, 0) + subtotalItem);

                    Map<String, Object> prodStat = ventasPorProducto.computeIfAbsent(codigo, k -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("codigo", codigo);
                        m.put("nombre", nombre);
                        m.put("categoria", categoria);
                        m.put("cantidadVendida", 0);
                        m.put("ingresos", 0);
                        m.put("stockActual", stockActualPorCodigo.getOrDefault(codigo, 0));
                        return m;
                    });
                    prodStat.put("cantidadVendida", (Integer) prodStat.get("cantidadVendida") + cantidad);
                    prodStat.put("ingresos", (Integer) prodStat.get("ingresos") + subtotalItem);
                }
            }
        }

        List<Map<String, Object>> detalleProductos = new ArrayList<>();
        for (Map<String, Object> prod : productos) {
            String cod = (String) prod.get("codigo");
            Map<String, Object> ventaInfo = ventasPorProducto.getOrDefault(cod, Map.of());
            int cantVendida = ventaInfo.get("cantidadVendida") instanceof Number n ? n.intValue() : 0;
            int ingresosProd = ventaInfo.get("ingresos") instanceof Number n ? n.intValue() : 0;
            long vistas = prod.get("visualizaciones") instanceof Number n ? n.longValue() : 0L;
            double conversion = vistas > 0 ? Math.round((cantVendida * 10000.0) / vistas) / 100.0 : 0.0;

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("codigo", cod);
            row.put("nombre", prod.getOrDefault("nombre", cod));
            row.put("categoria", prod.getOrDefault("categoria", "Sin Categoría"));
            row.put("origen", prod.getOrDefault("atributo", "V Región"));
            row.put("precio", prod.getOrDefault("precio", 0));
            row.put("stockActual", prod.getOrDefault("stock", 0));
            row.put("visualizaciones", vistas);
            row.put("cantidadVendida", cantVendida);
            row.put("ingresos", ingresosProd);
            row.put("tasaConversion", conversion);
            detalleProductos.add(row);
        }

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("totalIngresos", totalIngresos);
        dashboard.put("totalOrdenes", ordenes.size());
        dashboard.put("totalUnidadesVendidas", totalUnidadesVendidas);
        dashboard.put("totalVisualizaciones", totalVisualizaciones);
        dashboard.put("productoMasVisto", productoMasVisto);
        dashboard.put("ventasPorProducto", new ArrayList<>(ventasPorProducto.values()));
        dashboard.put("detalleProductos", detalleProductos);
        dashboard.put("ingresosPorCategoria", ingresosPorCategoria);
        dashboard.put("ingresosPorFecha", ingresosPorFecha);
        return dashboard;
    }

    public List<ModeloReporte> listarReportes() {
        return repositorioReporte.findAllByOrderByFechaGeneracionDesc();
    }

    public ModeloReporte buscarPorId(Long id) {
        return repositorioReporte.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reporte no encontrado con ID: " + id));
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public ModeloReporte generarYGuardarReporte(ModeloReporte solicitud, String authHeader) {
        Map<String, Object> stats = obtenerDatosGraficosDashboard(authHeader);
        List<Map<String, Object>> porProducto = (List<Map<String, Object>>) stats.get("ventasPorProducto");

        String topProducto = "Sin ventas registradas";
        int maxUnidades = -1;
        if (porProducto != null) {
            for (Map<String, Object> p : porProducto) {
                Integer cant = (Integer) p.getOrDefault("cantidadVendida", 0);
                if (cant > maxUnidades) {
                    maxUnidades = cant;
                    topProducto = p.get("nombre") + " (" + cant + " un.)";
                }
            }
        }

        ModeloReporte nuevo = ModeloReporte.builder()
                .titulo(solicitud.getTitulo() != null ? solicitud.getTitulo() : "Cierre de Ventas HuertoHogar")
                .periodo(solicitud.getPeriodo() != null ? solicitud.getPeriodo() : LocalDate.now().toString())
                .fechaGeneracion(LocalDateTime.now())
                .totalIngresos((Integer) stats.getOrDefault("totalIngresos", 0))
                .totalOrdenes((Integer) stats.getOrDefault("totalOrdenes", 0))
                .totalUnidadesVendidas((Integer) stats.getOrDefault("totalUnidadesVendidas", 0))
                .productoMasVendido(topProducto)
                .observaciones(solicitud.getObservaciones())
                .build();

        return repositorioReporte.save(nuevo);
    }

    @Transactional
    public ModeloReporte actualizarReporte(Long id, ModeloReporte datos) {
        ModeloReporte existente = buscarPorId(id);
        existente.setTitulo(datos.getTitulo());
        existente.setPeriodo(datos.getPeriodo());
        existente.setObservaciones(datos.getObservaciones());
        return repositorioReporte.save(existente);
    }

    @Transactional
    public void eliminarReporte(Long id) {
        ModeloReporte existente = buscarPorId(id);
        repositorioReporte.delete(existente);
    }
}
