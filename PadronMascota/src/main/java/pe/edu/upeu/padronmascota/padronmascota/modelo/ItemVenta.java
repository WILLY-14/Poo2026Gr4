package pe.edu.upeu.padronmascota.padronmascota.modelo;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Todo lo que se puede vender: un Producto (accesorio, comida...) o un ServicioVeterinario.
 * ABSTRACCIÓN + HERENCIA: tienen nombre y precio en común.
 * POLIMORFISMO: cada hija dice su tipo y si controla stock.
 */
public abstract class ItemVenta extends Entidad {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre puede tener hasta 100 letras")
    private String nombre;

    // BigDecimal y no double: el dinero necesita decimales exactos
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
    @DecimalMax(value = "99999.99", message = "El precio máximo es 99999.99")
    @Digits(integer = 5, fraction = 2, message = "El precio puede tener hasta 2 decimales")
    private BigDecimal precio;

    /** "Producto" o "Servicio". */
    public abstract String getTipo();

    /** true si al venderlo hay que descontar stock. */
    public abstract boolean controlaStock();

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.trim();
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    /** Dos items son el mismo si son del mismo tipo y tienen el mismo id. */
    public boolean esMismo(ItemVenta otro) {
        return otro != null && getClass() == otro.getClass() && getId() == otro.getId();
    }

    @Override
    public String toString() {
        return nombre;
    }
}
