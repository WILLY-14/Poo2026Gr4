package pe.edu.upeu.sisventas.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.sisventas.enums.Menus;
import pe.edu.upeu.sisventas.enums.TipoTab;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Acceso {
    String idAcceso;
    String urlAcceso;
    Menus menuNombre;
    String menuItemNombre;
    TipoTab nombreTab;
}
