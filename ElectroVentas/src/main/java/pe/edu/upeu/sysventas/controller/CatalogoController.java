package pe.edu.upeu.sysventas.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import pe.edu.upeu.sysventas.model.Categoria;
import pe.edu.upeu.sysventas.model.Marca;
import pe.edu.upeu.sysventas.service.ICategoriaService;
import pe.edu.upeu.sysventas.service.IMarcaService;

/** Mantenimiento de marcas y categorías con servicios independientes. */
public class CatalogoController {
    private final IMarcaService marcas;
    private final ICategoriaService categorias;
    public CatalogoController(IMarcaService marcas, ICategoriaService categorias) { this.marcas=marcas; this.categorias=categorias; }
    @FXML private TableView<Marca> tablaMarcas;
    @FXML private TableColumn<Marca,String> colMarca;
    @FXML private TableView<Categoria> tablaCategorias;
    @FXML private TableColumn<Categoria,String> colCategoria;
    @FXML private TextField txtMarca, txtCategoria;
    @FXML private Label estado;
    private Long marcaId, categoriaId;
    @FXML public void initialize() {
        colMarca.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colCategoria.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        tablaMarcas.getSelectionModel().selectedItemProperty().addListener((o,a,m)->{ if(m!=null){marcaId=m.getIdMarca();txtMarca.setText(m.getNombre());} });
        tablaCategorias.getSelectionModel().selectedItemProperty().addListener((o,a,c)->{ if(c!=null){categoriaId=c.getIdCategoria();txtCategoria.setText(c.getNombre());} });
        actualizar();
    }
    @FXML private void guardarMarca() {
        String nombre=txtMarca.getText().trim(); if(!validar(nombre))return;
        try {Marca m=new Marca(marcaId,nombre); if(marcaId==null) marcas.save(m); else marcas.update(marcaId,m);
            nuevaMarca(); actualizar(); mensaje("Marca guardada",false);}
        catch(IllegalArgumentException e){mensaje(e.getMessage(),true);} catch(Exception e){mensaje("Error en marca: "+e.getMessage(),true);}
    }
    @FXML private void guardarCategoria() {
        String nombre=txtCategoria.getText().trim(); if(!validar(nombre))return;
        try {Categoria c=new Categoria(categoriaId,nombre); if(categoriaId==null) categorias.save(c); else categorias.update(categoriaId,c);
            nuevaCategoria(); actualizar(); mensaje("Categoría guardada",false);}
        catch(IllegalArgumentException e){mensaje(e.getMessage(),true);} catch(Exception e){mensaje("Error en categoría: "+e.getMessage(),true);}
    }
    @FXML private void eliminarMarca() {
        Marca m=tablaMarcas.getSelectionModel().getSelectedItem(); if(m==null){mensaje("Seleccione una marca",true);return;}
        if(!confirmar("marca",m.getNombre()))return;
        try {marcas.delete(m.getIdMarca());nuevaMarca();actualizar();mensaje("Marca eliminada",false);}
        catch(Exception e){mensaje("No se puede eliminar una marca utilizada por productos",true);}
    }
    @FXML private void eliminarCategoria() {
        Categoria c=tablaCategorias.getSelectionModel().getSelectedItem(); if(c==null){mensaje("Seleccione una categoría",true);return;}
        if(!confirmar("categoría",c.getNombre()))return;
        try {categorias.delete(c.getIdCategoria());nuevaCategoria();actualizar();mensaje("Categoría eliminada",false);}
        catch(Exception e){mensaje("No se puede eliminar una categoría utilizada por productos",true);}
    }
    @FXML private void nuevaMarca(){marcaId=null;txtMarca.clear();tablaMarcas.getSelectionModel().clearSelection();}
    @FXML private void nuevaCategoria(){categoriaId=null;txtCategoria.clear();tablaCategorias.getSelectionModel().clearSelection();}
    private void actualizar(){try{tablaMarcas.setItems(FXCollections.observableArrayList(marcas.findAll()));tablaCategorias.setItems(FXCollections.observableArrayList(categorias.findAll()));}catch(Exception e){mensaje("Error de consulta: "+e.getMessage(),true);}}
    private boolean validar(String nombre){if(nombre.isBlank()||nombre.length()>20){mensaje("El nombre es obligatorio (máximo 20 caracteres)",true);return false;}return true;}
    private boolean confirmar(String tipo,String nombre){return new Alert(Alert.AlertType.CONFIRMATION,"¿Eliminar "+tipo+" "+nombre+"?",ButtonType.YES,ButtonType.NO).showAndWait().orElse(ButtonType.NO)==ButtonType.YES;}
    private void mensaje(String texto, boolean error){estado.setText(texto);estado.setStyle(error?"-fx-text-fill:#a25d58":"-fx-text-fill:#587f6b");}
}
