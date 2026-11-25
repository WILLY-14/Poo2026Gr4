package pe.edu.upeu.padronmascota.padronmascota.modelo;

/**
 * Consulta, vacuna, desparasitación... (tabla servicio).
 * Se llama ServicioVeterinario para no confundirlo con la capa "servicio" del proyecto.
 */
public class ServicioVeterinario extends ItemVenta {

    @Override
    public String getTipo() {
        return "Servicio";
    }

    @Override
    public boolean controlaStock() {
        return false; // un servicio no tiene stock
    }
}
