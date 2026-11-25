package pe.edu.upeu.sysventas.repository;

import pe.edu.upeu.sysventas.model.Categoria;
import pe.edu.upeu.sysventas.model.Marca;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoriaRepository extends AbstractJpaRepository<Categoria, Long>{

    @Override protected String getTableName() { return "categoria"; }
    @Override protected String getPkColumn() { return "id_categoria"; }

    @Override
    protected Categoria mapRow(ResultSet rs) throws SQLException {
        return Categoria.builder()
                .idCategoria(rs.getLong("id_categoria"))
                .nombre(rs.getString("nombre"))
                .build();
    }

    @Override
    protected Categoria insert(Connection conn, Categoria e) throws SQLException {
        long id = executeInsertGetKey(conn,
                "INSERT INTO categoria(nombre) VALUES(?)",
                e.getNombre());
        e.setIdCategoria(id);
        return e;
    }
    @Override
    protected Categoria updateRow(Connection conn, Categoria e) throws SQLException {
        executeUpdate(conn, "UPDATE categoria SET nombre=? WHERE id_categoria=?",
                e.getNombre(),
                e.getIdCategoria()
        ); return e;
    }
}
