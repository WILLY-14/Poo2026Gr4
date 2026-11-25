package pe.edu.upeu.padronmascota.padronmascota.modelo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Dueño de las mascotas (tabla propietario). HERENCIA: nombre y teléfono vienen de Persona. */
public class Propietario extends Persona {

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos")
    private String dni;

    @Size(max = 150, message = "La dirección puede tener hasta 150 letras")
    private String direccion;

    @Email(message = "El email debe tener el formato usuario@dominio.com")
    @Size(max = 100, message = "El email puede tener hasta 100 letras")
    private String email;

    private int numeroMascotas;   // calculado (columna "n° mascotas" del Excel)

    public Propietario() {
    }

    public Propietario(int id, String nombre, String telefono) {
        setId(id);
        setNombre(nombre);
        setTelefono(telefono);
    }

    @Override
    public String getTipo() {
        return "Propietario";
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni == null ? null : dni.trim();
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = vacioANull(direccion);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = vacioANull(email);
    }

    public int getNumeroMascotas() {
        return numeroMascotas;
    }

    public void setNumeroMascotas(int numeroMascotas) {
        this.numeroMascotas = numeroMascotas;
    }

    /** Los campos opcionales vacíos se guardan como NULL. */
    static String vacioANull(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
