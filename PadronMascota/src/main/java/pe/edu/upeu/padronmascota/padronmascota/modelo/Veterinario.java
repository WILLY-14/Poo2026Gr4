package pe.edu.upeu.padronmascota.padronmascota.modelo;

/** Doctor de la veterinaria (tabla veterinario). HERENCIA: reutiliza nombre y teléfono de Persona. */
public class Veterinario extends Persona {

    private int numeroAtenciones;   // calculado (columna "n° atenciones" del Excel)

    public Veterinario() {
    }

    public Veterinario(int id, String nombre, String telefono) {
        setId(id);
        setNombre(nombre);
        setTelefono(telefono);
    }

    @Override
    public String getTipo() {
        return "Veterinario";
    }

    public int getNumeroAtenciones() {
        return numeroAtenciones;
    }

    public void setNumeroAtenciones(int numeroAtenciones) {
        this.numeroAtenciones = numeroAtenciones;
    }
}
