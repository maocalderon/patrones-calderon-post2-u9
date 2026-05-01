package com.universidad.productosservice.service;

import com.universidad.productosservice.domain.Producto;
import com.universidad.productosservice.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio de negocio ProductoService.
 * Encapsula las reglas de validación y delega la persistencia al repositorio.
 */
@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public Producto crear(String nombre, Double precio, Integer stock) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (precio == null || precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }

        Producto producto = new Producto(null, nombre.strip(), precio, stock);
        return productoRepository.save(producto);
    }

    @Override
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));
    }

    @Override
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Override
    public Producto actualizarStock(Long id, Integer nuevoStock) {
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        Producto producto = buscarPorId(id);
        producto.setStock(nuevoStock);
        return productoRepository.save(producto);
    }

    @Override
    public void eliminar(Long id) {
        buscarPorId(id); // Lanza excepción si no existe
        productoRepository.deleteById(id);
    }
}
