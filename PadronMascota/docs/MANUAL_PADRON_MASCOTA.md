# Manual del sistema Padrón Mascota

Sistema de escritorio de una veterinaria (padrón de mascotas, clínica, ventas y compras), hecho con
**Java 21, JavaFX 21 (FXML), Maven, H2, HikariCP y Hibernate Validator**,
organizado en capas y aplicando Programación Orientada a Objetos (POO).

---

## 1. Cómo ejecutarlo

1. Abre la carpeta `PadronMascota` en IntelliJ (File > Open > `pom.xml` > *Open as Project*).
2. En el panel **Maven** pulsa ⟳ (*Reload*) para descargar las librerías.
3. Revisa en *File > Project Structure* que **SDK = 21** y **Language level = 21**.
4. Ejecuta la clase `Main`.
5. Entra con uno de estos usuarios:

| Usuario | Clave | Rol | Qué ve en el menú |
|---------|-------|-----|-------------------|
| `admin` | `admin123` | Administrador | Todo |
| `recepcion` | `recepcion123` | Recepción | Inicio, Recepción, Caja, Inventario (productos y servicios) |
| `lcondori` | `vet123` | Veterinario (Luis Condori) | Inicio, Recepción, Clínica, Configuración (razas y colores) |
| `ctorres` | `vet123` | Veterinario (Carmen Torres) | Inicio, Recepción, Clínica, Configuración (razas y colores) |

No hay que instalar ninguna base de datos: **H2** se crea sola en la carpeta `data/`
la primera vez, con los datos del Excel.

---

## 2. Estructura de carpetas

```
PadronMascota/
├── data/                         ← la base H2 (se crea sola: padron_mascota_pro.mv.db)
├── docs/                         ← este manual
├── language/                     ← textos en español, inglés y francés
│   ├── idiomas-es.properties
│   ├── idiomas-en.properties
│   ├── idiomas-fr.properties
│   └── PadronMascota.properties  ← idioma elegido (padronmascota.idioma=es)
├── pom.xml                       ← librerías (Maven)
└── src/main/
    ├── java/
    │   ├── module-info.java
    │   └── pe/edu/upeu/padronmascota/padronmascota/
    │       ├── Main.java, PadronMascotaApp.java   ← arranque
    │       ├── modelo/        Entidad, Persona, Propietario, Mascota, Usuario
    │       ├── enums/         Especie, EstadoVacunacion
    │       ├── dto/           OpcionCombo, OpcionMenu, ResumenPadron
    │       ├── excepcion/     ValidacionException, NoEncontradoException
    │       ├── repositorio/   IRepositorio, RepositorioBase, *Repositorio (SQL)
    │       ├── servicio/      interfaces I*Servicio
    │       │   └── impl/      ServicioCrudBase, *ServicioImpl (reglas de negocio)
    │       ├── seguridad/     Clave (BCrypt), Sesion
    │       ├── config/        DatabaseConfig (H2 + Hikari), ContenedorApp (inyección)
    │       ├── componentes/   GestorVentana, Idioma, Tema, Notificacion, Dialogo,
    │       │                  MarcadorError, TablaConAcciones
    │       └── controlador/   Login, Principal, Mascota, Propietario, Resumen
    └── resources/
        ├── application.properties   ← URL de la base de datos
        ├── db/schema.sql            ← tablas
        ├── db/datos.sql             ← datos del Excel
        ├── css/                     ← base.css + tema-oscuro/azul/verde.css
        └── pe/.../vistas/           ← login, principal, mascotas, propietarios, resumen (.fxml)
```

---

## 3. Las capas (de abajo hacia arriba)

```
 Vista (FXML)  ──►  Controlador  ──►  Servicio (interfaz)  ──►  Repositorio  ──►  H2
 mascotas.fxml      MascotaController  IMascotaServicio          MascotaRepositorio
                                       MascotaServicioImpl
```

| Capa | Qué hace | Ejemplo |
|------|----------|---------|
| **Modelo** | Los datos del sistema (clases simples con get/set y validaciones). | `Mascota`, `Propietario` |
| **Repositorio** | Solo SQL: guardar, buscar, actualizar y borrar en la base de datos. | `MascotaRepositorio.buscar(...)` |
| **Servicio** | Reglas de negocio: validar antes de guardar, no borrar un dueño con mascotas. | `PropietarioServicioImpl.eliminar` |
| **Controlador** | Une la pantalla con el servicio: lee los campos, llama al servicio, muestra mensajes. | `MascotaController.guardar` |
| **Vista** | El diseño de la pantalla en FXML (se puede abrir con Scene Builder). | `mascotas.fxml` |

Cada capa **solo conoce a la de abajo** y la conoce por su **interfaz**.
Así, si mañana cambias H2 por MySQL, solo tocas `DatabaseConfig` y el SQL de los repositorios.

---

## 4. Conceptos de POO en el código

| Concepto | Dónde se ve |
|----------|-------------|
| **Abstracción** | `Entidad` y `Persona` son `abstract`: no se crean solas, solo sus hijas. `IRepositorio`, `IServicioCrud` describen *qué* se hace, no *cómo*. |
| **Encapsulamiento** | Todos los atributos son `private` y se usan con get/set (`Mascota.getNombre()`). `Clave` esconde cómo se calcula la huella BCrypt. |
| **Herencia** | `Propietario extends Persona extends Entidad`; `Usuario extends Entidad`; `MascotaRepositorio extends RepositorioBase`; `MascotaServicioImpl extends ServicioCrudBase`. |
| **Polimorfismo** | Los controladores reciben `IMascotaServicio` (interfaz) y no `MascotaServicioImpl`. `RepositorioBase` llama a `mapear()`, y cada hija lo implementa a su manera. |
| **Composición** | `Mascota` *tiene un* `Propietario`; `ContenedorApp` *tiene* los servicios. |
| **Enums con datos** | `Especie` (PERRO, GATO, OTRO), `EstadoVacunacion` (AL_DIA, PENDIENTE) y `Tema` (CLARO, OSCURO, AZUL, VERDE con su CSS y color). |
| **Genéricos** | `RepositorioBase<T>`, `IServicioCrud<T>`, `OpcionCombo<T>`, `TablaConAcciones<T>`. |

---

## 5. Patrones de diseño usados

- **Inyección de dependencias manual** (`ContenedorApp`): crea los repositorios y servicios una sola vez
  y se los pasa a cada controlador por el **constructor**. El `FXMLLoader` usa
  `setControllerFactory(ContenedorApp.getInstancia()::crearControlador)`.
- **Singleton**: `ContenedorApp.getInstancia()`, el pool de `DatabaseConfig`.
- **Template Method**: `RepositorioBase` tiene el código JDBC repetido y deja a las hijas solo el `mapear()`;
  `ServicioCrudBase` valida siempre antes de `registrar`/`modificar`.
- **Repository**: toda la base de datos está escondida detrás de los repositorios.
- **DTO**: `ResumenPadron` y `FilaReporte` (records) llevan los números de la BD a la pantalla.
- **Template Method** también en `CrudController<T>` (pantallas de catálogo).

---

## 6. Qué pasa al pulsar "Guardar" en Mascotas

1. `MascotaController.guardar()` lee los campos y arma un objeto `Mascota`.
2. Llama a `mascotaServicio.registrar(m)` (o `modificar` si estaba editando).
3. `ServicioCrudBase` valida con Hibernate Validator las anotaciones del modelo:
   `@NotBlank`, `@NotNull`, `@Min(0)`, `@Max(40)`, `@Pattern(regexp = "9\\d{8}")`.
4. Si hay errores lanza `ValidacionException` con un mapa *campo → mensaje*.
   El controlador marca esos campos en rojo con `MarcadorError`.
5. Si todo está bien, `MascotaRepositorio.guardar` hace el `INSERT` y H2 devuelve el id nuevo.
6. El controlador recarga la tabla y muestra una `Notificacion` verde.

---

## 7. Login y roles

- Tabla `usuario` (con `id_rol` hacia la tabla `rol`). Si no existe, `schema.sql` crea **admin / admin123**.
- La clave **no se guarda en texto**: se guarda su hash **BCrypt** (`seguridad/Clave.java`, librería `jbcrypt`).
  BCrypt agrega una "sal" distinta cada vez, así que dos usuarios con la misma clave tienen hashes distintos.
- `UsuarioServicioImpl.login()` devuelve un `Optional<Usuario>`: vacío si el usuario o la clave están mal
  o si el usuario está **Inactivo**.
- El **menú depende del rol**: cada `OpcionMenu` dice qué roles la ven (`Rol.ADMIN`, `Rol.RECEPCION`, `Rol.VETERINARIO`).
- Un usuario con rol Veterinario debe estar unido a un `veterinario` (regla `@AssertTrue isVeterinarioCorrecto`).
- No puedes eliminarte a ti mismo y siempre debe quedar al menos un administrador activo.
- Al editar un usuario, si dejas la contraseña vacía se mantiene la anterior.
- `Sesion` guarda quién entró; su nombre se ve en la barra de abajo. *Sistema > Cerrar sesión* vuelve al login.
- *Sistema* también tiene los submenús **Tema** e **Idioma**.

En la pantalla de login, los **circulitos de colores** cambian el tema y el combo cambia el idioma.
---

## 8. Idiomas (internacionalización)

Los textos **no están escritos en el código**: están en `language/idiomas-xx.properties`.

```properties
# idiomas-es.properties
comun.guardar=Guardar
mascota.total=%d mascota(s)
```

- En el FXML se escriben con `%`: `<Button text="%comun.guardar" />`.
  `GestorVentana` le pasa los textos al `FXMLLoader` con `loader.setResources(Idioma.textos())`.
- En Java se usa `Idioma.t("comun.guardar")` o, con datos, `Idioma.t("mascota.total", 5)`.
- El idioma elegido se guarda en `language/PadronMascota.properties` para la próxima vez.
- Al cambiar el idioma, la pantalla se vuelve a cargar con los textos nuevos.

**Para agregar un idioma** (por ejemplo portugués): copia `idiomas-es.properties` como
`idiomas-pt.properties`, traduce los textos y agrega `{"pt", "Português"}` en `Idioma.DISPONIBLES`.

> Los mensajes de validación (anotaciones del modelo) y los nombres de Especie y Vacunación
> quedan en español, porque vienen del Excel.

---

## 9. Diseño y temas (CSS)

- `css/base.css` define **todo el diseño** de la veterinaria (tema *Clínica*, verde azulado):
  paleta con nombre (`-vet-primario`, `-vet-superficie`, `-vet-texto`...), fondo con **textura de huellitas**
  (`css/img/huellas-*.png`), encabezado de pantalla, paneles tipo tarjeta, campos, botones, tablas, menú y pestañas.
- `tema-oscuro.css`, `tema-azul.css` y `tema-verde.css` **solo cambian los colores** de la paleta.
- Todas las pantallas siguen la misma estructura:
  1. **Encabezado** (`encabezado`): ícono, título y subtítulo (`sub.*` en `language/`).
  2. **Formulario** a la izquierda (`panel-formulario`): título, campos con etiqueta y botones *Guardar* / *Nuevo*.
  3. **Tabla** a la derecha (`panel-tabla`): buscador y filtros, tabla y total abajo.
- Los botones de las tablas son **íconos** (lápiz = editar, papelera = eliminar, ojo = ver...); al pasar el mouse
  se ve su nombre. Las columnas nunca se cortan con "...": si no entran, aparece la barra horizontal.
- El enum `Tema` une cada tema con su archivo CSS. `GestorVentana` recuerda el tema elegido
  aunque cambies de pantalla (del login a la principal).

---

## 10. Base de datos H2

- `application.properties`: `db.url=jdbc:h2:file:./data/padron_mascota_pro;AUTO_SERVER=TRUE`, usuario `Wil2008`, clave `921923203`.
- Es un archivo **nuevo** (`padron_mascota_pro.mv.db`): la base vieja `padron_mascota` no se toca.
- Al arrancar, `DatabaseConfig` ejecuta `db/schema.sql` (crea las tablas que falten y los catálogos)
  y luego `db/datos.sql` (los datos del Excel) solo si la tabla `propietario` está vacía.
- Para **empezar de cero**: cierra el sistema y borra la carpeta `data/`.

**Verla en DBeaver:** Nueva conexión > **H2 Embedded** > en *Path* elige
`...\PadronMascota\data\padron_mascota_pro` (sin `.mv.db`), usuario `Wil2008`, clave `921923203`, y agrega
`;AUTO_SERVER=TRUE` al final de la URL para poder abrirla mientras el sistema está abierto.

### Las 26 tablas + 1 vista (igual que `padron_mascota.architect` y el Excel)

| Grupo | Tablas |
|-------|--------|
| Catálogos | `especie`, `estado_vacunacion`, `estado_cita`, `rol`, `metodo_pago`, `categoria_producto`, `color`, `raza` |
| Padrón | `propietario`, `mascota` |
| Personal | `veterinario`, `usuario` |
| Clínica | `vacuna`, `vacunacion`, `cita`, `atencion`, `atencion_insumo`, `receta`, `detalle_receta` |
| Ventas | `producto`, `servicio`, `venta`, `detalle_venta` |
| Compras | `proveedor`, `compra`, `detalle_compra` |
| Vista | `v_stock_producto` (calcula el stock) |

Reglas que pone la propia base de datos: DNI de 8 dígitos y único, RUC de 11 dígitos y único,
raza única por especie, un veterinario no puede tener dos citas a la misma fecha y hora,
la próxima dosis debe ser después de la vacunación, una factura no se repite para el mismo proveedor,
y cada línea de venta es **un producto o un servicio** (nunca los dos).
---

## 11. Stock, ventas y compras

**El stock no se guarda: se calcula.** La vista `v_stock_producto` suma:

```
stock = comprados (detalle_compra)
      − vendidos  (detalle_venta de ventas Pagadas)
      − usados    (atencion_insumo + 1 por cada vacunación cuya vacuna descuenta un producto)
```

Por eso en *Productos* el stock no se escribe: para subirlo se registra una **Compra**.
Así el número siempre cuadra con el Excel (por ejemplo Pipeta antipulgas: 8 − 3 − 1 = 4).

- **Ventas** (`VentaRepositorio.registrar`): en una **transacción** guarda la venta y sus líneas, y al final
  llama a `verificarStock`. Si algún producto queda en negativo lanza un error y se hace `rollback`: no se guarda nada.
- Una venta puede unirse a una **atención** (opcional); la atención debe ser de una mascota del mismo cliente.
- **Anular** una venta solo cambia su estado a *Anulada*; como la vista solo cuenta las Pagadas, el stock vuelve solo.
- **Atenciones** también usan transacción: sus insumos descuentan stock y la cita pasa a *Atendida*.
- **Eliminar una compra** solo se permite si el stock no queda negativo.
- `ItemVenta` es abstracta; `Producto` y `ServicioVeterinario` heredan de ella (`controlaStock()` es polimórfico).
---

## 12. Pantallas del sistema

El menú sigue el recorrido de un paciente: llega el dueño → se registra la mascota → cita → atención → se cobra.

| Menú | Pantalla | Qué hace |
|------|----------|----------|
| Inicio | Resumen | Lo primero que se ve: los números de la hoja *Resumen* del Excel y dos gráficos. |
| Recepción | Propietarios | DNI, teléfono, dirección, correo; cuántas mascotas tiene. |
| | Mascotas | Primero el dueño, luego especie, raza, color, sexo, fecha de registro, observaciones. *Baja/Alta* hace baja lógica. |
| | Citas | Agenda por veterinario; no permite dos citas a la misma hora. Botón *Cancelar*. |
| Clínica | Atenciones | Consulta con insumos (descuentan stock); puede venir de una cita. |
| | Vacunaciones | Dosis aplicadas, próxima dosis y estado Vigente/Vencida; filtro *Solo vencidas*. |
| | Recetas | Medicamentos con dosis, frecuencia y días para una atención. |
| Caja | Ventas y pagos | Carrito con productos y servicios, atención opcional, comprobante y método de pago. |
| | Reporte de ventas | Ventas pagadas por año, por mes (con gráfico), por día y por rango de fechas y método de pago. |
| Inventario | Productos y accesorios | Precio, stock mínimo y stock calculado. Las vacunas son productos de categoría *Vacuna*. |
| | Servicios | Consulta, baño, corte, etc. |
| | Compras | Factura del proveedor con lote y vencimiento por producto. |
| | Proveedores | RUC, nombre, teléfono. |
| Configuración | Veterinarios | El equipo médico. |
| | Usuarios | Usuario, contraseña (BCrypt), rol, veterinario y estado. |
| | Razas / Colores | Catálogos que usa Mascotas. |
| Sistema | Tema, Idioma, Cerrar sesión, Salir | Opciones del programa. |

Las pantallas de catálogo heredan de **`CrudController<T>`** (Template Method): la clase madre tiene buscar,
guardar, editar y eliminar, y cada hija solo dice sus columnas y cómo leer/llenar el formulario.

---

## 13. Receta: agregar una pantalla nueva

1. **Modelo**: crea la clase en `modelo/` (hereda de `Entidad`) con sus anotaciones de validación.
2. **Tabla**: agrégala en `db/schema.sql` con `CREATE TABLE IF NOT EXISTS`.
3. **Repositorio**: clase que herede de `RepositorioBase<T>` e implemente `IRepositorio<T>`.
4. **Servicio**: interfaz `IXxxServicio` + `XxxServicioImpl extends ServicioCrudBase<T>`.
5. **Vista**: `xxx.fxml` en `vistas/` con `fx:controller` y textos `%clave`.
6. **Controlador**: recibe el servicio por el constructor (si es un CRUD simple, hereda de `CrudController<T>`).
7. **ContenedorApp**: registra el servicio y el controlador.
8. **Menú**: agrega una línea en `MenuServicioImpl` y sus textos en los 3 archivos de `language/`.

---

## 14. Errores comunes

| Error | Solución |
|-------|----------|
| `release version 5 not supported` | Recargar Maven (⟳) y poner SDK/Language level 21. |
| `No se encontró ...language\idiomas-es.properties` | La carpeta `language` debe estar en la raíz del proyecto y el *Working directory* de la configuración de Run debe ser la carpeta del proyecto. |
| `No se pudo abrir la vista ...` | Mira el último `Caused by:` del error: casi siempre es un `fx:id` mal escrito o un `%clave` que falta en los `.properties`. |
| `Database may be already in use` | Hay otro programa usando la base sin `AUTO_SERVER=TRUE` (por ejemplo DBeaver). Ciérralo o agrega esa opción a la URL. |
| `Duplicate entry` / *Ese dato ya está registrado* | Ya existe ese DNI, RUC, usuario o nombre. |
| *No hay stock suficiente de ...* | Registra una Compra de ese producto antes de venderlo o usarlo. |

---

## 14. Crear el programa .exe (Windows)

1. Cierra el sistema si está abierto.
2. En la carpeta del proyecto, dale **doble clic a `crear-exe.bat`**.
   - Busca solo el JDK 21 (el de IntelliJ en `%USERPROFILE%\.jdks`, `JAVA_HOME` o `Program Files`).
   - Maven se descarga solo (`mvnw.cmd`); no hay que instalar nada más.
3. Espera a que diga **LISTO** (la primera vez tarda unos minutos).
4. El programa queda en **`dist\PadronMascota\PadronMascota.exe`**, con su propio Java incluido.

- Para llevarlo a otra PC, copia **toda la carpeta `dist\PadronMascota`** (la otra PC no necesita Java).
- La base de datos (`data\`) y los idiomas (`language\`) van **dentro** de esa carpeta, junto al `.exe`.
- Ponla en una carpeta donde se pueda escribir (Escritorio, Documentos, `D:\...`), **no** en `Program Files`.
- Para crear un acceso directo: clic derecho en `PadronMascota.exe` → *Enviar a* → *Escritorio*.
