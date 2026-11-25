package pe.edu.upeu.padronmascota.padronmascota.componentes;

/**
 * Una pantalla que se vuelve a cargar cuando vuelves a su pestaña.
 * Ej.: registras una mascota y al volver a Propietarios su columna "Mascotas" ya sale actualizada.
 */
public interface Refrescable {
    void refrescar();
}
