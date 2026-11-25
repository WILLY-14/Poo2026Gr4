package pe.edu.upeu.sysventas.repository;

import pe.edu.upeu.sysventas.model.Categoria;
import pe.edu.upeu.sysventas.model.Marca;
import pe.edu.upeu.sysventas.model.UnidMedida;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UnidadMedidaRepository extends AbstractJpaRepository<UnidMedida, Long>{

    @Override
    protected String getTableName() {return "unid_medida";}

    @Override
    protected String getPkColumn() {return "id_unidad";}

    @Override
    protected UnidMedida insert(Connection connection, UnidMedida entity) throws SQLException {
        long id = executeInsertGetKey(connection,
                "INSERT INTO unid_medida(nombre_medida) VALUES(?)",
                entity.getNombreMedida());
        entity.setIdUnidad(id);
        return entity;
    }

    @Override
    protected UnidMedida updateRow(Connection connection, UnidMedida entity) throws SQLException {
        executeUpdate(connection, "UPDATE unid_medida SET nombre_medida=? WHERE id_unidad=?",
                entity.getNombreMedida(),
                entity.getIdUnidad()
        ); return entity;
    }

    @Override
    protected UnidMedida mapRow(ResultSet rs) throws SQLException {
        return UnidMedida.builder()
                .idUnidad(rs.getLong("id_unidad"))
                .nombreMedida(rs.getString("nombre_medida"))
                .build();
    }
}
