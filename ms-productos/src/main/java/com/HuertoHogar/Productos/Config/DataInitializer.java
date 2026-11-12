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
                .descripcion("Manzanas Fuji crujientes y dulces, cultivadas en el Valle del Maule.")
                .imagen("/assets/img/manzanas-fuji.jpg").build(),
            ModeloProducto.builder()
                .codigo("FR002").nombre("Naranjas Valencia").categoria("Frutas Frescas")
                .precio(1000).unidad("kilo").stock(200).atributo("Alto en Vitamina C")
                .descripcion("Jugosas y ricas en vitamina C, ideales para zumos frescos y naturales.")
                .imagen("/assets/img/naranjas-valencia.jpg").build(),
            ModeloProducto.builder()
                .codigo("FR003").nombre("Platanos Cavendish").categoria("Frutas Frescas")
                .precio(800).unidad("kilo").stock(250).atributo("Rico en Potasio")
                .descripcion("Platanos maduros y dulces, perfectos para el desayuno o como snack energetico.")
                .imagen("/assets/img/platanos-cavendish.jpg").build(),
            ModeloProducto.builder()
                .codigo("VR001").nombre("Zanahorias Organicas").categoria("Verduras Organicas")
                .precio(900).unidad("kilo").stock(100).atributo("Region de O'Higgins")
                .descripcion("Zanahorias crujientes cultivadas sin pesticidas en la Region de O'Higgins.")
                .imagen("/assets/img/zanahorias-organicas.jpg").build(),
            ModeloProducto.builder()
                .codigo("VR002").nombre("Espinacas Frescas").categoria("Verduras Organicas")
                .precio(700).unidad("bolsa 500g").stock(80).atributo("Cosecha del Dia")
                .descripcion("Espinacas frescas y nutritivas, perfectas para ensaladas y batidos verdes.")
                .imagen("/assets/img/espinacas-frescas.jpg").build(),
            ModeloProducto.builder()
                .codigo("VR003").nombre("Pimientos Tricolores").categoria("Verduras Organicas")
                .precio(1500).unidad("malla 3 un.").stock(60).atributo("Rico en Antioxidantes")
                .descripcion("Pimientos rojos, amarillos y verdes, ideales para salteados y platos coloridos.")
                .imagen("/assets/img/pimientos-tricolores.jpg").build(),
            ModeloProducto.builder()
                .codigo("PO001").nombre("Miel Organica").categoria("Productos Organicos")
                .precio(5000).unidad("frasco 500g").stock(45).atributo("100% Pura de Abeja")
                .descripcion("Miel pura y organica producida por apicultores locales.")
                .imagen("/assets/img/miel-organica.jpg").build(),
            ModeloProducto.builder()
                .codigo("PO003").nombre("Quinua Organica").categoria("Productos Organicos")
                .precio(3200).unidad("bolsa 1kg").stock(75).atributo("Superalimento Andino")
                .descripcion("Grano andino rico en proteinas, fibra y minerales esenciales.")
                .imagen("/assets/img/quinua-organica.jpg").build()
        );
        return repositorioProducto.saveAll(iniciales);
    }
}
