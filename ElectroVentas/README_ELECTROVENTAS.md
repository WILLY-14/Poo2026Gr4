# ELECTROVENTAS — Sistema de ventas de productos electrónicos

Proyecto académico basado en **SysVentas** (Java 21, JavaFX, H2, Maven).

## Qué se completó en esta versión
- CRUD JavaFX de **clientes**, con filtro, validación DNI/RUC, selección de filas, mensajes, confirmación antes de borrar y corrección SQL para actualizar dirección.
- CRUD JavaFX de **marcas y categorías**, integrado al menú de producto.
- Inyección manual de dependencias en `AppContext` conectando controladores → servicios → repositorios.
- Corrección del script de inicio de base de datos (`schema_sv.sql` en lugar del script que únicamente agregaba usuarios).
- Guía de evaluación y reparto del trabajo entre seis integrantes en `docs/GUIA_EVALUACION_6_INTEGRANTES.md`.

## Ejecutar en Windows con IntelliJ
1. Instalar **JDK 21** y **Maven**. Abrir la carpeta `SysVentas` como proyecto Maven en IntelliJ IDEA.
2. Permitir descargar dependencias Maven desde internet.
3. Ejecutar con `mvn clean javafx:run` en el directorio `SysVentas` (o usar la configuración Maven equivalente del IDE).
4. H2 usa el archivo `./data/sysventas` relativo a la carpeta desde donde se ejecuta. La base proporcionada ya contiene datos del proyecto anterior.
5. Revisar el usuario de prueba en `schema_sv.sql` si usan base limpia. **No usar las contraseñas de ejemplo en un sistema real**.
6. En el menú, acceder a **Producto → Marcas y categorías**, **Producto → Gestión Productos**, **Venta → Reg. Cliente**, **Venta → Reg. Venta**, según el perfil.

## Nota técnica y limitaciones
- En este entorno no fue posible compilar ni ejecutar la interfaz JavaFX: Maven Wrapper necesita descargar Maven desde internet y el entorno no pudo acceder a `repo.maven.apache.org`. Los FXML nuevos fueron verificados como XML, pero hace falta prueba real en Windows.
- Los módulos preexistentes de ventas, autenticación y comprobantes **no han sido sometidos a prueba integral**.
- El modelo nativo **SQL Power Architect no está incluido**: crear/exportar el diagrama desde el esquema `schema_sv.sql` antes de entregar.
- La configuración de usuarios de demostración usa contraseñas sin hash; **esto debe corregirse antes de producción** y no se debe afirmar que existe cifrado seguro.
- Se conservaron datos de demostración del ZIP para facilitar la continuación del proyecto.
