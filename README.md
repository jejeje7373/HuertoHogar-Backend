# HuertoHogar - Backend de Microservicios (Spring Boot + Docker Compose)

Repositorio Backend independiente para el sistema **HuertoHogar**, disenado con **Java 21**, **Spring Boot 3.4.3**, **Spring Data JPA**, **H2 / MySQL**, **Lombok**, **Bean Validation** y **Docker Compose**.

## Arquitectura de Microservicios y Puertos Abiertos

Cada microservicio corre en su propio contenedor y expone su propio puerto hacia el host para ser consumido mediante **Axios** desde el proyecto Frontend:

| Microservicio | Puerto Abierto | Base de Datos | Endpoints Principales |
| :--- | :--- | :--- | :--- |
| **mysql-db** | `3306:3306` | MySQL 8.0 | Motor relacional para los 5 esquemas |
| **ms-usuarios** | `8081:8081` | `db_usuarios` | `/api/users`, `/api/auth/login`, `/api/auth/register` |
| **ms-categorias** | `8082:8082` | `db_categorias` | `/api/categories` |
| **ms-productos** | `8083:8083` | `db_productos` | `/api/products`, `/api/products/{codigo}/descontar-stock`, `/api/products/reset` |
| **ms-ventas** | `8084:8084` | `db_ventas` | `/api/orders`, `/api/orders/checkout`, `/api/orders/stats` |
| **ms-reportes** | `8085:8085` | `db_reportes` | `/api/reports`, `/api/reports/dashboard` |

## Ejecucion con Docker Compose

Desde la raiz de este repositorio ejecuta:

```bash
docker compose up --build -d
```

Para detener los contenedores:

```bash
docker compose down
```

## Ejecucion Local sin Docker (con Base de Datos H2 en Memoria)

Cada microservicio viene preconfigurado con **H2 en memoria** por defecto si se ejecuta directamente desde IntelliJ IDEA, Eclipse, NetBeans o VS Code:

```bash
cd ms-productos
mvn spring-boot:run
```
