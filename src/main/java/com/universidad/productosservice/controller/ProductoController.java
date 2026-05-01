package com.universidad.productosservice.controller;

import com.universidad.productosservice.domain.Producto;
import com.universidad.productosservice.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de Productos.
 * Expone endpoints HTTP para operaciones CRUD.
 * Las excepciones son manejadas globalmente por GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * POST /api/productos
     * Crea un nuevo producto.
     */
    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Map<String, Object> body) {
        String nombre = (String) body.get("nombre");
        Double precio = body.get("precio") != null
                ? Double.parseDouble(body.get("precio").toString())
                : null;
        Integer stock = body.get("stock") != null
                ? Integer.parseInt(body.get("stock").toString())
                : null;

        Producto creado = productoService.crear(nombre, precio, stock);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * GET /api/productos
     * Retorna todos los productos.
     */
    @GetMapping
    public ResponseEntity<List<Producto>> listarTodos() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    /**
     * GET /api/productos/{id}
     * Busca un producto por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    /**
     * PATCH /api/productos/{id}/stock
     * Actualiza el stock de un producto.
     */
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Producto> actualizarStock(@PathVariable Long id,
                                                     @RequestBody Map<String, Integer> body) {
        Integer nuevoStock = body.get("stock");
        return ResponseEntity.ok(productoService.actualizarStock(id, nuevoStock));
    }

    /**
     * DELETE /api/productos/{id}
     * Elimina un producto por ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
