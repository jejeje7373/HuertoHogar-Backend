package com.HuertoHogar.Productos;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductosSecurityCrudTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private String generarToken(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600_000))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    @Test
    void lecturaPublicaY404EnProductoInexistente() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").isNotEmpty());

        mockMvc.perform(get("/api/products/FR001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("FR001"));

        mockMvc.perform(get("/api/products/NO_EXISTE_999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crudProductosValidaRolesYCodigosHttp() throws Exception {
        String codigo = "TS" + (System.nanoTime() % 10000);
        String payload = """
                {
                  "codigo": "%s",
                  "nombre": "Tomate Cherry Orgánico",
                  "categoria": "Verduras",
                  "precio": 2500,
                  "unidad": "Bandeja 500g",
                  "stock": 40,
                  "atributo": "Limache",
                  "descripcion": "Cosecha fresca de Limache"
                }
                """.formatted(codigo);

        // Anónimo -> 401
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized());

        // Cliente -> 403
        String clienteToken = generarToken("cliente@huertohogar.cl", "CLIENTE");
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + clienteToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());

        // Admin con payload inválido (precio negativo) -> 400
        String adminToken = generarToken("admin@huertohogar.cl", "ADMIN");
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "BAD1",
                                  "nombre": "",
                                  "categoria": "Verduras",
                                  "precio": -10,
                                  "unidad": "Kg",
                                  "stock": -5
                                }
                                """))
                .andExpect(status().isBadRequest());

        // Admin válido -> 201
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(codigo));

        // Editar producto -> 200
        mockMvc.perform(put("/api/products/" + codigo)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload.replace("2500", "2800")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precio").value(2800));

        // Cliente intenta eliminar -> 403
        mockMvc.perform(delete("/api/products/" + codigo)
                        .header("Authorization", "Bearer " + clienteToken))
                .andExpect(status().isForbidden());

        // Admin elimina -> 204 y luego 404
        mockMvc.perform(delete("/api/products/" + codigo)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/" + codigo))
                .andExpect(status().isNotFound());
    }

    @Test
    void vendedorPuedeAgregarFrutaQuintaRegionYContarVisualizaciones() throws Exception {
        String codigo = "VD" + (System.nanoTime() % 10000);
        String vendedorToken = generarToken("vendedor@huertohogar.cl", "VENDEDOR");

        // Origen fuera de la 5ª Región -> 400 Bad Request
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + vendedorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "%s",
                                  "nombre": "Paltas Fuera Región",
                                  "categoria": "Frutas Frescas",
                                  "precio": 3000,
                                  "unidad": "kilo",
                                  "stock": 20,
                                  "atributo": "Mendoza, Argentina"
                                }
                                """.formatted(codigo)))
                .andExpect(status().isBadRequest());

        // Origen válido de la V Región -> 201 Created
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + vendedorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "%s",
                                  "nombre": "Chirimoyas de Quillota",
                                  "categoria": "Frutas Frescas",
                                  "precio": 3600,
                                  "unidad": "kilo",
                                  "stock": 35,
                                  "atributo": "Quillota, V Región"
                                }
                                """.formatted(codigo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.visualizaciones").value(0));

        // Incrementar visualización -> visualizaciones = 1
        mockMvc.perform(post("/api/products/" + codigo + "/visualizacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visualizaciones").value(1));

        // Vendedor NO puede eliminar todo el catálogo (/reset -> 403)
        mockMvc.perform(post("/api/products/reset")
                        .header("Authorization", "Bearer " + vendedorToken))
                .andExpect(status().isForbidden());
    }
}
