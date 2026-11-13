package com.universidad.refactoringu11.inventario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventarioServiceNotificacionTest {

    private final List<String> recibidas = new ArrayList<>();
    private InventarioService service;

    @BeforeEach
    void setUp() {
        service = new InventarioService();
        service.setNotificador((producto, cantidad) -> recibidas.add(producto.getNombre() + ":" + cantidad));
        service.agregarProducto(new Producto(1L, "Clean Code", TipoProducto.LIBRO, 45.0, 20, 5));
    }

    @Test
    void bajoStock_notificaUnaVezConProductoYCantidadDeReorden() {
        service.actualizarInventario(1L, 17, "SALIDA");
        assertEquals(List.of("Clean Code:12"), recibidas);
    }

    @Test
    void stockSuficiente_noNotifica() {
        service.actualizarInventario(1L, 10, "ENTRADA");
        assertEquals(List.of(), recibidas);
    }

    @Test
    void errorDeValidacion_noNotifica() {
        service.actualizarInventario(1L, 50, "SALIDA");
        assertEquals(List.of(), recibidas);
    }

    @Test
    void sinNotificador_elResultadoNoCambia() {
        InventarioService sinNotificador = new InventarioService();
        sinNotificador.agregarProducto(new Producto(1L, "Clean Code", TipoProducto.LIBRO, 45.0, 20, 5));
        assertEquals("OK_BAJO_STOCK_REORDEN_12", sinNotificador.actualizarInventario(1L, 17, "SALIDA"));
    }
}
