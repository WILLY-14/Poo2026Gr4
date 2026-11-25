-- Datos de ejemplo de padron_mascota_pro.xlsx (se cargan solo si la base está vacía)

-- color
INSERT INTO color (id_color, nombre) VALUES (1, 'Marrón');
INSERT INTO color (id_color, nombre) VALUES (2, 'Blanco y gris');
INSERT INTO color (id_color, nombre) VALUES (3, 'Atigrado');
INSERT INTO color (id_color, nombre) VALUES (4, 'Canela');
INSERT INTO color (id_color, nombre) VALUES (5, 'Verde');
INSERT INTO color (id_color, nombre) VALUES (6, 'Negro');
INSERT INTO color (id_color, nombre) VALUES (7, 'Blanco');
INSERT INTO color (id_color, nombre) VALUES (8, 'Gris');
ALTER TABLE color ALTER COLUMN id_color RESTART WITH 9;

-- raza
INSERT INTO raza (id_raza, nombre, id_especie) VALUES (1, 'Labrador', 1);
INSERT INTO raza (id_raza, nombre, id_especie) VALUES (2, 'Siamés', 2);
INSERT INTO raza (id_raza, nombre, id_especie) VALUES (3, 'Pitbull', 1);
INSERT INTO raza (id_raza, nombre, id_especie) VALUES (4, 'Mestizo', 1);
INSERT INTO raza (id_raza, nombre, id_especie) VALUES (5, 'Periquito', 3);
INSERT INTO raza (id_raza, nombre, id_especie) VALUES (6, 'Persa', 2);
INSERT INTO raza (id_raza, nombre, id_especie) VALUES (7, 'Pastor alemán', 1);
ALTER TABLE raza ALTER COLUMN id_raza RESTART WITH 8;

-- propietario
INSERT INTO propietario (id_propietario, dni, nombre, telefono, direccion, email) VALUES (1, '45879632', 'María Quispe Huamán', '959123456', 'Av. El Sol 123, Cusco', 'maria.quispe@example.com');
INSERT INTO propietario (id_propietario, dni, nombre, telefono, direccion, email) VALUES (2, '41236587', 'Jorge Mamani Ccori', '984555210', 'Jr. Puno 456, Cusco', 'jorge.mamani@example.com');
INSERT INTO propietario (id_propietario, dni, nombre, telefono, direccion, email) VALUES (3, '47851236', 'Rosa Flores Medina', '951887744', 'Calle Tullumayo 789, Cusco', 'rosa.flores@example.com');
ALTER TABLE propietario ALTER COLUMN id_propietario RESTART WITH 4;

-- veterinario
INSERT INTO veterinario (id_veterinario, nombre, telefono) VALUES (1, 'Luis Condori Apaza', '974321654');
INSERT INTO veterinario (id_veterinario, nombre, telefono) VALUES (2, 'Carmen Torres Huillca', '958741236');
ALTER TABLE veterinario ALTER COLUMN id_veterinario RESTART WITH 3;

-- mascota
INSERT INTO mascota (id_mascota, nombre, id_especie, id_raza, id_color, sexo, edad_aprox, fecha_registro, id_propietario, id_estado, activo, observaciones) VALUES (1, 'Rocky', 1, 1, 1, 'Macho', 3, DATE '2025-06-15', 1, 1, TRUE, NULL);
INSERT INTO mascota (id_mascota, nombre, id_especie, id_raza, id_color, sexo, edad_aprox, fecha_registro, id_propietario, id_estado, activo, observaciones) VALUES (2, 'Misha', 2, 2, 2, 'Hembra', 2, DATE '2025-06-15', 1, 1, TRUE, NULL);
INSERT INTO mascota (id_mascota, nombre, id_especie, id_raza, id_color, sexo, edad_aprox, fecha_registro, id_propietario, id_estado, activo, observaciones) VALUES (3, 'Thor', 1, 3, 3, 'Macho', 4, DATE '2025-07-02', 2, 2, TRUE, 'Alergia en la piel');
INSERT INTO mascota (id_mascota, nombre, id_especie, id_raza, id_color, sexo, edad_aprox, fecha_registro, id_propietario, id_estado, activo, observaciones) VALUES (4, 'Canela', 1, 4, 4, 'Hembra', 6, DATE '2025-09-10', 3, 1, TRUE, NULL);
INSERT INTO mascota (id_mascota, nombre, id_especie, id_raza, id_color, sexo, edad_aprox, fecha_registro, id_propietario, id_estado, activo, observaciones) VALUES (5, 'Kiwi', 3, 5, 5, 'Macho', 1, DATE '2025-11-12', 3, 2, TRUE, 'Ave: revisar plumaje');
ALTER TABLE mascota ALTER COLUMN id_mascota RESTART WITH 6;

-- usuario (claves: recepcion/recepcion123, lcondori/vet123, ctorres/vet123)
INSERT INTO usuario (id_usuario, username, clave, id_rol, id_veterinario, estado) VALUES (2, 'recepcion', '$2a$10$n7kYr69b90U8uM8MgOXZwermlDgfFEhbGiIaBC/dMNcB6ejvu8Nhy', 2, NULL, 'Activo');
INSERT INTO usuario (id_usuario, username, clave, id_rol, id_veterinario, estado) VALUES (3, 'lcondori', '$2a$10$z.1jyX2UkkfGAuUeKpUsYuRt3FF/dTKHIeu4YzcX20niBaNSrkqR.', 3, 1, 'Activo');
INSERT INTO usuario (id_usuario, username, clave, id_rol, id_veterinario, estado) VALUES (4, 'ctorres', '$2a$10$z.1jyX2UkkfGAuUeKpUsYuRt3FF/dTKHIeu4YzcX20niBaNSrkqR.', 3, 2, 'Activo');
ALTER TABLE usuario ALTER COLUMN id_usuario RESTART WITH 5;

-- producto
INSERT INTO producto (id_producto, nombre, id_categoria, precio, stock_minimo) VALUES (1, 'Croquetas perro 3kg', 1, 45, 5);
INSERT INTO producto (id_producto, nombre, id_categoria, precio, stock_minimo) VALUES (2, 'Collar mediano', 2, 18, 3);
INSERT INTO producto (id_producto, nombre, id_categoria, precio, stock_minimo) VALUES (3, 'Arena para gato 5kg', 2, 25, 3);
INSERT INTO producto (id_producto, nombre, id_categoria, precio, stock_minimo) VALUES (4, 'Comida gato 1.5kg', 1, 32, 4);
INSERT INTO producto (id_producto, nombre, id_categoria, precio, stock_minimo) VALUES (5, 'Pipeta antipulgas', 3, 22, 5);
INSERT INTO producto (id_producto, nombre, id_categoria, precio, stock_minimo) VALUES (6, 'Desparasitante tableta', 3, 12, 5);
INSERT INTO producto (id_producto, nombre, id_categoria, precio, stock_minimo) VALUES (7, 'Alpiste 1kg', 1, 8, 5);
INSERT INTO producto (id_producto, nombre, id_categoria, precio, stock_minimo) VALUES (8, 'Vacuna antirrábica (dosis)', 4, 25, 5);
INSERT INTO producto (id_producto, nombre, id_categoria, precio, stock_minimo) VALUES (9, 'Vacuna séxtuple (dosis)', 4, 40, 3);
INSERT INTO producto (id_producto, nombre, id_categoria, precio, stock_minimo) VALUES (10, 'Vacuna triple felina (dosis)', 4, 35, 3);
INSERT INTO producto (id_producto, nombre, id_categoria, precio, stock_minimo) VALUES (11, 'Vacuna antirrábica felina (dosis)', 4, 25, 3);
INSERT INTO producto (id_producto, nombre, id_categoria, precio, stock_minimo) VALUES (12, 'Jeringa 3ml', 5, 1.5, 20);
INSERT INTO producto (id_producto, nombre, id_categoria, precio, stock_minimo) VALUES (13, 'Algodón 100g', 5, 4, 2);
INSERT INTO producto (id_producto, nombre, id_categoria, precio, stock_minimo) VALUES (14, 'Vitaminas para aves 30ml', 3, 15, 2);
ALTER TABLE producto ALTER COLUMN id_producto RESTART WITH 15;

-- servicio
INSERT INTO servicio (id_servicio, nombre, precio) VALUES (1, 'Consulta general', 30);
INSERT INTO servicio (id_servicio, nombre, precio) VALUES (2, 'Vacuna antirrábica', 25);
INSERT INTO servicio (id_servicio, nombre, precio) VALUES (3, 'Desparasitación', 20);
INSERT INTO servicio (id_servicio, nombre, precio) VALUES (4, 'Control general', 30);
INSERT INTO servicio (id_servicio, nombre, precio) VALUES (5, 'Vacuna triple felina', 35);
ALTER TABLE servicio ALTER COLUMN id_servicio RESTART WITH 6;

-- vacuna
INSERT INTO vacuna (id_vacuna, nombre, id_especie, id_producto) VALUES (1, 'Antirrábica', 1, 8);
INSERT INTO vacuna (id_vacuna, nombre, id_especie, id_producto) VALUES (2, 'Séxtuple canina', 1, 9);
INSERT INTO vacuna (id_vacuna, nombre, id_especie, id_producto) VALUES (3, 'Triple felina', 2, 10);
INSERT INTO vacuna (id_vacuna, nombre, id_especie, id_producto) VALUES (4, 'Antirrábica felina', 2, 11);
ALTER TABLE vacuna ALTER COLUMN id_vacuna RESTART WITH 5;

-- vacunacion
INSERT INTO vacunacion (id_vacunacion, id_mascota, id_vacuna, fecha, proxima_dosis) VALUES (1, 1, 1, DATE '2026-03-10', DATE '2027-03-10');
INSERT INTO vacunacion (id_vacunacion, id_mascota, id_vacuna, fecha, proxima_dosis) VALUES (2, 1, 2, DATE '2026-05-15', DATE '2027-05-15');
INSERT INTO vacunacion (id_vacunacion, id_mascota, id_vacuna, fecha, proxima_dosis) VALUES (3, 2, 3, DATE '2026-04-02', DATE '2027-04-02');
INSERT INTO vacunacion (id_vacunacion, id_mascota, id_vacuna, fecha, proxima_dosis) VALUES (4, 3, 1, DATE '2025-08-20', DATE '2026-08-20');
INSERT INTO vacunacion (id_vacunacion, id_mascota, id_vacuna, fecha, proxima_dosis) VALUES (5, 4, 1, DATE '2026-06-01', DATE '2027-06-01');
ALTER TABLE vacunacion ALTER COLUMN id_vacunacion RESTART WITH 6;

-- cita
INSERT INTO cita (id_cita, id_mascota, id_veterinario, fecha, hora, motivo, id_estado_cita) VALUES (1, 3, 1, DATE '2026-11-05', TIME '09:00:00', 'Vacuna antirrábica', 1);
INSERT INTO cita (id_cita, id_mascota, id_veterinario, fecha, hora, motivo, id_estado_cita) VALUES (2, 5, 2, DATE '2026-11-06', TIME '10:30:00', 'Control general', 1);
INSERT INTO cita (id_cita, id_mascota, id_veterinario, fecha, hora, motivo, id_estado_cita) VALUES (3, 2, 2, DATE '2026-04-02', TIME '16:00:00', 'Vacuna triple felina', 2);
ALTER TABLE cita ALTER COLUMN id_cita RESTART WITH 4;

-- atencion
INSERT INTO atencion (id_atencion, id_mascota, id_veterinario, fecha, motivo, id_cita) VALUES (1, 1, 1, DATE '2026-03-10', 'Vacunación antirrábica', NULL);
INSERT INTO atencion (id_atencion, id_mascota, id_veterinario, fecha, motivo, id_cita) VALUES (2, 2, 2, DATE '2026-04-02', 'Vacunación triple felina', 3);
INSERT INTO atencion (id_atencion, id_mascota, id_veterinario, fecha, motivo, id_cita) VALUES (3, 3, 1, DATE '2026-09-14', 'Control de piel', NULL);
INSERT INTO atencion (id_atencion, id_mascota, id_veterinario, fecha, motivo, id_cita) VALUES (4, 5, 2, DATE '2026-10-05', 'Revisión de plumaje', NULL);
ALTER TABLE atencion ALTER COLUMN id_atencion RESTART WITH 5;

-- atencion_insumo
INSERT INTO atencion_insumo (id_atencion_insumo, id_atencion, id_producto, cantidad) VALUES (1, 1, 12, 1);
INSERT INTO atencion_insumo (id_atencion_insumo, id_atencion, id_producto, cantidad) VALUES (2, 1, 13, 1);
INSERT INTO atencion_insumo (id_atencion_insumo, id_atencion, id_producto, cantidad) VALUES (3, 2, 12, 1);
INSERT INTO atencion_insumo (id_atencion_insumo, id_atencion, id_producto, cantidad) VALUES (4, 3, 5, 1);
INSERT INTO atencion_insumo (id_atencion_insumo, id_atencion, id_producto, cantidad) VALUES (5, 3, 13, 1);
INSERT INTO atencion_insumo (id_atencion_insumo, id_atencion, id_producto, cantidad) VALUES (6, 4, 13, 1);
ALTER TABLE atencion_insumo ALTER COLUMN id_atencion_insumo RESTART WITH 7;

-- receta
INSERT INTO receta (id_receta, id_atencion, fecha, indicaciones) VALUES (1, 3, DATE '2026-09-14', 'Bañar con champú medicado cada 7 días');
INSERT INTO receta (id_receta, id_atencion, fecha, indicaciones) VALUES (2, 4, DATE '2026-10-05', 'Mantener la jaula limpia y seca');
ALTER TABLE receta ALTER COLUMN id_receta RESTART WITH 3;

-- detalle_receta
INSERT INTO detalle_receta (id_detalle_receta, id_receta, id_producto, dosis, frecuencia, dias) VALUES (1, 1, 5, '1 pipeta', 'Cada 30 días', 90);
INSERT INTO detalle_receta (id_detalle_receta, id_receta, id_producto, dosis, frecuencia, dias) VALUES (2, 1, 6, '1 tableta', 'Dosis única', 1);
INSERT INTO detalle_receta (id_detalle_receta, id_receta, id_producto, dosis, frecuencia, dias) VALUES (3, 2, 14, '3 gotas en el agua', 'Diario', 14);
ALTER TABLE detalle_receta ALTER COLUMN id_detalle_receta RESTART WITH 4;

-- proveedor
INSERT INTO proveedor (id_proveedor, ruc, nombre, telefono) VALUES (1, '20481234567', 'Distribuidora PetFood Cusco SAC', '984123456');
INSERT INTO proveedor (id_proveedor, ruc, nombre, telefono) VALUES (2, '20509876543', 'VetFarma Andina EIRL', '974654321');
ALTER TABLE proveedor ALTER COLUMN id_proveedor RESTART WITH 3;

-- compra
INSERT INTO compra (id_compra, id_proveedor, fecha_hora, numero_factura, id_usuario, total) VALUES (1, 1, TIMESTAMP '2025-10-01 09:00:00', 'F001-004512', 1, 934);
INSERT INTO compra (id_compra, id_proveedor, fecha_hora, numero_factura, id_usuario, total) VALUES (2, 2, TIMESTAMP '2025-10-03 10:30:00', 'E001-000873', 1, 287);
INSERT INTO compra (id_compra, id_proveedor, fecha_hora, numero_factura, id_usuario, total) VALUES (3, 1, TIMESTAMP '2025-10-05 11:00:00', 'F001-004600', 1, 320);
INSERT INTO compra (id_compra, id_proveedor, fecha_hora, numero_factura, id_usuario, total) VALUES (4, 1, TIMESTAMP '2026-08-01 09:15:00', 'F001-005210', 1, 340);
INSERT INTO compra (id_compra, id_proveedor, fecha_hora, numero_factura, id_usuario, total) VALUES (5, 2, TIMESTAMP '2025-08-01 08:30:00', 'E001-000790', 1, 850);
ALTER TABLE compra ALTER COLUMN id_compra RESTART WITH 6;

-- detalle_compra
INSERT INTO detalle_compra (id_detalle_compra, id_compra, id_producto, cantidad, costo_unitario, lote, fecha_vencimiento, subtotal) VALUES (1, 1, 1, 20, 32, 'PF2509-11', DATE '2026-09-30', 640);
INSERT INTO detalle_compra (id_detalle_compra, id_compra, id_producto, cantidad, costo_unitario, lote, fecha_vencimiento, subtotal) VALUES (2, 1, 4, 12, 22, 'PF2509-14', DATE '2026-12-31', 264);
INSERT INTO detalle_compra (id_detalle_compra, id_compra, id_producto, cantidad, costo_unitario, lote, fecha_vencimiento, subtotal) VALUES (3, 1, 7, 6, 5, 'PF2509-20', DATE '2026-11-15', 30);
INSERT INTO detalle_compra (id_detalle_compra, id_compra, id_producto, cantidad, costo_unitario, lote, fecha_vencimiento, subtotal) VALUES (4, 2, 5, 8, 14, 'VF2510-03', DATE '2027-06-30', 112);
INSERT INTO detalle_compra (id_detalle_compra, id_compra, id_producto, cantidad, costo_unitario, lote, fecha_vencimiento, subtotal) VALUES (5, 2, 6, 25, 7, 'VF2510-07', DATE '2027-03-31', 175);
INSERT INTO detalle_compra (id_detalle_compra, id_compra, id_producto, cantidad, costo_unitario, lote, fecha_vencimiento, subtotal) VALUES (6, 3, 2, 15, 10, NULL, NULL, 150);
INSERT INTO detalle_compra (id_detalle_compra, id_compra, id_producto, cantidad, costo_unitario, lote, fecha_vencimiento, subtotal) VALUES (7, 3, 3, 10, 17, NULL, NULL, 170);
INSERT INTO detalle_compra (id_detalle_compra, id_compra, id_producto, cantidad, costo_unitario, lote, fecha_vencimiento, subtotal) VALUES (8, 4, 1, 10, 34, 'PF2607-02', DATE '2027-07-31', 340);
INSERT INTO detalle_compra (id_detalle_compra, id_compra, id_producto, cantidad, costo_unitario, lote, fecha_vencimiento, subtotal) VALUES (9, 5, 8, 20, 12, 'VF2507-A1', DATE '2026-12-15', 240);
INSERT INTO detalle_compra (id_detalle_compra, id_compra, id_producto, cantidad, costo_unitario, lote, fecha_vencimiento, subtotal) VALUES (10, 5, 9, 10, 20, 'VF2507-S2', DATE '2026-10-30', 200);
INSERT INTO detalle_compra (id_detalle_compra, id_compra, id_producto, cantidad, costo_unitario, lote, fecha_vencimiento, subtotal) VALUES (11, 5, 10, 10, 18, 'VF2507-T3', DATE '2027-01-31', 180);
INSERT INTO detalle_compra (id_detalle_compra, id_compra, id_producto, cantidad, costo_unitario, lote, fecha_vencimiento, subtotal) VALUES (12, 5, 11, 10, 12, 'VF2507-A4', DATE '2027-02-28', 120);
INSERT INTO detalle_compra (id_detalle_compra, id_compra, id_producto, cantidad, costo_unitario, lote, fecha_vencimiento, subtotal) VALUES (13, 5, 12, 100, 0.5, 'VF2507-J9', DATE '2029-07-31', 50);
INSERT INTO detalle_compra (id_detalle_compra, id_compra, id_producto, cantidad, costo_unitario, lote, fecha_vencimiento, subtotal) VALUES (14, 5, 13, 10, 2, NULL, NULL, 20);
INSERT INTO detalle_compra (id_detalle_compra, id_compra, id_producto, cantidad, costo_unitario, lote, fecha_vencimiento, subtotal) VALUES (15, 5, 14, 5, 8, 'VF2507-V5', DATE '2026-11-20', 40);
ALTER TABLE detalle_compra ALTER COLUMN id_detalle_compra RESTART WITH 16;

-- venta
INSERT INTO venta (id_venta, tipo_comprobante, numero_comprobante, id_propietario, id_atencion, fecha_hora, id_metodo_pago, id_usuario, estado, total) VALUES (1, 'Boleta', 'B001-000001', 2, 3, TIMESTAMP '2026-09-14 10:45:00', 3, 2, 'Pagada', 93);
INSERT INTO venta (id_venta, tipo_comprobante, numero_comprobante, id_propietario, id_atencion, fecha_hora, id_metodo_pago, id_usuario, estado, total) VALUES (2, 'Boleta', 'B001-000002', 1, 2, TIMESTAMP '2026-04-02 16:30:00', 1, 2, 'Pagada', 67);
INSERT INTO venta (id_venta, tipo_comprobante, numero_comprobante, id_propietario, id_atencion, fecha_hora, id_metodo_pago, id_usuario, estado, total) VALUES (3, 'Boleta', 'B001-000003', 3, 4, TIMESTAMP '2026-10-05 12:15:00', 2, 1, 'Pagada', 46);
INSERT INTO venta (id_venta, tipo_comprobante, numero_comprobante, id_propietario, id_atencion, fecha_hora, id_metodo_pago, id_usuario, estado, total) VALUES (4, 'Boleta', 'B001-000004', 1, NULL, TIMESTAMP '2026-10-20 09:30:00', 4, 2, 'Pagada', 89);
INSERT INTO venta (id_venta, tipo_comprobante, numero_comprobante, id_propietario, id_atencion, fecha_hora, id_metodo_pago, id_usuario, estado, total) VALUES (5, 'Boleta', 'B001-000005', 3, NULL, TIMESTAMP '2026-10-25 18:10:00', 1, 2, 'Pagada', 42);
INSERT INTO venta (id_venta, tipo_comprobante, numero_comprobante, id_propietario, id_atencion, fecha_hora, id_metodo_pago, id_usuario, estado, total) VALUES (6, 'Boleta', 'B001-000006', 2, NULL, TIMESTAMP '2025-11-12 15:20:00', 1, 2, 'Pagada', 45);
INSERT INTO venta (id_venta, tipo_comprobante, numero_comprobante, id_propietario, id_atencion, fecha_hora, id_metodo_pago, id_usuario, estado, total) VALUES (7, 'Boleta', 'B001-000007', 1, NULL, TIMESTAMP '2025-12-03 11:05:00', 3, 2, 'Pagada', 54);
INSERT INTO venta (id_venta, tipo_comprobante, numero_comprobante, id_propietario, id_atencion, fecha_hora, id_metodo_pago, id_usuario, estado, total) VALUES (8, 'Boleta', 'B001-000008', 3, NULL, TIMESTAMP '2025-12-20 17:40:00', 2, 1, 'Pagada', 25);
INSERT INTO venta (id_venta, tipo_comprobante, numero_comprobante, id_propietario, id_atencion, fecha_hora, id_metodo_pago, id_usuario, estado, total) VALUES (9, 'Boleta', 'B001-000009', 2, NULL, TIMESTAMP '2026-10-25 10:00:00', 3, 2, 'Anulada', 12);
ALTER TABLE venta ALTER COLUMN id_venta RESTART WITH 10;

-- detalle_venta
INSERT INTO detalle_venta (id_detalle_venta, id_venta, id_servicio, id_producto, cantidad, precio, subtotal) VALUES (1, 1, 1, NULL, 1, 30, 30);
INSERT INTO detalle_venta (id_detalle_venta, id_venta, id_servicio, id_producto, cantidad, precio, subtotal) VALUES (2, 1, NULL, 1, 1, 45, 45);
INSERT INTO detalle_venta (id_detalle_venta, id_venta, id_servicio, id_producto, cantidad, precio, subtotal) VALUES (3, 1, NULL, 2, 1, 18, 18);
INSERT INTO detalle_venta (id_detalle_venta, id_venta, id_servicio, id_producto, cantidad, precio, subtotal) VALUES (4, 2, 5, NULL, 1, 35, 35);
INSERT INTO detalle_venta (id_detalle_venta, id_venta, id_servicio, id_producto, cantidad, precio, subtotal) VALUES (5, 2, NULL, 4, 1, 32, 32);
INSERT INTO detalle_venta (id_detalle_venta, id_venta, id_servicio, id_producto, cantidad, precio, subtotal) VALUES (6, 3, 1, NULL, 1, 30, 30);
INSERT INTO detalle_venta (id_detalle_venta, id_venta, id_servicio, id_producto, cantidad, precio, subtotal) VALUES (7, 3, NULL, 7, 2, 8, 16);
INSERT INTO detalle_venta (id_detalle_venta, id_venta, id_servicio, id_producto, cantidad, precio, subtotal) VALUES (8, 4, NULL, 1, 1, 45, 45);
INSERT INTO detalle_venta (id_detalle_venta, id_venta, id_servicio, id_producto, cantidad, precio, subtotal) VALUES (9, 4, NULL, 5, 2, 22, 44);
INSERT INTO detalle_venta (id_detalle_venta, id_venta, id_servicio, id_producto, cantidad, precio, subtotal) VALUES (10, 5, NULL, 6, 2, 12, 24);
INSERT INTO detalle_venta (id_detalle_venta, id_venta, id_servicio, id_producto, cantidad, precio, subtotal) VALUES (11, 5, NULL, 2, 1, 18, 18);
INSERT INTO detalle_venta (id_detalle_venta, id_venta, id_servicio, id_producto, cantidad, precio, subtotal) VALUES (12, 6, NULL, 1, 1, 45, 45);
INSERT INTO detalle_venta (id_detalle_venta, id_venta, id_servicio, id_producto, cantidad, precio, subtotal) VALUES (13, 7, NULL, 4, 1, 32, 32);
INSERT INTO detalle_venta (id_detalle_venta, id_venta, id_servicio, id_producto, cantidad, precio, subtotal) VALUES (14, 7, NULL, 5, 1, 22, 22);
INSERT INTO detalle_venta (id_detalle_venta, id_venta, id_servicio, id_producto, cantidad, precio, subtotal) VALUES (15, 8, NULL, 3, 1, 25, 25);
INSERT INTO detalle_venta (id_detalle_venta, id_venta, id_servicio, id_producto, cantidad, precio, subtotal) VALUES (16, 9, NULL, 6, 1, 12, 12);
ALTER TABLE detalle_venta ALTER COLUMN id_detalle_venta RESTART WITH 17;

