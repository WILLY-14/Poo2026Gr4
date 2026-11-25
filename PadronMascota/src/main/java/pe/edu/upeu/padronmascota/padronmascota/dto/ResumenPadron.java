package pe.edu.upeu.padronmascota.padronmascota.dto;

import java.math.BigDecimal;

/**
 * Los números de la tabla "Resumen" del Excel. Solo lleva datos (DTO).
 * Es un RECORD de Java: el compilador crea solo el constructor y los métodos para leer cada dato.
 */
public record ResumenPadron(
        int mascotasActivas, int perros, int gatos, int otros,
        int vacunacionAlDia, int vacunacionPendiente, double edadPromedio,
        int propietarios, int vacunasAplicadas, int dosisVencidas,
        int veterinarios, int atenciones, int citasPendientes, int citasAtendidas,
        int ventasPagadas, int ventasAnuladas, BigDecimal totalVendido, BigDecimal vendidoEsteAnio,
        int proveedores, int compras, BigDecimal totalComprado,
        int insumosUsados, int recetas, int lotesVencidos, int lotesPorVencer, int productosStockBajo) {
}
