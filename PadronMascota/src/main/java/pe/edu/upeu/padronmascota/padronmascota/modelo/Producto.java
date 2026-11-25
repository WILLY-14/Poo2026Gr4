package pe.edu.upeu.padronmascota.padronmascota.modelo;

import pe.edu.upeu.padronmascota.padronmascota.enums.CategoriaProducto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Accesorios, comida, medicamentos, vacunas, insumos (tabla producto).
 * HERENCIA: nombre y precio vienen de ItemVenta.
 * El stock NO se guarda: se calcula = comprados - vendidos - usados (vista v_stock_producto).
 */
public class Producto extends ItemVenta {

    @NotNull(message = "Elige la categoría")
    private CategoriaProducto categoria;

    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    // Calculados por la vista (la "Consulta stock" del Excel)
    private int comprados;
    private int vendidos;
    private int usados;
    private BigDecimal ultimoCosto;

    @Override
    public String getTipo() {
        return "Producto";
    }

    @Override
    public boolean controlaStock() {
        return true;
    }

    /** Stock actual = comprados - vendidos (ventas pagadas) - usados (insumos y vacunas). */
    public int getStock() {
        return comprados - vendidos - usados;
    }

    /** STOCK BAJO si el stock actual es menor o igual al mínimo. */
    public boolean stockBajo() {
        return stockMinimo != null && getStock() <= stockMinimo;
    }

    /** Margen = precio de venta - último costo de compra. */
    public BigDecimal getMargen() {
        return ultimoCosto == null || getPrecio() == null ? null : getPrecio().subtract(ultimoCosto);
    }

    public CategoriaProducto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaProducto categoria) {
        this.categoria = categoria;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public int getComprados() {
        return comprados;
    }

    public void setComprados(int comprados) {
        this.comprados = comprados;
    }

    public int getVendidos() {
        return vendidos;
    }

    public void setVendidos(int vendidos) {
        this.vendidos = vendidos;
    }

    public int getUsados() {
        return usados;
    }

    public void setUsados(int usados) {
        this.usados = usados;
    }

    public BigDecimal getUltimoCosto() {
        return ultimoCosto;
    }

    public void setUltimoCosto(BigDecimal ultimoCosto) {
        this.ultimoCosto = ultimoCosto;
    }
}
