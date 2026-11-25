package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import pe.edu.upeu.padronmascota.padronmascota.dto.FilaReporte;
import pe.edu.upeu.padronmascota.padronmascota.enums.MetodoPago;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * El "Reporte de ventas pagadas" del Excel: por año, por mes, por día y por rango de fechas.
 * Las ventas Anuladas NO cuentan.
 */
public class ReporteRepositorio extends RepositorioBase<FilaReporte> {

    @Override
    protected FilaReporte mapear(ResultSet rs) throws SQLException {
        return new FilaReporte(rs.getString("etiqueta"), rs.getInt("cantidad"), rs.getBigDecimal("total"));
    }

    public List<FilaReporte> porAnio() {
        // Primero se saca el año de cada venta (subconsulta) y luego se agrupa por ese año
        return consultar("SELECT CAST(anio AS VARCHAR) AS etiqueta, COUNT(*) AS cantidad, SUM(total) AS total "
                + "FROM (SELECT EXTRACT(YEAR FROM fecha_hora) AS anio, total FROM venta WHERE estado = 'Pagada') "
                + "GROUP BY anio ORDER BY anio");
    }

    /** Los 12 meses del año (los meses sin ventas salen con 0). etiqueta = número de mes 1..12. */
    public List<FilaReporte> porMes(int anio) {
        return consultar("SELECT CAST(x AS VARCHAR) AS etiqueta, "
                + "(SELECT COUNT(*) FROM venta WHERE estado = 'Pagada' AND EXTRACT(YEAR FROM fecha_hora) = ? "
                + "    AND EXTRACT(MONTH FROM fecha_hora) = x) AS cantidad, "
                + "(SELECT COALESCE(SUM(total), 0) FROM venta WHERE estado = 'Pagada' AND EXTRACT(YEAR FROM fecha_hora) = ? "
                + "    AND EXTRACT(MONTH FROM fecha_hora) = x) AS total "
                + "FROM SYSTEM_RANGE(1, 12) ORDER BY x", anio, anio);
    }

    /** Días con ventas. etiqueta = fecha (yyyy-MM-dd). */
    public List<FilaReporte> porDia() {
        return consultar("SELECT CAST(dia AS VARCHAR) AS etiqueta, COUNT(*) AS cantidad, SUM(total) AS total "
                + "FROM (SELECT CAST(fecha_hora AS DATE) AS dia, total FROM venta WHERE estado = 'Pagada') "
                + "GROUP BY dia ORDER BY dia");
    }

    /** Por rango de fechas (incluidas), agrupado por método de pago. metodo null = Todos. */
    public List<FilaReporte> porRango(LocalDate desde, LocalDate hasta, MetodoPago metodo) {
        return consultar("SELECT mp.nombre AS etiqueta, "
                + "(SELECT COUNT(*) FROM venta v WHERE v.estado = 'Pagada' AND v.id_metodo_pago = mp.id_metodo_pago "
                + "    AND CAST(v.fecha_hora AS DATE) BETWEEN ? AND ?) AS cantidad, "
                + "(SELECT COALESCE(SUM(v.total), 0) FROM venta v WHERE v.estado = 'Pagada' AND v.id_metodo_pago = mp.id_metodo_pago "
                + "    AND CAST(v.fecha_hora AS DATE) BETWEEN ? AND ?) AS total "
                + "FROM metodo_pago mp WHERE (CAST(? AS INT) IS NULL OR mp.id_metodo_pago = CAST(? AS INT)) ORDER BY mp.id_metodo_pago",
                desde, hasta, desde, hasta, metodo == null ? null : metodo.getId(), metodo == null ? null : metodo.getId());
    }

    /** Años con ventas (para el combo "Por mes del año"). */
    public List<Integer> anios() {
        return porAnio().stream().map(f -> Integer.parseInt(f.etiqueta())).toList();
    }
}
