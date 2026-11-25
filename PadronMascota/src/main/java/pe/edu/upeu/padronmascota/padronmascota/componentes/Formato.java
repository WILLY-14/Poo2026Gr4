package pe.edu.upeu.padronmascota.padronmascota.componentes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Cómo se muestran y se leen el dinero y las fechas en las pantallas. */
public final class Formato {

    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private Formato() {
    }

    /** 45 -> "S/ 45.00" */
    public static String dinero(BigDecimal monto) {
        return monto == null ? "" : "S/ " + monto.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public static String fechaHora(LocalDateTime fecha) {
        return fecha == null ? "" : fecha.format(FECHA_HORA);
    }

    public static String fecha(java.time.LocalDate fecha) {
        return fecha == null ? "" : fecha.format(FECHA);
    }

    public static String hora(java.time.LocalTime hora) {
        return hora == null ? "" : hora.format(HORA);
    }

    /** "9:30" o "09:30" -> 09:30. Vacío -> null. Lanza DateTimeParseException si no es una hora. */
    public static java.time.LocalTime leerHora(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        String t = texto.trim();
        if (t.length() == 4 && t.charAt(1) == ':') {
            t = "0" + t;
        }
        return java.time.LocalTime.parse(t, HORA);
    }

    /** "12,50" o "12.50" -> 12.50. Vacío -> null. Lanza NumberFormatException si no es número. */
    public static BigDecimal leerDinero(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return new BigDecimal(texto.trim().replace(",", "."));
    }

    /** "5" -> 5. Vacío -> null. Lanza NumberFormatException si no es número entero. */
    public static Integer leerEntero(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return Integer.parseInt(texto.trim());
    }
}
