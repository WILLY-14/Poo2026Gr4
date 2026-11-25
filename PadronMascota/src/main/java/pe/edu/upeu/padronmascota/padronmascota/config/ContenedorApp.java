package pe.edu.upeu.padronmascota.padronmascota.config;

import pe.edu.upeu.padronmascota.padronmascota.controlador.*;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.*;
import pe.edu.upeu.padronmascota.padronmascota.servicio.*;
import pe.edu.upeu.padronmascota.padronmascota.servicio.impl.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Aquí se "arma" toda la aplicación en un solo lugar (inyección de dependencias a mano).
 *
 * - Servicios: se crea UNO de cada uno y se reutiliza.
 * - Controladores: se crea uno NUEVO cada vez que se abre su pantalla,
 *   y recibe sus servicios por el constructor.
 */
public class ContenedorApp {

    private static ContenedorApp instancia;

    private final Map<Class<?>, Object> servicios = new HashMap<>();
    private final Map<Class<?>, Supplier<Object>> controladores = new HashMap<>();

    private ContenedorApp() {
        // 1. Repositorios: hablan con la base de datos H2 (una clase por tabla principal)
        MascotaRepositorio mascotaRepo = new MascotaRepositorio();
        PropietarioRepositorio propietarioRepo = new PropietarioRepositorio();
        ColorRepositorio colorRepo = new ColorRepositorio();
        RazaRepositorio razaRepo = new RazaRepositorio();
        VeterinarioRepositorio veterinarioRepo = new VeterinarioRepositorio();
        UsuarioRepositorio usuarioRepo = new UsuarioRepositorio();
        ProductoRepositorio productoRepo = new ProductoRepositorio();
        ServicioVeterinarioRepositorio servicioVetRepo = new ServicioVeterinarioRepositorio();
        VacunaRepositorio vacunaRepo = new VacunaRepositorio();
        VacunacionRepositorio vacunacionRepo = new VacunacionRepositorio();
        CitaRepositorio citaRepo = new CitaRepositorio();
        AtencionRepositorio atencionRepo = new AtencionRepositorio();
        RecetaRepositorio recetaRepo = new RecetaRepositorio();
        ProveedorRepositorio proveedorRepo = new ProveedorRepositorio();
        CompraRepositorio compraRepo = new CompraRepositorio();
        VentaRepositorio ventaRepo = new VentaRepositorio();
        ResumenRepositorio resumenRepo = new ResumenRepositorio();
        ReporteRepositorio reporteRepo = new ReporteRepositorio();

        // 2. Servicios: se guardan por su INTERFAZ (polimorfismo)
        servicios.put(IMenuServicio.class, new MenuServicioImpl());
        servicios.put(IUsuarioServicio.class, new UsuarioServicioImpl(usuarioRepo));
        servicios.put(IMascotaServicio.class, new MascotaServicioImpl(mascotaRepo));
        servicios.put(IPropietarioServicio.class, new PropietarioServicioImpl(propietarioRepo));
        servicios.put(IColorServicio.class, new ColorServicioImpl(colorRepo));
        servicios.put(IRazaServicio.class, new RazaServicioImpl(razaRepo));
        servicios.put(IVeterinarioServicio.class, new VeterinarioServicioImpl(veterinarioRepo));
        servicios.put(IProductoServicio.class, new ProductoServicioImpl(productoRepo));
        servicios.put(IServicioVeterinarioServicio.class, new ServicioVeterinarioServicioImpl(servicioVetRepo));
        servicios.put(IVacunaServicio.class, new VacunaServicioImpl(vacunaRepo));
        servicios.put(IVacunacionServicio.class, new VacunacionServicioImpl(vacunacionRepo, vacunaRepo));
        servicios.put(ICitaServicio.class, new CitaServicioImpl(citaRepo));
        servicios.put(IAtencionServicio.class, new AtencionServicioImpl(atencionRepo, citaRepo));
        servicios.put(IRecetaServicio.class, new RecetaServicioImpl(recetaRepo));
        servicios.put(IProveedorServicio.class, new ProveedorServicioImpl(proveedorRepo));
        servicios.put(ICompraServicio.class, new CompraServicioImpl(compraRepo));
        servicios.put(IVentaServicio.class, new VentaServicioImpl(ventaRepo, productoRepo, servicioVetRepo));
        servicios.put(IReporteServicio.class, new ReporteServicioImpl(resumenRepo, reporteRepo));

        // 3. Controladores: cómo crear cada uno
        controladores.put(LoginController.class,
                () -> new LoginController(servicio(IUsuarioServicio.class)));
        controladores.put(PrincipalController.class,
                () -> new PrincipalController(servicio(IMenuServicio.class)));
        // Padrón
        controladores.put(MascotaController.class,
                () -> new MascotaController(servicio(IMascotaServicio.class), servicio(IPropietarioServicio.class),
                        servicio(IRazaServicio.class), servicio(IColorServicio.class)));
        controladores.put(PropietarioController.class,
                () -> new PropietarioController(servicio(IPropietarioServicio.class)));
        controladores.put(RazaController.class,
                () -> new RazaController(servicio(IRazaServicio.class)));
        controladores.put(ColorController.class,
                () -> new ColorController(servicio(IColorServicio.class)));
        // Clínica
        controladores.put(VeterinarioController.class,
                () -> new VeterinarioController(servicio(IVeterinarioServicio.class)));
        controladores.put(CitaController.class,
                () -> new CitaController(servicio(ICitaServicio.class), servicio(IMascotaServicio.class),
                        servicio(IVeterinarioServicio.class), servicio(IPropietarioServicio.class)));
        controladores.put(AtencionController.class,
                () -> new AtencionController(servicio(IAtencionServicio.class), servicio(IMascotaServicio.class),
                        servicio(IVeterinarioServicio.class), servicio(ICitaServicio.class), servicio(IProductoServicio.class),
                        servicio(IServicioVeterinarioServicio.class)));
        controladores.put(RecetaController.class,
                () -> new RecetaController(servicio(IRecetaServicio.class), servicio(IAtencionServicio.class),
                        servicio(IProductoServicio.class), servicio(IMascotaServicio.class)));
        controladores.put(VacunaController.class,
                () -> new VacunaController(servicio(IVacunaServicio.class), servicio(IProductoServicio.class)));
        controladores.put(VacunacionController.class,
                () -> new VacunacionController(servicio(IVacunacionServicio.class), servicio(IMascotaServicio.class),
                        servicio(IVacunaServicio.class), servicio(IPropietarioServicio.class),
                        servicio(IProductoServicio.class)));
        // Ventas
        controladores.put(ProductoController.class,
                () -> new ProductoController(servicio(IProductoServicio.class)));
        controladores.put(ServicioVeterinarioController.class,
                () -> new ServicioVeterinarioController(servicio(IServicioVeterinarioServicio.class)));
        controladores.put(VentaController.class,
                () -> new VentaController(servicio(IVentaServicio.class), servicio(IPropietarioServicio.class),
                        servicio(IAtencionServicio.class), servicio(IServicioVeterinarioServicio.class)));
        // Compras
        controladores.put(ProveedorController.class,
                () -> new ProveedorController(servicio(IProveedorServicio.class)));
        controladores.put(CompraController.class,
                () -> new CompraController(servicio(ICompraServicio.class), servicio(IProveedorServicio.class),
                        servicio(IProductoServicio.class)));
        // Reportes y administración
        controladores.put(ResumenController.class,
                () -> new ResumenController(servicio(IReporteServicio.class)));
        controladores.put(ReporteVentasController.class,
                () -> new ReporteVentasController(servicio(IReporteServicio.class)));
        controladores.put(UsuarioController.class,
                () -> new UsuarioController(servicio(IUsuarioServicio.class), servicio(IVeterinarioServicio.class)));
    }

    public static synchronized ContenedorApp getInstancia() {
        if (instancia == null) {
            instancia = new ContenedorApp();
        }
        return instancia;
    }

    public <T> T servicio(Class<T> tipo) {
        Object s = servicios.get(tipo);
        if (s == null) {
            throw new IllegalStateException("No registraste el servicio " + tipo.getSimpleName() + " en ContenedorApp");
        }
        return tipo.cast(s);
    }

    /** Lo usa el FXMLLoader para crear el controlador que dice fx:controller. */
    public Object crearControlador(Class<?> tipo) {
        Supplier<Object> fabrica = controladores.get(tipo);
        if (fabrica == null) {
            throw new IllegalStateException("No registraste el controlador " + tipo.getSimpleName() + " en ContenedorApp");
        }
        return fabrica.get();
    }
}
