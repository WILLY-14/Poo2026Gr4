package pe.edu.upeu.padronmascotas.components;

public class ColumnInfo {
    private String field;
    private Double width;

    public ColumnInfo(String field, Double width) {
        this.field = field;
        this.width = width;
    }

    public String getField() {
        return field;
    }

    public Double getWidth() {
        return width;
    }
}