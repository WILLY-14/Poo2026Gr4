package pe.edu.uepu.model;

public class Persona {
    String nombre;

    public Persona(String nombre) {
        this.nombre = nombre;
    }

    public void conducir(Coche coche) {
        System.out.println(nombre + " conduce el coche");
        coche.acelerar();
        coche.frenar();
    }
}