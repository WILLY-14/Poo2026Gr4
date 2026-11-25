package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import pe.edu.upeu.padronmascota.padronmascota.modelo.ServicioVeterinario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Tabla servicio (consultas, vacunas, desparasitación...). */
public class ServicioVeterinarioRepositorio extends RepositorioBase<ServicioVeterinario>
        implements IRepositorio<ServicioVeterinario> {

    private static final String SELECT = "SELECT id_servicio, nombre, precio FROM servicio ";

    @Override
    protected ServicioVeterinario mapear(ResultSet rs) throws SQLException {
        ServicioVeterinario s = new ServicioVeterinario();
        s.setId(rs.getInt("id_servicio"));
        s.setNombre(rs.getString("nombre"));
        s.setPrecio(rs.getBigDecimal("precio"));
        return s;
    }

    @Override
    public ServicioVeterinario guardar(ServicioVeterinario s) {
        s.setId(insertar("INSERT INTO servicio (nombre, precio) VALUES (?, ?)", s.getNombre(), s.getPrecio()));
        return s;
    }

    @Override
    public ServicioVeterinario actualizar(ServicioVeterinario s) {
        ejecutar("UPDATE servicio SET nombre = ?, precio = ? WHERE id_servicio = ?", s.getNombre(), s.getPrecio(), s.getId());
        return s;
    }

    @Override
    public void eliminar(int id) {
        ejecutar("DELETE FROM servicio WHERE id_servicio = ?", id);
    }

    @Override
    public Optional<ServicioVeterinario> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE id_servicio = ?", id);
    }

    @Override
    public List<ServicioVeterinario> listar() {
        return consultar(SELECT + "ORDER BY id_servicio");
    }

    public List<ServicioVeterinario> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return listar();
        }
        return consultar(SELECT + "WHERE LOWER(nombre) LIKE ? ORDER BY id_servicio", "%" + texto.trim().toLowerCase() + "%");
    }

    public int contarVentas(int idServicio) {
        return contarFilas("SELECT COUNT(*) FROM detalle_venta WHERE id_servicio = ?", idServicio);
    }
}
