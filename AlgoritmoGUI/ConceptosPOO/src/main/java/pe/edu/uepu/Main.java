package pe.edu.uepu;


import pe.edu.uepu.model.Coche;
import pe.edu.uepu.model.Persona;
import pe.edu.uepu.model.Producto;

import java.util.ArrayList;
import java.util.List;

public class Main {
    static void main() {
        Producto producto= new Producto("P001",
                "Teclado", 80.0, 10);
        producto.mostrarInformacion();
        producto.actualizarPrecio(75.0);
        producto.aumentarStock(5);
        producto.mostrarInformacion();
        producto.setStock(producto.getStock()+5);
        producto.mostrarInformacion();


        List<Producto> listProd=new ArrayList();

        listProd.add(new Producto("P001", "Televisor", 4000, 6));
        listProd.add(new Producto("P002", "Celular", 118));
        listProd.add(new Producto("P003", "Tarjeta Grafica RTX", 5000));

        System.out.println("Nombre\tP. Venta\tIGV\tP.Total");
        for (Producto px:listProd){
            System.out.println(px.getNombre()+"\t"+px.getPrecioVenta()+"\t"+px.getIvg()+"\t"+px.getPrecio());
        }
        var pu=-545.52;
        var nombre="Juan";
        var po=new Producto("P001",
                "Teclado", 80.0, 10);
        System.out.println(po.getClass());
        System.out.println(pu instanceof double);
        System.out.println(nombre.getClass());

        System.out.println("Cochess!!!");
        Coche coche1 = new Coche("Toyota", 0);
        Coche coche2 = new Coche("Kia");
        System.out.println(coche1);
        Coche coche3=coche1;
        System.out.println(coche3);
        coche1=null;
        System.out.println(coche3);

        Persona persona1 = new Persona("Ana");

        /*coche1.mostrarEstado();
        coche2.mostrarEstado();
        persona1.conducir(coche1);
        coche1.mostrarEstado();*/

    }
}
