--C
INSERT INTO public.producto
(id_producto, nombre, tipo_producto, pu, id_categoria)
VALUES(nextval('producto_id_producto_seq'::regclass), 'Televisor', 'PRODUCTO', 1400, 1);

--R
SELECT id_producto, nombre, tipo_producto, pu, id_categoria
FROM public.producto;

--U
UPDATE public.producto
SET nombre='Lavadora', pu=1500
WHERE id_producto=2;


--D
DELETE FROM public.producto
WHERE id_producto=1;