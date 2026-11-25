# Manual Didáctico — Construcción de Padrón Mascota (JavaFX + Maven + H2)

> Sistema de escritorio para una **veterinaria**, construido con **Java 21**, **JavaFX 21**, **Maven**, **H2** (base de datos embebida en archivo), **HikariCP** (pool de conexiones), **Jakarta Validation / Hibernate Validator** y **BCrypt**, con una arquitectura en capas **Modelo → Repositorio → Servicio → Controlador → Vista (FXML)** e **inyección de dependencias manual** (sin Spring).
>
> El manual es **secuencial**: si creas cada archivo en el orden en que aparece, llegas al proyecto completo, compilable y ejecutable. El código que se muestra está copiado **tal cual** del proyecto real. Los módulos que repiten exactamente el mismo patrón (por ejemplo `Color`, `Raza`, `Proveedor`) se resumen en el **Anexo A** para no alargar el manual con código idéntico.

---

## Índice

1. [Paso 0 — Crear el proyecto Maven y su estructura](#1-paso-0--crear-el-proyecto-maven-y-su-estructura)
2. [Paso 1 — `pom.xml` (dependencias)](#2-paso-1--pomxml-dependencias)
3. [Paso 2 — `module-info.java`](#3-paso-2--module-infojava)
4. [Paso 3 — Base de datos H2 (`application.properties`, `schema.sql`, paquete `config`)](#4-paso-3--base-de-datos-h2)
5. [Paso 4 — Paquete `enums`](#5-paso-4--paquete-enums)
6. [Paso 5 — Paquete `excepcion`](#6-paso-5--paquete-excepcion)
7. [Paso 6 — Paquete `modelo`](#7-paso-6--paquete-modelo)
8. [Paso 7 — Paquete `dto`](#8-paso-7--paquete-dto)
9. [Paso 8 — Paquete `repositorio` (acceso a datos con JDBC)](#9-paso-8--paquete-repositorio)
10. [Paso 9 — Paquete `servicio` (reglas de negocio)](#10-paso-9--paquete-servicio)
11. [Paso 10 — Paquete `seguridad` (login y BCrypt)](#11-paso-10--paquete-seguridad)
12. [Paso 11 — Paquete `componentes` (utilidades de pantalla)](#12-paso-11--paquete-componentes)
13. [Paso 12 — `ContenedorApp` (inyección de dependencias)](#13-paso-12--contenedorapp)
14. [Paso 13 — Paquete `controlador` (MVC)](#14-paso-13--paquete-controlador)
15. [Paso 14 — Vistas FXML](#15-paso-14--vistas-fxml)
16. [Paso 15 — Diseño (CSS, temas) e idiomas](#16-paso-15--diseño-css-temas-e-idiomas)
17. [Paso 16 — Arranque de la aplicación (`Main` y `PadronMascotaApp`)](#17-paso-16--arranque-de-la-aplicación)
18. [Paso 17 — Ejecutar el proyecto y crear el `.exe`](#18-paso-17--ejecutar-el-proyecto-y-crear-el-exe)
19. [Paso 18 — Recorrido funcional: qué ocurre al pulsar "Guardar" en Mascotas](#19-paso-18--recorrido-funcional-guardar-en-mascotas)
20. [Anexo A — Resto de módulos del sistema](#20-anexo-a--resto-de-módulos-del-sistema)
21. [Anexo B — Receta para agregar una pantalla nueva](#21-anexo-b--receta-para-agregar-una-pantalla-nueva)
22. [Anexo C — Limitaciones actuales y mejoras sugeridas](#22-anexo-c--limitaciones-actuales-y-mejoras-sugeridas)
23. [Glosario](#23-glosario)

---

## 1. Paso 0 — Crear el proyecto Maven y su estructura

```
PadronMascota/
 ├── pom.xml
 ├── mvnw, mvnw.cmd, .mvn/          ← Maven Wrapper (no hay que instalar Maven)
 ├── crear-exe.bat                  ← crea el programa .exe (sección 18)
 ├── instalador/icono.ico           ← ícono del .exe
 ├── data/                          ← la base H2 se crea sola aquí
 ├── language/                      ← idiomas-es/en/fr.properties
 ├── docs/                          ← manuales
 └── src/main/
     ├── java/
     │   ├── module-info.java
     │   └── pe/edu/upeu/padronmascota/padronmascota/
     │       ├── Main.java
     │       ├── PadronMascotaApp.java
     │       ├── componentes/
     │       ├── config/
     │       ├── controlador/
     │       ├── dto/
     │       ├── enums/
     │       ├── excepcion/
     │       ├── modelo/
     │       ├── repositorio/
     │       ├── seguridad/
     │       └── servicio/
     │           └── impl/
     └── resources/
         ├── application.properties
         ├── icono.png
         ├── css/  (base.css, tema-oscuro.css, tema-azul.css, tema-verde.css, img/)
         ├── db/   (schema.sql, datos.sql)
         └── pe/edu/upeu/padronmascota/padronmascota/vistas/   (*.fxml)
```

Comando para crear las carpetas de una vez (Git Bash o PowerShell, dentro de la carpeta donde irá el proyecto):

```bash
mkdir -p PadronMascota/src/main/java/pe/edu/upeu/padronmascota/padronmascota/{componentes,config,controlador,dto,enums,excepcion,modelo,repositorio,seguridad,servicio/impl}
mkdir -p PadronMascota/src/main/resources/{css/img,db,pe/edu/upeu/padronmascota/padronmascota/vistas}
mkdir -p PadronMascota/{data,language,docs,instalador}
```

---

## 2. Paso 1 — `pom.xml` (dependencias)

Crea `PadronMascota/pom.xml`:

`pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>pe.edu.upeu.padronmascota</groupId>
    <artifactId>PadronMascota</artifactId>
    <version>1.0</version>

    <properties>
        <maven.compiler.release>21</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <javafx.version>21.0.5</javafx.version>
    </properties>

    <dependencies>
        <!-- Pantallas -->
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-fxml</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <!-- H2: base de datos embebida (igual que SysVentas), no hay que instalar nada -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>2.4.240</version>
        </dependency>
        <!-- Pool de conexiones -->
        <dependency>
            <groupId>com.zaxxer</groupId>
            <artifactId>HikariCP</artifactId>
            <version>6.3.0</version>
        </dependency>
        <!-- Validaciones con anotaciones: @NotBlank, @Pattern, @Min, @Max -->
        <dependency>
            <groupId>org.hibernate.validator</groupId>
            <artifactId>hibernate-validator</artifactId>
            <version>8.0.3.Final</version>
        </dependency>
        <!-- BCrypt: guarda las claves como hash (el Excel lo pide así) -->
        <dependency>
            <groupId>org.mindrot</groupId>
            <artifactId>jbcrypt</artifactId>
            <version>0.4</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Fija Java 21 para que IntelliJ no use la versión 5 por defecto -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
                <configuration>
                    <release>21</release>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-maven-plugin</artifactId>
                <version>0.0.8</version>
                <configuration>
                    <mainClass>pe.edu.upeu.padronmascota.padronmascota/pe.edu.upeu.padronmascota.padronmascota.Main</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### ¿Para qué sirve cada dependencia?

| Dependencia | Para qué |
|---|---|
| `javafx-controls`, `javafx-fxml` | Las pantallas: botones, tablas, campos, y la carga de los archivos `.fxml`. |
| `h2` | Base de datos **embebida**: vive en un archivo dentro de `data/`, no hay que instalar un servidor. |
| `HikariCP` | **Pool de conexiones**: reutiliza conexiones abiertas en vez de abrir una nueva en cada consulta. |
| `hibernate-validator` | Implementa **Jakarta Validation**: lee las anotaciones `@NotBlank`, `@Pattern`, `@Min`... del modelo. |
| `jbcrypt` | Guarda las contraseñas como **hash BCrypt**, nunca en texto plano. |
| `maven-compiler-plugin` | Fija Java 21. |
| `javafx-maven-plugin` | Permite ejecutar con `mvn javafx:run`. |

---

## 3. Paso 2 — `module-info.java`

Crea `src/main/java/module-info.java`:

`src/main/java/module-info.java`

```java
module pe.edu.upeu.padronmascota.padronmascota {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.zaxxer.hikari;
    requires com.h2database;
    requires jakarta.validation;
    requires org.hibernate.validator;
    requires com.fasterxml.classmate; // la usa Hibernate Validator por dentro
    requires org.jboss.logging;       // la usa Hibernate Validator por dentro
    requires jbcrypt;                 // hash BCrypt de las claves

    // JavaFX necesita "abrir" los controladores para leer los @FXML
    opens pe.edu.upeu.padronmascota.padronmascota.controlador to javafx.fxml;
    // Hibernate Validator lee las anotaciones del modelo
    opens pe.edu.upeu.padronmascota.padronmascota.modelo;

    exports pe.edu.upeu.padronmascota.padronmascota;
}
```

### Explicación

- `requires` → los módulos que usa el proyecto (JavaFX, JDBC, H2, HikariCP, validación, BCrypt).
- `opens ...controlador to javafx.fxml` → el `FXMLLoader` usa **reflexión** para llenar los campos `@FXML` de los controladores; sin `opens` no podría.
- `opens ...modelo` → Hibernate Validator también lee por reflexión las anotaciones del modelo.
- `exports` → el paquete principal (donde está `PadronMascotaApp`) queda visible para JavaFX, que es quien lo arranca.

---

## 4. Paso 3 — Base de datos H2

### 4.1 `application.properties`

Crea `src/main/resources/application.properties`. Aquí van la URL de la base, el usuario y la contraseña, y qué scripts se ejecutan al arrancar:

`src/main/resources/application.properties`

```properties
# ============================================================
# H2 - base de datos embebida (como SysVentas). No se instala nada:
# el archivo se crea solo en la carpeta data/ del proyecto.
# AUTO_SERVER=TRUE deja abrirla en DBeaver mientras la app corre.
# padron_mascota_pro = la base con las 26 tablas del modelo.
# ============================================================
db.url=jdbc:h2:file:./data/padron_mascota_pro;AUTO_SERVER=TRUE
db.username=Wil2008
db.password=921923203

# ============================================================
# Scripts que se ejecutan al arrancar
# ============================================================
db.ddl.script=db/schema.sql
db.datos.script=db/datos.sql
# true = al crear una base nueva la llena con los datos de ejemplo del Excel.
# false = la base empieza vacía (solo el usuario admin / admin123).
db.datosEjemplo=false
```

### 4.2 `schema.sql` — las 26 tablas y la vista de stock

Crea `src/main/resources/db/schema.sql`. Usa `CREATE TABLE IF NOT EXISTS`, así que se puede ejecutar en cada arranque sin borrar nada. Las tablas fijas (especie, rol, método de pago...) se llenan con `MERGE`, con los mismos ids que los `enum` de Java. La vista `v_stock_producto` calcula el stock: **comprado − vendido − usado**.

`src/main/resources/db/schema.sql`

```sql
-- =====================================================================
--  Padrón Mascota PRO - Base de datos H2
--  Las 26 tablas del modelo padron_mascota.architect / padron_mascota_pro.xlsx
--  Se ejecuta cada vez que arranca el sistema: IF NOT EXISTS no borra nada.
-- =====================================================================

-- ---------------------------------------------------------------------
--  1. Catálogos fijos (en Java son enums)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS especie (
    id_especie  INT PRIMARY KEY,
    codigo      VARCHAR(20) NOT NULL UNIQUE,
    nombre      VARCHAR(50) NOT NULL
);
MERGE INTO especie KEY (id_especie) VALUES (1, 'PERRO', 'Perro'), (2, 'GATO', 'Gato'), (3, 'OTRO', 'Otro');

CREATE TABLE IF NOT EXISTS estado_vacunacion (
    id_estado   INT PRIMARY KEY,
    codigo      VARCHAR(20) NOT NULL UNIQUE,
    nombre      VARCHAR(50) NOT NULL
);
MERGE INTO estado_vacunacion KEY (id_estado) VALUES (1, 'AL_DIA', 'Al día'), (2, 'PENDIENTE', 'Pendiente');

CREATE TABLE IF NOT EXISTS estado_cita (
    id_estado_cita INT PRIMARY KEY,
    codigo         VARCHAR(20) NOT NULL UNIQUE,
    nombre         VARCHAR(50) NOT NULL
);
MERGE INTO estado_cita KEY (id_estado_cita) VALUES (1, 'PENDIENTE', 'Pendiente'), (2, 'ATENDIDA', 'Atendida'), (3, 'CANCELADA', 'Cancelada');

CREATE TABLE IF NOT EXISTS rol (
    id_rol  INT PRIMARY KEY,
    codigo  VARCHAR(20) NOT NULL UNIQUE,
    nombre  VARCHAR(50) NOT NULL
);
MERGE INTO rol KEY (id_rol) VALUES (1, 'ADMIN', 'Administrador'), (2, 'RECEPCION', 'Recepcionista'), (3, 'VETERINARIO', 'Veterinario');

CREATE TABLE IF NOT EXISTS metodo_pago (
    id_metodo_pago INT PRIMARY KEY,
    nombre         VARCHAR(30) NOT NULL UNIQUE
);
MERGE INTO metodo_pago KEY (id_metodo_pago) VALUES (1, 'Efectivo'), (2, 'Tarjeta'), (3, 'Yape/Plin'), (4, 'Transferencia');

CREATE TABLE IF NOT EXISTS categoria_producto (
    id_categoria INT PRIMARY KEY,
    nombre       VARCHAR(50) NOT NULL UNIQUE
);
MERGE INTO categoria_producto KEY (id_categoria) VALUES
    (1, 'Comida'), (2, 'Accesorio'), (3, 'Medicamento'), (4, 'Vacuna'), (5, 'Insumo médico');

-- ---------------------------------------------------------------------
--  2. Catálogos que el usuario puede editar
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS color (
    id_color INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    nombre   VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS raza (
    id_raza     INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    nombre      VARCHAR(60) NOT NULL,
    id_especie  INT         NOT NULL,
    CONSTRAINT uq_raza UNIQUE (nombre, id_especie),
    CONSTRAINT fk_raza_especie FOREIGN KEY (id_especie) REFERENCES especie (id_especie)
);

-- ---------------------------------------------------------------------
--  3. Personas
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS propietario (
    id_propietario INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    dni            CHAR(8)      NOT NULL UNIQUE,
    nombre         VARCHAR(100) NOT NULL,
    telefono       CHAR(9)      NOT NULL,
    direccion      VARCHAR(150),
    email          VARCHAR(100),
    CONSTRAINT ck_propietario_dni      CHECK (REGEXP_LIKE(dni, '^[0-9]{8}$')),
    CONSTRAINT ck_propietario_telefono CHECK (REGEXP_LIKE(telefono, '^9[0-9]{8}$'))
);
-- Baja lógica del propietario (cuando ya tiene ventas o atenciones no se borra, se oculta)
ALTER TABLE propietario ADD COLUMN IF NOT EXISTS activo BOOLEAN DEFAULT TRUE NOT NULL;


CREATE TABLE IF NOT EXISTS veterinario (
    id_veterinario INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL,
    telefono       CHAR(9)      NOT NULL,
    CONSTRAINT ck_veterinario_telefono CHECK (REGEXP_LIKE(telefono, '^9[0-9]{8}$'))
);
-- Baja lógica: un veterinario que se fue queda con activo = FALSE (su historial no se pierde).
-- Con ALTER ... IF NOT EXISTS también se agrega a una base que ya existía.
ALTER TABLE veterinario ADD COLUMN IF NOT EXISTS activo BOOLEAN DEFAULT TRUE NOT NULL;

-- Usuario.clave se guarda como hash BCrypt, nunca la contraseña en texto
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario     INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    username       VARCHAR(50)  NOT NULL UNIQUE,
    clave          VARCHAR(100) NOT NULL,
    id_rol         INT          NOT NULL,
    id_veterinario INT,
    estado         VARCHAR(10)  NOT NULL DEFAULT 'Activo',
    CONSTRAINT ck_usuario_estado CHECK (estado IN ('Activo', 'Inactivo')),
    CONSTRAINT fk_usuario_rol         FOREIGN KEY (id_rol)         REFERENCES rol (id_rol),
    CONSTRAINT fk_usuario_veterinario FOREIGN KEY (id_veterinario) REFERENCES veterinario (id_veterinario)
);
-- Siempre deja el usuario admin / admin123 listo para entrar
MERGE INTO usuario (username, clave, id_rol, estado) KEY (username)
    VALUES ('admin', '$2a$10$USvu.WxM2lPvBS.Hm9Y7uuXoJUFMm605F8abkkV6ptCKaP/mTFrhS', 1, 'Activo');

-- ---------------------------------------------------------------------
--  4. Mascotas
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS mascota (
    id_mascota     INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    nombre         VARCHAR(60)  NOT NULL,
    id_especie     INT          NOT NULL,
    id_raza        INT,                    -- vacío = mascota "por completar" (creada al sacar cita)
    id_color       INT,
    sexo           VARCHAR(6),
    edad_aprox     TINYINT      NOT NULL,
    fecha_registro DATE         NOT NULL,
    id_propietario INT          NOT NULL,
    id_estado      INT          NOT NULL,
    activo         BOOLEAN      NOT NULL DEFAULT TRUE,   -- borrado lógico
    observaciones  VARCHAR(255),
    CONSTRAINT ck_mascota_sexo CHECK (sexo IN ('Macho', 'Hembra')),
    CONSTRAINT ck_mascota_edad CHECK (edad_aprox BETWEEN 0 AND 40),
    CONSTRAINT fk_mascota_especie     FOREIGN KEY (id_especie)     REFERENCES especie (id_especie),
    CONSTRAINT fk_mascota_raza        FOREIGN KEY (id_raza)        REFERENCES raza (id_raza),
    CONSTRAINT fk_mascota_color       FOREIGN KEY (id_color)       REFERENCES color (id_color),
    CONSTRAINT fk_mascota_propietario FOREIGN KEY (id_propietario) REFERENCES propietario (id_propietario),
    CONSTRAINT fk_mascota_estado      FOREIGN KEY (id_estado)      REFERENCES estado_vacunacion (id_estado)
);
-- Bases creadas antes: raza, color y sexo pasan a ser opcionales (mascota por completar)
ALTER TABLE mascota ALTER COLUMN id_raza SET NULL;
ALTER TABLE mascota ALTER COLUMN id_color SET NULL;
ALTER TABLE mascota ALTER COLUMN sexo SET NULL;

-- ---------------------------------------------------------------------
--  5. Productos y servicios
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS producto (
    id_producto  INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    nombre       VARCHAR(100)  NOT NULL UNIQUE,
    id_categoria INT           NOT NULL,
    precio       DECIMAL(10,2) NOT NULL,
    stock_minimo INT           NOT NULL DEFAULT 0,
    CONSTRAINT ck_producto_precio CHECK (precio >= 0),
    CONSTRAINT ck_producto_minimo CHECK (stock_minimo >= 0),
    CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria) REFERENCES categoria_producto (id_categoria)
);

CREATE TABLE IF NOT EXISTS servicio (
    id_servicio INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    nombre      VARCHAR(80)   NOT NULL UNIQUE,
    precio      DECIMAL(10,2) NOT NULL,
    CONSTRAINT ck_servicio_precio CHECK (precio >= 0)
);

-- ---------------------------------------------------------------------
--  6. Vacunas
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS vacuna (
    id_vacuna   INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    nombre      VARCHAR(60) NOT NULL,
    id_especie  INT         NOT NULL,
    id_producto INT,                       -- la dosis que se descuenta del stock
    CONSTRAINT uq_vacuna UNIQUE (nombre, id_especie),
    CONSTRAINT fk_vacuna_especie  FOREIGN KEY (id_especie)  REFERENCES especie (id_especie),
    CONSTRAINT fk_vacuna_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto)
);

CREATE TABLE IF NOT EXISTS vacunacion (
    id_vacunacion INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    id_mascota    INT  NOT NULL,
    id_vacuna     INT  NOT NULL,
    fecha         DATE NOT NULL,
    proxima_dosis DATE,
    CONSTRAINT ck_vacunacion_fechas CHECK (proxima_dosis IS NULL OR proxima_dosis > fecha),
    CONSTRAINT fk_vacunacion_mascota FOREIGN KEY (id_mascota) REFERENCES mascota (id_mascota),
    CONSTRAINT fk_vacunacion_vacuna  FOREIGN KEY (id_vacuna)  REFERENCES vacuna (id_vacuna)
);

-- ---------------------------------------------------------------------
--  7. Citas y atenciones
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cita (
    id_cita        INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    id_mascota     INT          NOT NULL,
    id_veterinario INT          NOT NULL,
    fecha          DATE         NOT NULL,
    hora           TIME         NOT NULL,
    motivo         VARCHAR(150) NOT NULL,
    id_estado_cita INT          NOT NULL DEFAULT 1,
    CONSTRAINT uq_cita_horario UNIQUE (id_veterinario, fecha, hora),
    CONSTRAINT fk_cita_mascota     FOREIGN KEY (id_mascota)     REFERENCES mascota (id_mascota),
    CONSTRAINT fk_cita_veterinario FOREIGN KEY (id_veterinario) REFERENCES veterinario (id_veterinario),
    CONSTRAINT fk_cita_estado      FOREIGN KEY (id_estado_cita) REFERENCES estado_cita (id_estado_cita)
);

CREATE TABLE IF NOT EXISTS atencion (
    id_atencion    INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    id_mascota     INT          NOT NULL,
    id_veterinario INT          NOT NULL,
    fecha          DATE         NOT NULL,
    motivo         VARCHAR(150) NOT NULL,
    id_cita        INT,                    -- opcional: atención sin cita
    CONSTRAINT fk_atencion_mascota     FOREIGN KEY (id_mascota)     REFERENCES mascota (id_mascota),
    CONSTRAINT fk_atencion_veterinario FOREIGN KEY (id_veterinario) REFERENCES veterinario (id_veterinario),
    CONSTRAINT fk_atencion_cita        FOREIGN KEY (id_cita)        REFERENCES cita (id_cita)
);

-- Lo que el doctor usa en la atención (jeringa, algodón...). No se cobra aparte, pero descuenta stock.
CREATE TABLE IF NOT EXISTS atencion_insumo (
    id_atencion_insumo INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    id_atencion        INT NOT NULL,
    id_producto        INT NOT NULL,
    cantidad           INT NOT NULL,
    CONSTRAINT ck_insumo_cantidad CHECK (cantidad > 0),
    CONSTRAINT fk_insumo_atencion FOREIGN KEY (id_atencion) REFERENCES atencion (id_atencion) ON DELETE CASCADE,
    CONSTRAINT fk_insumo_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto)
);

-- Receta = lo que el doctor indica para casa. No descuenta stock.
CREATE TABLE IF NOT EXISTS receta (
    id_receta    INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    id_atencion  INT  NOT NULL,
    fecha        DATE NOT NULL,
    indicaciones VARCHAR(255),
    CONSTRAINT fk_receta_atencion FOREIGN KEY (id_atencion) REFERENCES atencion (id_atencion)
);

CREATE TABLE IF NOT EXISTS detalle_receta (
    id_detalle_receta INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    id_receta         INT         NOT NULL,
    id_producto       INT         NOT NULL,
    dosis             VARCHAR(50) NOT NULL,
    frecuencia        VARCHAR(50) NOT NULL,
    dias              INT         NOT NULL,
    CONSTRAINT ck_detreceta_dias CHECK (dias > 0),
    CONSTRAINT fk_detreceta_receta   FOREIGN KEY (id_receta)   REFERENCES receta (id_receta) ON DELETE CASCADE,
    CONSTRAINT fk_detreceta_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto)
);

-- ---------------------------------------------------------------------
--  8. Compras (solo productos)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS proveedor (
    id_proveedor INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    ruc          CHAR(11)     NOT NULL UNIQUE,
    nombre       VARCHAR(120) NOT NULL,
    telefono     VARCHAR(15),
    CONSTRAINT ck_proveedor_ruc CHECK (REGEXP_LIKE(ruc, '^[0-9]{11}$'))
);

CREATE TABLE IF NOT EXISTS compra (
    id_compra      INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    id_proveedor   INT           NOT NULL,
    fecha_hora     TIMESTAMP     NOT NULL,
    numero_factura VARCHAR(20)   NOT NULL,
    id_usuario     INT           NOT NULL,
    total          DECIMAL(10,2) NOT NULL DEFAULT 0,
    CONSTRAINT uq_compra_factura UNIQUE (id_proveedor, numero_factura),
    CONSTRAINT fk_compra_proveedor FOREIGN KEY (id_proveedor) REFERENCES proveedor (id_proveedor),
    CONSTRAINT fk_compra_usuario   FOREIGN KEY (id_usuario)   REFERENCES usuario (id_usuario)
);

CREATE TABLE IF NOT EXISTS detalle_compra (
    id_detalle_compra INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    id_compra         INT           NOT NULL,
    id_producto       INT           NOT NULL,
    cantidad          INT           NOT NULL,
    costo_unitario    DECIMAL(10,2) NOT NULL,
    lote              VARCHAR(30),           -- opcional: los accesorios no vencen
    fecha_vencimiento DATE,
    subtotal          DECIMAL(10,2) NOT NULL,
    CONSTRAINT ck_detcompra_cantidad CHECK (cantidad > 0),
    CONSTRAINT fk_detcompra_compra   FOREIGN KEY (id_compra)   REFERENCES compra (id_compra),
    CONSTRAINT fk_detcompra_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto)
);

-- ---------------------------------------------------------------------
--  9. Ventas
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS venta (
    id_venta           INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    tipo_comprobante   VARCHAR(10)   NOT NULL,
    numero_comprobante VARCHAR(15)   NOT NULL UNIQUE,
    id_propietario     INT           NOT NULL,
    id_atencion        INT,                    -- opcional: vacío = solo productos
    fecha_hora         TIMESTAMP     NOT NULL,
    id_metodo_pago     INT           NOT NULL,
    id_usuario         INT           NOT NULL,
    estado             VARCHAR(10)   NOT NULL DEFAULT 'Pagada',
    total              DECIMAL(10,2) NOT NULL DEFAULT 0,
    CONSTRAINT ck_venta_comprobante CHECK (tipo_comprobante IN ('Boleta', 'Factura')),
    CONSTRAINT ck_venta_estado      CHECK (estado IN ('Pagada', 'Anulada')),
    CONSTRAINT fk_venta_propietario FOREIGN KEY (id_propietario) REFERENCES propietario (id_propietario),
    CONSTRAINT fk_venta_atencion    FOREIGN KEY (id_atencion)    REFERENCES atencion (id_atencion),
    CONSTRAINT fk_venta_metodo      FOREIGN KEY (id_metodo_pago) REFERENCES metodo_pago (id_metodo_pago),
    CONSTRAINT fk_venta_usuario     FOREIGN KEY (id_usuario)     REFERENCES usuario (id_usuario)
);

-- Cada línea lleva id_servicio O id_producto, nunca ambos ni ninguno
CREATE TABLE IF NOT EXISTS detalle_venta (
    id_detalle_venta INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    id_venta         INT           NOT NULL,
    id_servicio      INT,
    id_producto      INT,
    cantidad         INT           NOT NULL,
    precio           DECIMAL(10,2) NOT NULL,   -- precio al momento de la venta
    subtotal         DECIMAL(10,2) NOT NULL,
    CONSTRAINT ck_detventa_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_detventa_item CHECK ((id_servicio IS NULL AND id_producto IS NOT NULL)
                                    OR (id_servicio IS NOT NULL AND id_producto IS NULL)),
    CONSTRAINT fk_detventa_venta    FOREIGN KEY (id_venta)    REFERENCES venta (id_venta),
    CONSTRAINT fk_detventa_servicio FOREIGN KEY (id_servicio) REFERENCES servicio (id_servicio),
    CONSTRAINT fk_detventa_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto)
);

-- Cobro en Ventas: precio de la consulta (lo pone el doctor) y qué ya se cobró
ALTER TABLE atencion   ADD COLUMN IF NOT EXISTS precio_consulta DECIMAL(10,2);
ALTER TABLE atencion   ADD COLUMN IF NOT EXISTS id_venta INT;      -- venta que la cobró (vacío = falta cobrar)
ALTER TABLE vacunacion ADD COLUMN IF NOT EXISTS id_venta INT;
-- Línea de venta de algo que ya bajó del stock al usarse (la vacuna aplicada): no se descuenta otra vez
ALTER TABLE detalle_venta ADD COLUMN IF NOT EXISTS ya_descontado BOOLEAN DEFAULT FALSE NOT NULL;
ALTER TABLE detalle_venta ADD COLUMN IF NOT EXISTS descripcion VARCHAR(150);   -- "Consulta - Kiwi"
ALTER TABLE venta ADD COLUMN IF NOT EXISTS ruc_cliente VARCHAR(11);            -- solo en factura

-- ---------------------------------------------------------------------
--  10. Vista de stock (la "Consulta stock" del Excel)
--  Stock actual = comprados - vendidos (solo ventas Pagadas) - usados
--  Usados = insumos de atenciones + 1 dosis por cada vacunación
-- ---------------------------------------------------------------------
CREATE OR REPLACE VIEW v_stock_producto AS
SELECT p.id_producto,
       COALESCE((SELECT SUM(dc.cantidad) FROM detalle_compra dc WHERE dc.id_producto = p.id_producto), 0) AS comprados,
       COALESCE((SELECT SUM(dv.cantidad) FROM detalle_venta dv JOIN venta v ON v.id_venta = dv.id_venta
                 WHERE dv.id_producto = p.id_producto AND v.estado = 'Pagada' AND dv.ya_descontado = FALSE), 0) AS vendidos,
       COALESCE((SELECT SUM(ai.cantidad) FROM atencion_insumo ai WHERE ai.id_producto = p.id_producto), 0)
     + (SELECT COUNT(*) FROM vacunacion va JOIN vacuna vc ON vc.id_vacuna = va.id_vacuna
        WHERE vc.id_producto = p.id_producto) AS usados,
       (SELECT dc.costo_unitario FROM detalle_compra dc JOIN compra c ON c.id_compra = dc.id_compra
        WHERE dc.id_producto = p.id_producto ORDER BY c.fecha_hora DESC, dc.id_detalle_compra DESC LIMIT 1) AS ultimo_costo
FROM producto p;
```

El archivo `db/datos.sql` trae datos de ejemplo. Solo se carga si en `application.properties` pones `db.datosEjemplo=true` y la base está vacía.

### 4.3 `CarpetaApp` — dónde están `data/` y `language/`

Desde IntelliJ, la carpeta es la del proyecto. Desde el `.exe`, es la carpeta del programa, aunque lo abran desde un acceso directo. Crea `config/CarpetaApp.java`:

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/config/CarpetaApp.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.config;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * La carpeta donde están {@code data/} (base H2) y {@code language/} (idiomas).
 *
 * <ul>
 *   <li>Desde IntelliJ: la carpeta del proyecto (la de trabajo).</li>
 *   <li>Desde el .exe (jpackage): la carpeta del programa, aunque lo abran desde un acceso directo.</li>
 * </ul>
 */
public final class CarpetaApp {

    private static final Path BASE = calcular();

    private CarpetaApp() {
    }

    public static Path base() {
        return BASE;
    }

    private static Path calcular() {
        String exe = System.getProperty("jpackage.app-path");   // lo pone el lanzador del .exe
        if (exe != null) {
            Path dir = Path.of(exe).toAbsolutePath().getParent();
            // Windows: el .exe está junto a data/. Linux/Mac: el lanzador está en bin/
            for (Path p = dir; p != null; p = p.getParent()) {
                if (Files.isDirectory(p.resolve("language")) || Files.isDirectory(p.resolve("data"))) {
                    return p;
                }
            }
            return dir;
        }
        return Path.of("").toAbsolutePath();
    }
}
```

### 4.4 `DatabaseConfig` — abrir H2, crear tablas, pool de conexiones

Crea `config/DatabaseConfig.java`:

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/config/DatabaseConfig.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Configuración de la base de datos H2 (embebida, en archivo) con pool HikariCP,
 * igual idea que en SysVentas: no hay que instalar ningún servidor.
 *
 * <p>Al arrancar:
 * <ol>
 *   <li>Abre (o crea) el archivo {@code data/padron_mascota_pro.mv.db}.</li>
 *   <li>Ejecuta {@code db/schema.sql} (crea las 26 tablas si no existen).</li>
 *   <li>Si no hay propietarios, ejecuta {@code db/datos.sql} (datos del Excel).</li>
 * </ol>
 */
public final class DatabaseConfig {

    private static final String PROPERTIES_FILE = "application.properties";
    private static final String DEFAULT_URL = "jdbc:h2:file:./data/padron_mascota_pro;AUTO_SERVER=TRUE";

    private static volatile HikariDataSource dataSource;

    private DatabaseConfig() {
    }

    /** Abre el pool una sola vez y prepara las tablas. */
    public static synchronized void init() {
        if (dataSource != null) {
            return;
        }
        Properties props = loadProperties();
        String url = props.getProperty("db.url", DEFAULT_URL);
        // "./data/..." se toma desde la carpeta del programa (así el .exe siempre encuentra su base)
        String base = CarpetaApp.base().toString().replace('\\', '/');
        url = url.replace("jdbc:h2:file:./", "jdbc:h2:file:" + base + "/");

        HikariConfig config = new HikariConfig();
        config.setPoolName("PadronMascotaPool");
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl(url);
        config.setUsername(props.getProperty("db.username", "sa"));
        config.setPassword(props.getProperty("db.password", ""));
        // App de escritorio con un solo usuario: un pool pequeño es suficiente.
        config.setMaximumPoolSize(5);

        HikariDataSource ds;
        try {
            ds = new HikariDataSource(config);
        } catch (RuntimeException e) {
            throw new IllegalStateException("No fue posible abrir la base de datos H2: " + url, e);
        }
        try {
            ejecutarScript(ds, props.getProperty("db.ddl.script", "db/schema.sql"));
            // Datos de ejemplo del Excel: solo si en application.properties dice db.datosEjemplo=true
            boolean ejemplo = Boolean.parseBoolean(props.getProperty("db.datosEjemplo", "false"));
            if (ejemplo && contar(ds, "propietario") == 0) {
                ejecutarScript(ds, props.getProperty("db.datos.script", "db/datos.sql"));
            }
            reiniciarIdsVacios(ds);
        } catch (RuntimeException e) {
            ds.close();
            throw e;
        }
        dataSource = ds;
        System.out.println("H2 listo: " + url);
    }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            init();
        }
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    /** Cierra el pool al salir de la aplicación. */
    public static synchronized void shutdown() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }

    // ------------------------------------------------------------------
    //  IDs y "empezar de cero"
    // ------------------------------------------------------------------

    /** Tablas fijas (las llenan los enums con MERGE en schema.sql): nunca se vacían. */
    private static final java.util.Set<String> CATALOGOS_FIJOS = java.util.Set.of(
            "ESPECIE", "ESTADO_VACUNACION", "ESTADO_CITA", "ROL", "METODO_PAGO", "CATEGORIA_PRODUCTO");

    /**
     * Si una tabla quedó vacía, su ID vuelve a empezar en 1.
     * (La base nunca reutiliza números por sí sola: sin esto, tras borrar 1..7 el siguiente sería 8.)
     */
    public static void reiniciarIdsVacios() {
        reiniciarIdsVacios(getDataSource());
    }

    private static void reiniciarIdsVacios(DataSource ds) {
        try (Connection cn = ds.getConnection(); Statement st = cn.createStatement()) {
            for (String[] t : columnasIdentity(cn)) {
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + t[0])) {
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        st.execute("ALTER TABLE " + t[0] + " ALTER COLUMN " + t[1] + " RESTART WITH 1");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reiniciando los IDs: " + e.getMessage(), e);
        }
    }

    /** [tabla, columna] de cada ID autonumérico de la base. */
    private static java.util.List<String[]> columnasIdentity(Connection cn) throws SQLException {
        java.util.List<String[]> lista = new java.util.ArrayList<>();
        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("SELECT c.TABLE_NAME, c.COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS c "
                     + "JOIN INFORMATION_SCHEMA.TABLES t ON t.TABLE_SCHEMA = c.TABLE_SCHEMA AND t.TABLE_NAME = c.TABLE_NAME "
                     + "WHERE c.TABLE_SCHEMA = 'PUBLIC' AND c.IS_IDENTITY = 'YES' AND t.TABLE_TYPE = 'BASE TABLE'")) {
            while (rs.next()) {
                lista.add(new String[]{rs.getString(1), rs.getString(2)});
            }
        }
        return lista;
    }

    /**
     * EMPEZAR DE CERO: borra todos los registros (mascotas, propietarios, ventas, compras, razas,
     * colores, productos...) y deja los IDs en 1. Solo se conservan las tablas fijas y el usuario
     * que está usando el sistema, para poder seguir entrando con su misma clave.
     */
    public static void borrarTodo(int idUsuarioQueQueda) {
        try (Connection cn = getConnection(); Statement st = cn.createStatement()) {
            cn.setAutoCommit(false);
            try {
                st.execute("SET REFERENTIAL_INTEGRITY FALSE");
                java.util.List<String> tablas = new java.util.ArrayList<>();
                try (ResultSet rs = st.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
                        + "WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_TYPE = 'BASE TABLE'")) {
                    while (rs.next()) {
                        tablas.add(rs.getString(1));
                    }
                }
                for (String t : tablas) {
                    if (CATALOGOS_FIJOS.contains(t)) {
                        continue;
                    }
                    if (t.equals("USUARIO")) {
                        try (PreparedStatement ps = cn.prepareStatement("DELETE FROM usuario WHERE id_usuario <> ?")) {
                            ps.setInt(1, idUsuarioQueQueda);
                            ps.executeUpdate();
                        }
                        st.executeUpdate("UPDATE usuario SET id_veterinario = NULL");
                    } else {
                        st.executeUpdate("DELETE FROM " + t);
                    }
                }
                cn.commit();
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            } finally {
                st.execute("SET REFERENTIAL_INTEGRITY TRUE");
                cn.setAutoCommit(true);
            }
            // El usuario que queda conserva su ID; los demás vuelven a 1
            for (String[] t : columnasIdentity(cn)) {
                int siguiente = 1;
                try (ResultSet rs = st.executeQuery("SELECT COALESCE(MAX(" + t[1] + "), 0) + 1 FROM " + t[0])) {
                    rs.next();
                    siguiente = rs.getInt(1);
                }
                st.execute("ALTER TABLE " + t[0] + " ALTER COLUMN " + t[1] + " RESTART WITH " + siguiente);
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo vaciar la base: " + e.getMessage(), e);
        }
    }

    /** Cuántas filas tiene una tabla (para saber si hay que cargar datos de ejemplo). */
    private static int contar(DataSource ds, String tabla) {
        try (Connection cn = ds.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tabla)) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new IllegalStateException("Error revisando la tabla " + tabla, e);
        }
    }

    /** Lee un .sql de resources y lo ejecuta sentencia por sentencia (separadas por ;). */
    private static void ejecutarScript(DataSource ds, String script) {
        try (InputStream in = DatabaseConfig.class.getResourceAsStream("/" + script)) {
            if (in == null) {
                throw new IllegalStateException("No se encontró el script " + script);
            }
            String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8)
                    .lines()
                    .filter(linea -> !linea.strip().startsWith("--"))
                    .collect(Collectors.joining("\n"));

            // TRANSACCIÓN: o se ejecuta todo el script, o no se guarda nada
            try (Connection cn = ds.getConnection(); Statement st = cn.createStatement()) {
                cn.setAutoCommit(false);
                try {
                    for (String sentencia : sql.split(";")) {
                        if (!sentencia.isBlank()) {
                            st.execute(sentencia);
                        }
                    }
                    cn.commit();
                } catch (SQLException e) {
                    cn.rollback();
                    throw e;
                }
            }
        } catch (IOException | SQLException e) {
            throw new IllegalStateException("Error ejecutando el script " + script, e);
        }
    }

    private static Properties loadProperties() {
        try (InputStream in = DatabaseConfig.class.getResourceAsStream("/" + PROPERTIES_FILE)) {
            if (in == null) {
                throw new IllegalStateException("No se encontró " + PROPERTIES_FILE);
            }
            Properties props = new Properties();
            props.load(in);
            return props;
        } catch (IOException e) {
            throw new IllegalStateException("Error leyendo " + PROPERTIES_FILE, e);
        }
    }

    // Prueba rápida: dale Run a este main para saber si conecta
    public static void main(String[] args) {
        try (Connection cn = getConnection()) {
            System.out.println("Conexión exitosa a: " + cn.getMetaData().getURL());
        } catch (SQLException | RuntimeException e) {
            System.out.println("Error al conectar: " + e.getMessage());
        } finally {
            shutdown();
        }
    }
}
```

Puntos clave:

1. `init()` abre el pool **una sola vez** (`synchronized` + `if (dataSource != null) return`).
2. `ejecutarScript()` corre `schema.sql` **en una transacción**: o se crean todas las tablas o ninguna.
3. `reiniciarIdsVacios()` hace que una tabla vacía vuelva a numerar desde 1.
4. `borrarTodo()` es la opción *Sistema → Empezar de cero* del menú (solo para el administrador).
5. El `main()` de esta clase sirve para probar la conexión sin abrir las pantallas.

### 4.5 Ver la base con DBeaver

1. Cierra el sistema (H2 en archivo admite un solo dueño a la vez).
2. DBeaver → *Nueva conexión* → **H2 Embedded** → *Connect by: URL*:
   `jdbc:h2:file:D:/ruta/del/proyecto/data/padron_mascota_pro;IFEXISTS=TRUE` (sin `.mv.db` al final).
3. Usuario y contraseña: los de `application.properties`.
4. Si DBeaver avisa de la versión del driver, en *Editar driver → Bibliotecas* elige **H2 2.4.240**.
5. Antes de volver a abrir el sistema: clic derecho en la conexión → **Desconectar**.

---

## 5. Paso 4 — Paquete `enums`

Cada `enum` representa una tabla fija de la base de datos, con **el mismo id**. Así el repositorio convierte el número que viene de la BD en el enum (`desdeId`) y la pantalla muestra su nombre (`toString`).

### 5.1 `Especie`

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/enums/Especie.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.enums;

/**
 * Tabla especie como enum. El id es el mismo que en la base de datos.
 */
public enum Especie {
    PERRO(1, "Perro"),
    GATO(2, "Gato"),
    OTRO(3, "Otro");

    private final int id;
    private final String nombre;

    Especie(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    /** Convierte el id que viene de la BD en el enum. */
    public static Especie desdeId(int id) {
        for (Especie e : values()) {
            if (e.id == id) {
                return e;
            }
        }
        throw new IllegalArgumentException("No existe la especie con id " + id);
    }

    @Override
    public String toString() {
        return nombre; // lo que se ve en el ComboBox y en la tabla
    }
}
```

### 5.2 `Rol` (controla qué menús ve cada usuario)

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/enums/Rol.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.enums;

/**
 * Tabla rol como enum. Cada rol ve solo los menús que le tocan
 * (lo decide MenuServicioImpl).
 */
public enum Rol {
    ADMIN(1, "Administrador"),
    RECEPCION(2, "Recepcionista"),
    VETERINARIO(3, "Veterinario");

    private final int id;
    private final String nombre;

    Rol(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static Rol desdeId(int id) {
        for (Rol r : values()) {
            if (r.id == id) {
                return r;
            }
        }
        throw new IllegalArgumentException("No existe el rol con id " + id);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
```

### 5.3 Los demás enums

Siguen exactamente el mismo molde (`id`, `nombre`, `desdeId`, `toString`):

| Enum | Valores | Tabla |
|---|---|---|
| `Sexo` | Macho, Hembra | (columna de `mascota`) |
| `EstadoVacunacion` | Al día, Pendiente | `estado_vacunacion` |
| `EstadoCita` | Pendiente, Atendida, Cancelada | `estado_cita` |
| `MetodoPago` | Efectivo, Tarjeta, Yape/Plin, Transferencia | `metodo_pago` |
| `CategoriaProducto` | Comida, Accesorio, Medicamento, Vacuna, Insumo, ... | `categoria_producto` |
| `TipoComprobante` | Boleta, Factura | (columna de `venta`) |
| `EstadoVenta` | Pagada, Anulada | (columna de `venta`) |

---

## 6. Paso 5 — Paquete `excepcion`

### 6.1 `ValidacionException` — errores por campo

Lleva un `Map` campo → mensaje (`"telefono"` → `"El teléfono debe tener 9 dígitos..."`). Así el controlador sabe **qué campo** pintar en rojo.

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/excepcion/ValidacionException.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.excepcion;

import java.util.Map;

/**
 * Se lanza cuando un objeto no pasa las validaciones (@NotBlank, @Pattern, @Min...).
 * Lleva los errores por campo: "telefono" -> "El teléfono debe tener 9 dígitos..."
 */
public class ValidacionException extends RuntimeException {

    private final Map<String, String> errores;

    public ValidacionException(Map<String, String> errores) {
        super(errores.values().iterator().next());
        this.errores = errores;
    }

    public Map<String, String> getErrores() {
        return errores;
    }
}
```

### 6.2 `NoEncontradoException`

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/excepcion/NoEncontradoException.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.excepcion;

/** Se lanza cuando se busca un registro por id y no existe. */
public class NoEncontradoException extends RuntimeException {

    public NoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
```

---

## 7. Paso 6 — Paquete `modelo`

### 7.1 `Entidad` — la raíz de todo el modelo

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/modelo/Entidad.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.modelo;

/**
 * Clase base de todo el modelo (ABSTRACCIÓN + HERENCIA).
 * Toda tabla tiene un id, así que lo escribimos una sola vez aquí.
 */
public abstract class Entidad {

    private int id;   // ENCAPSULAMIENTO: privado, solo se usa con get/set

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /** true si todavía no se guardó en la BD (la BD aún no le dio un id). */
    public boolean esNuevo() {
        return id == 0;
    }
}
```

### 7.2 `Persona` — herencia de datos comunes

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/modelo/Persona.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.modelo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos que tiene cualquier persona del sistema (HERENCIA).
 * Hoy solo la usa Propietario, pero si mañana agregas Veterinario,
 * hereda nombre y teléfono sin volver a escribirlos.
 */
public abstract class Persona extends Entidad {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre puede tener hasta 100 letras")
    private String nombre;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "9\\d{8}", message = "El teléfono debe tener 9 dígitos y empezar con 9")
    private String telefono;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.trim();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono == null ? null : telefono.trim();
    }

    /** Cada hija dice qué tipo de persona es (POLIMORFISMO). */
    public abstract String getTipo();

    @Override
    public String toString() {
        return getNombre();
    }
}
```

### 7.3 `Propietario` y `Veterinario` — hijas de `Persona`

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/modelo/Propietario.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.modelo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Dueño de las mascotas (tabla propietario). HERENCIA: nombre y teléfono vienen de Persona. */
public class Propietario extends Persona {

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos")
    private String dni;

    @Size(max = 150, message = "La dirección puede tener hasta 150 letras")
    private String direccion;

    @Email(message = "El email debe tener el formato usuario@dominio.com")
    @Size(max = 100, message = "El email puede tener hasta 100 letras")
    private String email;

    private int numeroMascotas;   // calculado (columna "n° mascotas" del Excel)

    public Propietario() {
    }

    public Propietario(int id, String nombre, String telefono) {
        setId(id);
        setNombre(nombre);
        setTelefono(telefono);
    }

    @Override
    public String getTipo() {
        return "Propietario";
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni == null ? null : dni.trim();
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = vacioANull(direccion);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = vacioANull(email);
    }

    public int getNumeroMascotas() {
        return numeroMascotas;
    }

    public void setNumeroMascotas(int numeroMascotas) {
        this.numeroMascotas = numeroMascotas;
    }

    /** Los campos opcionales vacíos se guardan como NULL. */
    static String vacioANull(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
```

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/modelo/Veterinario.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.modelo;

/** Doctor de la veterinaria (tabla veterinario). HERENCIA: reutiliza nombre y teléfono de Persona. */
public class Veterinario extends Persona {

    private int numeroAtenciones;   // calculado (columna "n° atenciones" del Excel)

    public Veterinario() {
    }

    public Veterinario(int id, String nombre, String telefono) {
        setId(id);
        setNombre(nombre);
        setTelefono(telefono);
    }

    @Override
    public String getTipo() {
        return "Veterinario";
    }

    public int getNumeroAtenciones() {
        return numeroAtenciones;
    }

    public void setNumeroAtenciones(int numeroAtenciones) {
        this.numeroAtenciones = numeroAtenciones;
    }
}
```

### 7.4 `Raza` y `Color` — catálogos

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/modelo/Raza.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.modelo;

import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Catálogo de razas (tabla raza). Cada raza es de una especie. */
public class Raza extends Entidad {

    @NotBlank(message = "El nombre de la raza es obligatorio")
    @Size(max = 60, message = "La raza puede tener hasta 60 letras")
    private String nombre;

    @NotNull(message = "Elige la especie")
    private Especie especie;

    private int numeroMascotas;   // calculado con COUNT: cuántas mascotas activas tienen esta raza

    public Raza() {
    }

    public Raza(int id, String nombre, Especie especie) {
        setId(id);
        this.nombre = nombre;
        this.especie = especie;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.trim();
    }

    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        this.especie = especie;
    }

    public int getNumeroMascotas() {
        return numeroMascotas;
    }

    public void setNumeroMascotas(int numeroMascotas) {
        this.numeroMascotas = numeroMascotas;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
```

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/modelo/Color.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.modelo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Catálogo de colores de pelaje/plumaje (tabla color). */
public class Color extends Entidad {

    @NotBlank(message = "El nombre del color es obligatorio")
    @Size(max = 50, message = "El color puede tener hasta 50 letras")
    private String nombre;

    private int numeroMascotas;   // calculado con COUNT (columna "n° mascotas" del Excel)

    public Color() {
    }

    public Color(int id, String nombre) {
        setId(id);
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.trim();
    }

    public int getNumeroMascotas() {
        return numeroMascotas;
    }

    public void setNumeroMascotas(int numeroMascotas) {
        this.numeroMascotas = numeroMascotas;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
```

### 7.5 `Mascota` — la entidad central

Fíjate en las anotaciones: son las reglas que revisa el servicio antes de guardar.

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/modelo/Mascota.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.modelo;

import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoVacunacion;
import pe.edu.upeu.padronmascota.padronmascota.enums.Sexo;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** Tabla mascota. COMPOSICIÓN: tiene una raza, un color y un propietario (objetos, no solo ids). */
public class Mascota extends Entidad {

    @NotBlank(message = "El nombre de la mascota es obligatorio")
    @Size(max = 60, message = "El nombre puede tener hasta 60 letras")
    private String nombre;

    @NotNull(message = "Elige la especie")
    private Especie especie;

    @NotNull(message = "Elige la raza")
    private Raza raza;

    @NotNull(message = "Elige el color")
    private Color color;

    @NotNull(message = "Elige el sexo")
    private Sexo sexo;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    @Max(value = 40, message = "La edad máxima es 40 años")
    private Integer edadAprox;

    @NotNull(message = "La fecha de registro es obligatoria")
    @PastOrPresent(message = "La fecha de registro no puede ser futura")
    private LocalDate fechaRegistro;

    @NotNull(message = "Elige el propietario")
    private Propietario propietario;

    @NotNull(message = "Elige el estado de vacunación")
    private EstadoVacunacion estado;

    private boolean activo = true;   // borrado lógico: "eliminar" la marca como inactiva

    @Size(max = 255, message = "Las observaciones pueden tener hasta 255 letras")
    private String observaciones;

    /** La raza elegida debe ser de la misma especie (no existe un "Persa" perro). */
    @AssertTrue(message = "La raza no corresponde a la especie")
    public boolean isRazaDeLaEspecie() {
        return raza == null || especie == null || raza.getEspecie() == especie;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.trim();
    }

    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        this.especie = especie;
    }

    public Raza getRaza() {
        return raza;
    }

    public void setRaza(Raza raza) {
        this.raza = raza;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public Integer getEdadAprox() {
        return edadAprox;
    }

    public void setEdadAprox(Integer edadAprox) {
        this.edadAprox = edadAprox;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Propietario getPropietario() {
        return propietario;
    }

    public void setPropietario(Propietario propietario) {
        this.propietario = propietario;
    }

    public EstadoVacunacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoVacunacion estado) {
        this.estado = estado;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = Propietario.vacioANull(observaciones);
    }

    /** En los combos se ve "Rocky (María Quispe Huamán)" para no confundir mascotas con el mismo nombre. */
    @Override
    public String toString() {
        return propietario == null || propietario.getNombre() == null ? nombre : nombre + " (" + propietario.getNombre() + ")";
    }
}
```

### 7.6 `ItemVenta`, `Producto` y `ServicioVeterinario` — polimorfismo en la venta

En una venta se puede cobrar **un producto** (tiene stock) o **un servicio** (baño, consulta: no tiene stock). Los dos son un `ItemVenta`; cada uno responde distinto a `controlaStock()`.

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/modelo/ItemVenta.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.modelo;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Todo lo que se puede vender: un Producto (accesorio, comida...) o un ServicioVeterinario.
 * ABSTRACCIÓN + HERENCIA: tienen nombre y precio en común.
 * POLIMORFISMO: cada hija dice su tipo y si controla stock.
 */
public abstract class ItemVenta extends Entidad {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre puede tener hasta 100 letras")
    private String nombre;

    // BigDecimal y no double: el dinero necesita decimales exactos
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
    @DecimalMax(value = "99999.99", message = "El precio máximo es 99999.99")
    @Digits(integer = 5, fraction = 2, message = "El precio puede tener hasta 2 decimales")
    private BigDecimal precio;

    /** "Producto" o "Servicio". */
    public abstract String getTipo();

    /** true si al venderlo hay que descontar stock. */
    public abstract boolean controlaStock();

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.trim();
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    /** Dos items son el mismo si son del mismo tipo y tienen el mismo id. */
    public boolean esMismo(ItemVenta otro) {
        return otro != null && getClass() == otro.getClass() && getId() == otro.getId();
    }

    @Override
    public String toString() {
        return nombre;
    }
}
```

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/modelo/Producto.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.modelo;

import pe.edu.upeu.padronmascota.padronmascota.enums.CategoriaProducto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Accesorios, comida, medicamentos, vacunas, insumos (tabla producto).
 * HERENCIA: nombre y precio vienen de ItemVenta.
 * El stock NO se guarda: se calcula = comprados - vendidos - usados (vista v_stock_producto).
 */
public class Producto extends ItemVenta {

    @NotNull(message = "Elige la categoría")
    private CategoriaProducto categoria;

    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    // Calculados por la vista (la "Consulta stock" del Excel)
    private int comprados;
    private int vendidos;
    private int usados;
    private BigDecimal ultimoCosto;

    @Override
    public String getTipo() {
        return "Producto";
    }

    @Override
    public boolean controlaStock() {
        return true;
    }

    /** Stock actual = comprados - vendidos (ventas pagadas) - usados (insumos y vacunas). */
    public int getStock() {
        return comprados - vendidos - usados;
    }

    /** STOCK BAJO si el stock actual es menor o igual al mínimo. */
    public boolean stockBajo() {
        return stockMinimo != null && getStock() <= stockMinimo;
    }

    /** Margen = precio de venta - último costo de compra. */
    public BigDecimal getMargen() {
        return ultimoCosto == null || getPrecio() == null ? null : getPrecio().subtract(ultimoCosto);
    }

    public CategoriaProducto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaProducto categoria) {
        this.categoria = categoria;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public int getComprados() {
        return comprados;
    }

    public void setComprados(int comprados) {
        this.comprados = comprados;
    }

    public int getVendidos() {
        return vendidos;
    }

    public void setVendidos(int vendidos) {
        this.vendidos = vendidos;
    }

    public int getUsados() {
        return usados;
    }

    public void setUsados(int usados) {
        this.usados = usados;
    }

    public BigDecimal getUltimoCosto() {
        return ultimoCosto;
    }

    public void setUltimoCosto(BigDecimal ultimoCosto) {
        this.ultimoCosto = ultimoCosto;
    }
}
```

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/modelo/ServicioVeterinario.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.modelo;

/**
 * Consulta, vacuna, desparasitación... (tabla servicio).
 * Se llama ServicioVeterinario para no confundirlo con la capa "servicio" del proyecto.
 */
public class ServicioVeterinario extends ItemVenta {

    @Override
    public String getTipo() {
        return "Servicio";
    }

    @Override
    public boolean controlaStock() {
        return false; // un servicio no tiene stock
    }
}
```

### 7.7 `Venta` y `DetalleVenta` — composición

La venta **contiene** sus líneas y calcula su total. Nadie de afuera puede tocar la lista directamente: solo con `agregar()` y `quitar()` (encapsulamiento).

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/modelo/Venta.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.modelo;

import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoVenta;
import pe.edu.upeu.padronmascota.padronmascota.enums.MetodoPago;
import pe.edu.upeu.padronmascota.padronmascota.enums.TipoComprobante;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Una venta (tabla venta) con sus líneas (tabla detalle_venta).
 * COMPOSICIÓN: la venta TIENE un propietario, un método de pago y una lista de detalles.
 */
public class Venta extends Entidad {

    @NotNull(message = "Elige el tipo de comprobante")
    private TipoComprobante tipoComprobante;

    private String numeroComprobante;          // lo genera el repositorio: B001-000010

    @NotNull(message = "Elige el cliente (propietario)")
    private Propietario propietario;

    private Atencion atencion;                 // opcional: vacío = solo productos

    private LocalDateTime fechaHora;

    @NotNull(message = "Elige el método de pago")
    private MetodoPago metodoPago;

    private Usuario usuario;                   // quién registró la venta

    private EstadoVenta estado = EstadoVenta.PAGADA;

    private BigDecimal totalGuardado;

    private String rucCliente;                 // solo factura (11 dígitos)

    public String getRucCliente() {
        return rucCliente;
    }

    public void setRucCliente(String rucCliente) {
        this.rucCliente = rucCliente == null || rucCliente.isBlank() ? null : rucCliente.trim();
    }          // el total que vino de la BD (para el historial)

    @Valid
    @NotEmpty(message = "Agrega al menos un producto o servicio")
    private final List<DetalleVenta> detalles = new ArrayList<>();

    /** Agrega un item. Si ya estaba en la venta, suma la cantidad en la misma línea. */
    public void agregar(ItemVenta item, int cantidad) {
        agregar(item, cantidad, item.getPrecio());
    }

    /** Agrega con el precio que puso quien cobra (si ya está con el mismo precio, suma la cantidad). */
    public void agregar(ItemVenta item, int cantidad, java.math.BigDecimal precio) {
        for (DetalleVenta d : detalles) {
            if (!d.esPendiente() && d.getItem().esMismo(item) && d.getPrecio().compareTo(precio) == 0) {
                d.setCantidad(d.getCantidad() + cantidad);
                return;
            }
        }
        DetalleVenta nuevo = new DetalleVenta(item, cantidad);
        nuevo.setPrecio(precio);
        detalles.add(nuevo);
    }

    /** Cambia lo pendiente (consultas y vacunas sin cobrar) por lo del cliente nuevo. */
    public void ponerPendientes(List<DetalleVenta> pendientes) {
        detalles.removeIf(DetalleVenta::esPendiente);
        detalles.addAll(0, pendientes);
    }

    public void quitar(DetalleVenta detalle) {
        detalles.remove(detalle);
    }

    /** Suma de los subtotales. Si la venta vino de la BD sin detalles, usa el total guardado. */
    public BigDecimal getTotal() {
        if (detalles.isEmpty() && totalGuardado != null) {
            return totalGuardado;
        }
        return detalles.stream()
                .map(DetalleVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public TipoComprobante getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(TipoComprobante tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    public Propietario getPropietario() {
        return propietario;
    }

    public void setPropietario(Propietario propietario) {
        this.propietario = propietario;
    }

    public Atencion getAtencion() {
        return atencion;
    }

    public void setAtencion(Atencion atencion) {
        this.atencion = atencion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public EstadoVenta getEstado() {
        return estado;
    }

    public void setEstado(EstadoVenta estado) {
        this.estado = estado;
    }

    public void setTotalGuardado(BigDecimal totalGuardado) {
        this.totalGuardado = totalGuardado;
    }
}
```

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/modelo/DetalleVenta.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.modelo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/** Una línea de la venta: qué se vendió, cuántos y a qué precio (tabla detalle_venta). */
public class DetalleVenta extends Entidad {

    // POLIMORFISMO: puede ser un Producto o un ServicioVeterinario
    @NotNull(message = "Elige un producto o servicio")
    private ItemVenta item;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;

    // Precio al momento de la venta (si luego cambia el precio del producto, la venta no cambia)
    @NotNull
    private BigDecimal precio;

    // Si la línea vino de lo pendiente del cliente (consulta o vacuna ya hecha)
    private Integer idAtencion;
    private Integer idVacunacion;
    private boolean yaDescontado;   // la vacuna ya bajó del stock al aplicarse
    private String descripcion;     // "Consulta - Kiwi" (solo para mostrar)

    public boolean esPendiente() {
        return idAtencion != null || idVacunacion != null;
    }

    public String getDescripcion() {
        return descripcion != null ? descripcion : item.getNombre();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getIdAtencion() {
        return idAtencion;
    }

    public void setIdAtencion(Integer idAtencion) {
        this.idAtencion = idAtencion;
    }

    public Integer getIdVacunacion() {
        return idVacunacion;
    }

    public void setIdVacunacion(Integer idVacunacion) {
        this.idVacunacion = idVacunacion;
    }

    public boolean isYaDescontado() {
        return yaDescontado;
    }

    public void setYaDescontado(boolean yaDescontado) {
        this.yaDescontado = yaDescontado;
    }

    public DetalleVenta() {
    }

    public DetalleVenta(ItemVenta item, int cantidad) {
        this.item = item;
        this.cantidad = cantidad;
        this.precio = item.getPrecio();
    }

    public BigDecimal getSubtotal() {
        if (precio == null || cantidad == null) {
            return BigDecimal.ZERO;
        }
        return precio.multiply(BigDecimal.valueOf(cantidad));
    }

    public ItemVenta getItem() {
        return item;
    }

    public void setItem(ItemVenta item) {
        this.item = item;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}
```

El resto del modelo (`Cita`, `Atencion`, `AtencionInsumo`, `Vacuna`, `Vacunacion`, `Receta`, `DetalleReceta`, `Compra`, `DetalleCompra`, `Proveedor`, `Usuario`) sigue el mismo molde y se describe en el **Anexo A**.

---

## 8. Paso 7 — Paquete `dto`

### 8.1 `OpcionCombo<T>` — opciones de los filtros

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/dto/OpcionCombo.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.dto;

/**
 * Una opción para los ComboBox de filtro. valor = null significa "Todos".
 * Ejemplo: new OpcionCombo<>(null, "Todas"), new OpcionCombo<>(Especie.PERRO, "Perro")
 */
public class OpcionCombo<T> {

    private final T valor;
    private final String texto;

    public OpcionCombo(T valor, String texto) {
        this.valor = valor;
        this.texto = texto;
    }

    public T getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return texto;
    }
}
```

### 8.2 `OpcionMenu` — una opción del menú y qué roles la ven

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/dto/OpcionMenu.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.dto;

import pe.edu.upeu.padronmascota.padronmascota.enums.Rol;

import java.util.EnumSet;
import java.util.Set;

/**
 * Una opción del MenuBar: en qué menú va, qué texto muestra, qué vista (.fxml) abre
 * y qué roles la pueden ver. menu y texto son CLAVES de idioma (por ejemplo "menu.padron").
 */
public class OpcionMenu {

    private final String menu;
    private final String texto;
    private final String vista;
    private final Set<Rol> roles;

    public OpcionMenu(String menu, String texto, String vista, Rol... roles) {
        this.menu = menu;
        this.texto = texto;
        this.vista = vista;
        // Sin roles = la ven todos
        this.roles = roles.length == 0 ? EnumSet.allOf(Rol.class) : EnumSet.of(roles[0], roles);
    }

    public String getMenu() {
        return menu;
    }

    public String getTexto() {
        return texto;
    }

    public String getVista() {
        return vista;
    }

    public boolean permitidaPara(Rol rol) {
        return roles.contains(rol);
    }
}
```

`FilaReporte` (una fila del reporte de ventas) y `ResumenPadron` (los números de la pantalla Resumen) son DTO del mismo tipo: solo datos, sin lógica.

---

## 9. Paso 8 — Paquete `repositorio`

### 9.1 Contrato genérico `IRepositorio<T>`

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/repositorio/IRepositorio.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import java.util.List;
import java.util.Optional;

/**
 * ABSTRACCIÓN: las operaciones que todo repositorio CRUD debe tener.
 * <T> es el tipo de objeto del modelo (Mascota, Propietario...).
 */
public interface IRepositorio<T> {

    T guardar(T obj);

    T actualizar(T obj);

    void eliminar(int id);

    Optional<T> buscarPorId(int id);

    List<T> listar();
}
```

### 9.2 `RepositorioBase<T>` — todo el JDBC en un solo lugar (Template Method)

Abre la conexión, prepara la sentencia con parámetros `?` (evita la inyección SQL), recorre el `ResultSet` y cierra todo con *try-with-resources*. Cada hija solo implementa `mapear()`: cómo convertir una fila en su objeto.

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/repositorio/RepositorioBase.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import pe.edu.upeu.padronmascota.padronmascota.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Clase padre de todos los repositorios (HERENCIA).
 * Tiene el código JDBC que se repetiría en cada uno: abrir conexión,
 * poner los "?" y recorrer el ResultSet. Las hijas solo escriben su SQL.
 */
public abstract class RepositorioBase<T> {

    /** Cada hija dice cómo convertir una fila en su objeto. */
    protected abstract T mapear(ResultSet rs) throws SQLException;

    /** SELECT que devuelve varias filas. */
    protected List<T> consultar(String sql, Object... params) {
        try (Connection cn = DatabaseConfig.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ponerParametros(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar: " + e.getMessage(), e);
        }
    }

    /** SELECT que devuelve una sola fila, o vacío si no hay. */
    protected Optional<T> consultarUno(String sql, Object... params) {
        List<T> lista = consultar(sql, params);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    /** INSERT que devuelve el id que generó la base de datos (IDENTITY). */
    protected int insertar(String sql, Object... params) {
        try (Connection cn = DatabaseConfig.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ponerParametros(ps, params);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    return claves.getInt(1);
                }
                throw new SQLException("La base de datos no devolvió el id generado");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar: " + e.getMessage(), e);
        }
    }

    /** UPDATE o DELETE. Devuelve cuántas filas cambió. */
    protected int ejecutar(String sql, Object... params) {
        try (Connection cn = DatabaseConfig.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ponerParametros(ps, params);
            int filas = ps.executeUpdate();
            if (filas > 0 && sql.stripLeading().toUpperCase().startsWith("DELETE")) {
                DatabaseConfig.reiniciarIdsVacios();   // si la tabla quedó vacía, el próximo ID es 1
            }
            return filas;
        } catch (SQLException e) {
            throw new RuntimeException("Error al ejecutar: " + e.getMessage(), e);
        }
    }

    /** SELECT de una sola columna de texto (ej. nombres): devuelve la lista. */
    protected List<String> textos(String sql, Object... params) {
        try (Connection cn = DatabaseConfig.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ponerParametros(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<String> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(rs.getString(1));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar: " + e.getMessage(), e);
        }
    }

    /** SELECT COUNT(*) ... : devuelve el número. */
    protected int contarFilas(String sql, Object... params) {
        try (Connection cn = DatabaseConfig.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ponerParametros(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar: " + e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------------
    //  TRANSACCIONES: varias sentencias que se guardan todas juntas o ninguna
    // ------------------------------------------------------------------

    /** Lo que se hace dentro de la transacción (recibe la conexión abierta). */
    @FunctionalInterface
    protected interface TrabajoSql<R> {
        R ejecutar(Connection cn) throws SQLException;
    }

    /**
     * Abre una conexión, apaga el auto-commit, ejecuta el trabajo y hace commit.
     * Si algo falla (SQL o una regla del negocio), hace rollback: no se guarda nada.
     */
    protected <R> R enTransaccion(TrabajoSql<R> trabajo) {
        try (Connection cn = DatabaseConfig.getConnection()) {
            cn.setAutoCommit(false);
            try {
                R resultado = trabajo.ejecutar(cn);
                cn.commit();
                DatabaseConfig.reiniciarIdsVacios();   // por si la transacción borró y dejó tablas vacías
                return resultado;
            } catch (SQLException | RuntimeException e) {
                cn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en la transacción: " + e.getMessage(), e);
        }
    }

    /** INSERT dentro de una transacción. Devuelve el id generado. */
    protected static int insertarEn(Connection cn, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ponerParametros(ps, params);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                claves.next();
                return claves.getInt(1);
            }
        }
    }

    /** UPDATE o DELETE dentro de una transacción. */
    protected static int ejecutarEn(Connection cn, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ponerParametros(ps, params);
            return ps.executeUpdate();
        }
    }

    /**
     * Stock actual de un producto según la vista v_stock_producto
     * (comprados - vendidos - usados). Dentro de la transacción ya cuenta lo que acabamos de insertar.
     */
    protected static int stockActual(Connection cn, int idProducto) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT comprados - vendidos - usados FROM v_stock_producto WHERE id_producto = ?")) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /** Si después de descontar el stock quedó negativo, lanza el error (y la transacción hace rollback). */
    protected static void verificarStock(Connection cn, int idProducto, String nombre) throws SQLException {
        int stock = stockActual(cn, idProducto);
        if (stock < 0) {
            throw new IllegalStateException("No hay stock suficiente de " + nombre + " (faltan " + (-stock) + ")");
        }
    }

    /** LocalDate desde una columna DATE (null si está vacía). */
    protected static java.time.LocalDate fecha(ResultSet rs, String columna) throws SQLException {
        return rs.getObject(columna, java.time.LocalDate.class);
    }

    /** Integer desde una columna que puede ser NULL (getInt devolvería 0). */
    protected static Integer enteroONull(ResultSet rs, String columna) throws SQLException {
        int valor = rs.getInt(columna);
        return rs.wasNull() ? null : valor;
    }

    // Reemplaza cada "?" en orden. En JDBC el primer "?" es el 1, no el 0.
    private static void ponerParametros(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }
}
```

### 9.3 `PropietarioRepositorio`

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/repositorio/PropietarioRepositorio.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import pe.edu.upeu.padronmascota.padronmascota.modelo.Propietario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Tabla propietario: solo SQL, sin reglas de negocio. */
public class PropietarioRepositorio extends RepositorioBase<Propietario> implements IRepositorio<Propietario> {

    private static final String SELECT = "SELECT p.id_propietario, p.dni, p.nombre, p.telefono, p.direccion, p.email, "
            + "(SELECT COUNT(*) FROM mascota m WHERE m.id_propietario = p.id_propietario AND m.activo) AS n_mascotas "
            + "FROM propietario p ";

    @Override
    protected Propietario mapear(ResultSet rs) throws SQLException {
        Propietario p = new Propietario(rs.getInt("id_propietario"), rs.getString("nombre"), rs.getString("telefono"));
        p.setDni(rs.getString("dni"));
        p.setDireccion(rs.getString("direccion"));
        p.setEmail(rs.getString("email"));
        p.setNumeroMascotas(rs.getInt("n_mascotas"));
        return p;
    }

    @Override
    public Propietario guardar(Propietario p) {
        p.setId(insertar("INSERT INTO propietario (dni, nombre, telefono, direccion, email) VALUES (?, ?, ?, ?, ?)",
                p.getDni(), p.getNombre(), p.getTelefono(), p.getDireccion(), p.getEmail()));
        return p;
    }

    @Override
    public Propietario actualizar(Propietario p) {
        ejecutar("UPDATE propietario SET dni = ?, nombre = ?, telefono = ?, direccion = ?, email = ? WHERE id_propietario = ?",
                p.getDni(), p.getNombre(), p.getTelefono(), p.getDireccion(), p.getEmail(), p.getId());
        return p;
    }

    @Override
    public void eliminar(int id) {
        ejecutar("DELETE FROM propietario WHERE id_propietario = ?", id);
    }

    @Override
    public Optional<Propietario> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE p.id_propietario = ?", id);
    }

    @Override
    public List<Propietario> listar() {
        return consultar(SELECT + "WHERE p.activo ORDER BY p.id_propietario");   // solo los activos, por ID
    }

    /** Busca por nombre, DNI o teléfono. */
    public List<Propietario> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return listar();
        }
        String like = "%" + texto.trim().toLowerCase() + "%";
        return consultar(SELECT + "WHERE p.activo AND (LOWER(p.nombre) LIKE ? OR p.dni LIKE ? OR p.telefono LIKE ?) ORDER BY p.id_propietario",
                like, like, like);
    }

    /** ¿dni repetido? (otro propietario con el mismo DNI) */
    public boolean existeDni(String dni, int idDistinto) {
        return contarFilas("SELECT COUNT(*) FROM propietario WHERE dni = ? AND id_propietario <> ?", dni, idDistinto) > 0;
    }

    /** Baja lógica: el propietario y sus mascotas dejan de salir, pero sus ventas y atenciones se conservan. */
    public void darDeBaja(int idPropietario) {
        enTransaccion(cn -> {
            ejecutarEn(cn, "UPDATE mascota SET activo = FALSE WHERE id_propietario = ?", idPropietario);
            ejecutarEn(cn, "UPDATE propietario SET activo = FALSE WHERE id_propietario = ?", idPropietario);
            return null;
        });
    }

    /** Mascotas que se pueden borrar junto con él: las que no tienen atenciones. */
    public boolean mascotasTienenAtenciones(int idPropietario) {
        return contarFilas("SELECT COUNT(*) FROM atencion a JOIN mascota m ON m.id_mascota = a.id_mascota "
                + "WHERE m.id_propietario = ?", idPropietario) > 0;
    }

    /** Borrado DEFINITIVO del propietario con sus mascotas (y las citas y vacunaciones de ellas). */
    public void borrarConMascotas(int idPropietario) {
        enTransaccion(cn -> {
            String deSusMascotas = "IN (SELECT id_mascota FROM mascota WHERE id_propietario = ?)";
            ejecutarEn(cn, "DELETE FROM cita WHERE id_mascota " + deSusMascotas, idPropietario);
            ejecutarEn(cn, "DELETE FROM vacunacion WHERE id_mascota " + deSusMascotas, idPropietario);
            ejecutarEn(cn, "DELETE FROM mascota WHERE id_propietario = ?", idPropietario);
            ejecutarEn(cn, "DELETE FROM propietario WHERE id_propietario = ?", idPropietario);
            return null;
        });
    }

    public int contarMascotas(int idPropietario) {
        return contarFilas("SELECT COUNT(*) FROM mascota WHERE id_propietario = ?", idPropietario);
    }

    public int contarVentas(int idPropietario) {
        return contarFilas("SELECT COUNT(*) FROM venta WHERE id_propietario = ?", idPropietario);
    }
}
```

### 9.4 `MascotaRepositorio`

Une la mascota con su propietario, raza y color (`JOIN`) para traerlos en una sola consulta.

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/repositorio/MascotaRepositorio.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.repositorio;

import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoVacunacion;
import pe.edu.upeu.padronmascota.padronmascota.enums.Sexo;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Color;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Mascota;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Propietario;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Raza;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Tabla mascota con JOIN a raza, color y propietario (la "Consulta mascota" del Excel). */
public class MascotaRepositorio extends RepositorioBase<Mascota> implements IRepositorio<Mascota> {

    private static final String SELECT =
            "SELECT m.id_mascota, m.nombre, m.id_especie, m.id_raza, r.nombre AS raza, m.id_color, c.nombre AS color, "
            + "m.sexo, m.edad_aprox, m.fecha_registro, m.id_estado, m.activo, m.observaciones, "
            + "p.id_propietario, p.nombre AS propietario, p.telefono, p.dni "
            + "FROM mascota m "
            + "LEFT JOIN raza r ON r.id_raza = m.id_raza "      // LEFT: la mascota "por completar" aún no tiene raza
            + "LEFT JOIN color c ON c.id_color = m.id_color "
            + "JOIN propietario p ON p.id_propietario = m.id_propietario ";

    @Override
    protected Mascota mapear(ResultSet rs) throws SQLException {
        Mascota m = new Mascota();
        m.setId(rs.getInt("id_mascota"));
        m.setNombre(rs.getString("nombre"));
        m.setEspecie(Especie.desdeId(rs.getInt("id_especie")));
        Integer idRaza = enteroONull(rs, "id_raza");
        m.setRaza(idRaza == null ? null : new Raza(idRaza, rs.getString("raza"), m.getEspecie()));
        Integer idColor = enteroONull(rs, "id_color");
        m.setColor(idColor == null ? null : new Color(idColor, rs.getString("color")));
        String sexo = rs.getString("sexo");
        m.setSexo(sexo == null ? null : Sexo.desdeNombre(sexo));
        m.setEdadAprox(rs.getInt("edad_aprox"));
        m.setFechaRegistro(fecha(rs, "fecha_registro"));
        m.setEstado(EstadoVacunacion.desdeId(rs.getInt("id_estado")));
        m.setActivo(rs.getBoolean("activo"));
        m.setObservaciones(rs.getString("observaciones"));
        Propietario p = new Propietario(rs.getInt("id_propietario"), rs.getString("propietario"), rs.getString("telefono"));
        p.setDni(rs.getString("dni"));
        m.setPropietario(p);
        return m;
    }

    /** Una mascota "corta" (solo lo necesario) para las tablas de citas, atenciones y vacunas. */
    static Mascota corta(int id, String nombre, int idEspecie, int idPropietario, String propietario) {
        Mascota m = new Mascota();
        m.setId(id);
        m.setNombre(nombre);
        m.setEspecie(Especie.desdeId(idEspecie));
        m.setPropietario(new Propietario(idPropietario, propietario, null));
        return m;
    }

    @Override
    public Mascota guardar(Mascota m) {
        m.setId(insertar("INSERT INTO mascota (nombre, id_especie, id_raza, id_color, sexo, edad_aprox, fecha_registro, "
                        + "id_propietario, id_estado, activo, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                m.getNombre(), m.getEspecie().getId(), idDe(m.getRaza()), idDe(m.getColor()),
                m.getSexo() == null ? null : m.getSexo().getNombre(),
                m.getEdadAprox(), m.getFechaRegistro(), m.getPropietario().getId(), m.getEstado().getId(),
                m.isActivo(), m.getObservaciones()));
        return m;
    }

    @Override
    public Mascota actualizar(Mascota m) {
        ejecutar("UPDATE mascota SET nombre = ?, id_especie = ?, id_raza = ?, id_color = ?, sexo = ?, edad_aprox = ?, "
                        + "fecha_registro = ?, id_propietario = ?, id_estado = ?, activo = ?, observaciones = ? WHERE id_mascota = ?",
                m.getNombre(), m.getEspecie().getId(), idDe(m.getRaza()), idDe(m.getColor()),
                m.getSexo() == null ? null : m.getSexo().getNombre(),
                m.getEdadAprox(), m.getFechaRegistro(), m.getPropietario().getId(), m.getEstado().getId(),
                m.isActivo(), m.getObservaciones(), m.getId());
        return m;
    }

    private static Integer idDe(pe.edu.upeu.padronmascota.padronmascota.modelo.Entidad e) {
        return e == null ? null : e.getId();
    }

    /** Borrado lógico: no se elimina la fila, se marca activo = FALSE. */
    @Override
    public void eliminar(int id) {
        ejecutar("UPDATE mascota SET activo = FALSE WHERE id_mascota = ?", id);
    }

    /** Cuántas atenciones tiene (si tiene, no se puede borrar de verdad: hay recetas, insumos o ventas detrás). */
    public int contarAtenciones(int id) {
        return contarFilas("SELECT COUNT(*) FROM atencion WHERE id_mascota = ?", id);
    }

    /** Lo que se borraría junto con la mascota: [citas, vacunaciones, atenciones, recetas, ventas ligadas]. */
    public int[] contarCitasYVacunas(int id) {
        String deSusAtenciones = "IN (SELECT id_atencion FROM atencion WHERE id_mascota = ?)";
        return new int[]{contarFilas("SELECT COUNT(*) FROM cita WHERE id_mascota = ?", id),
                contarFilas("SELECT COUNT(*) FROM vacunacion WHERE id_mascota = ?", id),
                contarAtenciones(id),
                contarFilas("SELECT COUNT(*) FROM receta WHERE id_atencion " + deSusAtenciones, id),
                contarFilas("SELECT COUNT(*) FROM venta WHERE id_atencion " + deSusAtenciones, id)};
    }

    /**
     * Borrado DEFINITIVO con todo su historial, en una sola transacción:
     * recetas (y su detalle), atenciones (y sus insumos), citas, vacunaciones y la mascota.
     * Las ventas NO se borran (es plata que ya entró): solo se les quita la atención.
     */
    public void borrarDefinitivo(int id) {
        enTransaccion(cn -> {
            String deSusAtenciones = "IN (SELECT id_atencion FROM atencion WHERE id_mascota = ?)";
            ejecutarEn(cn, "UPDATE venta SET id_atencion = NULL WHERE id_atencion " + deSusAtenciones, id);
            ejecutarEn(cn, "DELETE FROM detalle_receta WHERE id_receta IN (SELECT id_receta FROM receta WHERE id_atencion "
                    + deSusAtenciones + ")", id);
            ejecutarEn(cn, "DELETE FROM receta WHERE id_atencion " + deSusAtenciones, id);
            ejecutarEn(cn, "DELETE FROM atencion_insumo WHERE id_atencion " + deSusAtenciones, id);
            ejecutarEn(cn, "DELETE FROM atencion WHERE id_mascota = ?", id);
            ejecutarEn(cn, "DELETE FROM cita WHERE id_mascota = ?", id);
            ejecutarEn(cn, "DELETE FROM vacunacion WHERE id_mascota = ?", id);
            ejecutarEn(cn, "DELETE FROM mascota WHERE id_mascota = ?", id);
            return null;
        });
    }

    public void reactivar(int id) {
        ejecutar("UPDATE mascota SET activo = TRUE WHERE id_mascota = ?", id);
    }

    @Override
    public Optional<Mascota> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE m.id_mascota = ?", id);
    }

    /** Solo las activas (como el filtro del Excel). */
    @Override
    public List<Mascota> listar() {
        return consultar(SELECT + "WHERE m.activo ORDER BY m.nombre");
    }

    /**
     * Filtro de mascotas del Excel. especie / estado null = Todos.
     * soloActivas = false muestra también las dadas de baja.
     */
    public List<Mascota> buscar(String texto, Especie especie, EstadoVacunacion estado, boolean soloActivas) {
        StringBuilder sql = new StringBuilder(SELECT).append("WHERE 1 = 1 ");
        List<Object> params = new ArrayList<>();
        if (soloActivas) {
            sql.append("AND m.activo ");
        }
        if (texto != null && !texto.isBlank()) {
            sql.append("AND (LOWER(m.nombre) LIKE ? OR LOWER(r.nombre) LIKE ? OR LOWER(p.nombre) LIKE ?) ");
            String like = "%" + texto.trim().toLowerCase() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (especie != null) {
            sql.append("AND m.id_especie = ? ");
            params.add(especie.getId());
        }
        if (estado != null) {
            sql.append("AND m.id_estado = ? ");
            params.add(estado.getId());
        }
        sql.append("ORDER BY m.id_mascota");
        return consultar(sql.toString(), params.toArray());
    }

    /** Mascotas activas de un propietario (para el combo de Ventas). */
    public List<Mascota> dePropietario(int idPropietario) {
        return consultar(SELECT + "WHERE m.activo AND m.id_propietario = ? ORDER BY m.nombre", idPropietario);
    }
}
```

---

## 10. Paso 9 — Paquete `servicio`

### 10.1 Contrato genérico `IServicioCrud<T>`

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/servicio/IServicioCrud.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.servicio;

import java.util.List;

/**
 * Lo que cualquier servicio CRUD ofrece a las pantallas.
 * El controlador solo conoce esta interfaz, no sabe cómo está hecha por dentro.
 */
public interface IServicioCrud<T> {

    T registrar(T obj);

    T modificar(int id, T obj);

    void eliminar(int id);

    T obtener(int id);

    List<T> listarTodos();
}
```

### 10.2 `Validador` — revisa las anotaciones del modelo

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/servicio/impl/Validador.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.excepcion.ValidacionException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Revisa las anotaciones del modelo (@NotBlank, @Pattern, @Min...).
 * Lo usan ServicioCrudBase y VentaServicioImpl.
 */
public final class Validador {

    // Un solo validador para toda la app (crearlo es lento).
    // ParameterMessageInterpolator: usa los mensajes tal cual los escribimos en las anotaciones.
    private static final Validator VALIDADOR = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory()
            .getValidator();

    private Validador() {
    }

    /** Si el objeto tiene errores, lanza ValidacionException con un mensaje por campo. */
    public static <T> void validar(T obj) {
        Set<ConstraintViolation<T>> violaciones = VALIDADOR.validate(obj);
        if (violaciones.isEmpty()) {
            return;
        }
        Map<String, String> errores = new LinkedHashMap<>();
        violaciones.stream()
                .sorted(Comparator.comparing(v -> v.getPropertyPath().toString()))
                .forEach(v -> errores.putIfAbsent(v.getPropertyPath().toString(), v.getMessage()));
        throw new ValidacionException(errores);
    }
}
```

### 10.3 `ServicioCrudBase<T>` — el CRUD común (Template Method)

`registrar()` siempre hace lo mismo: **validar anotaciones → validar reglas de negocio → guardar**. Cada servicio hijo solo dice cuál es su repositorio y, si lo necesita, sobrescribe `validarNegocio()`.

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/servicio/impl/ServicioCrudBase.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.excepcion.NoEncontradoException;
import pe.edu.upeu.padronmascota.padronmascota.excepcion.ValidacionException;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Entidad;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.IRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IServicioCrud;

import java.util.List;

/**
 * Implementación común para todos los servicios CRUD (HERENCIA).
 * Antes de guardar, revisa las anotaciones del modelo (@NotBlank, @Pattern, @Min...).
 * Cada hija solo dice cuál es su repositorio.
 */
public abstract class ServicioCrudBase<T extends Entidad> implements IServicioCrud<T> {

    protected abstract IRepositorio<T> repositorio();

    @Override
    public T registrar(T obj) {
        validar(obj);
        validarNegocio(obj);
        return repositorio().guardar(obj);
    }

    @Override
    public T modificar(int id, T obj) {
        obtener(id); // si no existe, lanza NoEncontradoException
        validar(obj);
        validarNegocio(obj);
        return repositorio().actualizar(obj);
    }

    @Override
    public void eliminar(int id) {
        obtener(id);
        repositorio().eliminar(id);
    }

    @Override
    public T obtener(int id) {
        return repositorio().buscarPorId(id)
                .orElseThrow(() -> new NoEncontradoException("No existe el registro con id " + id));
    }

    @Override
    public List<T> listarTodos() {
        return repositorio().listar();
    }

    /** Si el objeto tiene errores, lanza ValidacionException con un mensaje por campo. */
    protected void validar(T obj) {
        Validador.validar(obj);
    }

    /**
     * Reglas que necesitan la base de datos (por ejemplo "DNI repetido").
     * Cada hija la sobrescribe si la necesita (POLIMORFISMO). Por defecto no hace nada.
     */
    protected void validarNegocio(T obj) {
    }

    /** Lanza el error de un solo campo, para que la pantalla lo marque en rojo. */
    protected static void errorEnCampo(String campo, String mensaje) {
        throw new ValidacionException(java.util.Map.of(campo, mensaje));
    }
}
```

### 10.4 Propietario

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/servicio/IPropietarioServicio.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.servicio;


import pe.edu.upeu.padronmascota.padronmascota.modelo.Propietario;

import java.util.List;

public interface IPropietarioServicio extends IServicioCrud<Propietario> {

    /** Busca por nombre, DNI o teléfono. */
    List<Propietario> buscar(String texto);
}
```

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/servicio/impl/PropietarioServicioImpl.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.modelo.Propietario;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.IRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.PropietarioRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IPropietarioServicio;

import java.util.List;

public class PropietarioServicioImpl extends ServicioCrudBase<Propietario> implements IPropietarioServicio {

    private final PropietarioRepositorio repo;

    public PropietarioServicioImpl(PropietarioRepositorio repo) {
        this.repo = repo;
    }

    @Override
    protected IRepositorio<Propietario> repositorio() {
        return repo;
    }

    @Override
    public List<Propietario> buscar(String texto) {
        return repo.buscar(texto);
    }

    /** dni: 8 dígitos y UNIQUE. */
    @Override
    protected void validarNegocio(Propietario p) {
        if (repo.existeDni(p.getDni(), p.getId())) {
            errorEnCampo("dni", "Ya existe un propietario con el DNI " + p.getDni());
        }
    }

    /**
     * Eliminar un propietario:
     *  - Si NO tiene ventas ni atenciones: se borra de verdad, junto con sus mascotas (y sus citas y vacunaciones).
     *  - Si tiene ventas o atenciones: se da de baja (él y sus mascotas dejan de salir) para no perder las cuentas.
     */
    @Override
    public void eliminar(int id) {
        obtener(id); // si no existe lanza NoEncontradoException
        if (repo.contarVentas(id) > 0 || repo.mascotasTienenAtenciones(id)) {
            repo.darDeBaja(id);
        } else {
            repo.borrarConMascotas(id);
        }
    }
}
```

### 10.5 Mascota

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/servicio/IMascotaServicio.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.servicio;

import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoVacunacion;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Mascota;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Propietario;

import java.util.List;

public interface IMascotaServicio extends IServicioCrud<Mascota> {

    List<Mascota> buscar(String texto, Especie especie, EstadoVacunacion estado, boolean soloActivas);

    /** Vuelve a activar una mascota dada de baja. */
    void reactivar(int id);

    /** [citas, vacunaciones] que se borrarían junto con la mascota. */
    int[] historialQueSeBorra(int id);

    /** Borra la mascota de verdad (con sus citas y vacunaciones). No se puede si tiene atenciones. */
    void eliminarDefinitivo(int id);

    List<Mascota> dePropietario(int idPropietario);

    /**
     * Registra una mascota con lo mínimo (nombre, especie y dueño) al sacar una cita.
     * Queda "por completar": raza, color, sexo y edad se llenan después en Mascotas.
     */
    Mascota registrarPorCompletar(String nombre, Especie especie, Propietario propietario);
}
```

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/servicio/impl/MascotaServicioImpl.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.servicio.impl;

import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoVacunacion;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Mascota;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Propietario;
import pe.edu.upeu.padronmascota.padronmascota.excepcion.ValidacionException;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.IRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.repositorio.MascotaRepositorio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IMascotaServicio;

import java.util.List;

public class MascotaServicioImpl extends ServicioCrudBase<Mascota> implements IMascotaServicio {

    private final MascotaRepositorio repo;

    // El repositorio llega por el constructor: lo crea el ContenedorApp
    public MascotaServicioImpl(MascotaRepositorio repo) {
        this.repo = repo;
    }

    @Override
    protected IRepositorio<Mascota> repositorio() {
        return repo;
    }

    @Override
    public List<Mascota> buscar(String texto, Especie especie, EstadoVacunacion estado, boolean soloActivas) {
        return repo.buscar(texto, especie, estado, soloActivas);
    }

    /** Borrado lógico: la mascota no se borra, se marca activo = No (el repositorio hace el UPDATE). */
    @Override
    public void eliminar(int id) {
        super.eliminar(id);
    }

    @Override
    public int[] historialQueSeBorra(int id) {
        return repo.contarCitasYVacunas(id);
    }

    @Override
    public void eliminarDefinitivo(int id) {
        obtener(id);   // si no existe, avisa
        repo.borrarDefinitivo(id);   // borra también su historial (las ventas se conservan)
    }

    @Override
    public void reactivar(int id) {
        obtener(id);
        repo.reactivar(id);
    }

    @Override
    public List<Mascota> dePropietario(int idPropietario) {
        return repo.dePropietario(idPropietario);
    }

    @Override
    public Mascota registrarPorCompletar(String nombre, Especie especie, Propietario propietario) {
        java.util.Map<String, String> errores = new java.util.LinkedHashMap<>();
        if (nombre == null || nombre.isBlank()) {
            errores.put("mascota", "Escribe el nombre de la mascota");
        }
        if (especie == null) {
            errores.put("especie", "Elige la especie de la mascota nueva");
        }
        if (propietario == null) {
            errores.put("propietario", "Escribe el dueño de la mascota nueva");
        }
        if (!errores.isEmpty()) {
            throw new ValidacionException(errores);
        }
        Mascota m = new Mascota();
        m.setNombre(nombre.trim());
        m.setEspecie(especie);
        m.setPropietario(propietario);
        m.setEdadAprox(0);
        m.setFechaRegistro(java.time.LocalDate.now());
        m.setEstado(EstadoVacunacion.PENDIENTE);
        m.setObservaciones("POR COMPLETAR: registrada al sacar una cita (falta raza, color, sexo y edad)");
        return repo.guardar(m);   // sin validar raza/color/sexo: se completan después en Mascotas
    }
}
```

### 10.6 Menú según el rol

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/servicio/IMenuServicio.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.servicio;

import pe.edu.upeu.padronmascota.padronmascota.dto.OpcionMenu;

import java.util.List;

public interface IMenuServicio {

    /** Las opciones que se dibujan en el MenuBar de la ventana principal. */
    List<OpcionMenu> opciones();
}
```

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/servicio/impl/MenuServicioImpl.java`

```java
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
```

---

## 11. Paso 10 — Paquete `seguridad`

### 11.1 `Clave` — contraseñas con BCrypt

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/seguridad/Clave.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.seguridad;

import org.mindrot.jbcrypt.BCrypt;

/**
 * La clave NO se guarda tal cual en la base de datos: se guarda su hash BCrypt.
 * BCrypt agrega una "sal" al azar, así dos usuarios con la misma clave tienen hash distinto.
 * Al iniciar sesión, BCrypt.checkpw compara lo que escribió el usuario con el hash guardado.
 */
public final class Clave {

    private Clave() {
    }

    /** "admin123" -> "$2a$10$...." (60 caracteres, distinto cada vez) */
    public static String hash(String clave) {
        return BCrypt.hashpw(clave, BCrypt.gensalt(10));
    }

    public static boolean coincide(String claveEscrita, String hashGuardado) {
        if (hashGuardado == null || !hashGuardado.startsWith("$2")) {
            return false;
        }
        return BCrypt.checkpw(claveEscrita, hashGuardado);
    }
}
```

### 11.2 `Sesion` — quién entró al sistema

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/seguridad/Sesion.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.seguridad;

import pe.edu.upeu.padronmascota.padronmascota.modelo.Usuario;

/** Guarda quién inició sesión mientras el sistema está abierto. */
public final class Sesion {

    private static Usuario usuario;

    private Sesion() {
    }

    public static void iniciar(Usuario u) {
        usuario = u;
    }

    public static void cerrar() {
        usuario = null;
    }

    public static Usuario usuario() {
        return usuario;
    }

    public static boolean activa() {
        return usuario != null;
    }
}
```

---

## 12. Paso 11 — Paquete `componentes`

Clases de pantalla que usan **todos** los controladores, para no repetir código.

### 12.1 `TablaConAcciones<T>` — columnas y botones de la tabla

Arma las columnas con una función por columna (`m -> m.getNombre()`) y agrega al final la columna **Acciones** con botones de ícono (lápiz, papelera...). Lo que hace cada botón llega como `Consumer<T>` (Strategy).

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/componentes/TablaConAcciones.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.componentes;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Arma las columnas de un TableView sin escribir una por una en el FXML.
 * Cada columna recibe una función que saca el valor del objeto,
 * por ejemplo: m -> m.getEspecie().getNombre()
 */
public class TablaConAcciones<T> {

    private final TableView<T> tabla;

    public TablaConAcciones(TableView<T> tabla) {
        this.tabla = tabla;
        tabla.getColumns().clear();
        // Si las columnas entran, se estiran hasta llenar la tabla; si no entran,
        // aparece la barra horizontal en vez de cortar los textos con "..."
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tabla.widthProperty().addListener((o, antes, ahora) -> ajustarPolitica());
        tabla.getColumns().addListener((javafx.collections.ListChangeListener<TableColumn<T, ?>>) c -> ajustarPolitica());
        tabla.setPlaceholder(new Label(Idioma.t("comun.sinDatos")));   // tabla vacía
    }

    /** Agrega una columna de datos. Devuelve this para poder encadenar llamadas. */
    public TablaConAcciones<T> columna(String titulo, Function<T, Object> valor, double ancho) {
        TableColumn<T, Object> col = new TableColumn<>(titulo);
        col.setCellValueFactory(fila -> new ReadOnlyObjectWrapper<>(valor.apply(fila.getValue())));
        double minimo = Math.max(ancho, anchoDelTitulo(titulo));
        col.setMinWidth(minimo);   // el título y los datos nunca se recortan
        col.setPrefWidth(minimo);
        tabla.getColumns().add(col);
        return this;
    }

    // ------------------------------------------------------------------
    //  Botones con ícono (el texto aparece al pasar el mouse)
    // ------------------------------------------------------------------

    /** Ancho de la columna Acciones según cuántos botones tiene (índice = cantidad). */
    private static final double[] ANCHO_ACCIONES = {0, 70, 106, 142};

    private static final String LAPIZ = "M3 17.3 V21 h3.7 L17.8 9.9 l-3.7 -3.7 z M20.7 7 a1 1 0 0 0 0 -1.4 l-2.3 -2.3 a1 1 0 0 0 -1.4 0 l-1.8 1.8 l3.7 3.7 z";
    private static final String PAPELERA = "M6 19 a2 2 0 0 0 2 2 h8 a2 2 0 0 0 2 -2 V7 H6 z M19 4 h-3.5 l-1 -1 h-5 l-1 1 H5 v2 h14 z";
    private static final String OJO = "M12 4.5 C7 4.5 2.7 7.6 1 12 c1.7 4.4 6 7.5 11 7.5 s9.3 -3.1 11 -7.5 C21.3 7.6 17 4.5 12 4.5 z M12 17 a5 5 0 1 1 0 -10 a5 5 0 0 1 0 10 z M12 9 a3 3 0 1 0 0 6 a3 3 0 0 0 0 -6 z";
    private static final String QUITAR = "M19 6.4 L17.6 5 L12 10.6 L6.4 5 L5 6.4 L10.6 12 L5 17.6 L6.4 19 L12 13.4 L17.6 19 L19 17.6 L13.4 12 z";
    private static final String PROHIBIDO = "M12 2 a10 10 0 1 0 0 20 a10 10 0 0 0 0 -20 z M4 12 c0 -1.8 0.6 -3.5 1.7 -4.9 l11.2 11.2 A8 8 0 0 1 4 12 z M18.3 16.9 L7.1 5.7 A8 8 0 0 1 18.3 16.9 z";
    private static final String ENCENDIDO = "M11 2 h2 v10 h-2 z M17.6 5.1 l-1.4 1.4 A7 7 0 1 1 7.8 6.5 L6.4 5.1 A9 9 0 1 0 17.6 5.1 z";
    private static final String IMPRESORA = "M19 8 H5 a3 3 0 0 0 -3 3 v6 h4 v4 h12 v-4 h4 v-6 a3 3 0 0 0 -3 -3 z M16 19 H8 v-5 h8 z M18 3 H6 v4 h12 z";
    private static final String CARNET = "M20 4 H4 a2 2 0 0 0 -2 2 v12 a2 2 0 0 0 2 2 h16 a2 2 0 0 0 2 -2 V6 a2 2 0 0 0 -2 -2 z M8 7 a2.5 2.5 0 1 1 0 5 a2.5 2.5 0 0 1 0 -5 z M12.5 17 h-9 c0 -2 2 -3.2 4.5 -3.2 s4.5 1.2 4.5 3.2 z M20 15 h-6 v-2 h6 z M20 11 h-6 V9 h6 z";
    private static final String RECIBO = "M18 2 H6 v20 l2 -1.5 l2 1.5 l2 -1.5 l2 1.5 l2 -1.5 l2 1.5 z M15 15 H9 v-2 h6 z M15 11 H9 V9 h6 z M15 7 H9 V5 h6 z";

    /** Ícono que corresponde al texto del botón (según la clave de idioma). */
    private static String icono(String texto) {
        // HashMap (no Map.of): en algún idioma dos textos podrían ser iguales
        java.util.Map<String, String> iconos = new java.util.HashMap<>();
        iconos.put(Idioma.t("venta.anular"), PROHIBIDO);
        iconos.put(Idioma.t("cita.cancelar"), PROHIBIDO);
        iconos.put(Idioma.t("mascota.baja"), ENCENDIDO);
        iconos.put(Idioma.t("impresion.imprimir"), IMPRESORA);
        iconos.put(Idioma.t("impresion.carnet"), CARNET);
        iconos.put(Idioma.t("venta.comprobanteBtn"), RECIBO);
        iconos.put(Idioma.t("venta.quitar"), QUITAR);
        iconos.put(Idioma.t("venta.ver"), OJO);
        iconos.put(Idioma.t("comun.eliminar"), PAPELERA);
        iconos.put(Idioma.t("comun.editar"), LAPIZ);
        return iconos.get(texto);
    }

    /** Botón chico de la tabla: con ícono y el texto como ayuda; si no hay ícono, con texto. */
    private static Button boton(String texto, String clase) {
        Button b = new Button();
        b.getStyleClass().addAll("btn", "btn-chico", clase);
        String svg = icono(texto);
        if (svg == null) {
            b.setText(texto);
        } else {
            javafx.scene.shape.SVGPath forma = new javafx.scene.shape.SVGPath();
            forma.setContent(svg);
            forma.setScaleX(0.68);
            forma.setScaleY(0.68);
            b.setGraphic(new javafx.scene.Group(forma));
            b.getStyleClass().add("btn-icono");
        }
        javafx.scene.control.Tooltip ayuda = new javafx.scene.control.Tooltip(texto);
        ayuda.setShowDelay(javafx.util.Duration.millis(250));
        b.setTooltip(ayuda);
        return b;
    }

    /** Elige entre "llenar el ancho" o "permitir desplazarse" según si las columnas entran. */
    private void ajustarPolitica() {
        double minimos = 0;
        for (TableColumn<T, ?> c : tabla.getColumns()) {
            minimos += c.getMinWidth();
        }
        boolean entran = minimos + 20 <= tabla.getWidth(); // 20 = barra vertical y bordes
        tabla.setColumnResizePolicy(entran
                ? TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
                : TableView.UNCONSTRAINED_RESIZE_POLICY);
    }

    /** Ancho que necesita el título de la columna (en negrita) más su margen. */
    private static double anchoDelTitulo(String titulo) {
        javafx.scene.text.Text t = new javafx.scene.text.Text(titulo);
        t.setFont(javafx.scene.text.Font.font(null, javafx.scene.text.FontWeight.BOLD, 12));
        return Math.ceil(t.getLayoutBounds().getWidth()) + 22;
    }

    /**
     * Agrega la columna con los botones Editar y Eliminar.
     * Si alEliminar es null, no se muestra el botón Eliminar.
     */
    public TablaConAcciones<T> acciones(Consumer<T> alEditar, Consumer<T> alEliminar) {
        return acciones(Idioma.t("comun.editar"), alEditar, Idioma.t("comun.eliminar"), alEliminar);
    }

    /** Con un TERCER botón (rojo) a la derecha; el segundo queda gris. Ej.: Editar | Baja/Alta | Eliminar. */
    public TablaConAcciones<T> acciones(String texto1, Consumer<T> accion1, String texto2, Consumer<T> accion2,
                                        String texto3, Consumer<T> accion3) {
        TableColumn<T, Void> col = new TableColumn<>(Idioma.t("comun.acciones"));
        col.setPrefWidth(ANCHO_ACCIONES[3]);
        col.setMinWidth(ANCHO_ACCIONES[3]);
        col.setSortable(false);
        col.setCellFactory(c -> new TableCell<>() {
            private final Button b1 = boton(texto1, "btn-primario");
            private final Button b2 = boton(texto2, "btn-secundario");
            private final Button b3 = boton(texto3, "btn-peligro");
            private final HBox botones = new HBox(6, b1, b2, b3);
            { botones.setAlignment(javafx.geometry.Pos.CENTER_LEFT); }

            {
                b1.setOnAction(e -> accion1.accept(getTableRow().getItem()));
                b2.setOnAction(e -> accion2.accept(getTableRow().getItem()));
                b3.setOnAction(e -> accion3.accept(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean vacia) {
                super.updateItem(item, vacia);
                setGraphic(vacia || getTableRow().getItem() == null ? null : botones);
            }
        });
        tabla.getColumns().add(col);   // Acciones al final de la tabla
        return this;
    }

    /** Igual, pero con otros textos en los botones (por ejemplo "Ver" y "Anular"). */
    public TablaConAcciones<T> acciones(String textoEditar, Consumer<T> alEditar,
                                        String textoEliminar, Consumer<T> alEliminar) {
        TableColumn<T, Void> col = new TableColumn<>(Idioma.t("comun.acciones"));
        col.setPrefWidth(ANCHO_ACCIONES[alEliminar == null ? 1 : 2]);
        col.setMinWidth(col.getPrefWidth()); // los botones nunca se recortan
        col.setSortable(false);
        col.setCellFactory(c -> new TableCell<>() {
            private final Button btnEditar = boton(textoEditar, "btn-primario");
            private final Button btnEliminar = boton(textoEliminar, "btn-peligro");
            private final HBox botones = new HBox(6, btnEditar);
            { botones.setAlignment(javafx.geometry.Pos.CENTER_LEFT); }

            {
                btnEditar.setOnAction(e -> alEditar.accept(getTableRow().getItem()));
                if (alEliminar != null) {
                    btnEliminar.setOnAction(e -> alEliminar.accept(getTableRow().getItem()));
                    botones.getChildren().add(btnEliminar);
                }
            }

            @Override
            protected void updateItem(Void item, boolean vacia) {
                super.updateItem(item, vacia);
                setGraphic(vacia || getTableRow().getItem() == null ? null : botones);
            }
        });
        tabla.getColumns().add(col);   // Acciones al final de la tabla
        return this;
    }
}
```

### 12.2 `Sugerencias` y `CampoBuscable<T>` — escribir con sugerencias

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/componentes/Sugerencias.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.componentes;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.function.Function;

/**
 * Autocompletar para un TextField: mientras escribes, abajo salen sugerencias.
 * Son solo ayuda: puedes escribir cualquier cosa (por ejemplo una raza nueva).
 *
 *   Sugerencias.en(txtRaza, texto -> nombresDeRazas(texto));
 */
public final class Sugerencias {

    private Sugerencias() {
    }

    /** buscar recibe lo escrito (en minúsculas) y devuelve los textos a sugerir. */
    public static void en(TextField campo, Function<String, List<String>> buscar) {
        ContextMenu menu = new ContextMenu();
        boolean[] eligiendo = {false};
        campo.textProperty().addListener((obs, antes, ahora) -> {
            if (eligiendo[0]) {
                return;
            }
            String t = ahora == null ? "" : ahora.trim().toLowerCase();
            menu.getItems().clear();
            if (!t.isEmpty()) {
                for (String texto : buscar.apply(t)) {
                    if (texto.equalsIgnoreCase(ahora.trim())) {
                        continue;   // ya está escrito completo
                    }
                    MenuItem item = new MenuItem(texto);
                    item.setOnAction(e -> {
                        eligiendo[0] = true;
                        campo.setText(texto);
                        campo.positionCaret(texto.length());
                        eligiendo[0] = false;
                    });
                    menu.getItems().add(item);
                }
            }
            if (menu.getItems().isEmpty() || !campo.isFocused()) {
                menu.hide();
            } else if (!menu.isShowing()) {
                menu.show(campo, Side.BOTTOM, 0, 0);
            }
        });
        campo.focusedProperty().addListener((obs, antes, enfocado) -> {
            if (!enfocado) {
                menu.hide();
            }
        });
    }

    /** Pone un texto sin abrir las sugerencias (al llenar el formulario para editar). */
    public static void ponerTexto(TextField campo, String texto) {
        campo.setText(texto == null ? "" : texto);
    }
}
```

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/componentes/CampoBuscable.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.componentes;

import pe.edu.upeu.padronmascota.padronmascota.modelo.Entidad;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Un TextField para ESCRIBIR algo que ya existe (mascota, veterinario, producto...),
 * con sugerencias mientras escribes. Reemplaza a los ComboBox de "Elegir".
 *
 * La lista se pide a la base CADA VEZ que escribes (fuente.get()), así nunca muestra
 * datos viejos o borrados. Tiene getValue / setValue / valueProperty como un ComboBox.
 *
 *   cboMascota = new CampoBuscable<>(txtMascota, () -> servicio.listarTodos(), m -> m.getNombre());
 */
public class CampoBuscable<T> {

    private final TextField campo;
    private final Supplier<List<T>> fuente;
    private final Function<T, String> etiqueta;
    private final ObjectProperty<T> valor = new SimpleObjectProperty<>();
    private boolean poniendo;

    public CampoBuscable(TextField campo, Supplier<List<T>> fuente, Function<T, String> etiqueta) {
        this.campo = campo;
        this.fuente = fuente;
        this.etiqueta = etiqueta;
        Sugerencias.en(campo, t -> fuente.get().stream()
                .map(etiqueta).filter(e -> e.toLowerCase().contains(t)).toList());
        // Cada vez que cambia lo escrito, se busca a quién corresponde (o null si a nadie)
        campo.textProperty().addListener((obs, antes, ahora) -> {
            if (!poniendo) {
                valor.set(buscar(ahora));
            }
        });
    }

    /**
     * Lo escrito corresponde a: el que tiene exactamente esa etiqueta; si no, el ÚNICO
     * cuya etiqueta empieza con lo escrito (ej. "Kiwi" encuentra "Kiwi - Rosa Flores").
     */
    private T buscar(String texto) {
        String t = texto == null ? "" : texto.trim().toLowerCase();
        if (t.isEmpty()) {
            return null;
        }
        List<T> todos = fuente.get();
        for (T x : todos) {
            if (etiqueta.apply(x).equalsIgnoreCase(t)) {
                return x;
            }
        }
        List<T> empiezan = todos.stream().filter(x -> {
            String e = etiqueta.apply(x).toLowerCase();
            return e.startsWith(t + " ") || e.startsWith(t + " -");
        }).toList();
        return empiezan.size() == 1 ? empiezan.get(0) : null;
    }

    public T getValue() {
        return valor.get();
    }

    /** Pone el objeto y su texto (sin abrir sugerencias). null deja el campo vacío. */
    public void setValue(T obj) {
        poniendo = true;
        Sugerencias.ponerTexto(campo, obj == null ? "" : etiqueta.apply(obj));
        poniendo = false;
        valor.set(obj);
    }

    /** Busca en la base el que tiene ese id (los objetos de la tabla no son los mismos). */
    public void elegirPorId(T obj) {
        setValue(obj == null ? null
                : fuente.get().stream().filter(x -> mismoId(x, obj)).findFirst().orElse(null));
    }

    private static boolean mismoId(Object a, Object b) {
        return a instanceof Entidad x && b instanceof Entidad y ? x.getId() == y.getId() : a.equals(b);
    }

    public ObjectProperty<T> valueProperty() {
        return valor;
    }

    public TextField getCampo() {
        return campo;
    }
}
```

### 12.3 `MarcadorError` — campos en rojo

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/componentes/MarcadorError.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.componentes;

import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Pinta de rojo los campos con error y les pone un tooltip con el mensaje.
 * Recuerda qué campos marcó para poder limpiarlos todos de una vez.
 */
public class MarcadorError {

    private final List<Control> marcados = new ArrayList<>();
    private final List<String> mensajes = new ArrayList<>();

    public void marcar(Control campo, String mensaje) {
        if (!campo.getStyleClass().contains("campo-error")) {
            campo.getStyleClass().add("campo-error");
        }
        Tooltip tip = new Tooltip(mensaje);
        tip.getStyleClass().add("tooltip-error");
        tip.setShowDelay(Duration.millis(100));
        campo.setTooltip(tip);
        marcados.add(campo);
        mensajes.add(mensaje);
    }

    /** El primer mensaje de error (para mostrarlo como aviso, no solo al pasar el mouse). */
    public String primerMensaje() {
        return mensajes.isEmpty() ? null : mensajes.get(0);
    }

    public void limpiar() {
        for (Control c : marcados) {
            c.getStyleClass().remove("campo-error");
            c.setTooltip(null);
        }
        marcados.clear();
        mensajes.clear();
    }

    public boolean hayErrores() {
        return !marcados.isEmpty();
    }

    /** Pone el cursor en el primer campo con error. */
    public void enfocarPrimero() {
        if (!marcados.isEmpty()) {
            marcados.get(0).requestFocus();
        }
    }
}
```

### 12.4 `Notificacion` — aviso flotante

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/componentes/Notificacion.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.componentes;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * Mensaje pequeño que aparece abajo de la ventana y se desvanece solo
 * (no hay que darle "Aceptar" como a un Alert).
 */
public class Notificacion {

    public enum Tipo { EXITO, ERROR, INFO }

    private Notificacion() {
    }

    public static void mostrar(Window ventana, String mensaje, Tipo tipo) {
        Label label = new Label(mensaje);
        label.getStyleClass().addAll("notificacion", "notificacion-" + tipo.name().toLowerCase());

        StackPane caja = new StackPane(label);
        caja.setPadding(new Insets(4));

        Popup popup = new Popup();
        popup.getContent().add(caja);
        popup.setAutoHide(true);
        popup.show(ventana);
        // Centrado horizontal, cerca del borde de abajo
        popup.setX(ventana.getX() + (ventana.getWidth() - caja.getWidth()) / 2);
        popup.setY(ventana.getY() + ventana.getHeight() - caja.getHeight() - 60);

        caja.setOpacity(0);
        FadeTransition entrar = new FadeTransition(Duration.millis(250), caja);
        entrar.setToValue(1);
        FadeTransition salir = new FadeTransition(Duration.millis(400), caja);
        salir.setToValue(0);
        SequentialTransition animacion = new SequentialTransition(entrar, new PauseTransition(Duration.seconds(2)), salir);
        animacion.setOnFinished(e -> popup.hide());
        animacion.play();
    }
}
```

### 12.5 `Dialogo` — confirmaciones y errores

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/componentes/Dialogo.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.componentes;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/** Ventanas de confirmación y error. */
public class Dialogo {

    private Dialogo() {
    }

    /** El diálogo usa los mismos colores (tema) que la ventana principal. */
    private static void estilo(Alert a) {
        javafx.stage.Stage principal = GestorVentana.getStage();
        if (principal != null && principal.getScene() != null) {
            a.initOwner(principal);
            a.getDialogPane().getStylesheets().setAll(principal.getScene().getStylesheets());
        }
    }

    public static boolean confirmar(String mensaje) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        a.setTitle(Idioma.t("dialogo.confirmar"));
        estilo(a);
        return a.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    public static void error(String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR, mensaje);
        a.setHeaderText(null);
        a.setTitle(Idioma.t("dialogo.error"));
        estilo(a);
        a.showAndWait();
    }

    /** Ventana con texto en letra de ancho fijo (para que las columnas del detalle queden alineadas). */
    public static void detalle(String titulo, String texto) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        javafx.scene.control.Label contenido = new javafx.scene.control.Label(texto);
        contenido.setStyle("-fx-font-family: monospace;");
        a.getDialogPane().setContent(contenido);
        a.setResizable(true);
        estilo(a);
        a.showAndWait();
    }

    /** Ventana con información (por ejemplo, el detalle de una venta o una receta). */
    public static void info(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, mensaje);
        a.setHeaderText(titulo);
        a.setTitle(titulo);
        a.getDialogPane().setMinWidth(460);
        a.setResizable(true);
        estilo(a);
        a.showAndWait();
    }
}
```

### 12.6 `Combos`, `Formato` y `Refrescable`

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/componentes/Combos.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.componentes;

import pe.edu.upeu.padronmascota.padronmascota.dto.OpcionCombo;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

import java.util.Collection;
import java.util.function.Function;

/** Ayudas para llenar ComboBox sin repetir código en cada pantalla. */
public final class Combos {

    private Combos() {
    }

    /** Llena el combo con la lista (se ve el toString de cada objeto). */
    public static <T> void llenar(ComboBox<T> combo, Collection<T> items) {
        combo.setItems(FXCollections.observableArrayList(items));
    }

    /** Combo de filtro: primero "Todos" (valor null) y luego cada item. Deja elegido "Todos". */
    public static <T> void filtro(ComboBox<OpcionCombo<T>> combo, String textoTodos, Collection<T> items,
                                  Function<T, String> texto) {
        combo.getItems().clear();
        combo.getItems().add(new OpcionCombo<>(null, textoTodos));
        for (T item : items) {
            combo.getItems().add(new OpcionCombo<>(item, texto.apply(item)));
        }
        combo.getSelectionModel().selectFirst();
    }

    /** Valor elegido en un combo de filtro (null = Todos). */
    public static <T> T valorFiltro(ComboBox<OpcionCombo<T>> combo) {
        OpcionCombo<T> op = combo.getValue();
        return op == null ? null : op.getValor();
    }

    /** Elige en el combo el item con ese id (los objetos de la tabla no son los mismos del combo). */
    public static <T extends pe.edu.upeu.padronmascota.padronmascota.modelo.Entidad> void elegirPorId(ComboBox<T> combo, T obj) {
        combo.setValue(obj == null ? null
                : combo.getItems().stream().filter(x -> x.getId() == obj.getId()).findFirst().orElse(null));
    }
}
```

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/componentes/Formato.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.componentes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Cómo se muestran y se leen el dinero y las fechas en las pantallas. */
public final class Formato {

    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private Formato() {
    }

    /** 45 -> "S/ 45.00" */
    public static String dinero(BigDecimal monto) {
        return monto == null ? "" : "S/ " + monto.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public static String fechaHora(LocalDateTime fecha) {
        return fecha == null ? "" : fecha.format(FECHA_HORA);
    }

    public static String fecha(java.time.LocalDate fecha) {
        return fecha == null ? "" : fecha.format(FECHA);
    }

    public static String hora(java.time.LocalTime hora) {
        return hora == null ? "" : hora.format(HORA);
    }

    /** "9:30" o "09:30" -> 09:30. Vacío -> null. Lanza DateTimeParseException si no es una hora. */
    public static java.time.LocalTime leerHora(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        String t = texto.trim();
        if (t.length() == 4 && t.charAt(1) == ':') {
            t = "0" + t;
        }
        return java.time.LocalTime.parse(t, HORA);
    }

    /** "12,50" o "12.50" -> 12.50. Vacío -> null. Lanza NumberFormatException si no es número. */
    public static BigDecimal leerDinero(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return new BigDecimal(texto.trim().replace(",", "."));
    }

    /** "5" -> 5. Vacío -> null. Lanza NumberFormatException si no es número entero. */
    public static Integer leerEntero(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return Integer.parseInt(texto.trim());
    }
}
```

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/componentes/Refrescable.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.componentes;

/**
 * Una pantalla que se vuelve a cargar cuando vuelves a su pestaña.
 * Ej.: registras una mascota y al volver a Propietarios su columna "Mascotas" ya sale actualizada.
 */
public interface Refrescable {
    void refrescar();
}
```

### 12.7 `Idioma` y `Tema`

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/componentes/Idioma.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.componentes;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Textos de la aplicación en varios idiomas (internacionalización, "i18n").
 *
 * <p>Los textos están FUERA del código, en la carpeta {@code language/} del proyecto:
 * <ul>
 *   <li>{@code idiomas-es.properties}, {@code idiomas-en.properties}, {@code idiomas-fr.properties}:
 *       una línea {@code clave=texto} por cada texto.</li>
 *   <li>{@code PadronMascota.properties}: guarda el idioma elegido ({@code padronmascota.idioma=es}).</li>
 * </ul>
 * En el FXML se usa {@code text="%clave"} y en Java {@code Idioma.t("clave")}.
 */
public final class Idioma {

    private static final Path CARPETA = pe.edu.upeu.padronmascota.padronmascota.config.CarpetaApp.base().resolve("language");
    private static final Path CONFIG = CARPETA.resolve("PadronMascota.properties");
    private static final String CLAVE_IDIOMA = "padronmascota.idioma";

    /** Idiomas disponibles: código y nombre que se muestra. */
    public static final String[][] DISPONIBLES = {
            {"es", "Español"},
            {"en", "English"},
            {"fr", "Français"}
    };

    private static String actual;
    private static ResourceBundle textos;

    private Idioma() {
    }

    /** Código del idioma actual: es, en o fr. */
    public static String actual() {
        if (actual == null) {
            actual = leerConfig().getProperty(CLAVE_IDIOMA, "es");
        }
        return actual;
    }

    /** Los textos del idioma actual (el FXMLLoader los usa para los %clave). */
    public static ResourceBundle textos() {
        if (textos == null) {
            textos = cargar(actual());
        }
        return textos;
    }

    /** Texto traducido. Si la clave no existe, devuelve la clave para que se note. */
    public static String t(String clave) {
        try {
            return textos().getString(clave);
        } catch (MissingResourceException e) {
            return clave;
        }
    }

    /** Texto con datos: t("mascota.total", 5) con "mascota.total=%d mascota(s)". */
    public static String t(String clave, Object... datos) {
        return String.format(t(clave), datos);
    }

    /** Cambia el idioma y lo guarda para la próxima vez que se abra el sistema. */
    public static void cambiar(String codigo) {
        actual = codigo;
        textos = cargar(codigo);
        Properties config = leerConfig();
        config.setProperty(CLAVE_IDIOMA, codigo);
        try (Writer w = Files.newBufferedWriter(CONFIG, StandardCharsets.UTF_8)) {
            config.store(w, "Idioma del sistema Padron Mascota (es, en, fr)");
        } catch (IOException e) {
            System.out.println("No se pudo guardar el idioma: " + e.getMessage());
        }
    }

    private static ResourceBundle cargar(String codigo) {
        Path archivo = CARPETA.resolve("idiomas-" + codigo + ".properties");
        if (!Files.exists(archivo)) {
            throw new IllegalStateException("No se encontró " + archivo.toAbsolutePath()
                    + ". La carpeta language debe estar en la raíz del proyecto.");
        }
        try (Reader r = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            return new PropertyResourceBundle(r);
        } catch (IOException e) {
            throw new IllegalStateException("Error leyendo " + archivo, e);
        }
    }

    private static Properties leerConfig() {
        Properties p = new Properties();
        if (Files.exists(CONFIG)) {
            try (Reader r = Files.newBufferedReader(CONFIG, StandardCharsets.UTF_8)) {
                p.load(r);
            } catch (IOException e) {
                System.out.println("No se pudo leer " + CONFIG + ": " + e.getMessage());
            }
        }
        return p;
    }
}
```

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/componentes/Tema.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.componentes;

/**
 * Los temas del sistema (ENUM con datos): clave de idioma, archivo CSS y un color
 * para dibujar su circulito en el login.
 */
public enum Tema {
    CLARO("tema.claro", null, "#0e8a83"),
    OSCURO("tema.oscuro", "/css/tema-oscuro.css", "#1d2626"),
    AZUL("tema.azul", "/css/tema-azul.css", "#2563a8"),
    VERDE("tema.verde", "/css/tema-verde.css", "#2e8b57");

    private final String clave;
    private final String css;
    private final String color;

    Tema(String clave, String css, String color) {
        this.clave = clave;
        this.css = css;
        this.color = color;
    }

    public String getCss() {
        return css;
    }

    public String getColor() {
        return color;
    }

    /** Nombre en el idioma actual. */
    public String nombre() {
        return Idioma.t(clave);
    }

    /** El tema que está puesto ahora. */
    public static Tema actual() {
        String css = GestorVentana.getTemaActual();
        for (Tema t : values()) {
            if (java.util.Objects.equals(t.css, css)) {
                return t;
            }
        }
        return CLARO;
    }

    public void aplicar() {
        GestorVentana.cambiarTema(css);
    }
}
```

### 12.8 `GestorVentana` — carga los FXML y cambia el tema

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/componentes/GestorVentana.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.componentes;

import pe.edu.upeu.padronmascota.padronmascota.PadronMascotaApp;
import pe.edu.upeu.padronmascota.padronmascota.config.ContenedorApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Maneja la ventana (Stage): carga los FXML con el ContenedorApp y los textos
 * del idioma actual, y recuerda el tema (CSS) elegido.
 */
public class GestorVentana {

    private static final String BASE_CSS = "/css/base.css";

    private static Stage stage;
    private static String temaActual;   // null = tema claro

    private GestorVentana() {
    }

    public static void setStage(Stage s) {
        stage = s;
    }

    public static Stage getStage() {
        return stage;
    }

    public static String getTemaActual() {
        return temaActual;
    }

    /** Carga un FXML de la carpeta vistas. El ContenedorApp crea su controlador. */
    public static Parent cargar(String vista) {
        try {
            FXMLLoader loader = new FXMLLoader(PadronMascotaApp.class.getResource("vistas/" + vista));
            loader.setResources(Idioma.textos());   // para los text="%clave" del FXML
            loader.setControllerFactory(ContenedorApp.getInstancia()::crearControlador);
            Parent raiz = loader.load();
            // Guardamos el controlador en la vista para poder refrescarla al volver a su pestaña
            raiz.getProperties().put("controlador", loader.getController());
            return raiz;
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo abrir la vista " + vista, e);
        }
    }

    /** Muestra la ventana principal (grande). */
    public static void mostrar(String vista) {
        mostrar(vista, 1280, 760, true);
        stage.setMaximized(true); // la ventana principal ocupa toda la pantalla
    }

    /** Muestra una vista ocupando toda la ventana, con el tamaño indicado. */
    public static void mostrar(String vista, double ancho, double alto, boolean redimensionable) {
        Scene escena = new Scene(cargar(vista), ancho, alto);
        if (!redimensionable) {
            stage.setMaximized(false); // primero se quita el "maximizado" de la ventana principal
        }
        stage.setScene(escena);
        stage.setTitle(Idioma.t("app.titulo"));
        stage.setResizable(redimensionable);
        if (!redimensionable) {
            stage.sizeToScene();       // y la ventana vuelve al tamaño exacto del login (480 x 530)
            // Windows a veces aplica el "maximizado" un instante después: se revisa otra vez
            javafx.application.Platform.runLater(() -> {
                if (stage.isMaximized()) {
                    stage.setMaximized(false);
                }
                stage.sizeToScene();
                stage.centerOnScreen();
            });
        }
        stage.centerOnScreen();
        cambiarTema(temaActual);
    }

    /** temaCss = null deja solo el estilo base (claro). */
    public static void cambiarTema(String temaCss) {
        temaActual = temaCss;
        Scene escena = stage.getScene();
        escena.getStylesheets().setAll(GestorVentana.class.getResource(BASE_CSS).toExternalForm());
        if (temaCss != null) {
            escena.getStylesheets().add(GestorVentana.class.getResource(temaCss).toExternalForm());
        }
    }
}
```

`Impresion` (receta, carnet de vacunas y comprobante listos para imprimir o guardar en PDF) se describe en el Anexo A.

---

## 13. Paso 12 — `ContenedorApp`

Arma **toda** la aplicación en un solo lugar: crea una vez cada repositorio y servicio, y registra cómo crear cada controlador con sus servicios. `GestorVentana` se lo pasa al `FXMLLoader` con `setControllerFactory`.

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/config/ContenedorApp.java`

```java
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
```

### ¿Por qué hacerlo a mano si Spring lo hace solo?

Para **ver** cómo funciona la inyección de dependencias: quién crea los objetos, en qué orden y cómo llegan al controlador. Spring automatiza exactamente esto con anotaciones (`@Service`, `@Autowired`). Al hacerlo a mano no hay "magia" y cada dependencia está escrita.

---

## 14. Paso 13 — Paquete `controlador`

### 14.1 `CrudController<T>` — la pantalla CRUD genérica (Template Method)

Tiene el algoritmo de **buscar, nuevo, guardar, editar y eliminar**. Cada pantalla hija solo dice su servicio, sus columnas y cómo leer y llenar su formulario.

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/controlador/CrudController.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.Refrescable;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Dialogo;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.MarcadorError;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Notificacion;
import pe.edu.upeu.padronmascota.padronmascota.componentes.TablaConAcciones;
import pe.edu.upeu.padronmascota.padronmascota.excepcion.ValidacionException;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Entidad;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IServicioCrud;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Pantalla CRUD genérica: formulario a la izquierda, buscador y tabla a la derecha.
 * TEMPLATE METHOD + HERENCIA: aquí está todo lo que se repite (guardar, editar, eliminar,
 * marcar errores, avisos) y cada hija solo dice SUS campos y SUS columnas.
 *
 * <p>El FXML de la hija debe tener: txtBuscar, tabla, lblTotal y lblModo.
 * Los textos salen de language/ con el prefijo de la hija: veterinario.nuevo, veterinario.total...
 */
public abstract class CrudController<T extends Entidad> implements Refrescable {

    @FXML protected TextField txtBuscar;
    @FXML protected TableView<T> tabla;
    @FXML protected Label lblTotal;
    @FXML protected Label lblModo;

    protected final MarcadorError errores = new MarcadorError();
    protected T enEdicion;   // null = registrando uno nuevo

    // ---------- Lo que cada hija tiene que decir ----------

    protected abstract IServicioCrud<T> servicio();

    /** Prefijo de los textos en language/: "veterinario", "color"... */
    protected abstract String prefijo();

    protected abstract void columnas(TablaConAcciones<T> tabla);

    protected abstract List<T> buscarEnServicio(String texto);

    /** Arma el objeto con lo escrito. Si un campo no se puede leer, lo marca con errores.marcar(...). */
    protected abstract T leerFormulario();

    protected abstract void llenarFormulario(T obj);

    protected abstract void limpiarFormulario();

    /** nombre del atributo del modelo -> campo de la pantalla (para pintar los errores). */
    protected abstract Map<String, Control> campos();

    // ---------- Ganchos opcionales ----------

    /** Llenar combos, listeners... Se llama antes de armar la tabla. */
    protected void configurar() {
    }

    /** Botones de la tabla. Por defecto Editar y Eliminar. */
    protected void acciones(TablaConAcciones<T> t) {
        t.acciones(this::editar, this::eliminar);
    }

    protected String textoTotal(List<T> lista) {
        return Idioma.t(prefijo() + ".total", lista.size());
    }

    // ---------- El flujo común ----------

    @FXML
    protected void initialize() {
        configurar();
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, antes, ahora) -> buscar());
        }
        TablaConAcciones<T> t = new TablaConAcciones<>(tabla);
        columnas(t);
        acciones(t);
        nuevo();
        buscar();
    }

    @FXML
    protected void buscar() {
        try {
            List<T> lista = buscarEnServicio(txtBuscar == null ? null : txtBuscar.getText());
            tabla.setItems(FXCollections.observableArrayList(lista));
            lblTotal.setText(textoTotal(lista));
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.buscar", e.getMessage()));
        }
    }

    /** Al volver a la pestaña se recarga la tabla (los conteos pueden haber cambiado en otra pantalla). */
    @Override
    public void refrescar() {
        buscar();
    }

    @FXML
    protected void nuevo() {
        enEdicion = null;
        lblModo.setText(Idioma.t(prefijo() + ".nuevo"));
        errores.limpiar();
        limpiarFormulario();
    }

    @FXML
    protected void guardar() {
        errores.limpiar();
        T obj = leerFormulario();
        if (obj == null || errores.hayErrores()) {
            errores.enfocarPrimero();
            if (errores.primerMensaje() != null) {
                avisar(errores.primerMensaje(), Notificacion.Tipo.ERROR);
            }
            return;
        }
        try {
            if (enEdicion == null) {
                servicio().registrar(obj);
                avisar(Idioma.t(prefijo() + ".registrado"), Notificacion.Tipo.EXITO);
            } else {
                obj.setId(enEdicion.getId());
                servicio().modificar(obj.getId(), obj);
                avisar(Idioma.t(prefijo() + ".actualizado"), Notificacion.Tipo.EXITO);
            }
            nuevo();
            buscar();
        } catch (ValidacionException e) {
            mostrarErrores(e);
        } catch (IllegalStateException e) {
            Dialogo.error(e.getMessage());
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.guardar", mensajeAmigable(e)));
        }
    }

    protected void editar(T obj) {
        errores.limpiar();
        enEdicion = obj;
        lblModo.setText(Idioma.t("comun.editando", obj.toString()));
        llenarFormulario(obj);
    }

    protected void eliminar(T obj) {
        // Si la pantalla tiene su propio texto (ej. propietario.confirmarEliminar) se usa ese
        String clave = prefijo() + ".confirmarEliminar";
        String pregunta = Idioma.t(clave).equals(clave)
                ? Idioma.t("comun.confirmarEliminar", obj.toString())
                : Idioma.t(clave, obj.toString());
        if (!Dialogo.confirmar(pregunta)) {
            return;
        }
        try {
            servicio().eliminar(obj.getId());
            avisar(Idioma.t(prefijo() + ".eliminado"), Notificacion.Tipo.INFO);
            if (enEdicion != null && enEdicion.getId() == obj.getId()) {
                nuevo();
            }
            buscar();
        } catch (IllegalStateException e) {
            Dialogo.error(e.getMessage());
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.eliminar", mensajeAmigable(e)));
        }
    }

    // ---------- Ayudas para las hijas ----------

    /** Pinta en rojo cada campo con error y muestra el primer mensaje. */
    protected void mostrarErrores(ValidacionException e) {
        Map<String, Control> campos = campos();
        e.getErrores().forEach((campo, mensaje) -> {
            if (campos.containsKey(campo)) {
                errores.marcar(campos.get(campo), mensaje);
            }
        });
        errores.enfocarPrimero();
        avisar(e.getErrores().values().iterator().next(), Notificacion.Tipo.ERROR);
    }

    /** Lee un entero; si no es número, marca el campo y devuelve null. Vacío = null (lo valida el modelo). */
    protected Integer leerEntero(TextField campo) {
        String texto = campo.getText();
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException ex) {
            errores.marcar(campo, Idioma.t("comun.enteroInvalido"));
            return null;
        }
    }

    /** Lee dinero ("12.50" o "12,50"); si no es número, marca el campo y devuelve null. */
    protected BigDecimal leerDinero(TextField campo) {
        String texto = campo.getText();
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(texto.trim().replace(",", "."));
        } catch (NumberFormatException ex) {
            errores.marcar(campo, Idioma.t("comun.numeroInvalido"));
            return null;
        }
    }

    protected void avisar(String mensaje, Notificacion.Tipo tipo) {
        Notificacion.mostrar(tabla.getScene().getWindow(), mensaje, tipo);
    }

    /** Traduce los errores de H2 más comunes a algo que el usuario entienda. */
    static String mensajeAmigable(RuntimeException e) {
        String m = String.valueOf(e.getMessage());
        if (m.contains("Unique index") || m.contains("primary key violation")) {
            return Idioma.t("error.duplicado");
        }
        if (m.contains("Referential integrity")) {
            return Idioma.t("error.enUso");
        }
        if (m.contains("Check constraint")) {
            return Idioma.t("error.datoInvalido");
        }
        return m;
    }
}
```

### 14.2 `PropietarioController`

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/controlador/PropietarioController.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.TablaConAcciones;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Propietario;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IPropietarioServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IServicioCrud;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.Map;

/** Dueños de las mascotas: DNI (8 dígitos, único), nombre, teléfono, dirección y email. */
public class PropietarioController extends CrudController<Propietario> {

    @FXML private TextField txtDni;
    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtEmail;

    private final IPropietarioServicio propietarioServicio;

    public PropietarioController(IPropietarioServicio propietarioServicio) {
        this.propietarioServicio = propietarioServicio;
    }

    @Override
    protected IServicioCrud<Propietario> servicio() {
        return propietarioServicio;
    }

    @Override
    protected String prefijo() {
        return "propietario";
    }

    @Override
    protected void columnas(TablaConAcciones<Propietario> t) {
        t.columna(Idioma.t("comun.id"), Propietario::getId, 40)
                .columna(Idioma.t("propietario.dni"), Propietario::getDni, 80)
                .columna(Idioma.t("comun.nombre"), Propietario::getNombre, 170)
                .columna(Idioma.t("comun.telefono"), Propietario::getTelefono, 85)
                .columna(Idioma.t("propietario.direccion"), Propietario::getDireccion, 170)
                .columna(Idioma.t("propietario.email"), Propietario::getEmail, 170)
                .columna(Idioma.t("propietario.mascotas"), Propietario::getNumeroMascotas, 80);
    }

    @Override
    protected List<Propietario> buscarEnServicio(String texto) {
        return propietarioServicio.buscar(texto);
    }

    @Override
    protected Propietario leerFormulario() {
        Propietario p = new Propietario();
        p.setDni(txtDni.getText());
        p.setNombre(txtNombre.getText());
        p.setTelefono(txtTelefono.getText());
        p.setDireccion(txtDireccion.getText());
        p.setEmail(txtEmail.getText());
        return p;
    }

    @Override
    protected void llenarFormulario(Propietario p) {
        txtDni.setText(p.getDni());
        txtNombre.setText(p.getNombre());
        txtTelefono.setText(p.getTelefono());
        txtDireccion.setText(p.getDireccion());
        txtEmail.setText(p.getEmail());
        txtDni.requestFocus();
    }

    @Override
    protected void limpiarFormulario() {
        txtDni.clear();
        txtNombre.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        txtEmail.clear();
    }

    @Override
    protected Map<String, Control> campos() {
        return Map.of("dni", txtDni, "nombre", txtNombre, "telefono", txtTelefono,
                "direccion", txtDireccion, "email", txtEmail);
    }
}
```

### 14.3 `MascotaController`

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/controlador/MascotaController.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.Combos;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Dialogo;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Formato;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Notificacion;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Sugerencias;
import pe.edu.upeu.padronmascota.padronmascota.componentes.TablaConAcciones;
import pe.edu.upeu.padronmascota.padronmascota.dto.OpcionCombo;
import pe.edu.upeu.padronmascota.padronmascota.enums.Especie;
import pe.edu.upeu.padronmascota.padronmascota.enums.EstadoVacunacion;
import pe.edu.upeu.padronmascota.padronmascota.enums.Sexo;
import pe.edu.upeu.padronmascota.padronmascota.excepcion.ValidacionException;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Color;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Mascota;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Propietario;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Raza;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IColorServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IMascotaServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IPropietarioServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IRazaServicio;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IServicioCrud;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Padrón de mascotas. Raza y color se ESCRIBEN (con sugerencias); si son nuevos se agregan solos a su catálogo.
 * "Eliminar" es un borrado lógico: la mascota queda inactiva (activo = No).
 */
public class MascotaController extends CrudController<Mascota> {

    @FXML private TextField txtNombre;
    @FXML private ComboBox<Especie> cboEspecie;
    @FXML private TextField txtRaza;
    @FXML private TextField txtColor;
    @FXML private ComboBox<Sexo> cboSexo;
    @FXML private TextField txtEdad;
    @FXML private DatePicker dtpFechaRegistro;
    @FXML private TextField txtPropietario;   // se escribe nombre o DNI, con sugerencias
    @FXML private javafx.scene.control.Label lblDniProp;
    @FXML private TextField txtDniProp;       // solo para un propietario nuevo
    @FXML private javafx.scene.control.Label lblTelProp;
    @FXML private TextField txtTelProp;       // solo para un propietario nuevo
    @FXML private ComboBox<EstadoVacunacion> cboEstado;
    @FXML private TextArea txtObservaciones;
    @FXML private ComboBox<OpcionCombo<Especie>> cboFiltroEspecie;
    @FXML private ComboBox<OpcionCombo<EstadoVacunacion>> cboFiltroEstado;
    @FXML private CheckBox chkInactivas;

    private final IMascotaServicio mascotaServicio;
    private final IPropietarioServicio propietarioServicio;
    private final IRazaServicio razaServicio;
    private final IColorServicio colorServicio;

    public MascotaController(IMascotaServicio mascotaServicio, IPropietarioServicio propietarioServicio,
                             IRazaServicio razaServicio, IColorServicio colorServicio) {
        this.mascotaServicio = mascotaServicio;
        this.propietarioServicio = propietarioServicio;
        this.razaServicio = razaServicio;
        this.colorServicio = colorServicio;
    }

    @Override
    protected void configurar() {
        Combos.llenar(cboEspecie, Arrays.asList(Especie.values()));
        Combos.llenar(cboSexo, Arrays.asList(Sexo.values()));
        Combos.llenar(cboEstado, Arrays.asList(EstadoVacunacion.values()));
        Combos.filtro(cboFiltroEspecie, Idioma.t("comun.todas"), Arrays.asList(Especie.values()), Especie::getNombre);
        Combos.filtro(cboFiltroEstado, Idioma.t("comun.todos"), Arrays.asList(EstadoVacunacion.values()), EstadoVacunacion::getNombre);
        // Raza: sugiere las razas de la especie elegida que contienen lo escrito
        Sugerencias.en(txtRaza, t -> cboEspecie.getValue() == null ? List.of()
                : razaServicio.buscar(t, cboEspecie.getValue()).stream().map(Raza::getNombre).toList());
        // Color: sugiere los colores que contienen lo escrito
        Sugerencias.en(txtColor, t -> colorServicio.buscar(t).stream().map(Color::getNombre).toList());
        // Propietario: sugiere por nombre o DNI ("María Quispe - 45879632")
        Sugerencias.en(txtPropietario, t -> propietarioServicio.listarTodos().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(t) || (p.getDni() != null && p.getDni().contains(t)))
                .map(MascotaController::etiqueta).toList());
        txtPropietario.textProperty().addListener((obs, antes, ahora) -> mostrarCamposPropietarioNuevo());
    }

    /** Busca la raza escrita en la especie; si no existe la registra. null si falta algo (y marca el error). */
    private Raza razaEscrita(Especie especie) {
        String nombre = txtRaza.getText() == null ? "" : txtRaza.getText().trim();
        if (nombre.isEmpty() || especie == null) {
            return null;   // la validación de Mascota avisa "Elige la raza" / "Elige la especie"
        }
        Raza r = razaServicio.deEspecie(especie).stream()
                .filter(x -> x.getNombre().equalsIgnoreCase(nombre)).findFirst().orElse(null);
        if (r != null) {
            return r;
        }
        Raza nueva = new Raza();
        nueva.setNombre(nombre);
        nueva.setEspecie(especie);
        try {
            return razaServicio.registrar(nueva);   // raza nueva: se agrega al catálogo
        } catch (ValidacionException e) {
            errores.marcar(txtRaza, e.getErrores().values().iterator().next());
        } catch (RuntimeException e) {
            errores.marcar(txtRaza, mensajeAmigable(e));
        }
        return null;
    }

    /** Igual que la raza: busca el color escrito y si no existe lo registra. */
    private Color colorEscrito() {
        String nombre = txtColor.getText() == null ? "" : txtColor.getText().trim();
        if (nombre.isEmpty()) {
            return null;
        }
        Color c = colorServicio.listarTodos().stream()
                .filter(x -> x.getNombre().equalsIgnoreCase(nombre)).findFirst().orElse(null);
        if (c != null) {
            return c;
        }
        Color nuevo = new Color();
        nuevo.setNombre(nombre);
        try {
            return colorServicio.registrar(nuevo);
        } catch (ValidacionException e) {
            errores.marcar(txtColor, e.getErrores().values().iterator().next());
        } catch (RuntimeException e) {
            errores.marcar(txtColor, mensajeAmigable(e));
        }
        return null;
    }

    /** Cómo se ve un propietario en el campo y en las sugerencias. */
    private static String etiqueta(Propietario p) {
        return p.getDni() == null ? p.getNombre() : p.getNombre() + " - " + p.getDni();
    }

    /** Busca el propietario escrito: por "nombre - dni", por DNI o por nombre exacto. null si es nuevo. */
    private Propietario propietarioPorTexto(String texto) {
        String t = texto.trim();
        List<Propietario> todos = propietarioServicio.listarTodos();
        return todos.stream().filter(p -> etiqueta(p).equalsIgnoreCase(t)).findFirst()
                .or(() -> todos.stream().filter(p -> t.equals(p.getDni())).findFirst())
                .or(() -> todos.stream().filter(p -> p.getNombre().equalsIgnoreCase(t)).findFirst())
                .orElse(null);
    }

    /** Si el propietario escrito no existe, aparecen DNI y Teléfono para registrarlo. */
    private void mostrarCamposPropietarioNuevo() {
        String t = txtPropietario.getText() == null ? "" : txtPropietario.getText().trim();
        boolean nuevo = !t.isEmpty() && propietarioPorTexto(t) == null;
        for (javafx.scene.Node n : List.of(lblDniProp, txtDniProp, lblTelProp, txtTelProp)) {
            n.setVisible(nuevo);
            n.setManaged(nuevo);
        }
    }

    /** Devuelve el propietario escrito; si es nuevo lo registra con el DNI y teléfono escritos. */
    private Propietario propietarioEscrito() {
        String t = txtPropietario.getText() == null ? "" : txtPropietario.getText().trim();
        if (t.isEmpty()) {
            return null;   // la validación de Mascota avisa "Elige el propietario"
        }
        Propietario p = propietarioPorTexto(t);
        if (p != null) {
            return p;
        }
        Propietario nuevo = new Propietario();
        nuevo.setNombre(t);
        nuevo.setDni(txtDniProp.getText() == null ? "" : txtDniProp.getText().trim());
        nuevo.setTelefono(txtTelProp.getText() == null ? "" : txtTelProp.getText().trim());
        try {
            return propietarioServicio.registrar(nuevo);
        } catch (ValidacionException e) {
            e.getErrores().forEach((campo, msg) -> errores.marcar(
                    "dni".equals(campo) ? txtDniProp : "telefono".equals(campo) ? txtTelProp : txtPropietario, msg));
        } catch (RuntimeException e) {
            errores.marcar(txtPropietario, mensajeAmigable(e));
        }
        return null;
    }

    @Override
    protected IServicioCrud<Mascota> servicio() {
        return mascotaServicio;
    }

    @Override
    protected String prefijo() {
        return "mascota";
    }

    @Override
    protected void columnas(TablaConAcciones<Mascota> t) {
        t.columna(Idioma.t("comun.id"), Mascota::getId, 40)
                .columna(Idioma.t("comun.nombre"), Mascota::getNombre, 90)
                .columna(Idioma.t("mascota.especie"), Mascota::getEspecie, 65)
                .columna(Idioma.t("mascota.raza"), Mascota::getRaza, 100)
                .columna(Idioma.t("mascota.color"), Mascota::getColor, 95)
                .columna(Idioma.t("mascota.sexo"), Mascota::getSexo, 65)
                .columna(Idioma.t("mascota.edadCorta"), Mascota::getEdadAprox, 45)
                .columna(Idioma.t("mascota.registro"), m -> Formato.fecha(m.getFechaRegistro()), 100)
                .columna(Idioma.t("mascota.propietario"), m -> m.getPropietario().getNombre(), 150)
                .columna(Idioma.t("mascota.vacunacion"), Mascota::getEstado, 80)
                .columna(Idioma.t("mascota.activo"), m -> m.isActivo() ? Idioma.t("comun.si") : Idioma.t("comun.no"), 50);
    }

    /** Las inactivas tienen el botón "Reactivar" en vez de "Dar de baja". */
    @Override
    protected void acciones(TablaConAcciones<Mascota> t) {
        t.acciones(Idioma.t("comun.editar"), this::editar, Idioma.t("mascota.baja"), this::bajaOReactivar,
                Idioma.t("comun.eliminar"), this::borrarDefinitivo);
    }

    @Override
    protected List<Mascota> buscarEnServicio(String texto) {
        boolean soloActivas = chkInactivas == null || !chkInactivas.isSelected();
        return mascotaServicio.buscar(texto, Combos.valorFiltro(cboFiltroEspecie), Combos.valorFiltro(cboFiltroEstado), soloActivas);
    }

    private void bajaOReactivar(Mascota m) {
        if (m.isActivo()) {
            eliminar(m);   // CrudController pide confirmación y el servicio la marca inactiva
            return;
        }
        try {
            mascotaServicio.reactivar(m.getId());
            avisar(Idioma.t("mascota.reactivada"), Notificacion.Tipo.EXITO);
            buscar();
        } catch (RuntimeException e) {
            Dialogo.error(e.getMessage());
        }
    }

    /** Borra la mascota de verdad, después de avisar qué se borra con ella. */
    private void borrarDefinitivo(Mascota m) {
        int[] h = mascotaServicio.historialQueSeBorra(m.getId());
        if (!Dialogo.confirmar(Idioma.t("mascota.confirmarBorrar", m.getNombre(), h[0], h[1], h[2], h[3], h[4]))) {
            return;
        }
        try {
            mascotaServicio.eliminarDefinitivo(m.getId());
            if (enEdicion != null && enEdicion.getId() == m.getId()) {
                nuevo();
            }
            avisar(Idioma.t("mascota.borrada", m.getNombre()), Notificacion.Tipo.INFO);
            buscar();
        } catch (IllegalStateException e) {
            Dialogo.error(e.getMessage());
        } catch (RuntimeException e) {
            Dialogo.error(Idioma.t("error.eliminar", mensajeAmigable(e)));
        }
    }

    @Override
    protected Mascota leerFormulario() {
        Mascota m = new Mascota();
        m.setNombre(txtNombre.getText());
        m.setEspecie(cboEspecie.getValue());
        m.setRaza(razaEscrita(cboEspecie.getValue()));
        m.setColor(colorEscrito());
        m.setSexo(cboSexo.getValue());
        m.setEdadAprox(leerEntero(txtEdad));
        m.setFechaRegistro(dtpFechaRegistro.getValue());
        m.setPropietario(propietarioEscrito());
        m.setEstado(cboEstado.getValue());
        m.setObservaciones(txtObservaciones.getText());
        m.setActivo(enEdicion == null || enEdicion.isActivo());
        return m;
    }

    @Override
    protected void llenarFormulario(Mascota m) {
        txtNombre.setText(m.getNombre());
        cboEspecie.setValue(m.getEspecie());
        Sugerencias.ponerTexto(txtRaza, m.getRaza() == null ? "" : m.getRaza().getNombre());
        Sugerencias.ponerTexto(txtColor, m.getColor() == null ? "" : m.getColor().getNombre());
        cboSexo.setValue(m.getSexo());
        txtEdad.setText(String.valueOf(m.getEdadAprox()));
        dtpFechaRegistro.setValue(m.getFechaRegistro());
        Propietario dueno = m.getPropietario() == null ? null : propietarioServicio.obtener(m.getPropietario().getId());
        Sugerencias.ponerTexto(txtPropietario, dueno == null ? "" : etiqueta(dueno));
        cboEstado.setValue(m.getEstado());
        txtObservaciones.setText(m.getObservaciones());
        txtNombre.requestFocus();
    }

    @Override
    protected void limpiarFormulario() {
        txtNombre.clear();
        cboEspecie.setValue(null);
        txtRaza.clear();
        txtColor.clear();
        cboSexo.setValue(null);
        txtEdad.clear();
        dtpFechaRegistro.setValue(LocalDate.now());
        txtPropietario.clear();
        txtDniProp.clear();
        txtTelProp.clear();
        cboEstado.setValue(null);
        txtObservaciones.clear();
    }

    @Override
    protected Map<String, Control> campos() {
        return Map.ofEntries(
                Map.entry("nombre", txtNombre), Map.entry("especie", cboEspecie), Map.entry("raza", txtRaza),
                Map.entry("razaDeLaEspecie", txtRaza), Map.entry("color", txtColor), Map.entry("sexo", cboSexo),
                Map.entry("edadAprox", txtEdad), Map.entry("fechaRegistro", dtpFechaRegistro),
                Map.entry("propietario", txtPropietario), Map.entry("estado", cboEstado),
                Map.entry("observaciones", txtObservaciones));
    }
}
```

### 14.4 `LoginController`

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/controlador/LoginController.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.GestorVentana;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Tema;
import pe.edu.upeu.padronmascota.padronmascota.modelo.Usuario;
import pe.edu.upeu.padronmascota.padronmascota.seguridad.Sesion;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IUsuarioServicio;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Optional;

/**
 * Pantalla de inicio de sesión. Arriba tiene un circulito por cada tema
 * para cambiar los colores, y un combo para elegir el idioma.
 */
public class LoginController {

    @FXML private HBox cajaTemas;
    @FXML private ComboBox<String> cboIdioma;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtClave;
    @FXML private Label lblMensaje;

    private final IUsuarioServicio usuarioServicio;

    public LoginController(IUsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    /** Abre la pantalla de login (al iniciar y al cerrar sesión). */
    public static void mostrarLogin() {
        GestorVentana.mostrar("login.fxml", 480, 530, false);
    }

    @FXML
    private void initialize() {
        dibujarTemas();
        for (String[] idioma : Idioma.DISPONIBLES) {
            cboIdioma.getItems().add(idioma[1]);
            if (idioma[0].equals(Idioma.actual())) {
                cboIdioma.getSelectionModel().select(idioma[1]);
            }
        }
        cboIdioma.setOnAction(e -> cambiarIdioma());
        lblMensaje.setText("");
        Platform.runLater(txtUsuario::requestFocus);
    }

    private void dibujarTemas() {
        for (Tema tema : Tema.values()) {
            Circle c = new Circle(12, Color.web(tema.getColor()));
            c.getStyleClass().add("circulo-tema");
            c.setCursor(Cursor.HAND);
            Tooltip.install(c, new Tooltip(tema.nombre()));
            c.setOnMouseClicked(e -> tema.aplicar());
            cajaTemas.getChildren().add(c);
        }
    }

    private void cambiarIdioma() {
        int i = cboIdioma.getSelectionModel().getSelectedIndex();
        String codigo = Idioma.DISPONIBLES[i][0];
        if (!codigo.equals(Idioma.actual())) {
            Idioma.cambiar(codigo);
            mostrarLogin(); // recarga el login con los textos nuevos
        }
    }

    @FXML
    private void ingresar() {
        String usuario = txtUsuario.getText();
        String clave = txtClave.getText();
        if (usuario.isBlank() || clave.isEmpty()) {
            lblMensaje.setText(Idioma.t("login.vacio"));
            return;
        }
        Optional<Usuario> u;
        try {
            u = usuarioServicio.login(usuario, clave);
        } catch (RuntimeException e) {
            // Por ejemplo: la base está abierta en DBeaver o no se pudo crear
            e.printStackTrace();
            Throwable causa = e;
            while (causa.getCause() != null) {
                causa = causa.getCause();
            }
            lblMensaje.setText("Error de base de datos: " + causa.getMessage());
            return;
        }
        if (u.isEmpty()) {
            lblMensaje.setText(Idioma.t("login.error"));
            txtClave.clear();
            txtClave.requestFocus();
            return;
        }
        Sesion.iniciar(u.get());
        GestorVentana.mostrar("principal.fxml");
    }

    @FXML
    private void cerrar() {
        Platform.exit();
    }
}
```

### 14.5 `PrincipalController` — menú y pestañas

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/controlador/PrincipalController.java`

```java
package pe.edu.upeu.padronmascota.padronmascota.controlador;

import pe.edu.upeu.padronmascota.padronmascota.componentes.Dialogo;
import pe.edu.upeu.padronmascota.padronmascota.componentes.GestorVentana;
import pe.edu.upeu.padronmascota.padronmascota.config.DatabaseConfig;
import pe.edu.upeu.padronmascota.padronmascota.enums.Rol;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Idioma;
import pe.edu.upeu.padronmascota.padronmascota.componentes.Tema;
import pe.edu.upeu.padronmascota.padronmascota.dto.OpcionMenu;
import pe.edu.upeu.padronmascota.padronmascota.seguridad.Sesion;
import pe.edu.upeu.padronmascota.padronmascota.servicio.IMenuServicio;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleGroup;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ventana principal: el MenuBar se arma con las opciones del IMenuServicio
 * y cada pantalla se abre en una pestaña. También tiene los menús Tema, Idioma y Sistema.
 */
public class PrincipalController {

    @FXML private MenuBar menuBar;
    @FXML private TabPane tabPane;
    @FXML private Label lblEstado;

    private final IMenuServicio menuServicio;

    public PrincipalController(IMenuServicio menuServicio) {
        this.menuServicio = menuServicio;
    }

    @FXML
    private void initialize() {
        armarMenus();
        abrirPestana(Idioma.t("opcion.resumen"), "resumen.fxml"); // Inicio: lo primero que se ve
        String quien = Sesion.activa() ? Sesion.usuario().getNombre() : "-";
        lblEstado.setText(Idioma.t("estado.barra", quien));
    }

    private void armarMenus() {
        menuBar.getMenus().clear();

        // Agrupamos las opciones por menú, respetando el orden. Las claves se traducen aquí.
        Map<String, Menu> menus = new LinkedHashMap<>();
        for (OpcionMenu op : menuServicio.opciones()) {
            Menu menu = menus.computeIfAbsent(op.getMenu(), clave -> new Menu(Idioma.t(clave)));
            String texto = Idioma.t(op.getTexto());
            MenuItem item = new MenuItem(texto);
            item.setOnAction(e -> abrirPestana(texto, op.getVista()));
            menu.getItems().add(item);
        }
        menuBar.getMenus().addAll(menus.values());
        menuBar.getMenus().add(menuSistema()); // Tema e Idioma van dentro de Sistema
    }

    private Menu menuTemas() {
        Menu menu = new Menu(Idioma.t("menu.tema"));
        ToggleGroup grupo = new ToggleGroup();
        for (Tema tema : Tema.values()) {
            RadioMenuItem item = new RadioMenuItem(tema.nombre());
            item.setToggleGroup(grupo);
            item.setSelected(tema == Tema.actual());
            item.setOnAction(e -> tema.aplicar());
            menu.getItems().add(item);
        }
        return menu;
    }

    private Menu menuIdiomas() {
        Menu menu = new Menu(Idioma.t("menu.idioma"));
        ToggleGroup grupo = new ToggleGroup();
        for (String[] idioma : Idioma.DISPONIBLES) {
            RadioMenuItem item = new RadioMenuItem(idioma[1]);
            item.setToggleGroup(grupo);
            item.setSelected(idioma[0].equals(Idioma.actual()));
            item.setOnAction(e -> {
                Idioma.cambiar(idioma[0]);
                GestorVentana.mostrar("principal.fxml"); // se vuelve a cargar con los textos nuevos
            });
            menu.getItems().add(item);
        }
        return menu;
    }

    private Menu menuSistema() {
        Menu menu = new Menu(Idioma.t("menu.sistema"));
        MenuItem cerrarSesion = new MenuItem(Idioma.t("sistema.cerrarSesion"));
        cerrarSesion.setOnAction(e -> {
            Sesion.cerrar();
            LoginController.mostrarLogin();
        });
        MenuItem salir = new MenuItem(Idioma.t("sistema.salir"));
        salir.setOnAction(e -> Platform.exit());
        menu.getItems().addAll(menuTemas(), menuIdiomas(), new SeparatorMenuItem(), cerrarSesion);
        // Solo el Administrador puede vaciar la base
        if (Sesion.activa() && Sesion.usuario().getRol() == Rol.ADMIN) {
            MenuItem vaciar = new MenuItem(Idioma.t("sistema.vaciarBase"));
            vaciar.setOnAction(e -> vaciarBase());
            menu.getItems().addAll(new SeparatorMenuItem(), vaciar);
        }
        menu.getItems().addAll(new SeparatorMenuItem(), salir);
        return menu;
    }

    /** Sistema > Empezar de cero: borra todos los registros (pide confirmar dos veces). */
    private void vaciarBase() {
        if (!Dialogo.confirmar(Idioma.t("sistema.vaciarPregunta1"))
                || !Dialogo.confirmar(Idioma.t("sistema.vaciarPregunta2"))) {
            return;
        }
        try {
            DatabaseConfig.borrarTodo(Sesion.usuario().getId());
            Dialogo.info(Idioma.t("sistema.vaciarBase"), Idioma.t("sistema.vaciarListo", Sesion.usuario().getUsername()));
            Sesion.cerrar();
            LoginController.mostrarLogin();
        } catch (RuntimeException e) {
            Dialogo.error(e.getMessage());
        }
    }

    /** Si la pestaña ya está abierta la selecciona; si no, la crea. */
    private void abrirPestana(String titulo, String vista) {
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().equals(titulo)) {
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }
        javafx.scene.Parent contenido = GestorVentana.cargar(vista);
        Tab tab = new Tab(titulo, contenido);
        // Al volver a esta pestaña se recargan sus datos (si la pantalla es Refrescable)
        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected() && contenido.getProperties().get("controlador")
                    instanceof pe.edu.upeu.padronmascota.padronmascota.componentes.Refrescable r) {
                r.refrescar();
            }
        });
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }
}
```

### Puntos clave de los controladores

1. Reciben sus servicios **por constructor**, nunca hacen `new` de un servicio.
2. Dependen de **interfaces** (`IMascotaServicio`), no de clases concretas.
3. Los campos `@FXML` los llena el `FXMLLoader` por reflexión (por eso el `opens` en `module-info`).
4. Si el servicio lanza `ValidacionException`, el controlador pinta en rojo el campo con error (`MarcadorError`).

---

## 15. Paso 14 — Vistas FXML

Todas las pantallas tienen la **misma estructura**: encabezado con ícono, formulario a la izquierda (`panel-formulario`) y tabla a la derecha (`panel-tabla`). Los textos `%clave` salen del archivo de idioma.

### 15.1 `login.fxml`

`src/main/resources/pe/edu/upeu/padronmascota/padronmascota/vistas/login.fxml`

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.shape.SVGPath?>

<!--
  Login con fondo degradado + huellitas y una tarjeta al centro.
  Los colores salen del tema (-color-primario), así cambia con los circulitos.
  Los fx:id son los mismos de antes: el LoginController no cambia.
-->
<StackPane styleClass="login" xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1"
           fx:controller="pe.edu.upeu.padronmascota.padronmascota.controlador.LoginController">

   <!-- Capa 1: huellitas de adorno (no reciben clics) -->
   <Pane mouseTransparent="true">
      <SVGPath content="M12 12.5 c-3 0 -6.5 3.2 -6.5 6 c0 1.8 1.4 2.8 3.2 2.8 c1.3 0 2.2 -0.7 3.3 -0.7 c1.1 0 2 0.7 3.3 0.7 c1.8 0 3.2 -1 3.2 -2.8 c0 -2.8 -3.5 -6 -6.5 -6 z M2.5 10.5 a2.2 2.7 0 1 0 4.4 0 a2.2 2.7 0 1 0 -4.4 0 z M6.6 5.5 a2.3 2.9 0 1 0 4.6 0 a2.3 2.9 0 1 0 -4.6 0 z M12.8 5.5 a2.3 2.9 0 1 0 4.6 0 a2.3 2.9 0 1 0 -4.6 0 z M17.1 10.5 a2.2 2.7 0 1 0 4.4 0 a2.2 2.7 0 1 0 -4.4 0 z" styleClass="huella-fondo" layoutX="30" layoutY="90" rotate="-20" scaleX="2.2" scaleY="2.2" />
      <SVGPath content="M12 12.5 c-3 0 -6.5 3.2 -6.5 6 c0 1.8 1.4 2.8 3.2 2.8 c1.3 0 2.2 -0.7 3.3 -0.7 c1.1 0 2 0.7 3.3 0.7 c1.8 0 3.2 -1 3.2 -2.8 c0 -2.8 -3.5 -6 -6.5 -6 z M2.5 10.5 a2.2 2.7 0 1 0 4.4 0 a2.2 2.7 0 1 0 -4.4 0 z M6.6 5.5 a2.3 2.9 0 1 0 4.6 0 a2.3 2.9 0 1 0 -4.6 0 z M12.8 5.5 a2.3 2.9 0 1 0 4.6 0 a2.3 2.9 0 1 0 -4.6 0 z M17.1 10.5 a2.2 2.7 0 1 0 4.4 0 a2.2 2.7 0 1 0 -4.4 0 z" styleClass="huella-fondo" layoutX="400" layoutY="70" rotate="25" scaleX="1.6" scaleY="1.6" />
      <SVGPath content="M12 12.5 c-3 0 -6.5 3.2 -6.5 6 c0 1.8 1.4 2.8 3.2 2.8 c1.3 0 2.2 -0.7 3.3 -0.7 c1.1 0 2 0.7 3.3 0.7 c1.8 0 3.2 -1 3.2 -2.8 c0 -2.8 -3.5 -6 -6.5 -6 z M2.5 10.5 a2.2 2.7 0 1 0 4.4 0 a2.2 2.7 0 1 0 -4.4 0 z M6.6 5.5 a2.3 2.9 0 1 0 4.6 0 a2.3 2.9 0 1 0 -4.6 0 z M12.8 5.5 a2.3 2.9 0 1 0 4.6 0 a2.3 2.9 0 1 0 -4.6 0 z M17.1 10.5 a2.2 2.7 0 1 0 4.4 0 a2.2 2.7 0 1 0 -4.4 0 z" styleClass="huella-fondo" layoutX="60" layoutY="470" rotate="15" scaleX="1.4" scaleY="1.4" />
      <SVGPath content="M12 12.5 c-3 0 -6.5 3.2 -6.5 6 c0 1.8 1.4 2.8 3.2 2.8 c1.3 0 2.2 -0.7 3.3 -0.7 c1.1 0 2 0.7 3.3 0.7 c1.8 0 3.2 -1 3.2 -2.8 c0 -2.8 -3.5 -6 -6.5 -6 z M2.5 10.5 a2.2 2.7 0 1 0 4.4 0 a2.2 2.7 0 1 0 -4.4 0 z M6.6 5.5 a2.3 2.9 0 1 0 4.6 0 a2.3 2.9 0 1 0 -4.6 0 z M12.8 5.5 a2.3 2.9 0 1 0 4.6 0 a2.3 2.9 0 1 0 -4.6 0 z M17.1 10.5 a2.2 2.7 0 1 0 4.4 0 a2.2 2.7 0 1 0 -4.4 0 z" styleClass="huella-fondo" layoutX="410" layoutY="430" rotate="-30" scaleX="2.6" scaleY="2.6" />
      <SVGPath content="M12 12.5 c-3 0 -6.5 3.2 -6.5 6 c0 1.8 1.4 2.8 3.2 2.8 c1.3 0 2.2 -0.7 3.3 -0.7 c1.1 0 2 0.7 3.3 0.7 c1.8 0 3.2 -1 3.2 -2.8 c0 -2.8 -3.5 -6 -6.5 -6 z M2.5 10.5 a2.2 2.7 0 1 0 4.4 0 a2.2 2.7 0 1 0 -4.4 0 z M6.6 5.5 a2.3 2.9 0 1 0 4.6 0 a2.3 2.9 0 1 0 -4.6 0 z M12.8 5.5 a2.3 2.9 0 1 0 4.6 0 a2.3 2.9 0 1 0 -4.6 0 z M17.1 10.5 a2.2 2.7 0 1 0 4.4 0 a2.2 2.7 0 1 0 -4.4 0 z" styleClass="huella-fondo" layoutX="360" layoutY="260" rotate="40" scaleX="1.2" scaleY="1.2" />
      <SVGPath content="M12 12.5 c-3 0 -6.5 3.2 -6.5 6 c0 1.8 1.4 2.8 3.2 2.8 c1.3 0 2.2 -0.7 3.3 -0.7 c1.1 0 2 0.7 3.3 0.7 c1.8 0 3.2 -1 3.2 -2.8 c0 -2.8 -3.5 -6 -6.5 -6 z M2.5 10.5 a2.2 2.7 0 1 0 4.4 0 a2.2 2.7 0 1 0 -4.4 0 z M6.6 5.5 a2.3 2.9 0 1 0 4.6 0 a2.3 2.9 0 1 0 -4.6 0 z M12.8 5.5 a2.3 2.9 0 1 0 4.6 0 a2.3 2.9 0 1 0 -4.6 0 z M17.1 10.5 a2.2 2.7 0 1 0 4.4 0 a2.2 2.7 0 1 0 -4.4 0 z" styleClass="huella-fondo" layoutX="20" layoutY="290" rotate="-45" scaleX="1.0" scaleY="1.0" />
      <SVGPath content="M12 12.5 c-3 0 -6.5 3.2 -6.5 6 c0 1.8 1.4 2.8 3.2 2.8 c1.3 0 2.2 -0.7 3.3 -0.7 c1.1 0 2 0.7 3.3 0.7 c1.8 0 3.2 -1 3.2 -2.8 c0 -2.8 -3.5 -6 -6.5 -6 z M2.5 10.5 a2.2 2.7 0 1 0 4.4 0 a2.2 2.7 0 1 0 -4.4 0 z M6.6 5.5 a2.3 2.9 0 1 0 4.6 0 a2.3 2.9 0 1 0 -4.6 0 z M12.8 5.5 a2.3 2.9 0 1 0 4.6 0 a2.3 2.9 0 1 0 -4.6 0 z M17.1 10.5 a2.2 2.7 0 1 0 4.4 0 a2.2 2.7 0 1 0 -4.4 0 z" styleClass="huella-fondo" layoutX="200" layoutY="560" rotate="10" scaleX="1.8" scaleY="1.8" />
   </Pane>

   <!-- Capa 2: contenido -->
   <VBox alignment="TOP_CENTER" spacing="18.0">
      <padding>
         <Insets bottom="24.0" left="34.0" right="34.0" top="16.0" />
      </padding>

      <!-- Arriba: circulitos de tema (se dibujan desde el controlador) e idioma -->
      <HBox alignment="CENTER_LEFT" spacing="8.0">
         <Label text="%login.tema" minWidth="-Infinity" styleClass="login-claro" />
         <HBox fx:id="cajaTemas" alignment="CENTER_LEFT" spacing="8.0" />
         <Region HBox.hgrow="ALWAYS" />
         <Label text="%login.idioma" minWidth="-Infinity" styleClass="login-claro" />
         <ComboBox fx:id="cboIdioma" minWidth="-Infinity" prefWidth="115.0" />
      </HBox>

      <!-- Tarjeta -->
      <VBox alignment="TOP_CENTER" spacing="14.0" styleClass="login-tarjeta" VBox.vgrow="NEVER">
         <VBox.margin>
            <Insets top="34.0" />
         </VBox.margin>
         <padding>
            <Insets bottom="26.0" left="28.0" right="28.0" top="0.0" />
         </padding>

         <!-- Logo: círculo con una huella (sale un poco por encima de la tarjeta) -->
         <StackPane styleClass="login-logo" maxWidth="-Infinity" maxHeight="-Infinity">
            <VBox.margin>
               <Insets top="-42.0" />
            </VBox.margin>
            <SVGPath content="M12 12.5 c-3 0 -6.5 3.2 -6.5 6 c0 1.8 1.4 2.8 3.2 2.8 c1.3 0 2.2 -0.7 3.3 -0.7 c1.1 0 2 0.7 3.3 0.7 c1.8 0 3.2 -1 3.2 -2.8 c0 -2.8 -3.5 -6 -6.5 -6 z M2.5 10.5 a2.2 2.7 0 1 0 4.4 0 a2.2 2.7 0 1 0 -4.4 0 z M6.6 5.5 a2.3 2.9 0 1 0 4.6 0 a2.3 2.9 0 1 0 -4.6 0 z M12.8 5.5 a2.3 2.9 0 1 0 4.6 0 a2.3 2.9 0 1 0 -4.6 0 z M17.1 10.5 a2.2 2.7 0 1 0 4.4 0 a2.2 2.7 0 1 0 -4.4 0 z" styleClass="login-logo-huella" scaleX="2.3" scaleY="2.3" />
         </StackPane>

         <VBox alignment="CENTER" spacing="2.0">
            <Label text="%app.titulo" styleClass="login-titulo" />
            <Label text="%login.titulo" styleClass="texto-suave" />
         </VBox>

         <VBox spacing="12.0">
            <VBox.margin>
               <Insets top="6.0" />
            </VBox.margin>
            <!-- Campo usuario con icono -->
            <HBox alignment="CENTER_LEFT" spacing="8.0" styleClass="campo-login">
               <SVGPath content="M12 12 a4 4 0 1 0 0 -8 a4 4 0 0 0 0 8 z M4 20 c0 -3.3 3.6 -6 8 -6 s8 2.7 8 6 v1 H4 z" styleClass="campo-icono" />
               <TextField fx:id="txtUsuario" promptText="%login.usuario" HBox.hgrow="ALWAYS" />
            </HBox>
            <!-- Campo clave con icono -->
            <HBox alignment="CENTER_LEFT" spacing="8.0" styleClass="campo-login">
               <SVGPath fillRule="EVEN_ODD" content="M7 10 V7 a5 5 0 0 1 10 0 v3 h1 a1 1 0 0 1 1 1 v9 a1 1 0 0 1 -1 1 H6 a1 1 0 0 1 -1 -1 v-9 a1 1 0 0 1 1 -1 z M9 10 h6 V7 a3 3 0 0 0 -6 0 z" styleClass="campo-icono" />
               <PasswordField fx:id="txtClave" promptText="%login.clave" HBox.hgrow="ALWAYS" />
            </HBox>

            <Label fx:id="lblMensaje" styleClass="login-error" wrapText="true" minHeight="-Infinity" />

            <Button onAction="#ingresar" defaultButton="true" maxWidth="Infinity"
                    styleClass="btn-login" text="%login.ingresar" />
            <Button onAction="#cerrar" cancelButton="true" maxWidth="Infinity"
                    styleClass="btn-login-secundario" text="%login.cerrar" />
         </VBox>
      </VBox>

      <Label text="Veterinaria · Java 21 + JavaFX" styleClass="login-pie" />
   </VBox>
</StackPane>
```

### 15.2 `principal.fxml` — menú, pestañas y barra de estado

`src/main/resources/pe/edu/upeu/padronmascota/padronmascota/vistas/principal.fxml`

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.control.MenuBar?>
<?import javafx.scene.control.TabPane?>
<?import javafx.scene.layout.BorderPane?>
<?import javafx.scene.layout.HBox?>

<BorderPane xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1"
            fx:controller="pe.edu.upeu.padronmascota.padronmascota.controlador.PrincipalController">
   <!-- Arriba: el menú (se llena desde el controlador) -->
   <top>
      <MenuBar fx:id="menuBar" />
   </top>
   <!-- Centro: cada pantalla se abre como pestaña -->
   <center>
      <TabPane fx:id="tabPane" tabClosingPolicy="ALL_TABS" />
   </center>
   <!-- Abajo: barra de estado -->
   <bottom>
      <HBox styleClass="barra-estado">
         <padding>
            <Insets bottom="4.0" left="10.0" right="10.0" top="4.0" />
         </padding>
         <Label fx:id="lblEstado" />
      </HBox>
   </bottom>
</BorderPane>
```

### 15.3 `propietarios.fxml`

`src/main/resources/pe/edu/upeu/padronmascota/padronmascota/vistas/propietarios.fxml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.scene.shape.SVGPath?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<!-- Pantalla CRUD: formulario a la izquierda, buscador y tabla a la derecha -->
<BorderPane styleClass="pantalla" xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1"
            fx:controller="pe.edu.upeu.padronmascota.padronmascota.controlador.PropietarioController">

   <top>
      <!-- Encabezado: igual en todas las pantallas -->
      <HBox styleClass="encabezado">
         <BorderPane.margin><Insets bottom="14.0" /></BorderPane.margin>
         <StackPane styleClass="encabezado-icono">
            <SVGPath content="M12 12 a4 4 0 1 0 0 -8 a4 4 0 0 0 0 8 z M4 20 c0 -3.3 3.6 -6 8 -6 s8 2.7 8 6 v1 H4 z" fill="WHITE" scaleX="1.05" scaleY="1.05" />
         </StackPane>
         <VBox alignment="CENTER_LEFT" spacing="1.0" HBox.hgrow="ALWAYS">
            <Label styleClass="encabezado-titulo" text="%opcion.propietarios" />
            <Label styleClass="encabezado-subtitulo" text="%sub.propietarios" />
         </VBox>
      </HBox>
   </top>

   <left>
      <ScrollPane fitToWidth="true" hbarPolicy="NEVER" styleClass="scroll-formulario">
      <VBox spacing="10.0" prefWidth="380.0" styleClass="panel-formulario">
         <padding>
            <Insets bottom="18.0" left="18.0" right="18.0" top="20.0" />
         </padding>
         <Label fx:id="lblModo" styleClass="titulo" wrapText="true" minHeight="-Infinity" />
         <GridPane hgap="8.0" vgap="9.0">
            <columnConstraints>
               <ColumnConstraints minWidth="128.0" prefWidth="128.0" />
               <ColumnConstraints hgrow="ALWAYS" />
            </columnConstraints>
            <Label text="%propietario.dni" GridPane.rowIndex="0" />
            <TextField fx:id="txtDni" promptText="%propietario.dniPrompt" GridPane.columnIndex="1" GridPane.rowIndex="0" />
            <Label text="%comun.nombre" GridPane.rowIndex="1" />
            <TextField fx:id="txtNombre" GridPane.columnIndex="1" GridPane.rowIndex="1" />
            <Label text="%comun.telefono" GridPane.rowIndex="2" />
            <TextField fx:id="txtTelefono" promptText="%comun.telefonoPrompt" GridPane.columnIndex="1" GridPane.rowIndex="2" />
            <Label text="%propietario.direccion" GridPane.rowIndex="3" />
            <TextField fx:id="txtDireccion" GridPane.columnIndex="1" GridPane.rowIndex="3" />
            <Label text="%propietario.email" GridPane.rowIndex="4" />
            <TextField fx:id="txtEmail" promptText="usuario@dominio.com" GridPane.columnIndex="1" GridPane.rowIndex="4" />
         </GridPane>
         <HBox spacing="8.0">
            <Button onAction="#guardar" styleClass="btn, btn-exito" text="%comun.guardar" defaultButton="true" minWidth="-Infinity" />
            <Button onAction="#nuevo" styleClass="btn, btn-secundario" text="%comun.nuevo" minWidth="-Infinity" />
         </HBox>
      </VBox>
      </ScrollPane>
   </left>

   <center>
      <VBox spacing="10.0" styleClass="panel-tabla">
         <padding><Insets bottom="12.0" left="14.0" right="14.0" top="14.0" /></padding>
         <BorderPane.margin>
            <Insets left="14.0" />
         </BorderPane.margin>
         <HBox alignment="CENTER_LEFT" spacing="8.0">
            <TextField fx:id="txtBuscar" prefWidth="240.0" promptText="%propietario.buscar" />
         </HBox>
         <TableView fx:id="tabla" VBox.vgrow="ALWAYS" />
         <Label fx:id="lblTotal" styleClass="texto-suave" />
      </VBox>
   </center>
</BorderPane>
```

### 15.4 `mascotas.fxml`

`src/main/resources/pe/edu/upeu/padronmascota/padronmascota/vistas/mascotas.fxml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.scene.shape.SVGPath?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<!-- Pantalla CRUD: formulario a la izquierda, buscador y tabla a la derecha -->
<BorderPane styleClass="pantalla" xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1"
            fx:controller="pe.edu.upeu.padronmascota.padronmascota.controlador.MascotaController">

   <top>
      <!-- Encabezado: igual en todas las pantallas -->
      <HBox styleClass="encabezado">
         <BorderPane.margin><Insets bottom="14.0" /></BorderPane.margin>
         <StackPane styleClass="encabezado-icono">
            <SVGPath content="M12 12.5 c-3 0 -6.5 3.2 -6.5 6 c0 1.8 1.4 2.8 3.2 2.8 c1.3 0 2.2 -0.7 3.3 -0.7 c1.1 0 2 0.7 3.3 0.7 c1.8 0 3.2 -1 3.2 -2.8 c0 -2.8 -3.5 -6 -6.5 -6 z M2.5 10.5 a2.2 2.7 0 1 0 4.4 0 a2.2 2.7 0 1 0 -4.4 0 z M6.6 5.5 a2.3 2.9 0 1 0 4.6 0 a2.3 2.9 0 1 0 -4.6 0 z M12.8 5.5 a2.3 2.9 0 1 0 4.6 0 a2.3 2.9 0 1 0 -4.6 0 z M17.1 10.5 a2.2 2.7 0 1 0 4.4 0 a2.2 2.7 0 1 0 -4.4 0 z" fill="WHITE" scaleX="1.05" scaleY="1.05" />
         </StackPane>
         <VBox alignment="CENTER_LEFT" spacing="1.0" HBox.hgrow="ALWAYS">
            <Label styleClass="encabezado-titulo" text="%opcion.mascotas" />
            <Label styleClass="encabezado-subtitulo" text="%sub.mascotas" />
         </VBox>
      </HBox>
   </top>

   <left>
      <ScrollPane fitToWidth="true" hbarPolicy="NEVER" styleClass="scroll-formulario">
      <VBox spacing="10.0" prefWidth="390.0" styleClass="panel-formulario">
         <padding>
            <Insets bottom="18.0" left="18.0" right="18.0" top="20.0" />
         </padding>
         <Label fx:id="lblModo" styleClass="titulo" wrapText="true" minHeight="-Infinity" />
         <GridPane hgap="8.0" vgap="9.0">
            <columnConstraints>
               <ColumnConstraints minWidth="128.0" prefWidth="128.0" />
               <ColumnConstraints hgrow="ALWAYS" />
            </columnConstraints>
            <!-- 1° el dueño: así la mascota siempre queda asociada a alguien -->
            <Label text="%mascota.propietario" GridPane.rowIndex="0" />
            <TextField fx:id="txtPropietario" promptText="%mascota.propietarioPrompt" GridPane.columnIndex="1" GridPane.rowIndex="0" />
            <!-- Campos que solo aparecen si lo escrito es nuevo (agrupados para que no dejen huecos) -->
            <GridPane hgap="8.0" vgap="9.0" GridPane.columnSpan="2" GridPane.rowIndex="1">
               <columnConstraints>
                  <ColumnConstraints minWidth="128.0" prefWidth="128.0" />
                  <ColumnConstraints hgrow="ALWAYS" />
               </columnConstraints>
               <Label fx:id="lblDniProp" text="%propietario.dni" visible="false" managed="false" GridPane.rowIndex="0" />
               <TextField fx:id="txtDniProp" promptText="%propietario.dniPrompt" visible="false" managed="false" GridPane.columnIndex="1" GridPane.rowIndex="0" />
               <Label fx:id="lblTelProp" text="%comun.telefono" visible="false" managed="false" GridPane.rowIndex="1" />
               <TextField fx:id="txtTelProp" promptText="%propietario.telefonoPrompt" visible="false" managed="false" GridPane.columnIndex="1" GridPane.rowIndex="1" />
            </GridPane>
            <!-- 2° los datos de la mascota -->
            <Label text="%comun.nombre" GridPane.rowIndex="2" />
            <TextField fx:id="txtNombre" GridPane.columnIndex="1" GridPane.rowIndex="2" />
            <Label text="%mascota.especie" GridPane.rowIndex="3" />
            <ComboBox fx:id="cboEspecie" maxWidth="Infinity" promptText="%comun.elegir" GridPane.columnIndex="1" GridPane.rowIndex="3" />
            <Label text="%mascota.raza" GridPane.rowIndex="4" />
            <TextField fx:id="txtRaza" promptText="%mascota.razaPrompt" GridPane.columnIndex="1" GridPane.rowIndex="4" />
            <Label text="%mascota.color" GridPane.rowIndex="5" />
            <TextField fx:id="txtColor" promptText="%mascota.colorPrompt" GridPane.columnIndex="1" GridPane.rowIndex="5" />
            <Label text="%mascota.sexo" GridPane.rowIndex="6" />
            <ComboBox fx:id="cboSexo" maxWidth="Infinity" promptText="%comun.elegir" GridPane.columnIndex="1" GridPane.rowIndex="6" />
            <Label text="%mascota.edad" GridPane.rowIndex="7" />
            <TextField fx:id="txtEdad" promptText="%mascota.edadPrompt" GridPane.columnIndex="1" GridPane.rowIndex="7" />
            <Label text="%mascota.fechaRegistro" GridPane.rowIndex="8" />
            <DatePicker fx:id="dtpFechaRegistro" maxWidth="Infinity" GridPane.columnIndex="1" GridPane.rowIndex="8" />
            <Label text="%mascota.vacunacion" GridPane.rowIndex="9" />
            <ComboBox fx:id="cboEstado" maxWidth="Infinity" promptText="%comun.elegir" GridPane.columnIndex="1" GridPane.rowIndex="9" />
            <Label text="%mascota.observaciones" GridPane.rowIndex="10" />
            <TextArea fx:id="txtObservaciones" prefRowCount="3" wrapText="true" GridPane.columnIndex="1" GridPane.rowIndex="10" />
         </GridPane>
         <HBox spacing="8.0">
            <Button onAction="#guardar" styleClass="btn, btn-exito" text="%comun.guardar" defaultButton="true" minWidth="-Infinity" />
            <Button onAction="#nuevo" styleClass="btn, btn-secundario" text="%comun.nuevo" minWidth="-Infinity" />
         </HBox>
      </VBox>
      </ScrollPane>
   </left>

   <center>
      <VBox spacing="10.0" styleClass="panel-tabla">
         <padding><Insets bottom="12.0" left="14.0" right="14.0" top="14.0" /></padding>
         <BorderPane.margin>
            <Insets left="14.0" />
         </BorderPane.margin>
         <HBox alignment="CENTER_LEFT" spacing="8.0">
            <TextField fx:id="txtBuscar" prefWidth="240.0" promptText="%mascota.buscar" />
            <ComboBox fx:id="cboFiltroEspecie" onAction="#buscar" prefWidth="110.0" />
            <ComboBox fx:id="cboFiltroEstado" onAction="#buscar" prefWidth="110.0" />
            <CheckBox fx:id="chkInactivas" onAction="#buscar" text="%mascota.verInactivas" minWidth="-Infinity" />
         </HBox>
         <TableView fx:id="tabla" VBox.vgrow="ALWAYS" />
         <Label fx:id="lblTotal" styleClass="texto-suave" />
      </VBox>
   </center>
</BorderPane>
```

### Cómo leer un FXML

- `fx:controller` → qué clase controla la pantalla (la crea `ContenedorApp`).
- `fx:id="txtNombre"` → se conecta con `@FXML private TextField txtNombre;` del controlador.
- `onAction="#guardar"` → al pulsar, se ejecuta el método `guardar()` del controlador.
- `text="%comun.guardar"` → el texto sale de `language/idiomas-xx.properties`.
- `styleClass="btn, btn-exito"` → el estilo sale de `css/base.css`.

---

## 16. Paso 15 — Diseño (CSS, temas) e idiomas

### 16.1 `base.css`

Define todo el diseño (tema *Clínica*): una paleta con nombre (`-vet-primario`, `-vet-superficie`...), fondo con textura de huellitas (`css/img/huellas-*.png`), encabezado, paneles tipo tarjeta, campos, botones, tablas, menú y pestañas.

`src/main/resources/css/base.css`

```css
/* ==========================================================================
   PADRÓN MASCOTA · Diseño de la veterinaria
   --------------------------------------------------------------------------
   Este archivo define TODO el diseño (tema "Clínica", claro y verde azulado).
   Los otros temas (tema-*.css) solo cambian los colores de la sección 1.

   1. Paleta (colores con nombre)       6. Tablas
   2. Fondo con textura de huellitas    7. Menú, pestañas y barra de estado
   3. Encabezado de cada pantalla       8. Tarjetas del Resumen
   4. Paneles (tarjetas blancas)        9. Login
   5. Campos y botones                 10. Avisos y detalles
   ========================================================================== */

/* ---------- 1. Paleta ---------- */
.root {
    -vet-primario: #0e8a83;          /* verde azulado clínico */
    -vet-primario-oscuro: #0a5e5a;
    -vet-primario-suave: #d9f2ef;    /* fondos de selección y encabezados de tabla */
    -vet-acento: #f29d52;            /* naranja cálido: detalles "mascota" */
    -vet-exito: #2f9e63;
    -vet-peligro: #d64545;
    -vet-aviso: #e0a526;

    -vet-fondo: #eef4f3;
    -vet-superficie: #ffffff;
    -vet-superficie-2: #f5f9f8;      /* filas alternas, campos de solo lectura */
    -vet-borde: #dbe5e3;
    -vet-texto: #1f2d2c;
    -vet-texto-suave: #61736f;
    -vet-ayuda: #98a8a5;             /* texto de ayuda (prompt) dentro de los campos */
    -vet-sombra: rgba(16, 60, 56, 0.10);

    /* Compatibilidad con nombres anteriores */
    -color-primario: -vet-primario;
    -color-exito: -vet-exito;
    -color-peligro: -vet-peligro;

    /* Modena (los controles de JavaFX) toma sus colores de aquí */
    -fx-base: #e9f1ef;
    -fx-background: -vet-fondo;
    -fx-control-inner-background: -vet-superficie;
    -fx-control-inner-background-alt: -vet-superficie-2;
    -fx-accent: -vet-primario;
    -fx-focus-color: -vet-primario;
    -fx-faint-focus-color: transparent;
    -fx-selection-bar: -vet-primario-suave;
    -fx-selection-bar-non-focused: -vet-primario-suave;
    -fx-selection-bar-text: -vet-texto;
    -fx-text-base-color: -vet-texto;
    -fx-text-background-color: -vet-texto;
    -fx-text-inner-color: -vet-texto;
    -fx-prompt-text-fill: derive(-vet-texto-suave, 35%);

    -fx-font-family: "Segoe UI";
    -fx-font-size: 13px;
}

/* ---------- 2. Fondo con textura de huellitas ---------- */
.tab-pane > .tab-content-area,
.pantalla {
    -fx-background-color: linear-gradient(to bottom, derive(-vet-fondo, 2%), derive(-vet-fondo, -3%));
    -fx-background-image: url("img/huellas-claro.png");
    -fx-background-repeat: repeat;
}
.pantalla {
    -fx-padding: 14 16 12 16;
}

.titulo {
    -fx-font-size: 16px;
    -fx-font-weight: bold;
    -fx-text-fill: -vet-texto;
}
.subtitulo {
    -fx-font-size: 14px;
    -fx-font-weight: bold;
    -fx-text-fill: -vet-primario-oscuro;
    -fx-padding: 8 0 2 0;
}
.texto-suave {
    -fx-text-fill: -vet-texto-suave;
}

/* ---------- 3. Encabezado de cada pantalla ---------- */
.encabezado {
    -fx-padding: 14 20 14 16;
    -fx-spacing: 14;
    -fx-alignment: center-left;
    -fx-background-color: linear-gradient(to right, -vet-primario-oscuro 0%, -vet-primario 65%, derive(-vet-primario, 22%) 100%);
    -fx-background-image: url("img/huellas-banner.png");
    -fx-background-repeat: repeat;
    -fx-background-radius: 14;
    -fx-effect: dropshadow(gaussian, -vet-sombra, 14, 0, 0, 4);
}
.encabezado-icono {
    -fx-min-width: 46; -fx-min-height: 46;
    -fx-max-width: 46; -fx-max-height: 46;
    -fx-background-color: rgba(255, 255, 255, 0.18);
    -fx-background-radius: 23;
    -fx-border-color: rgba(255, 255, 255, 0.45);
    -fx-border-radius: 23;
    -fx-border-width: 1.5;
}
.encabezado-icono > SVGPath {
    -fx-fill: white;
}
.encabezado-titulo {
    -fx-font-size: 20px;
    -fx-font-weight: bold;
    -fx-text-fill: white;
}
.encabezado-subtitulo {
    -fx-font-size: 12.5px;
    -fx-text-fill: rgba(255, 255, 255, 0.85);
}
/* Botón dentro del encabezado (ej.: "Actualizar" del Resumen) */
.encabezado .btn-primario {
    -fx-background-color: rgba(255, 255, 255, 0.92);
    -fx-text-fill: -vet-primario-oscuro;
}

/* ---------- 4. Paneles (tarjetas) ---------- */
.panel-formulario,
.panel-tabla {
    -fx-background-color: -vet-superficie;
    -fx-background-radius: 14;
    -fx-border-color: -vet-borde;
    -fx-border-radius: 14;
    -fx-effect: dropshadow(gaussian, -vet-sombra, 16, 0, 0, 4);
}
.panel-formulario {
    /* Línea de color arriba del formulario: lo identifica como "zona de edición" */
    -fx-background-color: -vet-primario, -vet-superficie;
    -fx-background-insets: 0, 4 0 0 0;
    -fx-background-radius: 14, 12 12 14 14;
}
.panel-formulario > .titulo {
    -fx-text-fill: -vet-primario-oscuro;
    -fx-font-size: 16px;
}
.panel-formulario .label {
    -fx-text-fill: -vet-texto-suave;
    -fx-font-weight: bold;
    -fx-font-size: 12px;
}
/* Excepciones: estas etiquetas del formulario mantienen su propio estilo */
.panel-formulario .label.titulo {
    -fx-font-size: 17px;
    -fx-text-fill: -vet-primario-oscuro;
    -fx-padding: 0 0 4 0;
}
.panel-formulario .label.texto-suave {
    -fx-font-weight: normal;
    -fx-font-size: 12.5px;
}
.panel-formulario .label.total-venta {
    -fx-font-size: 21px;
    -fx-text-fill: -vet-primario-oscuro;
}
.panel-formulario .button .label,
.panel-formulario .check-box .label {
    -fx-font-size: 13px;
}
.panel-formulario .separator .line {
    -fx-border-color: -vet-borde;
}
.panel-tabla .titulo {
    -fx-text-fill: -vet-primario-oscuro;
}

/* Formularios largos con barra de desplazamiento: transparentes */
.scroll-formulario,
.scroll-formulario > .viewport,
.scroll-transparente,
.scroll-transparente > .viewport {
    -fx-background-color: transparent;
    -fx-background-insets: 0;
    -fx-padding: 0;
}
.scroll-formulario {
    -fx-padding: 0 6 12 0;   /* deja ver la sombra del panel */
}

/* ---------- 5. Campos y botones ---------- */
.text-field,
.password-field,
.text-area,
.combo-box,
.date-picker,
.spinner {
    -fx-background-color: -vet-borde, -vet-superficie;
    -fx-background-insets: 0, 1;
    -fx-background-radius: 8, 7;
    -fx-text-fill: -vet-texto;
}
.text-field,
.password-field {
    -fx-padding: 7 10 7 10;
}
/* Texto y texto de ayuda de TODOS los campos (también el de fecha y combos editables) */
.text-input,
.date-picker > .text-field,
.combo-box .text-field {
    -fx-text-fill: -vet-texto;
    -fx-prompt-text-fill: -vet-ayuda;
    -fx-highlight-fill: -vet-primario;
    -fx-highlight-text-fill: white;
}
.text-area .content {
    -fx-background-color: -vet-superficie;
    -fx-background-radius: 7;
}
.date-picker > .text-field {
    -fx-background-color: transparent;
}
.combo-box .list-cell {
    -fx-text-fill: -vet-texto;
    -fx-padding: 5 8 5 8;
}
.text-field:focused,
.password-field:focused,
.text-area:focused,
.combo-box:focused,
.date-picker:focused {
    -fx-background-color: -vet-primario, -vet-superficie;
    -fx-background-insets: 0, 2;
    -fx-effect: dropshadow(gaussian, derive(-vet-primario, 55%), 6, 0.3, 0, 0);
}
.combo-box .arrow,
.date-picker .arrow-button .arrow {
    -fx-background-color: -vet-primario;
}
.combo-box .arrow-button,
.date-picker .arrow-button {
    -fx-background-color: transparent;
}
.combo-box-popup .list-view {
    -fx-background-color: -vet-borde, -vet-superficie;
    -fx-background-insets: 0, 1;
    -fx-background-radius: 8;
    -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.18), 12, 0, 0, 4);
}
.combo-box-popup .list-view .list-cell:filled:hover,
.combo-box-popup .list-view .list-cell:filled:selected {
    -fx-background-color: -vet-primario-suave;
    -fx-text-fill: -vet-texto;
}
.check-box .box {
    -fx-background-color: -vet-borde, -vet-superficie;
    -fx-background-radius: 4;
}
.check-box:selected .box {
    -fx-background-color: -vet-primario;
}
.check-box:selected .mark {
    -fx-background-color: white;
}

/* Botones */
.btn {
    -fx-font-weight: bold;
    -fx-padding: 8 16 8 16;
    -fx-background-radius: 8;
    -fx-border-radius: 8;
    -fx-cursor: hand;
    -fx-background-insets: 0;
}
.btn-chico {
    -fx-font-size: 11px;
    -fx-padding: 4 10 4 10;
    -fx-background-radius: 6;
}
.btn-primario {
    -fx-background-color: linear-gradient(to bottom, derive(-vet-primario, 12%), -vet-primario);
    -fx-text-fill: white;
}
.btn-exito {
    -fx-background-color: linear-gradient(to bottom, derive(-vet-exito, 12%), -vet-exito);
    -fx-text-fill: white;
    -fx-effect: dropshadow(gaussian, derive(-vet-exito, 40%), 6, 0, 0, 2);
}
.btn-peligro {
    -fx-background-color: linear-gradient(to bottom, derive(-vet-peligro, 12%), -vet-peligro);
    -fx-text-fill: white;
}
.btn-secundario {
    -fx-background-color: -vet-superficie-2;
    -fx-border-color: -vet-borde;
    -fx-text-fill: -vet-texto;
}
/* Botones chicos de las tablas: estilo "suave" (fondo tenue, texto de color) */
.btn-chico.btn-primario {
    -fx-background-color: derive(-vet-primario, 82%);
    -fx-text-fill: -vet-primario-oscuro;
}
.btn-chico.btn-peligro {
    -fx-background-color: derive(-vet-peligro, 82%);
    -fx-text-fill: derive(-vet-peligro, -25%);
}
.btn-chico.btn-secundario {
    -fx-background-color: derive(-vet-aviso, 75%);
    -fx-border-color: transparent;
    -fx-text-fill: derive(-vet-aviso, -45%);
}
.btn:hover {
    -fx-opacity: 0.88;
}
.btn:pressed {
    -fx-translate-y: 1;
}
.btn-chico.btn-primario:hover { -fx-background-color: -vet-primario; -fx-text-fill: white; -fx-opacity: 1; }
.btn-chico.btn-peligro:hover { -fx-background-color: -vet-peligro; -fx-text-fill: white; -fx-opacity: 1; }
.btn-chico.btn-secundario:hover { -fx-background-color: -vet-aviso; -fx-text-fill: white; -fx-opacity: 1; }

/* Botones con ícono de las tablas (Editar, Eliminar, Ver...) */
.btn-icono {
    -fx-padding: 5 7 5 7;
    -fx-min-width: 30;
    -fx-min-height: 26;
}
.btn-icono.btn-primario SVGPath { -fx-fill: -vet-primario-oscuro; }
.btn-icono.btn-peligro SVGPath { -fx-fill: derive(-vet-peligro, -25%); }
.btn-icono.btn-secundario SVGPath { -fx-fill: derive(-vet-aviso, -45%); }
.btn-icono:hover SVGPath { -fx-fill: white; }

/* ---------- 6. Tablas ---------- */
.table-view {
    -fx-background-color: -vet-borde, -vet-superficie;
    -fx-background-insets: 0, 1;
    -fx-background-radius: 10;
    -fx-padding: 1;
    -fx-table-cell-border-color: transparent;
}
.table-view .column-header-background {
    -fx-background-color: -vet-primario-suave;
    -fx-background-radius: 9 9 0 0;
}
.table-view .column-header,
.table-view .filler {
    -fx-background-color: transparent;
    -fx-border-color: transparent derive(-vet-primario-suave, -6%) transparent transparent;
    -fx-size: 36;
}
.table-view .column-header .label {
    -fx-text-fill: -vet-primario-oscuro;
    -fx-font-weight: bold;
    -fx-font-size: 12px;
    -fx-alignment: center-left;
    -fx-padding: 0 8 0 8;
}
.table-view .column-header .arrow {
    -fx-background-color: -vet-primario;
}
.table-row-cell {
    -fx-background-color: -vet-superficie;
    -fx-cell-size: 36;
    -fx-border-color: transparent transparent -vet-borde transparent;
    -fx-border-width: 0 0 1 0;
}
.table-row-cell:odd {
    -fx-background-color: -vet-superficie-2;
}
.table-row-cell:empty {
    -fx-background-color: transparent;
    -fx-border-color: transparent;
}
.table-row-cell:filled:hover {
    -fx-background-color: derive(-vet-primario-suave, 40%);
}
.table-row-cell:filled:selected {
    -fx-background-color: -vet-primario-suave;
    -fx-border-color: transparent transparent -vet-primario transparent;
}
.table-cell {
    -fx-padding: 0 8 0 8;
    -fx-alignment: center-left;
    -fx-text-fill: -vet-texto;
}
.table-row-cell:filled:selected .table-cell {
    -fx-text-fill: -vet-texto;
}
.table-view .placeholder .label {
    -fx-text-fill: -vet-texto-suave;
    -fx-font-size: 14px;
}

/* Barras de desplazamiento finas */
.scroll-bar {
    -fx-background-color: transparent;
}
.scroll-bar .thumb {
    -fx-background-color: derive(-vet-borde, -15%);
    -fx-background-radius: 6;
    -fx-background-insets: 2;
}
.scroll-bar .thumb:hover {
    -fx-background-color: -vet-primario;
}
/* Los botones de flecha se ocultan, pero dan el grosor (10 px) a la barra */
.scroll-bar .increment-button,
.scroll-bar .decrement-button {
    -fx-background-color: transparent;
    -fx-padding: 0;
}
.scroll-bar:vertical .increment-arrow,
.scroll-bar:vertical .decrement-arrow {
    -fx-shape: "M0 0 h1 v1 h-1 z";
    -fx-background-color: transparent;
    -fx-padding: 0 5 0 5;
}
.scroll-bar:horizontal .increment-arrow,
.scroll-bar:horizontal .decrement-arrow {
    -fx-shape: "M0 0 h1 v1 h-1 z";
    -fx-background-color: transparent;
    -fx-padding: 5 0 5 0;
}

/* ---------- 7. Menú, pestañas y barra de estado ---------- */
.menu-bar {
    -fx-background-color: linear-gradient(to right, -vet-primario-oscuro, -vet-primario);
    -fx-padding: 2 8 2 8;
    -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.20), 6, 0, 0, 2);
}
.menu-bar > .container > .menu-button {
    -fx-background-radius: 6;
    -fx-padding: 6 12 6 12;
}
.menu-bar > .container > .menu-button > .label {
    -fx-text-fill: white;
    -fx-font-weight: bold;
}
.menu-bar > .container > .menu-button:hover,
.menu-bar > .container > .menu-button:showing {
    -fx-background-color: rgba(255, 255, 255, 0.18);
}
.context-menu {
    -fx-background-color: -vet-borde, -vet-superficie;
    -fx-background-insets: 0, 1;
    -fx-background-radius: 8;
    -fx-padding: 6;
    -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.18), 14, 0, 0, 4);
}
.menu-item {
    -fx-background-radius: 6;
    -fx-padding: 7 14 7 10;
}
.menu-item .label {
    -fx-text-fill: -vet-texto;
}
.menu-item:focused {
    -fx-background-color: -vet-primario-suave;
}
.menu-item:focused .label {
    -fx-text-fill: -vet-primario-oscuro;
}

.tab-pane > .tab-header-area {
    -fx-padding: 8 10 0 10;
}
.tab-pane > .tab-header-area > .tab-header-background {
    -fx-background-color: -vet-superficie;
    -fx-effect: dropshadow(gaussian, -vet-sombra, 6, 0, 0, 1);
}
.tab-pane > .tab-header-area > .headers-region > .tab {
    -fx-background-color: transparent;
    -fx-background-insets: 0;
    -fx-background-radius: 8 8 0 0;
    -fx-padding: 6 12 6 12;
    -fx-border-color: transparent;
    -fx-border-width: 0 0 3 0;
}
.tab-pane > .tab-header-area > .headers-region > .tab:hover {
    -fx-background-color: -vet-superficie-2;
}
.tab-pane > .tab-header-area > .headers-region > .tab:selected {
    -fx-background-color: -vet-primario-suave;
    -fx-border-color: -vet-primario;
}
.tab-pane > .tab-header-area > .headers-region > .tab .tab-label {
    -fx-text-fill: -vet-texto-suave;
    -fx-font-weight: bold;
}
.tab-pane > .tab-header-area > .headers-region > .tab:selected .tab-label {
    -fx-text-fill: -vet-primario-oscuro;
}
.tab-pane > .tab-header-area > .headers-region > .tab:selected .focus-indicator {
    -fx-border-color: transparent;
}
.tab .tab-close-button {
    -fx-background-color: -vet-texto-suave;
}
.tab .tab-close-button:hover {
    -fx-background-color: -vet-peligro;
}
/* Pestañas internas (Reporte de ventas): sin textura extra */
.pantalla .tab-pane > .tab-content-area {
    -fx-background-color: transparent;
    -fx-background-image: null;
    -fx-padding: 12 0 0 0;
}
.pantalla .tab-pane > .tab-header-area {
    -fx-padding: 0;
}
.pantalla .tab-pane > .tab-header-area > .tab-header-background {
    -fx-background-color: transparent;
    -fx-effect: null;
    -fx-border-color: transparent transparent -vet-borde transparent;
}

.barra-estado {
    -fx-background-color: -vet-superficie;
    -fx-border-color: -vet-borde transparent transparent transparent;
    -fx-alignment: center-left;
}
.barra-estado .label {
    -fx-text-fill: -vet-texto-suave;
    -fx-font-size: 12px;
}

/* ---------- 8. Tarjetas del Resumen ---------- */
.tarjeta {
    -fx-padding: 14 18 14 18;
    -fx-min-width: 165;
    -fx-spacing: 2;
    -fx-background-color: -vet-primario, -vet-superficie;
    -fx-background-insets: 0, 0 0 0 5;   /* franja de color a la izquierda */
    -fx-background-radius: 12, 0 12 12 0;
    -fx-effect: dropshadow(gaussian, -vet-sombra, 12, 0, 0, 3);
}
.tarjeta:hover {
    -fx-effect: dropshadow(gaussian, derive(-vet-primario, 30%), 14, 0, 0, 4);
    -fx-translate-y: -1;
}
.tarjeta-titulo {
    -fx-font-size: 12px;
    -fx-text-fill: -vet-texto-suave;
    -fx-font-weight: bold;
}
.tarjeta-valor {
    -fx-font-size: 26px;
    -fx-font-weight: bold;
    -fx-text-fill: -vet-primario-oscuro;
}
/* Gráficos del Resumen dentro de un panel */
.chart {
    -fx-background-color: -vet-superficie;
    -fx-background-radius: 14;
    -fx-padding: 12;
    -fx-effect: dropshadow(gaussian, -vet-sombra, 12, 0, 0, 3);
}
.chart-title {
    -fx-text-fill: -vet-primario-oscuro;
    -fx-font-weight: bold;
    -fx-font-size: 14px;
}
.chart-legend {
    -fx-background-color: transparent;
}
.chart-legend-item, .chart-pie-label {
    -fx-text-fill: -vet-texto;
}
.default-color0.chart-pie { -fx-pie-color: -vet-primario; }
.default-color1.chart-pie { -fx-pie-color: -vet-acento; }
.default-color2.chart-pie { -fx-pie-color: #6c8ae4; }
.default-color3.chart-pie { -fx-pie-color: #c76bb5; }
.default-color4.chart-pie { -fx-pie-color: -vet-aviso; }
.default-color5.chart-pie { -fx-pie-color: #8aa29e; }

/* ---------- 9. Login ---------- */
.login {
    -fx-background-color: linear-gradient(to bottom right,
            derive(-vet-primario, 20%) 0%,
            -vet-primario 45%,
            derive(-vet-primario-oscuro, -45%) 100%);
}
.huella-fondo {
    -fx-fill: rgba(255, 255, 255, 0.10);
}
.login-claro {
    -fx-text-fill: white;
    -fx-font-weight: bold;
}
.login-pie {
    -fx-text-fill: rgba(255, 255, 255, 0.75);
    -fx-font-size: 11px;
}
.login-tarjeta {
    -fx-background-color: -vet-superficie;
    -fx-background-radius: 20;
    -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.35), 30, 0.12, 0, 10);
}
.login-logo {
    -fx-min-width: 86; -fx-min-height: 86;
    -fx-pref-width: 86; -fx-pref-height: 86;
    -fx-background-color: linear-gradient(to bottom, derive(-vet-primario, 18%), -vet-primario-oscuro);
    -fx-background-radius: 43;
    -fx-border-color: -vet-superficie;
    -fx-border-width: 4;
    -fx-border-radius: 43;
    -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.30), 12, 0, 0, 4);
}
.login-logo-huella {
    -fx-fill: white;
}
.login-titulo {
    -fx-font-size: 25px;
    -fx-font-weight: bold;
    -fx-text-fill: -vet-primario-oscuro;
}
.login-error {
    -fx-text-fill: -vet-peligro;
    -fx-font-weight: bold;
}
.campo-login {
    -fx-padding: 3 14 3 14;
    -fx-background-color: -vet-superficie-2;
    -fx-background-radius: 24;
    -fx-border-color: -vet-borde;
    -fx-border-radius: 24;
    -fx-border-width: 1.5;
}
.campo-login:focus-within {
    -fx-border-color: -vet-primario;
    -fx-effect: dropshadow(gaussian, derive(-vet-primario, 50%), 8, 0.2, 0, 0);
}
.campo-login .text-field,
.campo-login .password-field,
.campo-login .text-field:focused,
.campo-login .password-field:focused {
    -fx-background-color: transparent;
    -fx-effect: null;
    -fx-padding: 7 0 7 0;
    -fx-font-size: 14px;
}
.campo-icono {
    -fx-fill: -vet-primario;
}
.btn-login {
    -fx-background-color: linear-gradient(to right, -vet-primario, -vet-primario-oscuro);
    -fx-background-radius: 24;
    -fx-text-fill: white;
    -fx-font-size: 14px;
    -fx-font-weight: bold;
    -fx-padding: 11 0 11 0;
    -fx-cursor: hand;
    -fx-effect: dropshadow(gaussian, derive(-vet-primario, 20%), 10, 0, 0, 3);
}
.btn-login:hover {
    -fx-background-color: linear-gradient(to right, derive(-vet-primario, 12%), -vet-primario);
}
.btn-login:pressed {
    -fx-translate-y: 1;
    -fx-effect: null;
}
.btn-login-secundario {
    -fx-background-color: transparent;
    -fx-background-radius: 24;
    -fx-border-color: -vet-borde;
    -fx-border-radius: 24;
    -fx-border-width: 1.5;
    -fx-text-fill: -vet-texto-suave;
    -fx-font-weight: bold;
    -fx-padding: 9 0 9 0;
    -fx-cursor: hand;
}
.btn-login-secundario:hover {
    -fx-background-color: -vet-superficie-2;
    -fx-border-color: -vet-peligro;
    -fx-text-fill: -vet-peligro;
}
.circulo-tema {
    -fx-stroke: white;
    -fx-stroke-width: 2;
    -fx-cursor: hand;
}
.circulo-tema:hover {
    -fx-stroke-width: 3.5;
    -fx-scale-x: 1.15;
    -fx-scale-y: 1.15;
}

/* ---------- 10. Avisos y detalles ---------- */
.total-venta {
    -fx-font-size: 21px;
    -fx-font-weight: bold;
    -fx-text-fill: -vet-primario-oscuro;
    -fx-background-color: -vet-primario-suave;
    -fx-background-radius: 10;
    -fx-padding: 8 14 8 14;
    -fx-max-width: infinity;
}
.campo-error,
.campo-error:focused {
    -fx-background-color: -vet-peligro, -vet-superficie;
    -fx-background-insets: 0, 2;
}
.tooltip-error {
    -fx-background-color: -vet-peligro;
    -fx-text-fill: white;
    -fx-font-size: 12px;
}
.tooltip {
    -fx-background-color: -vet-primario-oscuro;
    -fx-text-fill: white;
    -fx-background-radius: 6;
    -fx-font-size: 12px;
}
.notificacion {
    -fx-padding: 12 20 12 20;
    -fx-background-radius: 10;
    -fx-text-fill: white;
    -fx-font-weight: bold;
    -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.30), 12, 0, 0, 3);
}
.notificacion-exito { -fx-background-color: #2f9e63; }
.notificacion-error { -fx-background-color: #d64545; }
.notificacion-info  { -fx-background-color: #0e8a83; }

/* Diálogos (Alert) */
.dialog-pane {
    -fx-background-color: -vet-superficie;
}
.dialog-pane > .header-panel {
    -fx-background-color: -vet-primario-suave;
}
.dialog-pane > .header-panel .label {
    -fx-text-fill: -vet-primario-oscuro;
    -fx-font-weight: bold;
}
.dialog-pane > .button-bar .button {
    -fx-background-radius: 8;
    -fx-padding: 7 16 7 16;
}
.dialog-pane > .button-bar .button:default {
    -fx-background-color: -vet-primario;
    -fx-text-fill: white;
    -fx-font-weight: bold;
}
```

### 16.2 Un tema = solo cambiar colores

Los otros temas (`tema-oscuro.css`, `tema-azul.css`, `tema-verde.css`) **solo redefinen la paleta**. Por ejemplo, el azul:

`src/main/resources/css/tema-azul.css`

```css
/* Tema Azul: misma estructura, paleta azul océano */
.root {
    -vet-primario: #2563a8;
    -vet-primario-oscuro: #183f6e;
    -vet-primario-suave: #dce8f7;
    -vet-acento: #f29d52;
    -vet-fondo: #eef3f9;
    -vet-superficie-2: #f5f8fc;
    -vet-borde: #d8e2ee;
    -vet-texto: #1c2733;
    -vet-texto-suave: #5b6b7d;
    -vet-sombra: rgba(20, 50, 90, 0.10);
    -fx-base: #e8eef6;
}
```

### 16.3 Idiomas

Los textos no están escritos en el código: salen de `language/idiomas-es.properties`, `-en` y `-fr` (UTF-8). En el FXML se usan como `%clave` y en Java como `Idioma.t("clave")`. Ejemplo de algunas claves:

```properties
menu.inicio=Inicio
menu.recepcion=Recepción
opcion.mascotas=Mascotas
sub.mascotas=El padrón de pacientes peludos
comun.guardar=Guardar
```

---

## 17. Paso 16 — Arranque de la aplicación

### 17.1 `Main.java` — punto de entrada

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/Main.java`

```java
package pe.edu.upeu.padronmascota.padronmascota;

import javafx.application.Application;

/** Dale Run a esta clase para abrir el sistema. */
public class Main {

    public static void main(String[] args) {
        Application.launch(PadronMascotaApp.class, args);
    }
}
```

`Main` existe aparte de `PadronMascotaApp` para que el `.exe` pueda arrancar JavaFX desde una clase que **no** hereda de `Application`.

### 17.2 `PadronMascotaApp.java` — ciclo de vida JavaFX

`src/main/java/pe/edu/upeu/padronmascota/padronmascota/PadronMascotaApp.java`

```java
package pe.edu.upeu.padronmascota.padronmascota;

import pe.edu.upeu.padronmascota.padronmascota.componentes.GestorVentana;
import pe.edu.upeu.padronmascota.padronmascota.config.DatabaseConfig;
import pe.edu.upeu.padronmascota.padronmascota.controlador.LoginController;
import javafx.application.Application;
import javafx.stage.Stage;

/** La aplicación JavaFX: abre primero el login. */
public class PadronMascotaApp extends Application {

    @Override
    public void start(Stage stage) {
        GestorVentana.setStage(stage);
        // Ícono de la ventana y de la barra de tareas (huellita)
        var icono = PadronMascotaApp.class.getResourceAsStream("/icono.png");
        if (icono != null) {
            stage.getIcons().add(new javafx.scene.image.Image(icono));
        }
        LoginController.mostrarLogin();   // primero el login
        stage.show();
    }

    @Override
    public void stop() {
        DatabaseConfig.shutdown(); // cierra las conexiones al salir
    }
}
```

- `start()` → pone el ícono, abre el login y muestra la ventana.
- `stop()` → al cerrar, libera las conexiones de la base de datos (`DatabaseConfig.shutdown()`).

---

## 18. Paso 17 — Ejecutar el proyecto y crear el `.exe`

### 18.1 Desde IntelliJ

Ejecuta la clase **`Main`** (botón ▶ con la configuración *Main*). La primera vez se crea `data/padron_mascota_pro.mv.db` con un único usuario: **`admin` / `admin123`**.

También desde la consola, en la carpeta del proyecto:

```bash
mvnw.cmd javafx:run
```

### 18.2 Crear el `.exe` (Windows)

Doble clic en **`crear-exe.bat`**. El script:

1. Busca un JDK 21 (el de IntelliJ en `%USERPROFILE%\.jdks`, `JAVA_HOME` o `Program Files`).
2. Compila con el Maven Wrapper y junta las librerías en `target\app`.
3. Ejecuta **`jpackage`**, que crea `dist\PadronMascota\PadronMascota.exe` **con su propio Java incluido**.
4. Copia `data\` y `language\` junto al `.exe`.

`crear-exe.bat`

```bat
@echo off
chcp 65001 >nul
setlocal
cd /d "%~dp0"

echo ============================================================
echo   Padron Mascota - crear el programa .exe para Windows
echo ============================================================
echo.

rem --- 1. Buscar un JDK 21 (trae la herramienta jpackage) ---
rem     Orden: JAVA_HOME, los JDK que descarga IntelliJ (%USERPROFILE%\.jdks), Program Files
set "JDK="
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\jpackage.exe" set "JDK=%JAVA_HOME%"
if not defined JDK for /d %%D in ("%USERPROFILE%\.jdks\*21*") do if exist "%%D\bin\jpackage.exe" set "JDK=%%D"
if not defined JDK for /d %%D in ("%ProgramFiles%\Java\jdk-21*") do if exist "%%D\bin\jpackage.exe" set "JDK=%%D"
if not defined JDK for /d %%D in ("%ProgramFiles%\Eclipse Adoptium\jdk-21*") do if exist "%%D\bin\jpackage.exe" set "JDK=%%D"
if not defined JDK (
    echo [ERROR] No se encontro un JDK 21.
    echo En IntelliJ mira la carpeta en: File ^> Project Structure ^> SDK
    echo y ejecuta antes:  set JAVA_HOME=esa_carpeta
    pause
    exit /b 1
)
set "JAVA_HOME=%JDK%"
set "PATH=%JDK%\bin;%PATH%"
echo Usando el JDK: %JDK%
echo.

rem --- 2. Compilar y juntar las librerias (Maven se descarga solo) ---
echo [1/3] Compilando el proyecto...
call mvnw.cmd -q -B clean package dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=target\app
if errorlevel 1 (
    echo [ERROR] No se pudo compilar. Revisa los mensajes de arriba.
    pause
    exit /b 1
)
copy /y target\PadronMascota-1.0.jar target\app\ >nul

rem --- 3. Crear el programa con su propio Java incluido ---
echo [2/3] Creando PadronMascota.exe ^(tarda un par de minutos^)...
if exist dist rmdir /s /q dist
jpackage --type app-image ^
  --name PadronMascota ^
  --app-version 1.0 ^
  --vendor "Padron Mascota" ^
  --icon instalador\icono.ico ^
  --input target\app ^
  --main-jar PadronMascota-1.0.jar ^
  --main-class pe.edu.upeu.padronmascota.padronmascota.Main ^
  --java-options "-Dfile.encoding=UTF-8" ^
  --dest dist
if errorlevel 1 (
    echo [ERROR] jpackage fallo. Revisa los mensajes de arriba.
    pause
    exit /b 1
)

rem --- 4. Copiar la base de datos y los idiomas junto al .exe ---
echo [3/3] Copiando base de datos e idiomas...
xcopy /e /i /y data dist\PadronMascota\data >nul
xcopy /e /i /y language dist\PadronMascota\language >nul

echo.
echo ============================================================
echo   LISTO
echo   Programa: dist\PadronMascota\PadronMascota.exe
echo   Para llevarlo a otra PC copia TODA la carpeta dist\PadronMascota
echo   (la otra PC no necesita tener Java instalado).
echo ============================================================
explorer dist\PadronMascota
pause
```

Para llevarlo a otra computadora se copia **toda** la carpeta `dist\PadronMascota` (la otra PC no necesita Java). Si Windows muestra *"Windows protegió su PC"*: *Más información → Ejecutar de todas formas* (el programa no tiene firma digital).

---

## 19. Paso 18 — Recorrido funcional: "Guardar" en Mascotas

Así colaboran todas las capas cuando se registra una mascota:

1. El usuario llena el formulario y pulsa **Guardar** → JavaFX ejecuta `CrudController.guardar()` (`onAction="#guardar"`).
2. `guardar()` llama a `MascotaController.leerFormulario()`: arma un objeto `Mascota` con lo escrito. Si el dueño es nuevo, primero lo registra con `IPropietarioServicio`; si la raza o el color son nuevos, se agregan al catálogo.
3. Si no se está editando ninguna fila (`enEdicion == null`), el controlador llama a `servicio().registrar(m)`; si se está editando, a `servicio().modificar(id, m)` (Controlador → Servicio, por interfaz).
4. `ServicioCrudBase.registrar()` ejecuta `Validador.validar(m)`: Hibernate Validator revisa `@NotBlank`, `@Min`, `@Max`... de `Mascota`. Si algo falla, lanza `ValidacionException` con un mensaje por campo.
5. Después ejecuta `validarNegocio(m)`: las reglas que necesitan la base de datos. `MascotaServicioImpl` no la necesita, pero `PropietarioServicioImpl` la usa para rechazar un DNI repetido.
6. Si todo está bien, llama a `MascotaRepositorio.guardar(m)` (Servicio → Repositorio).
7. El repositorio ejecuta el `INSERT` con `RepositorioBase.insertar()` (JDBC + HikariCP + H2) y la base devuelve el `id` generado (IDENTITY).
8. De vuelta en el controlador: se muestra una `Notificacion` verde, se limpia el formulario y se vuelve a buscar, y la tabla se actualiza.
9. Si hubo `ValidacionException`, el controlador pinta en rojo los campos con error (`MarcadorError`) y muestra el primer mensaje.

Este mismo recorrido, con las mismas capas, se repite en **todas** las pantallas.

---

## 20. Anexo A — Resto de módulos del sistema

Cada módulo tiene sus 5 piezas: **modelo → repositorio → servicio (interfaz + impl) → controlador → vista**. El menú sigue el recorrido de un paciente en la veterinaria:

| Menú | Pantalla | Modelo | Repositorio / Servicio | Controlador / Vista | Reglas importantes |
|---|---|---|---|---|---|
| Inicio | Resumen | `ResumenPadron` (DTO) | `ResumenRepositorio` / `IReporteServicio` | `ResumenController` / `resumen.fxml` | Tarjetas con los totales y dos gráficos. |
| Recepción | Propietarios | `Propietario` | `PropietarioRepositorio` / `IPropietarioServicio` | `PropietarioController` / `propietarios.fxml` | DNI de 8 dígitos y único. Con ventas o atenciones se da de baja en vez de borrar. |
| Recepción | Mascotas | `Mascota` | `MascotaRepositorio` / `IMascotaServicio` | `MascotaController` / `mascotas.fxml` | Primero el dueño. Baja/Alta lógica. Raza y color nuevos se agregan solos. |
| Recepción | Citas | `Cita` | `CitaRepositorio` / `ICitaServicio` | `CitaController` / `citas.fxml` | Un veterinario no puede tener dos citas a la misma hora. |
| Clínica | Atenciones | `Atencion` + `AtencionInsumo` | `AtencionRepositorio` / `IAtencionServicio` | `AtencionController` / `atenciones.fxml` | Transacción: guarda la atención, descuenta los insumos del stock y marca la cita como Atendida. El precio sale del Servicio elegido. |
| Clínica | Vacunaciones | `Vacunacion`, `Vacuna` | `VacunacionRepositorio` / `IVacunacionServicio` | `VacunacionController` / `vacunaciones.fxml` | Descuenta 1 del stock. Refuerzo Vigente/Vencido. Imprime el carnet. |
| Clínica | Recetas | `Receta` + `DetalleReceta` | `RecetaRepositorio` / `IRecetaServicio` | `RecetaController` / `recetas.fxml` | No descuenta stock. Se imprime para el dueño. |
| Caja | Ventas y pagos | `Venta` + `DetalleVenta`, `ItemVenta` | `VentaRepositorio` / `IVentaServicio` | `VentaController` / `ventas.fxml` | Carga sola lo que el cliente debe (consultas y vacunas de los últimos 30 días). Boleta o Factura (RUC). Anular en vez de borrar. |
| Caja | Reporte de ventas | `FilaReporte` (DTO) | `ReporteRepositorio` / `IReporteServicio` | `ReporteVentasController` / `reporte_ventas.fxml` | Solo cuenta ventas Pagadas. Por año, mes, día y rango. |
| Inventario | Productos | `Producto` | `ProductoRepositorio` / `IProductoServicio` | `ProductoController` / `productos.fxml` | Stock = comprado − vendido − usado (vista `v_stock_producto`). Margen = precio − último costo. |
| Inventario | Servicios | `ServicioVeterinario` | `ServicioVeterinarioRepositorio` / `IServicioVeterinarioServicio` | `ServicioVeterinarioController` / `servicios.fxml` | Baño, consulta, operación... con su precio, sin stock. |
| Inventario | Compras | `Compra` + `DetalleCompra` | `CompraRepositorio` / `ICompraServicio` | `CompraController` / `compras.fxml` | Sube el stock. Lote y vencimiento por línea. Un producto nuevo exige precio de venta. |
| Inventario | Proveedores | `Proveedor` | `ProveedorRepositorio` / `IProveedorServicio` | `ProveedorController` / `proveedores.fxml` | RUC de 11 dígitos y único. |
| Configuración | Veterinarios | `Veterinario` | `VeterinarioRepositorio` / `IVeterinarioServicio` | `VeterinarioController` / `veterinarios.fxml` | Hereda de `Persona`. |
| Configuración | Usuarios | `Usuario` | `UsuarioRepositorio` / `IUsuarioServicio` | `UsuarioController` / `usuarios.fxml` | Clave con BCrypt. Rol Veterinario exige un veterinario. Siempre queda un administrador activo. |
| Configuración | Razas / Colores | `Raza`, `Color` | `RazaRepositorio`, `ColorRepositorio` | `RazaController`, `ColorController` | Catálogos que usa Mascotas. |

**Roles:** `ADMIN` ve todo. `VETERINARIO` ve Inicio, Recepción, Clínica, Razas y Colores. `RECEPCION` ve Inicio, Recepción, Caja, Productos y Servicios. Lo decide `MenuServicioImpl` (sección 10.6).

**Impresión:** `componentes/Impresion` arma una hoja (receta, carnet de vacunas, comprobante) y la manda a la impresora con `PrinterJob`. Para guardarla en PDF se elige *Microsoft Print to PDF*.

---

## 21. Anexo B — Receta para agregar una pantalla nueva

Ejemplo: un catálogo **Alergias**.

1. **Tabla**: en `db/schema.sql`, `CREATE TABLE IF NOT EXISTS alergia (id_alergia INT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(60) NOT NULL UNIQUE);`
2. **Modelo**: `modelo/Alergia extends Entidad`, con `@NotBlank` en `nombre`.
3. **Repositorio**: `repositorio/AlergiaRepositorio extends RepositorioBase<Alergia> implements IRepositorio<Alergia>`. Implementa `mapear()` y los 5 métodos con SQL.
4. **Servicio**: `servicio/IAlergiaServicio extends IServicioCrud<Alergia>` + `servicio/impl/AlergiaServicioImpl extends ServicioCrudBase<Alergia>` (solo `repositorio()` y, si hace falta, `validarNegocio()`).
5. **Controlador**: `controlador/AlergiaController extends CrudController<Alergia>`: columnas, `leerFormulario`, `llenarFormulario`, `limpiarFormulario`, `campos`.
6. **Vista**: copia `colores.fxml` como `alergias.fxml` y cambia `fx:controller` y los `fx:id`.
7. **ContenedorApp**: registra el repositorio, el servicio y la fábrica del controlador.
8. **Menú**: una línea en `MenuServicioImpl`, por ejemplo `new OpcionMenu("menu.configuracion", "opcion.alergias", "alergias.fxml", ADMIN, VETERINARIO)`.
9. **Textos**: `opcion.alergias` y `sub.alergias` en los 3 archivos de `language/`.

Gracias a las clases base, la pantalla nueva tiene buscar, guardar, editar, eliminar, validación en rojo y avisos **sin escribirlos otra vez**.

---

## 22. Anexo C — Limitaciones actuales y mejoras sugeridas

1. **Base en un archivo local.** H2 embebida es ideal para una sola computadora. Para varias cajas en red habría que pasar a un servidor (PostgreSQL o MySQL). Como todo el SQL está en los repositorios, el cambio queda dentro del paquete `repositorio` y de `DatabaseConfig`.
2. **Un solo programa abre la base a la vez.** Para ver los datos en DBeaver hay que cerrar el sistema (o desconectar DBeaver antes de abrirlo).
3. **Las contraseñas de la base están en `application.properties`.** Está bien para un proyecto de clase; en producción irían en una variable de entorno.
4. **El `.exe` no tiene firma digital**, por eso Windows muestra un aviso la primera vez.
5. **No hay pruebas automáticas (JUnit).** Una mejora natural es probar los servicios: validaciones, stock y totales de venta.
6. **Precio menor que el costo.** El sistema muestra el margen negativo, pero todavía no avisa al guardar un producto con precio de venta menor que su costo.

---

## 23. Glosario

| Término | Significado |
|---|---|
| **POO** | Programación Orientada a Objetos. |
| **CRUD** | Crear, leer, actualizar y eliminar (*Create, Read, Update, Delete*). |
| **DI (Inyección de dependencias)** | Un objeto recibe ya armados los objetos que necesita (aquí, por constructor) en vez de crearlos con `new`. |
| **Singleton** | Una sola instancia de una clase en toda la aplicación (`ContenedorApp`). |
| **Template Method** | La clase padre define los pasos de un algoritmo y las hijas completan los pasos específicos. |
| **Repository** | Clase que esconde el acceso a datos (el SQL) detrás de métodos como `guardar` o `listar`. |
| **DTO** | Objeto que solo transporta datos entre capas, sin lógica de negocio. |
| **JDBC** | La API estándar de Java para ejecutar SQL (`Connection`, `PreparedStatement`, `ResultSet`). |
| **H2** | Base de datos escrita en Java que puede guardarse en un archivo dentro del proyecto. |
| **HikariCP** | Pool de conexiones: reutiliza conexiones abiertas para que las consultas sean rápidas. |
| **Transacción** | Varias operaciones que se guardan todas juntas (commit) o ninguna (rollback). |
| **Bean Validation** | Validar objetos con anotaciones (`@NotBlank`, `@Pattern`, `@Min`), implementado por Hibernate Validator. |
| **BCrypt** | Algoritmo para guardar contraseñas como hash, con una "sal" aleatoria. |
| **FXML** | Archivo XML de JavaFX que describe una pantalla, separado del código Java. |
| **`@FXML`** | Le indica al `FXMLLoader` que llene ese campo con el control del FXML o que llame a ese método ante un evento. |
| **JPMS (`module-info.java`)** | Sistema de módulos de Java: qué se usa (`requires`), qué se expone (`exports`) y qué se abre a la reflexión (`opens`). |
| **jpackage** | Herramienta del JDK que crea un programa nativo (`.exe`) con su propio Java incluido. |

---

*Fin del manual. Sigue los pasos en orden para construir Padrón Mascota desde cero; usa los Anexos A-C para entender el resto de módulos y extenderlo con la misma arquitectura.*
