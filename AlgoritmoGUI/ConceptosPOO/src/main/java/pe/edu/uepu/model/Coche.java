package pe.edu.uepu.model;

public class Coche {
    String marca;
     int velocidad;

    public Coche(String marca, int velocidadInicial) {
        this.marca = marca;
        this.velocidad = velocidadInicial;
    }

    public Coche(String marca) {
        this(marca, 0);
    }

    public void acelerar() {
        velocidad = velocidad + 10;
    }

    public void frenar() {
        velocidad = velocidad - 10;
    }

    public void mostrarEstado() {
        System.out.println(marca + " - Velocidad: " + velocidad);
    }

    @Override
    public String toString() {
        return marca+"\t"+velocidad;
    }
}