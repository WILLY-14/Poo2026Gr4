# Sustentación — Sistema de ventas de electrónicos (6 integrantes)

## Caso de negocio
Una tienda comercializa laptops, televisores, celulares, accesorios y otros productos electrónicos. Necesita registrar su catálogo (categoría, marca, producto, unidad), clientes y operaciones de venta, almacenar los movimientos en H2 y emitir comprobantes de venta. El sistema de escritorio fue construido con JavaFX.

## Requerimientos funcionales
RF01: registrar, listar, modificar y eliminar marcas.
RF02: registrar, listar, modificar y eliminar categorías.
RF03: registrar, consultar, modificar y eliminar productos con precios y stock.
RF04: registrar, buscar, modificar y eliminar clientes con validación de documentos.
RF05: registrar ventas con cliente, artículos, cantidades y total.
RF06: emitir un comprobante en PDF cuando el módulo existente lo permita.
RF07: autenticar usuarios y mostrar las funcionalidades según su perfil.
RF08: mostrar confirmación y errores durante operaciones CRUD.

## Requerimientos no funcionales
RNF01: Java 21, JavaFX y estructura en capas.
RNF02: H2 con tablas y claves foráneas.
RNF03: consultas SQL parametrizadas para mitigar inyección SQL.
RNF04: control de errores sin perder silenciosamente las operaciones.
RNF05: usar GitHub con aportes y commits identificables de cada integrante.

## Cómo explicar las tres capas
**Presentación**: los `*Controller` responden a los botones y leen los formularios JavaFX. **Servicio**: `IClienteService`, `IMarcaService`, `ICategoriaService` y demás orquestan las operaciones. **Repositorio**: `ClienteRepository`, `MarcaRepository`, `CategoriaRepository` hablan con la base H2 mediante SQL. Ejemplo de flujo: botón Guardar → `ClienteController.guardar()` → `IClienteService.save()` → `ClienteRepository.insert()` → H2.

## Reparto sugerido para seis integrantes
| Persona | Responsabilidad defendible | Demostración en vivo | Pregunta posible y respuesta |
|---|---|---|---|
| 1 | CRUD productos y stock | Crear y editar producto electrónico | ¿Qué es CRUD? Crear, leer, actualizar y eliminar información. |
| 2 | CRUD clientes | Ingresar cliente, buscarlo y editar dirección | ¿Por qué validar? Evita datos incompletos y documentos inconsistentes. |
| 3 | CRUD marcas | Crear una marca y modificarla | ¿Qué hace el servicio? Separa la lógica de negocio del controlador y repositorio. |
| 4 | CRUD categorías | Crear categoría y consultar su lista | ¿Para qué sirven claves foráneas? Mantienen coherencia entre productos y categorías. |
| 5 | Ventas y comprobantes | Crear una venta de prueba y mostrar el comprobante | ¿Cómo se registra una venta? Se almacena cabecera y detalle; se debe controlar la transacción. |
| 6 | Base H2, login, arquitectura y GitHub | Mostrar esquema, proyecto y commits | ¿Qué son las tres capas? Presentación, lógica de negocio y acceso a datos. |

**Importante:** el reparto es una **propuesta**; cada integrante debe comprobar que entiende y puede defender el código concreto que le corresponda. El PDF del docente exige que **cada uno realice** un CRUD o funcionalidad mayor; no basta memorizar preguntas.

## Lista de verificación según rúbrica
- [x] Código separado en controladores, servicios y repositorios (estructura existente).
- [x] H2 con esquema SQL y relación de datos.
- [x] JavaFX con nuevas pantallas de clientes y catálogos.
- [x] CRUD cliente, marca y categoría implementado en código.
- [x] Validación y feedback visual en los módulos nuevos.
- [ ] Compilación y prueba de extremo a extremo en PC con Maven e internet.
- [ ] Exportación del modelo a SQL Power Architect y captura del diagrama.
- [ ] Investigación adicional *realmente implementada y demostrada* (p.ej., paginación o hash de contraseñas).
- [ ] Revisar seguridad de contraseñas existentes y credenciales de demo.
- [ ] Pruebas del registro de venta, stock, PDF y restricciones en base.
- [ ] Commits individuales en GitHub y acceso al profesor `dmamanipar` si el repositorio es privado.

## Pruebas que deben hacer antes de sustentar
1. Registrar marca "Lenovo" y categoría "Laptops"; confirmar que aparecen al registrar un producto.
2. Crear producto "Laptop Lenovo ThinkPad", stock 5, precio positivo, y verificarlo en su tabla.
3. Intentar registrar un cliente con DNI de 7 dígitos: debe salir validación; corregir a 8, guardar, cerrar y volver a abrir.
4. Cambiar la dirección del cliente: debe persistir tras reiniciar.
5. Intentar borrar una marca usada por un producto: debe mostrar error controlado.
6. Registrar una venta real de prueba; verificar total, detalle, existencia y comprobante.
7. Confirmar que un usuario con permisos distintos solo ve las opciones asignadas.
8. Mostrar el código SQL y el diagrama en SQL Power Architect.

## Preguntas rápidas
**¿Qué es JavaFX?** Una biblioteca para crear interfaces gráficas de escritorio en Java.
**¿Qué es H2?** Motor relacional embebido usado para guardar los registros localmente.
**¿Qué es un repositorio?** Clase que centraliza la persistencia y consultas a la BD.
**¿Por qué no SQL dentro del controlador?** Para separar responsabilidades y facilitar mantenimiento/pruebas.
**¿Qué es una llave primaria?** Identificador único de cada registro.
**¿Qué es una transacción?** Conjunto de operaciones que se confirman o revierten juntas para mantener consistencia.
**¿Qué diferencia hay entre autenticar y autorizar?** Autenticar comprueba identidad; autorizar decide permisos.
