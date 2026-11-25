package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import pe.edu.upeu.padronmascota.padronmascota.dto.ResumenPadron;
import pe.edu.upeu.padronmascota.padronmascota.modelo.DetalleCompra;

import java.sql.ResultSet;
import java.sql.SQLException;

/** Calcula todos los números del "Resumen" del Excel con una sola consulta. */
public class ResumenRepositorio extends RepositorioBase<ResumenPadron> {

    private static final String SQL = "SELECT "
            + "(SELECT COUNT(*) FROM mascota WHERE activo) AS mascotas, "
            + "(SELECT COUNT(*) FROM mascota WHERE activo AND id_especie = 1) AS perros, "
            + "(SELECT COUNT(*) FROM mascota WHERE activo AND id_especie = 2) AS gatos, "
            + "(SELECT COUNT(*) FROM mascota WHERE activo AND id_especie = 3) AS otros, "
            + "(SELECT COUNT(*) FROM mascota WHERE activo AND id_estado = 1) AS al_dia, "
            + "(SELECT COUNT(*) FROM mascota WHERE activo AND id_estado = 2) AS pendiente, "
            + "(SELECT COALESCE(AVG(CAST(edad_aprox AS DOUBLE)), 0) FROM mascota WHERE activo) AS edad, "
            + "(SELECT COUNT(*) FROM propietario) AS propietarios, "
            + "(SELECT COUNT(*) FROM vacunacion) AS vacunas, "
            + "(SELECT COUNT(*) FROM vacunacion WHERE proxima_dosis < CURRENT_DATE) AS vencidas, "
            + "(SELECT COUNT(*) FROM veterinario) AS veterinarios, "
            + "(SELECT COUNT(*) FROM atencion) AS atenciones, "
            + "(SELECT COUNT(*) FROM cita WHERE id_estado_cita = 1) AS citas_pend, "
            + "(SELECT COUNT(*) FROM cita WHERE id_estado_cita = 2) AS citas_aten, "
            + "(SELECT COUNT(*) FROM venta WHERE estado = 'Pagada') AS pagadas, "
            + "(SELECT COUNT(*) FROM venta WHERE estado = 'Anulada') AS anuladas, "
            + "(SELECT COALESCE(SUM(total), 0) FROM venta WHERE estado = 'Pagada') AS vendido, "
            + "(SELECT COALESCE(SUM(total), 0) FROM venta WHERE estado = 'Pagada' "
            + "    AND EXTRACT(YEAR FROM fecha_hora) = EXTRACT(YEAR FROM CURRENT_DATE)) AS vendido_anio, "
            + "(SELECT COUNT(*) FROM proveedor) AS proveedores, "
            + "(SELECT COUNT(*) FROM compra) AS compras, "
            + "(SELECT COALESCE(SUM(total), 0) FROM compra) AS comprado, "
            + "(SELECT COALESCE(SUM(cantidad), 0) FROM atencion_insumo) AS insumos, "
            + "(SELECT COUNT(*) FROM receta) AS recetas, "
            + "(SELECT COUNT(*) FROM detalle_compra WHERE fecha_vencimiento < CURRENT_DATE) AS lotes_vencidos, "
            + "(SELECT COUNT(*) FROM detalle_compra WHERE fecha_vencimiento >= CURRENT_DATE "
            + "    AND fecha_vencimiento <= DATEADD('DAY', " + DetalleCompra.DIAS_POR_VENCER + ", CURRENT_DATE)) AS lotes_por_vencer, "
            + "(SELECT COUNT(*) FROM producto p JOIN v_stock_producto s ON s.id_producto = p.id_producto "
            + "    WHERE s.comprados - s.vendidos - s.usados <= p.stock_minimo) AS stock_bajo";

    @Override
    protected ResumenPadron mapear(ResultSet rs) throws SQLException {
        return new ResumenPadron(
                rs.getInt("mascotas"), rs.getInt("perros"), rs.getInt("gatos"), rs.getInt("otros"),
                rs.getInt("al_dia"), rs.getInt("pendiente"), rs.getDouble("edad"),
                rs.getInt("propietarios"), rs.getInt("vacunas"), rs.getInt("vencidas"),
                rs.getInt("veterinarios"), rs.getInt("atenciones"), rs.getInt("citas_pend"), rs.getInt("citas_aten"),
                rs.getInt("pagadas"), rs.getInt("anuladas"), rs.getBigDecimal("vendido"), rs.getBigDecimal("vendido_anio"),
                rs.getInt("proveedores"), rs.getInt("compras"), rs.getBigDecimal("comprado"),
                rs.getInt("insumos"), rs.getInt("recetas"), rs.getInt("lotes_vencidos"), rs.getInt("lotes_por_vencer"),
                rs.getInt("stock_bajo"));
    }

    public ResumenPadron calcular() {
        return consultarUno(SQL).orElseThrow();
    }
}
