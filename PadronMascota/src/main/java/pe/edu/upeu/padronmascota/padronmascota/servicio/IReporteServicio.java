package pe.edu.upeu.padronmascota.padronmascota.servicio;

import pe.edu.upeu.padronmascota.padronmascota.dto.FilaReporte;
import pe.edu.upeu.padronmascota.padronmascota.dto.ResumenPadron;
import pe.edu.upeu.padronmascota.padronmascota.enums.MetodoPago;

import java.time.LocalDate;
import java.util.List;

/** Resumen y reportes de ventas del Excel. */
public interface IReporteServicio {

    ResumenPadron resumen();

    List<FilaReporte> ventasPorAnio();

    List<FilaReporte> ventasPorMes(int anio);

    List<FilaReporte> ventasPorDia();

    List<FilaReporte> ventasPorRango(LocalDate desde, LocalDate hasta, MetodoPago metodo);

    List<Integer> aniosConVentas();
}
