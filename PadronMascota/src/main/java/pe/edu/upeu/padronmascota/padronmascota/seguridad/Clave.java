package pe.edu.upeu.padronmascota.padronmascota.seguridad;

import org.mindrot.jbcrypt.BCrypt;

/**
 * La clave NO se guarda tal cual en la base de datos: se guarda su hash BCrypt.
 * BCrypt agrega una "sal" al azar, así dos usuarios con la misma clave tienen hash distinto.
 * Al iniciar sesión, BCrypt.checkpw compara lo que escribió el usuario con el hash guardado.
 */
public final class Clave {

    private Clave() {
    }

    /** "admin123" -> "$2a$10$...." (60 caracteres, distinto cada vez) */
    public static String hash(String clave) {
        return BCrypt.hashpw(clave, BCrypt.gensalt(10));
    }

    public static boolean coincide(String claveEscrita, String hashGuardado) {
        if (hashGuardado == null || !hashGuardado.startsWith("$2")) {
            return false;
        }
        return BCrypt.checkpw(claveEscrita, hashGuardado);
    }
}
