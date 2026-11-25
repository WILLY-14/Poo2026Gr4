package pe.edu.upeu.padronmascota.padronmascota.dto;

/**
 * Una opción para los ComboBox de filtro. valor = null significa "Todos".
 * Ejemplo: new OpcionCombo<>(null, "Todas"), new OpcionCombo<>(Especie.PERRO, "Perro")
 */
public class OpcionCombo<T> {

    private final T valor;
    private final String texto;

    public OpcionCombo(T valor, String texto) {
        this.valor = valor;
        this.texto = texto;
    }

    public T getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return texto;
    }
}
