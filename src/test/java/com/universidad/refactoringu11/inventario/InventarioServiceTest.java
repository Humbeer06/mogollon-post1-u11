package com.universidad.refactoringu11.inventario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de caracterizacion: documentan el comportamiento ACTUAL de
 * actualizarInventario(), incluidos los casos limite y los comportamientos
 * discutibles, para usarlos como red de seguridad durante el refactor.
 */
class InventarioServiceTest {

    private InventarioService service;
    private Producto libro;

    @BeforeEach
    void setUp() {
        service = new InventarioService();
        libro = new Producto(1L, "Clean Code", TipoProducto.LIBRO, 45.0, 20, 5);
        service.agregarProducto(libro);
    }

    // ---------- Casos base ----------

    @Test
    void entrada_incrementaStockYRetornaOk() {
        assertEquals("OK_30", service.actualizarInventario(1L, 10, "ENTRADA"));
        assertEquals(30, libro.getStock());
    }

    @Test
    void salida_conStockSuficiente_descuentaYRetornaOk() {
        assertEquals("OK_12", service.actualizarInventario(1L, 8, "SALIDA"));
    }

    @Test
    void salida_conStockInsuficiente_retornaErrorYNoModificaStock() {
        assertEquals("ERROR_STOCK_INSUFICIENTE", service.actualizarInventario(1L, 50, "SALIDA"));
        assertEquals(20, libro.getStock());
    }

    @Test
    void salida_dejaStockBajoElMinimo_activaReordenConCantidadCalculada() {
        // stockMinimo=5, stock inicial=20, salida de 17 -> nuevoStock=3 (<5)
        // cantidadReorden = stockMinimo*3 - nuevoStock = 15 - 3 = 12
        assertEquals("OK_BAJO_STOCK_REORDEN_12", service.actualizarInventario(1L, 17, "SALIDA"));
        assertEquals(3, libro.getStock());
    }

    @Test
    void tipoMovimientoInvalido_retornaError() {
        assertEquals("ERROR_DATOS_INVALIDOS", service.actualizarInventario(1L, 5, "TRANSFERENCIA"));
    }

    @Test
    void productoInexistente_retornaError() {
        assertEquals("ERROR_PRODUCTO_NO_EXISTE", service.actualizarInventario(99L, 5, "ENTRADA"));
    }

    // ---------- Valores limite ----------

    @Test
    void salida_quedaExactamenteEnElMinimo_noActivaReorden() {
        // 20 - 15 = 5; la condicion es estrictamente "menor que" el minimo
        assertEquals("OK_5", service.actualizarInventario(1L, 15, "SALIDA"));
        assertEquals(5, libro.getStock());
    }

    @Test
    void salida_dejaStockEnCero_esValidaYActivaReorden() {
        // 20 - 20 = 0 (no es negativo); reorden = 15 - 0 = 15
        assertEquals("OK_BAJO_STOCK_REORDEN_15", service.actualizarInventario(1L, 20, "SALIDA"));
        assertEquals(0, libro.getStock());
    }

    @Test
    void productoIdNulo_retornaErrorDeDatos() {
        assertEquals("ERROR_DATOS_INVALIDOS", service.actualizarInventario(null, 5, "ENTRADA"));
    }

    @Test
    void cantidadCero_retornaErrorDeDatos() {
        assertEquals("ERROR_DATOS_INVALIDOS", service.actualizarInventario(1L, 0, "ENTRADA"));
        assertEquals(20, libro.getStock());
    }

    // ---------- Comportamientos actuales discutibles (se preservan) ----------

    @Test
    void entrada_sobreProductoYaBajoElMinimo_tambienActivaReorden() {
        Producto escaso = new Producto(2L, "Teclado", TipoProducto.ELECTRONICO, 100.0, 1, 5);
        service.agregarProducto(escaso);
        // 1 + 1 = 2 (<5): la alerta se evalua tambien en las entradas; reorden = 15 - 2 = 13
        assertEquals("OK_BAJO_STOCK_REORDEN_13", service.actualizarInventario(2L, 1, "ENTRADA"));
    }

    @Test
    void entradaConCantidadNegativa_descuentaStockSinValidar_comportamientoActual() {
        // Hoy no se valida el signo de la cantidad: una "entrada" negativa resta stock.
        assertEquals("OK_15", service.actualizarInventario(1L, -5, "ENTRADA"));
        assertEquals(15, libro.getStock());
    }

    @Test
    void tipoMovimientoNulo_lanzaNullPointerException_comportamientoActual() {
        assertThrows(NullPointerException.class,
                () -> service.actualizarInventario(1L, 5, null));
    }

    @Test
    void conVariosProductos_actualizaSoloElIndicado() {
        Producto camisa = new Producto(3L, "Camisa", TipoProducto.ROPA, 40.0, 10, 2);
        service.agregarProducto(camisa);
        assertEquals("OK_14", service.actualizarInventario(3L, 4, "ENTRADA"));
        assertEquals(14, camisa.getStock());
        assertEquals(20, libro.getStock());
    }

    // ---------- Efectos observables por consola (registro y alerta) ----------

    @Test
    void salidaConAlerta_imprimeRegistroYLuegoLaAlerta() {
        String salida = capturarConsola(() -> service.actualizarInventario(1L, 17, "SALIDA"));
        assertEquals(List.of(
                "SALIDA de 17 unidades de Clean Code -> stock final: 3",
                "ALERTA: Clean Code bajo stock minimo. Reordenar 12 unidades."),
                List.of(salida.strip().split("\\R")));
    }

    @Test
    void entradaSinAlerta_imprimeSoloElRegistro() {
        String salida = capturarConsola(() -> service.actualizarInventario(1L, 10, "ENTRADA"));
        assertEquals(List.of("ENTRADA de 10 unidades de Clean Code -> stock final: 30"),
                List.of(salida.strip().split("\\R")));
    }

    @Test
    void errores_noImprimenNadaNiModificanStock() {
        String salida = capturarConsola(() -> {
            service.actualizarInventario(1L, 50, "SALIDA");
            service.actualizarInventario(99L, 5, "ENTRADA");
            service.actualizarInventario(1L, 5, "TRANSFERENCIA");
        });
        assertEquals("", salida);
        assertEquals(20, libro.getStock());
    }

    private static String capturarConsola(Runnable accion) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true));
        try {
            accion.run();
        } finally {
            System.setOut(original);
        }
        return buffer.toString();
    }
}
