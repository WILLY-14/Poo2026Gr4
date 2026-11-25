package pe.edu.upeu.padronmascota.padronmascota.excepcion;

/** Se lanza cuando se busca un registro por id y no existe. */
public class NoEncontradoException extends RuntimeException {

    public NoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
