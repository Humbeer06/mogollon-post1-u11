package com.universidad.refactoringu11.reportes;

import com.universidad.refactoringu11.inventario.Producto;
import com.universidad.refactoringu11.inventario.TipoProducto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReporteBajoStockServiceTest {

    private final ReporteBajoStockService reporte = new ReporteBajoStockService();

    @Test
    void sinAlertas_indicaQueNoHayProductosBajoStock() {
        assertEquals("Sin productos bajo stock minimo", reporte.generarReporte());
    }

    @Test
    void conUnaAlerta_listaElProductoYLaCantidadAReordenar() {
        reporte.notificar(new Producto(1L, "Clean Code", TipoProducto.LIBRO, 45.0, 3, 5), 12);
        assertEquals("Productos bajo stock minimo:\n- Clean Code: reordenar 12 unidades",
                reporte.generarReporte());
    }

    @Test
    void conVariasAlertas_lasListaEnElOrdenDeLlegada() {
        reporte.notificar(new Producto(1L, "Clean Code", TipoProducto.LIBRO, 45.0, 3, 5), 12);
        reporte.notificar(new Producto(2L, "Teclado", TipoProducto.ELECTRONICO, 100.0, 1, 4), 11);
        assertEquals("Productos bajo stock minimo:\n- Clean Code: reordenar 12 unidades\n- Teclado: reordenar 11 unidades",
                reporte.generarReporte());
    }
}
