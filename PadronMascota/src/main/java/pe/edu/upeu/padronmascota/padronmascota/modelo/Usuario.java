package pe.edu.upeu.padronmascota.padronmascota.modelo;

import pe.edu.upeu.padronmascota.padronmascota.enums.Rol;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Quien entra al sistema (tabla usuario). La clave se guarda como hash BCrypt. */
public class Usuario extends Entidad {

    @NotBlank(message = "El usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El usuario debe tener de 3 a 50 letras")
    @Pattern(regexp = "[a-zA-Z0-9._]+", message = "El usuario solo puede tener letras, números, punto y _")
    private String username;

    private String clave;          // hash BCrypt, nunca la clave en texto

    @NotNull(message = "Elige el rol")
    private Rol rol;

    private Veterinario veterinario;   // solo si el rol es Veterinario

    private boolean activo = true;     // columna estado: 'Activo' / 'Inactivo'

    private String claveTexto;         // la clave que se escribió en el formulario (NO se guarda)

    /** Usuario.id_veterinario solo se llena si el rol es Veterinario. */
    @AssertTrue(message = "Un usuario Veterinario debe estar unido a un veterinario")
    public boolean isVeterinarioCorrecto() {
        return rol != Rol.VETERINARIO || veterinario != null;
    }

    /** Lo que se ve en la barra de abajo: el nombre del doctor o el username. */
    public String getNombre() {
        return veterinario != null ? veterinario.getNombre() : username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim().toLowerCase();
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getClaveTexto() {
        return claveTexto;
    }

    public void setClaveTexto(String claveTexto) {
        this.claveTexto = claveTexto;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getEstado() {
        return activo ? "Activo" : "Inactivo";
    }

    @Override
    public String toString() {
        return username;
    }
}
