package pe.edu.upeu.padronmascota.padronmascota.dto;

import java.math.BigDecimal;

/** Una fila del reporte de ventas: etiqueta (año, mes, día o método), n° de ventas y total. */
public record FilaReporte(String etiqueta, int cantidad, BigDecimal total) {
}
