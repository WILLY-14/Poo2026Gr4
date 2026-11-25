package pe.edu.upeu.padronmascota.padronmascota.modelo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** A quién se le compran los productos (tabla proveedor). RUC único. */
public class Proveedor extends Entidad {

    @NotBlank(message = "El RUC es obligatorio")
    @Pattern(regexp = "\\d{11}", message = "El RUC debe tener 11 dígitos")
    private String ruc;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre puede tener hasta 120 letras")
    private String nombre;

    @Pattern(regexp = "\\d{6,15}", message = "El teléfono debe tener de 6 a 15 dígitos")
    private String telefono;

    private int numeroCompras;   // calculado (columna "n° compras" del Excel)

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc == null ? null : ruc.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.trim();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = Propietario.vacioANull(telefono);
    }

    public int getNumeroCompras() {
        return numeroCompras;
    }

    public void setNumeroCompras(int numeroCompras) {
        this.numeroCompras = numeroCompras;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
