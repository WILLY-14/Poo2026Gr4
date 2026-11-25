# ElectroVentas – Requerimientos y Manual de Usuario

## 1. Lista de requerimientos
| Cód. | Requerimiento |
|------|---------------|
| RF01 | Iniciar sesión con usuario y clave (clave cifrada con SHA-256). |
| RF02 | Mantenimiento de **Marcas** y **Categorías** (no se permiten nombres repetidos). |
| RF03 | Mantenimiento de **Productos** (marca, categoría, unidad, precio, stock). |
| RF04 | Mantenimiento de **Clientes** (DNI/RUC, nombres, dirección). |
| RF05 | **Registro de venta**: carrito por cliente, un solo renglón por producto, control de stock en tiempo real. |
| RF06 | Generar comprobante (boleta) y dejar el formulario limpio para la siguiente venta. |
| RF07 | Menús y funcionalidades según el **rol/perfil** del usuario. |
| RNF01 | Arquitectura por capas: Repositorio → Servicio → Controlador (JavaFX). |
| RNF02 | Validación de campos con mensajes de la acción realizada. |

## 2. Ingreso al sistema
1. Escriba **usuario** y **clave** y pulse **Ingresar** (o Enter).
2. Si falta algún dato o las credenciales son incorrectas, aparece un mensaje.
3. El menú superior mostrará solo las opciones del perfil del usuario (Administrador / Reporte).

## 3. Catálogo (Marcas y Categorías) – Administrador
1. Escriba el nombre y pulse **Guardar**. Si ya existe (aunque cambie mayúsculas/minúsculas) se muestra un aviso y no se guarda.
2. Para editar, seleccione la fila, cambie el nombre y pulse **Guardar**.
3. **Nuevo** limpia el campo. **Eliminar** borra la fila seleccionada (no se puede si algún producto la usa).

## 4. Registro de venta
1. **Cliente:** escriba el DNI/RUC (o nombre) y elíjalo de la lista; si no existe, pulse **Buscar** y luego **Add** para registrarlo.
2. **Producto:** busque por nombre, escriba la **cantidad** (el P. Total se calcula solo) y pulse **Agregar**.
3. Si agrega el mismo producto otra vez, la cantidad se **suma en la misma fila**.
4. El campo **Stock** muestra lo disponible (stock menos lo ya agregado al carrito). No se puede superar el stock.
5. Pulse **Registrar venta**: se descuenta el stock, se muestra la boleta y el formulario (cliente, producto y totales) queda **limpio**.

## 5. Mejoras adicionales (investigación)
- Cifrado de claves con SHA-256 usando el usuario como sal.
- Control de stock en línea y validaciones con mensajes emergentes (Toast).
