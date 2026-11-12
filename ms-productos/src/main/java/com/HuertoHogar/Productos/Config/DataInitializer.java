package com.HuertoHogar.Productos.Config;

import com.HuertoHogar.Productos.Modelo.ModeloProducto;
import com.HuertoHogar.Productos.Repositorio.RepositorioProducto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RepositorioProducto repositorioProducto;

    @Override
    public void run(String... args) {
        if (repositorioProducto.count() == 0) {
            cargarProductosPorDefecto();
        }
    }

    @Transactional
    public List<ModeloProducto> restaurarCatalogoOriginal() {
        repositorioProducto.deleteAll();
        return cargarProductosPorDefecto();
    }

    private List<ModeloProducto> cargarProductosPorDefecto() {
        List<ModeloProducto> iniciales = List.of(
            ModeloProducto.builder()
                .codigo("FR001").nombre("Manzanas Fuji").categoria("Frutas Frescas")
                .precio(1200).unidad("kilo").stock(150).atributo("Valle de Aconcagua, San Felipe")
                .descripcion("Manzanas Fuji crujientes y dulces, cultivadas en el Valle de Aconcagua (V Región). Perfectas para meriendas saludables o repostería natural.")
                .imagen("assets/img/manzanas.svg").visualizaciones(420L).vendedorEmail("vendedor@huertohogar.cl").build(),
            ModeloProducto.builder()
                .codigo("FR002").nombre("Naranjas Valencia").categoria("Frutas Frescas")
                .precio(1000).unidad("kilo").stock(200).atributo("Hijuelas, V Región")
                .descripcion("Jugosas y aromáticas, estas naranjas Valencia cosechadas en Hijuelas son ideales para zumos frescos matutinos.")
                .imagen("assets/img/naranjas.svg").visualizaciones(315L).vendedorEmail("vendedor@huertohogar.cl").build(),
            ModeloProducto.builder()
                .codigo("FR003").nombre("Plátanos Cavendish").categoria("Frutas Frescas")
                .precio(800).unidad("kilo").stock(250).atributo("Isla de Pascua, V Región")
                .descripcion("Plátanos maduros y dulces de la V Región, perfectos para el desayuno, batidos o como snack energético para deportistas y niños.")
                .imagen("assets/img/platanos.svg").visualizaciones(198L).vendedorEmail("vendedor@huertohogar.cl").build(),
            ModeloProducto.builder()
                .codigo("VR001").nombre("Zanahorias Orgánicas").categoria("Verduras Orgánicas")
                .precio(900).unidad("kilo").stock(100).atributo("Limache, V Región")
                .descripcion("Zanahorias crujientes cultivadas sin pesticidas en los fértiles suelos de Limache. Excelente fuente de betacarotenos y fibra.")
                .imagen("assets/img/zanahorias.svg").visualizaciones(276L).vendedorEmail("vendedor@huertohogar.cl").build(),
            ModeloProducto.builder()
                .codigo("VR002").nombre("Espinacas Frescas").categoria("Verduras Orgánicas")
                .precio(700).unidad("bolsa 500g").stock(80).atributo("Olmué, V Región")
                .descripcion("Hojas tiernas y nutritivas de espinaca recién cosechada en Olmué. Ideales para ensaladas crudas, salteados o batidos verdes détox.")
                .imagen("assets/img/espinacas.svg").visualizaciones(184L).vendedorEmail("vendedor@huertohogar.cl").build(),
            ModeloProducto.builder()
                .codigo("VR003").nombre("Pimientos Tricolores").categoria("Verduras Orgánicas")
                .precio(1500).unidad("malla 3 un.").stock(60).atributo("Quillota, V Región")
                .descripcion("Trío de pimientos rojo, amarillo y verde cosechados en Quillota. Aportan color, textura crocante y alto contenido de antioxidantes.")
                .imagen("assets/img/pimientos.svg").visualizaciones(249L).vendedorEmail("vendedor@huertohogar.cl").build(),
            ModeloProducto.builder()
                .codigo("PO001").nombre("Miel Orgánica").categoria("Productos Orgánicos")
                .precio(5000).unidad("frasco 500g").stock(45).atributo("Casablanca, V Región")
                .descripcion("Miel pura de abeja cosechada en apiarios sustentables del valle de Casablanca (V Región). 100% natural, sin aditivos ni azúcares añadidos.")
                .imagen("assets/img/miel.svg").visualizaciones(512L).vendedorEmail("vendedor@huertohogar.cl").build(),
            ModeloProducto.builder()
                .codigo("PO003").nombre("Quinua Orgánica").categoria("Productos Orgánicos")
                .precio(3200).unidad("bolsa 1kg").stock(75).atributo("Petorca, V Región")
                .descripcion("Quinua blanca prelavada de cultivo ecológico en Petorca (V Región). Superalimento completo con los 9 aminoácidos esenciales, libre de gluten.")
                .imagen("assets/img/quinua.svg").visualizaciones(165L).vendedorEmail("vendedor@huertohogar.cl").build()
        );
        return repositorioProducto.saveAll(iniciales);
    }
}
