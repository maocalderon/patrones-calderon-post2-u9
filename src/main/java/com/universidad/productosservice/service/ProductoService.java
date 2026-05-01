package com.universidad.productosservice.service;

import com.universidad.productosservice.domain.Producto;

import java.util.List;

/**
 * Interfaz del servicio de negocio para gestión de Productos.
 * Define el contrato de operaciones disponibles.
 */
public interface ProductoService {

    /**
     * Crea un nuevo producto con validaciones de negocio.
     *
     * @param nombre nombre del producto (no puede ser vacío ni nulo)
     * @param precio precio del producto (debe ser mayor a cero)
     * @param stock  cantidad en stock (no puede ser negativo)
     * @return el producto creado y persistido
     * @throws IllegalArgumentException si algún parámetro no cumple las reglas
     */
    Producto crear(String nombre, Double precio, Integer stock);

    /**
     * Busca un producto por su identificador único.
     *
     * @param id identificador del producto
     * @return el producto encontrado
     * @throws RuntimeException si el producto no existe
     */
    Producto buscarPorId(Long id);

    /**
     * Retorna todos los productos registrados.
     *
     * @return lista de todos los productos
     */
    List<Producto> listarTodos();

    /**
     * Actualiza el stock de un producto existente.
     *
     * @param id         identificador del producto
     * @param nuevoStock nuevo valor del stock (no puede ser negativo)
     * @return el producto actualizado
     * @throws IllegalArgumentException si nuevoStock es negativo
     * @throws RuntimeException         si el producto no existe
     */
    Producto actualizarStock(Long id, Integer nuevoStock);

    /**
     * Elimina un producto por su identificador.
     *
     * @param id identificador del producto a eliminar
     * @throws RuntimeException si el producto no existe
     */
    void eliminar(Long id);
}
