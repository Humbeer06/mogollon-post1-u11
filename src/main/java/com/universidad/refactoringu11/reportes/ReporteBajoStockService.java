package com.universidad.refactoringu11.reportes;

import com.universidad.refactoringu11.inventario.NotificadorBajoStock;
import com.universidad.refactoringu11.inventario.Producto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReporteBajoStockService implements NotificadorBajoStock {

    private final List<String> alertas = new ArrayList<>();

    @Override
    public void notificar(Producto producto, int cantidadReorden) {
        alertas.add(producto.getNombre() + ": reordenar " + cantidadReorden + " unidades");
    }

    public String generarReporte() {
        if (alertas.isEmpty()) {
            return "Sin productos bajo stock minimo";
        }
        return "Productos bajo stock minimo:\n- " + String.join("\n- ", alertas);
    }
}
