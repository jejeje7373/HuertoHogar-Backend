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
                .precio(1200).unidad("kilo").stock(150).atributo("Valle del Maule")
                .descripcion("Manzanas Fuji crujientes y dulces, cultivadas en el Valle del Maule. Perfectas para meriendas saludables o reposteria natural.")
                .imagen("assets/img/manzanas.svg").build(),
            ModeloProducto.builder()
                .codigo("FR002").nombre("Naranjas Valencia").categoria("Frutas Frescas")
                .precio(1000).unidad("kilo").stock(200).atributo("Ricas en Vitamina C")
                .descripcion("Jugosas y aromaticas, estas naranjas Valencia son ideales para zumos frescos matutinos. Cosechadas en el punto optimo de madurez.")
                .imagen("assets/img/naranjas.svg").build(),
            ModeloProducto.builder()
                .codigo("FR003").nombre("Plátanos Cavendish").categoria("Frutas Frescas")
                .precio(800).unidad("kilo").stock(250).atributo("Ricos en Potasio")
                .descripcion("Platanos maduros y dulces, perfectos para el desayuno, batidos o como snack energetico para deportistas y ninos.")
                .imagen("assets/img/platanos.svg").build(),
            ModeloProducto.builder()
                .codigo("VR001").nombre("Zanahorias Orgánicas").categoria("Verduras Orgánicas")
                .precio(900).unidad("kilo").stock(100).atributo("Región de O'Higgins")
                .descripcion("Zanahorias crujientes cultivadas sin pesticidas en los fertiles suelos de O'Higgins. Excelente fuente de betacarotenos y fibra.")
                .imagen("assets/img/zanahorias.svg").build(),
            ModeloProducto.builder()
                .codigo("VR002").nombre("Espinacas Frescas").categoria("Verduras Orgánicas")
                .precio(700).unidad("bolsa 500g").stock(80).atributo("Cultivo Hidropónico")
                .descripcion("Hojas tiernas y nutritivas de espinaca recien cosechada. Ideales para ensaladas crudas, salteados o batidos verdes detox.")
                .imagen("assets/img/espinacas.svg").build(),
            ModeloProducto.builder()
                .codigo("VR003").nombre("Pimientos Tricolores").categoria("Verduras Orgánicas")
                .precio(1500).unidad("malla 3 un.").stock(60).atributo("Invernaderos Solares")
                .descripcion("Trio de pimientos rojo, amarillo y verde. Aportan color, textura crocante y alto contenido de antioxidantes a tus preparaciones.")
                .imagen("assets/img/pimientos.svg").build(),
            ModeloProducto.builder()
                .codigo("PO001").nombre("Miel Orgánica").categoria("Productos Orgánicos")
                .precio(5000).unidad("frasco 500g").stock(45).atributo("Bosque Nativo del Sur")
                .descripcion("Miel pura de abeja cosechada en apiarios sustentables del sur de Chile. 100% natural, sin aditivos ni azucares anadidos.")
                .imagen("assets/img/miel.svg").build(),
            ModeloProducto.builder()
                .codigo("PO003").nombre("Quinua Orgánica").categoria("Productos Orgánicos")
                .precio(3200).unidad("bolsa 1kg").stock(75).atributo("Grano Andino Ancestral")
                .descripcion("Quinua real blanca prelavada de cultivo ecologico. Superalimento completo con los 9 aminoacidos esenciales, libre de gluten.")
                .imagen("assets/img/quinua.svg").build()
        );
        return repositorioProducto.saveAll(iniciales);
    }
}
