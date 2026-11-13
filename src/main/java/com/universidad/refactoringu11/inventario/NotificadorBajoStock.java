package com.universidad.refactoringu11.inventario;

/**
 * Puerto de salida del dominio de inventario: quien quiera enterarse de un
 * producto bajo stock implementa esta interfaz. Vive en inventario (el paquete
 * de mayor nivel) para que la dependencia apunte hacia el dominio.
 */
public interface NotificadorBajoStock {

    void notificar(Producto producto, int cantidadReorden);
}
