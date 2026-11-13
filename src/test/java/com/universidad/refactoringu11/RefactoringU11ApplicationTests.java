package com.universidad.refactoringu11;

import com.universidad.refactoringu11.inventario.InventarioService;
import com.universidad.refactoringu11.inventario.Producto;
import com.universidad.refactoringu11.inventario.TipoProducto;
import com.universidad.refactoringu11.reportes.ReporteBajoStockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RefactoringU11ApplicationTests {

    @Autowired
    private InventarioService inventario;

    @Autowired
    private ReporteBajoStockService reporte;

    @Test
    void contextLoads() {
    }

    @Test
    void bajoStockLlegaAlReporteAtravesDelPuerto() {
        inventario.agregarProducto(new Producto(100L, "Monitor", TipoProducto.ELECTRONICO, 300.0, 10, 4));
        inventario.actualizarInventario(100L, 8, "SALIDA");
        assertTrue(reporte.generarReporte().contains("Monitor: reordenar 10 unidades"));
    }
}
