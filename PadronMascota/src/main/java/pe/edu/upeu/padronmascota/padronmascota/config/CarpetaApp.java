package pe.edu.upeu.padronmascota.padronmascota.config;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * La carpeta donde están {@code data/} (base H2) y {@code language/} (idiomas).
 *
 * <ul>
 *   <li>Desde IntelliJ: la carpeta del proyecto (la de trabajo).</li>
 *   <li>Desde el .exe (jpackage): la carpeta del programa, aunque lo abran desde un acceso directo.</li>
 * </ul>
 */
public final class CarpetaApp {

    private static final Path BASE = calcular();

    private CarpetaApp() {
    }

    public static Path base() {
        return BASE;
    }

    private static Path calcular() {
        String exe = System.getProperty("jpackage.app-path");   // lo pone el lanzador del .exe
        if (exe != null) {
            Path dir = Path.of(exe).toAbsolutePath().getParent();
            // Windows: el .exe está junto a data/. Linux/Mac: el lanzador está en bin/
            for (Path p = dir; p != null; p = p.getParent()) {
                if (Files.isDirectory(p.resolve("language")) || Files.isDirectory(p.resolve("data"))) {
                    return p;
                }
            }
            return dir;
        }
        return Path.of("").toAbsolutePath();
    }
}
