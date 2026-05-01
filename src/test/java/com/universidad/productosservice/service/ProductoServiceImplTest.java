package com.universidad.productosservice.service;

import com.universidad.productosservice.domain.Producto;
import com.universidad.productosservice.repository.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias de ProductoServiceImpl")
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Captor
    private ArgumentCaptor<Producto> productoCaptor;

    // ─────────────────────────────────────────────────────────────
    // Happy Path — casos exitosos
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("crear: datos válidos retorna producto guardado con id asignado")
    void crear_datosValidos_retornaProductoGuardado() {
        Producto guardado = new Producto(1L, "Laptop", 1500.0, 10);
        when(productoRepository.save(any(Producto.class))).thenReturn(guardado);

        Producto resultado = productoService.crear("Laptop", 1500.0, 10);

        assertNotNull(resultado.getId(), "El id no debe ser nulo después de guardar");
        assertEquals("Laptop", resultado.getNombre());
        assertEquals(1500.0, resultado.getPrecio());
        assertEquals(10, resultado.getStock());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("buscarPorId: producto existente retorna el producto correcto")
    void buscarPorId_existente_retornaProducto() {
        Producto producto = new Producto(1L, "Mouse", 50.0, 100);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Producto resultado = productoService.buscarPorId(1L);

        assertEquals("Mouse", resultado.getNombre());
        assertEquals(50.0, resultado.getPrecio());
        assertEquals(100, resultado.getStock());
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("listarTodos: retorna lista completa de productos")
    void listarTodos_retornaListaDeProductos() {
        List<Producto> productos = List.of(
                new Producto(1L, "Teclado", 80.0, 20),
                new Producto(2L, "Monitor", 350.0, 5)
        );
        when(productoRepository.findAll()).thenReturn(productos);

        List<Producto> resultado = productoService.listarTodos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("actualizarStock: stock válido actualiza y persiste el producto")
    void actualizarStock_valorValido_actualizaProducto() {
        Producto producto = new Producto(1L, "Auriculares", 120.0, 10);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        Producto resultado = productoService.actualizarStock(1L, 25);

        assertEquals(25, resultado.getStock());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    // ─────────────────────────────────────────────────────────────
    // Pruebas de Error y @ParameterizedTest
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("buscarPorId: id inexistente lanza RuntimeException con mensaje correcto")
    void buscarPorId_noExistente_lanzaRuntimeException() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class,
                () -> productoService.buscarPorId(99L));

        assertTrue(excepcion.getMessage().contains("99"),
                "El mensaje de error debe incluir el id buscado");
    }

    @ParameterizedTest(name = "nombre=''{0}'' debe lanzar IllegalArgumentException")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n", "   "})
    @DisplayName("crear: nombre inválido lanza IllegalArgumentException")
    void crear_nombreInvalido_lanzaIllegalArgumentException(String nombre) {
        assertThrows(IllegalArgumentException.class,
                () -> productoService.crear(nombre, 100.0, 5));

        verifyNoInteractions(productoRepository);
    }

    @ParameterizedTest(name = "precio={0} debe lanzar IllegalArgumentException")
    @ValueSource(doubles = {0.0, -1.0, -100.0, -0.01})
    @DisplayName("crear: precio inválido lanza IllegalArgumentException")
    void crear_precioInvalido_lanzaIllegalArgumentException(double precio) {
        assertThrows(IllegalArgumentException.class,
                () -> productoService.crear("Producto", precio, 5));

        verifyNoInteractions(productoRepository);
    }

    @ParameterizedTest(name = "stock={0} debe lanzar IllegalArgumentException")
    @ValueSource(ints = {-1, -5, -100})
    @DisplayName("crear: stock negativo lanza IllegalArgumentException")
    void crear_stockNegativo_lanzaIllegalArgumentException(int stock) {
        assertThrows(IllegalArgumentException.class,
                () -> productoService.crear("Producto", 50.0, stock));

        verifyNoInteractions(productoRepository);
    }

    @Test
    @DisplayName("actualizarStock: stock negativo lanza IllegalArgumentException")
    void actualizarStock_stockNegativo_lanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> productoService.actualizarStock(1L, -10));

        verifyNoInteractions(productoRepository);
    }

    @Test
    @DisplayName("eliminar: producto inexistente lanza RuntimeException")
    void eliminar_productoInexistente_lanzaRuntimeException() {
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> productoService.eliminar(999L));

        verify(productoRepository, never()).deleteById(any());
    }

    // ─────────────────────────────────────────────────────────────
    // ArgumentCaptor y Verificación Avanzada
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("crear: nombre con espacios se normaliza con strip() antes de guardar")
    void crear_nombreConEspacios_guardaNombreNormalizado() {
        when(productoRepository.save(any())).thenAnswer(inv -> {
            Producto p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        productoService.crear("  Laptop Pro  ", 1500.0, 5);

        verify(productoRepository).save(productoCaptor.capture());
        Producto capturado = productoCaptor.getValue();

        assertEquals("Laptop Pro", capturado.getNombre(),
                "El nombre debe estar normalizado sin espacios al inicio/fin");
        assertEquals(1500.0, capturado.getPrecio());
    }

    @Test
    @DisplayName("eliminar: producto existente invoca deleteById y findById exactamente una vez")
    void eliminar_productoExistente_llamaDeleteById() {
        Producto producto = new Producto(1L, "Teclado", 80.0, 20);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        doNothing().when(productoRepository).deleteById(1L);

        productoService.eliminar(1L);

        verify(productoRepository, times(1)).deleteById(1L);
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("crear: precio nulo lanza IllegalArgumentException")
    void crear_precioNulo_lanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> productoService.crear("Producto", null, 5));

        verifyNoInteractions(productoRepository);
    }

    @Test
    @DisplayName("crear: stock nulo lanza IllegalArgumentException")
    void crear_stockNulo_lanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> productoService.crear("Producto", 50.0, null));

        verifyNoInteractions(productoRepository);
    }

    @Test
    @DisplayName("actualizarStock: producto inexistente lanza RuntimeException")
    void actualizarStock_productoInexistente_lanzaRuntimeException() {
        when(productoRepository.findById(55L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> productoService.actualizarStock(55L, 10));

        verify(productoRepository, never()).save(any());
    }
}
