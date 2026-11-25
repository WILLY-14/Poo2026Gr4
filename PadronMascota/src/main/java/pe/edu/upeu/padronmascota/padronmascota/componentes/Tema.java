package pe.edu.upeu.padronmascota.padronmascota.componentes;

/**
 * Los temas del sistema (ENUM con datos): clave de idioma, archivo CSS y un color
 * para dibujar su circulito en el login.
 */
public enum Tema {
    CLARO("tema.claro", null, "#0e8a83"),
    OSCURO("tema.oscuro", "/css/tema-oscuro.css", "#1d2626"),
    AZUL("tema.azul", "/css/tema-azul.css", "#2563a8"),
    VERDE("tema.verde", "/css/tema-verde.css", "#2e8b57");

    private final String clave;
    private final String css;
    private final String color;

    Tema(String clave, String css, String color) {
        this.clave = clave;
        this.css = css;
        this.color = color;
    }

    public String getCss() {
        return css;
    }

    public String getColor() {
        return color;
    }

    /** Nombre en el idioma actual. */
    public String nombre() {
        return Idioma.t(clave);
    }

    /** El tema que está puesto ahora. */
    public static Tema actual() {
        String css = GestorVentana.getTemaActual();
        for (Tema t : values()) {
            if (java.util.Objects.equals(t.css, css)) {
                return t;
            }
        }
        return CLARO;
    }

    public void aplicar() {
        GestorVentana.cambiarTema(css);
    }
}
