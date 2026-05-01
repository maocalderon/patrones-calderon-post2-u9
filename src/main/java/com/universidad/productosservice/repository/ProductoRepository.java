package com.universidad.productosservice.repository;

import com.universidad.productosservice.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad Producto.
 * Extiende JpaRepository para operaciones CRUD básicas.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Busca productos cuyo nombre contenga el texto indicado (case-insensitive).
     *
     * @param nombre fragmento del nombre a buscar
     * @return lista de productos coincidentes
     */
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Verifica si existe un producto con el nombre exacto indicado.
     *
     * @param nombre nombre exacto del producto
     * @return true si existe, false en caso contrario
     */
    boolean existsByNombre(String nombre);
}
