package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.excepcion.ValidacionException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Revisa las anotaciones del modelo (@NotBlank, @Pattern, @Min...).
 * Lo usan ServicioCrudBase y VentaServicioImpl.
 */
public final class Validador {

    // Un solo validador para toda la app (crearlo es lento).
    // ParameterMessageInterpolator: usa los mensajes tal cual los escribimos en las anotaciones.
    private static final Validator VALIDADOR = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory()
            .getValidator();

    private Validador() {
    }

    /** Si el objeto tiene errores, lanza ValidacionException con un mensaje por campo. */
    public static <T> void validar(T obj) {
        Set<ConstraintViolation<T>> violaciones = VALIDADOR.validate(obj);
        if (violaciones.isEmpty()) {
            return;
        }
        Map<String, String> errores = new LinkedHashMap<>();
        violaciones.stream()
                .sorted(Comparator.comparing(v -> v.getPropertyPath().toString()))
                .forEach(v -> errores.putIfAbsent(v.getPropertyPath().toString(), v.getMessage()));
        throw new ValidacionException(errores);
    }
}
