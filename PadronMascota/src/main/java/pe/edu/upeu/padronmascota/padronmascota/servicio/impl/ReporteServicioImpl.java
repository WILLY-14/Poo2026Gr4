package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.dto.FilaReporte;
import pe.edu.upeu.padronmascota.padronmascota.dto.ResumenPadron;
import pe.edu.upeu.padronmascota.padronmascota.enums.MetodoPago;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.ReporteRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.ResumenRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IReporteServicio;

import java.time.LocalDate;
import java.util.List;

public class ReporteServicioImpl implements IReporteServicio {

    private final ResumenRepositorio resumenRepo;
    private final ReporteRepositorio reporteRepo;

    public ReporteServicioImpl(ResumenRepositorio resumenRepo, ReporteRepositorio reporteRepo) {
        this.resumenRepo = resumenRepo;
        this.reporteRepo = reporteRepo;
    }

    @Override
    public ResumenPadron resumen() {
        return resumenRepo.calcular();
    }

    @Override
    public List<FilaReporte> ventasPorAnio() {
        return reporteRepo.porAnio();
    }

    @Override
    public List<FilaReporte> ventasPorMes(int anio) {
        return reporteRepo.porMes(anio);
    }

    @Override
    public List<FilaReporte> ventasPorDia() {
        return reporteRepo.porDia();
    }

    @Override
    public List<FilaReporte> ventasPorRango(LocalDate desde, LocalDate hasta, MetodoPago metodo) {
        if (desde == null || hasta == null) {
            throw new IllegalStateException("Elige las dos fechas del rango");
        }
        if (hasta.isBefore(desde)) {
            throw new IllegalStateException("La fecha 'Hasta' debe ser igual o posterior a 'Desde'");
        }
        return reporteRepo.porRango(desde, hasta, metodo);
    }

    @Override
    public List<Integer> aniosConVentas() {
        return reporteRepo.anios();
    }
}
