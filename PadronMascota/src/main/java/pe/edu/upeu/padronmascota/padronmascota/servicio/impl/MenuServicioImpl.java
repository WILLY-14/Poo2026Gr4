package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.dto.OpcionMenu;
import pe.edu.upeu.padronmascota.padronmascota.enums.Rol;
import pe.edu.upeu.padronmascota.padronmascota.seguridad.Sesion;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IMenuServicio;

import java.util.List;

import static pe.edu.upeu.padronmascota.padronmascota.enums.Rol.ADMIN;
import static pe.edu.upeu.padronmascota.padronmascota.enums.Rol.RECEPCION;
import static pe.edu.upeu.padronmascota.padronmascota.enums.Rol.VETERINARIO;

public class MenuServicioImpl implements IMenuServicio {

    // Para agregar una pantalla nueva: crea su .fxml, agrega una línea aquí
    // y pon sus textos en language/idiomas-xx.properties.
    // Se guardan CLAVES de idioma, no textos: el PrincipalController las traduce.
    // Al final van los roles que la pueden ver (sin roles = todos).
    // Orden = el recorrido de un paciente en la veterinaria:
    // llega el dueño -> se registra la mascota -> cita -> atención -> se cobra -> se repone stock.
    private static final List<OpcionMenu> TODAS = List.of(
            // 1. Inicio: cómo va el día
            new OpcionMenu("menu.inicio", "opcion.resumen", "resumen.fxml"),

            // 2. Recepción: dueño, mascota y cita
            new OpcionMenu("menu.recepcion", "opcion.propietarios", "propietarios.fxml"),
            new OpcionMenu("menu.recepcion", "opcion.mascotas", "mascotas.fxml"),
            new OpcionMenu("menu.recepcion", "opcion.citas", "citas.fxml"),

            // 3. Clínica: el veterinario atiende, vacuna y receta
            new OpcionMenu("menu.clinica", "opcion.atenciones", "atenciones.fxml", ADMIN, VETERINARIO),
            new OpcionMenu("menu.clinica", "opcion.vacunaciones", "vacunaciones.fxml", ADMIN, VETERINARIO),
            new OpcionMenu("menu.clinica", "opcion.recetas", "recetas.fxml", ADMIN, VETERINARIO),

            // 4. Caja: se cobra y se revisa lo vendido
            new OpcionMenu("menu.caja", "opcion.nuevaVenta", "ventas.fxml", ADMIN, RECEPCION),
            new OpcionMenu("menu.caja", "opcion.reporteVentas", "reporte_ventas.fxml", ADMIN, RECEPCION),

            // 5. Inventario: lo que se vende y cómo se repone
            // (Las vacunas son Productos de categoría "Vacuna")
            new OpcionMenu("menu.inventario", "opcion.productos", "productos.fxml", ADMIN, RECEPCION),
            new OpcionMenu("menu.inventario", "opcion.servicios", "servicios.fxml", ADMIN, RECEPCION),
            new OpcionMenu("menu.inventario", "opcion.compras", "compras.fxml", ADMIN),
            new OpcionMenu("menu.inventario", "opcion.proveedores", "proveedores.fxml", ADMIN),

            // 6. Configuración: datos que se llenan una vez
            new OpcionMenu("menu.configuracion", "opcion.veterinarios", "veterinarios.fxml", ADMIN),
            new OpcionMenu("menu.configuracion", "opcion.usuarios", "usuarios.fxml", ADMIN),
            new OpcionMenu("menu.configuracion", "opcion.razas", "razas.fxml", ADMIN, VETERINARIO),
            new OpcionMenu("menu.configuracion", "opcion.colores", "colores.fxml", ADMIN, VETERINARIO)
    );

    /** Solo las opciones que puede ver el rol del usuario que entró. */
    @Override
    public List<OpcionMenu> opciones() {
        Rol rol = Sesion.activa() ? Sesion.usuario().getRol() : ADMIN;
        return TODAS.stream().filter(op -> op.permitidaPara(rol)).toList();
    }
}
