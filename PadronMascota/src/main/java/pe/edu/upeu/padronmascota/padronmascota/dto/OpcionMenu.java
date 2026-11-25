package pe.edu.upeu.padronmascota.padronmascota.dto;

import pe.edu.upeu.padronmascota.padronmascota.enums.Rol;

import java.util.EnumSet;
import java.util.Set;

/**
 * Una opción del MenuBar: en qué menú va, qué texto muestra, qué vista (.fxml) abre
 * y qué roles la pueden ver. menu y texto son CLAVES de idioma (por ejemplo "menu.padron").
 */
public class OpcionMenu {

    private final String menu;
    private final String texto;
    private final String vista;
    private final Set<Rol> roles;

    public OpcionMenu(String menu, String texto, String vista, Rol... roles) {
        this.menu = menu;
        this.texto = texto;
        this.vista = vista;
        // Sin roles = la ven todos
        this.roles = roles.length == 0 ? EnumSet.allOf(Rol.class) : EnumSet.of(roles[0], roles);
    }

    public String getMenu() {
        return menu;
    }

    public String getTexto() {
        return texto;
    }

    public String getVista() {
        return vista;
    }

    public boolean permitidaPara(Rol rol) {
        return roles.contains(rol);
    }
}
