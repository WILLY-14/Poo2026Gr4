package pe.edu.upeu.sysventas.service.impl;

import pe.edu.upeu.sysventas.dto.MenuMenuItenTO;
import pe.edu.upeu.sysventas.service.IMenuMenuItemDao;

import java.util.*;

public class MenuMenuItemDaoImp implements IMenuMenuItemDao {
    private static String t(Properties p, String k, String d) { String v = p.getProperty(k); return v == null ? d : v; }

    @Override
    public List<MenuMenuItenTO> listaAccesos(String perfil, Properties idioma) {
        List<MenuMenuItenTO> lista = new ArrayList<>();

        lista.add(new MenuMenuItenTO("miprincipal", "/view/login.fxml",
                t(idioma,"menu.nombre.principal","menu.nombre.principal"),
                t(idioma,"menuitem.nombre.salir","menuitem.nombre.salir"), "Salir", "C"));

        lista.add(new MenuMenuItenTO("miproducto", "/view/main_producto.fxml",
                t(idioma,"menu.nombre.producto","menu.nombre.producto"), t(idioma,"menuitem.nombre.producto","menuitem.nombre.producto"), "Gestión Productos", "T"));

        lista.add(new MenuMenuItenTO("micliente", "/view/main_cliente.fxml",
                t(idioma,"menu.nombre.venta","Venta"), t(idioma,"menuitem.nombre.cliente","Reg. Cliente"), "Gestionar Cliente", "T"));
        lista.add(new MenuMenuItenTO("miventa", "/view/main_venta.fxml",
                t(idioma,"menu.nombre.venta","Venta"), t(idioma,"menuitem.nombre.venta","Reg. Venta"), "Gestionar Ventas", "T"));

        lista.add(new MenuMenuItenTO("micatalogo", "/view/main_catalogo.fxml",
                t(idioma,"menu.nombre.catalogo","Producto"), t(idioma,"menuitem.nombre.catalogo","Marcas y categorías"), "Gestión Catálogo", "T"));
        List<MenuMenuItenTO> accesoReal = new ArrayList<>();

        accesoReal.add(lista.get(0));

        switch (perfil) {
            case "Administrador":
                accesoReal.add(lista.get(1)); // miproducto
                accesoReal.add(lista.get(2)); // micliente
                accesoReal.add(lista.get(4)); // micatalogo
                break;
            case "Root":
                accesoReal = lista;
                break;
            case "Reporte":
                accesoReal.add(lista.get(2)); // micliente
                accesoReal.add(lista.get(4)); // micatalogo
                break;
            default:
                throw new AssertionError();
        }
        return accesoReal;

    }

    @Override
    public Map<String, String[]> accesosAutorizados(List<MenuMenuItenTO> accesos) {
        Map<String, String[]> menuConfig = new HashMap<>();
        for (MenuMenuItenTO menu : accesos) {
            menuConfig.put("mi" + menu.getIdNombreObj(),  new String[]{menu.getRutaFile(), menu.getNombreTab(),menu.getTipoTab() });
        }
        return menuConfig;
    }

    @Override
    public int[] contarMenuMunuItem(List<MenuMenuItenTO> data) {
        int menui = 0, menuitem = 0;
        String menuN = "";
        for (MenuMenuItenTO mmiItem : data) {
            if (!mmiItem.getMenunombre().equals(menuN)) {
                menuN = mmiItem.getMenunombre();
                menui++;
            }
            if (!mmiItem.getMenuitemnombre().equals("")) {
                menuitem++;
            }
        }
        return new int[]{menui, menuitem};
    }
}
