package pe.edu.upeu.sysventas.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
public class Perfil {
    Long idPerfil;
    String nombre;
    String codigo;
    List<Acceso> accesos;
}
