package com.universidad.productosservice.repository;

import com.universidad.productosservice.domain.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para ProductoRepository usando @DataJpaTest.
 * Spring Boot configura automáticamente H2 en memoria y revierte
 * cada prueba en una transacción, garantizando aislamiento total.
 */
@DataJpaTest
@DisplayName("Pruebas de integración de ProductoRepository con @DataJpaTest")
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @BeforeEach
    void setUp() {
        productoRepository.deleteAll();
    }

    @Test
    @DisplayName("save: asigna id automáticamente al persistir un producto nuevo")
    void save_asignaIdAutomaticamente() {
        Producto guardado = productoRepository
                .save(new Producto(null, "Laptop", 1500.0, 10));

        assertNotNull(guardado.getId(), "El id no debe ser nulo tras guardar");
        assertTrue(guardado.getId() > 0, "El id debe ser positivo");
    }

    @Test
    @DisplayName("findById: retorna el producto correcto cuando el id existe")
    void findById_existente_retornaProducto() {
        Producto guardado = productoRepository
                .save(new Producto(null, "Mouse", 50.0, 100));

        Optional<Producto> resultado = productoRepository.findById(guardado.getId());

        assertTrue(resultado.isPresent(), "El producto debe estar presente");
        assertEquals("Mouse", resultado.get().getNombre());
        assertEquals(50.0, resultado.get().getPrecio());
        assertEquals(100, resultado.get().getStock());
    }

    @Test
    @DisplayName("findById: retorna Optional vacío cuando el id no existe")
    void findById_noExistente_retornaOptionalVacio() {
        Optional<Producto> resultado = productoRepository.findById(999L);

        assertFalse(resultado.isPresent(), "No debe encontrarse ningún producto con id 999");
    }

    @Test
    @DisplayName("findAll: retorna lista completa con todos los productos guardados")
    void findAll_retornaListaCompleta() {
        productoRepository.save(new Producto(null, "Teclado", 80.0, 50));
        productoRepository.save(new Producto(null, "Monitor", 350.0, 20));

        List<Producto> productos = productoRepository.findAll();

        assertEquals(2, productos.size(), "Deben existir exactamente 2 productos");
    }

    @Test
    @DisplayName("deleteById: elimina el producto correctamente de la base de datos")
    void deleteById_eliminaProducto() {
        Producto guardado = productoRepository
                .save(new Producto(null, "Webcam", 90.0, 15));

        productoRepository.deleteById(guardado.getId());

        assertFalse(productoRepository.findById(guardado.getId()).isPresent(),
                "El producto eliminado no debe encontrarse en la BD");
    }

    @Test
    @DisplayName("findByNombreContainingIgnoreCase: retorna productos que coinciden con el fragmento")
    void findByNombreContainingIgnoreCase_retornaCoincidencias() {
        productoRepository.save(new Producto(null, "Laptop Pro", 1500.0, 5));
        productoRepository.save(new Producto(null, "Laptop Air", 1200.0, 3));
        productoRepository.save(new Producto(null, "Mouse", 50.0, 100));

        List<Producto> resultado = productoRepository.findByNombreContainingIgnoreCase("laptop");

        assertEquals(2, resultado.size(), "Deben encontrarse 2 productos con 'laptop' en el nombre");
    }

    @Test
    @DisplayName("existsByNombre: retorna true cuando el nombre exacto existe")
    void existsByNombre_nombreExistente_retornaTrue() {
        productoRepository.save(new Producto(null, "Teclado Mecánico", 120.0, 10));

        boolean existe = productoRepository.existsByNombre("Teclado Mecánico");

        assertTrue(existe, "Debe existir un producto con ese nombre exacto");
    }

    @Test
    @DisplayName("existsByNombre: retorna false cuando el nombre no existe")
    void existsByNombre_nombreInexistente_retornaFalse() {
        boolean existe = productoRepository.existsByNombre("Producto Inexistente");

        assertFalse(existe, "No debe existir un producto con ese nombre");
    }
}
