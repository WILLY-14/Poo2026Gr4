package pe.edu.uepu.model;

public class Producto {
     String codigo;
     String nombre;
     double precio;
     int stock;


    public Producto(){}
    public Producto(String codigo, String nombre, double precio, int stock) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }
    public Producto(String codigo, String nombre, double precio) {
        this(codigo, nombre, precio, 0);
    }

   public void mostrarInformacion() {
        System.out.println(codigo + " - " + nombre + " - S/ " + precio + " - Stock: " + stock);
    }

    public void actualizarPrecio(double nuevoPrecio) {
        precio = nuevoPrecio;
    }

    public void aumentarStock(int cantidad) {
        stock = stock + cantidad;
    }

    public double getIvg(){
        if(precio<0)
            throw new IllegalStateException();
        return precio-precio/1.18;
    }

    public double getPrecioVenta(){
        return precio/1.18;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}