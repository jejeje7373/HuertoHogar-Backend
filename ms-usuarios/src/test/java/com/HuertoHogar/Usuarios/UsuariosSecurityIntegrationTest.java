package com.HuertoHogar.Usuarios;

import com.HuertoHogar.Usuarios.Modelo.ModeloUsuario;
import com.HuertoHogar.Usuarios.Repositorio.RepositorioUsuario;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UsuariosSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    private String obtenerToken(String email, String password) throws Exception {
        String body = objectMapper.writeValueAsString(
                java.util.Map.of("email", email, "password", password)
        );
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("token").asText();
    }

    @Test
    void loginAdminExitosoYNoExponePassword() throws Exception {
        String body = objectMapper.writeValueAsString(
                java.util.Map.of("email", "admin@huertohogar.cl", "password", "Admin123*")
        );
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.role").value("ADMIN"))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        assertNull(json.get("user").get("password"), "El hash de contraseña nunca debe serializarse");
    }

    @Test
    void loginConCredencialesInvalidasRetorna401() throws Exception {
        String body = objectMapper.writeValueAsString(
                java.util.Map.of("email", "admin@huertohogar.cl", "password", "ClaveIncorrecta")
        );
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registroPublicoHasheaPasswordYAsignaRolCliente() throws Exception {
        String email = "nuevo.cliente." + System.nanoTime() + "@huertohogar.cl";
        String body = """
                {
                  "nombre": "Nuevo Cliente",
                  "email": "%s",
                  "password": "MiClaveSegura123",
                  "role": "ADMIN"
                }
                """.formatted(email);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.role").value("CLIENTE"));

        ModeloUsuario guardado = repositorioUsuario.findByEmail(email).orElseThrow();
        assertNotEquals("MiClaveSegura123", guardado.getPassword());
        assertTrue(guardado.getPassword().startsWith("$2"), "Debe usar hash BCrypt");
        assertEquals("CLIENTE", guardado.getRole());

        // Registro público como VENDEDOR permitido y recibe 403 en /api/users
        String emailVendedor = "nuevo.vendedor." + System.nanoTime() + "@huertohogar.cl";
        String bodyVendedor = """
                {
                  "nombre": "Nuevo Vendedor",
                  "email": "%s",
                  "password": "ClaveVendedor123*",
                  "role": "VENDEDOR"
                }
                """.formatted(emailVendedor);
        MvcResult vendRes = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyVendedor))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.role").value("VENDEDOR"))
                .andReturn();
        String vendToken = objectMapper.readTree(vendRes.getResponse().getContentAsString()).get("token").asText();
        mockMvc.perform(get("/api/users").header("Authorization", "Bearer " + vendToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void rutasPrivadasBloqueanAnonimoYClienteYPermitenAdmin() throws Exception {
        // Sin token -> 401
        mockMvc.perform(get("/api/users")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());

        // Con rol CLIENTE -> 403 en CRUD de usuarios, 200 en /api/auth/me
        String clienteToken = obtenerToken("cliente@huertohogar.cl", "Password123*");
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + clienteToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("CLIENTE"));

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + clienteToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + clienteToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Intento\",\"email\":\"x@y.cl\",\"password\":\"123456\"}"))
                .andExpect(status().isForbidden());

        // Con rol ADMIN -> CRUD completo (201, 200, PATCH estado, 403 al loguear inactivo, 204 DELETE, 404)
        String adminToken = obtenerToken("admin@huertohogar.cl", "Admin123*");
        String emailCrud = "crud." + System.nanoTime() + "@huertohogar.cl";
        String createBody = """
                {
                  "nombre": "Operador Prueba",
                  "email": "%s",
                  "password": "Password123*",
                  "role": "ADMIN"
                }
                """.formatted(emailCrud);

        MvcResult created = mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andReturn();

        Long userId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        // Desactivar usuario
        mockMvc.perform(patch("/api/users/" + userId + "/estado")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"activo\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));

        // Usuario inactivo recibe 403 al intentar login
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                java.util.Map.of("email", emailCrud, "password", "Password123*")
                        )))
                .andExpect(status().isForbidden());

        // Eliminar usuario
        mockMvc.perform(delete("/api/users/" + userId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Buscar usuario eliminado -> 404
        mockMvc.perform(get("/api/users/" + userId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
