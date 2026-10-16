package pe.edu.upeu.sysventas.model;

import lombok.Data;
import pe.edu.upeu.sysventas.enums.Menus;
import pe.edu.upeu.sysventas.enums.TipoTab;

@Data
public class Acceso {
    String idAcceso;
    String urlAcceso;
    String menuItemNombre;
    Menus menuNombre;
    TipoTab nombreTab;

}
