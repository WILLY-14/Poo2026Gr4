package pe.edu.upeu.padronmascotas.repository;

import pe.edu.upeu.padronmascotas.model.Mascota;

public class MascotaRepository extends AbstractJpaRepository<Mascota, Long> {
    private long sequence = 1;

    @Override
    protected Long getId(Mascota entity) {
        return entity.getIdMascota();
    }

    @Override
    protected void setId(Mascota entity, Long id) {
        entity.setIdMascota(id);
    }

    @Override
    protected Long generateId() {
        return sequence++;
    }
}