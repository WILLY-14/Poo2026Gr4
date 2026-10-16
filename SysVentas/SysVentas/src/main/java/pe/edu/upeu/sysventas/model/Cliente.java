package pe.edu.upeu.sysventas.model;

import lombok.Getter;
import lombok.Setter;
import pe.edu.upeu.sysventas.enums.TipoDocumento;
@Getter
@Setter
public class Cliente {
    String dniruc;
    String nombres;
    String repLegal;
    String direccion;
    TipoDocumento tipoDocumento;
}
