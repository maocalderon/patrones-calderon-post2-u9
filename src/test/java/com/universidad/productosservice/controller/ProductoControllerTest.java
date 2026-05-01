package com.universidad.productosservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universidad.productosservice.domain.Producto;
import com.universidad.productosservice.exception.GlobalExceptionHandler;
import com.universidad.productosservice.service.ProductoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para ProductoController usando @WebMvcTest.
 * Carga únicamente la capa web. El servicio se provee como @MockBean
 * para controlar las respuestas sin depender de la BD.
 */
@WebMvcTest(ProductoController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("Pruebas de integración de ProductoController con @WebMvcTest")
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoService productoService;

    @Test
    @DisplayName("GET /api/productos: retorna 200 con lista de productos")
    void listarProductos_retorna200ConLista() throws Exception {
        List<Producto> lista = List.of(
                new Producto(1L, "Laptop", 1500.0, 10),
                new Producto(2L, "Mouse", 50.0, 100));
        when(productoService.listarTodos()).thenReturn(lista);

        mockMvc.perform(get("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Laptop"))
                .andExpect(jsonPath("$[1].nombre").value("Mouse"));
    }

    @Test
    @DisplayName("POST /api/productos: datos válidos retorna 201 con producto creado")
    void crearProducto_datosValidos_retorna201() throws Exception {
        Producto creado = new Producto(1L, "Tablet", 800.0, 5);
        when(productoService.crear(anyString(), anyDouble(), anyInt()))
                .thenReturn(creado);

        String json = """
                {"nombre":"Tablet","precio":800.0,"stock":5}""";

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Tablet"))
                .andExpect(jsonPath("$.precio").value(800.0));
    }

    @Test
    @DisplayName("GET /api/productos/{id}: producto no existente retorna 404")
    void buscarProducto_noExistente_retorna404() throws Exception {
        when(productoService.buscarPorId(99L))
                .thenThrow(new RuntimeException("Producto no encontrado: 99"));

        mockMvc.perform(get("/api/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Producto no encontrado: 99"));
    }

    @Test
    @DisplayName("GET /api/productos/{id}: producto existente retorna 200 con datos correctos")
    void buscarProducto_existente_retorna200() throws Exception {
        Producto producto = new Producto(1L, "Teclado", 80.0, 20);
        when(productoService.buscarPorId(1L)).thenReturn(producto);

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Teclado"))
                .andExpect(jsonPath("$.precio").value(80.0));
    }

    @Test
    @DisplayName("POST /api/productos: nombre vacío retorna 400 Bad Request")
    void crearProducto_nombreInvalido_retorna400() throws Exception {
        when(productoService.crear(eq(""), anyDouble(), anyInt()))
                .thenThrow(new IllegalArgumentException("El nombre no puede estar vacío"));

        String json = """
                {"nombre":"","precio":100.0,"stock":5}""";

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El nombre no puede estar vacío"));
    }

    @Test
    @DisplayName("PATCH /api/productos/{id}/stock: actualiza stock y retorna 200")
    void actualizarStock_valido_retorna200() throws Exception {
        Producto actualizado = new Producto(1L, "Monitor", 350.0, 25);
        when(productoService.actualizarStock(1L, 25)).thenReturn(actualizado);

        String json = """
                {"stock":25}""";

        mockMvc.perform(patch("/api/productos/1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(25));
    }

    @Test
    @DisplayName("DELETE /api/productos/{id}: elimina producto existente y retorna 204")
    void eliminarProducto_existente_retorna204() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }
}
