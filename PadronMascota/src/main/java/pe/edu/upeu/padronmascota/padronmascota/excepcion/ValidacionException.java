package pe.edu.upeu.padronmascota.padronmascota.excepcion;

import java.util.Map;

/**
 * Se lanza cuando un objeto no pasa las validaciones (@NotBlank, @Pattern, @Min...).
 * Lleva los errores por campo: "telefono" -> "El teléfono debe tener 9 dígitos..."
 */
public class ValidacionException extends RuntimeException {

    private final Map<String, String> errores;

    public ValidacionException(Map<String, String> errores) {
        super(errores.values().iterator().next());
        this.errores = errores;
    }

    public Map<String, String> getErrores() {
        return errores;
    }
}
