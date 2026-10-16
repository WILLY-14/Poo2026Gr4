package pe.edu.upeu.sysventas.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysventas.SysVentas;
import pe.edu.upeu.sysventas.config.AppContext;
import pe.edu.upeu.sysventas.dto.SessionManager;
import pe.edu.upeu.sysventas.model.Usuario;
import pe.edu.upeu.sysventas.service.IUsuarioService;

@RequiredArgsConstructor
public class LoginController {

    final private IUsuarioService us;

    @FXML
    TextField txtUsuario;

    @FXML
    PasswordField txtClave;

    @FXML
    public void login(ActionEvent e){
       us.loginUsuario(txtUsuario.getText(), txtClave.getText())
               .ifPresentOrElse(
                       usuario ->{
                           abrirPantalla(e,usuario);
                       },
                       () -> System.out.println("Usuario no encontrado"));

    }

    public void abrirPantalla(ActionEvent e, Usuario usuario){

        SessionManager.getInstance().setUserId(usuario.getIdUsuario());
        SessionManager.getInstance().setUserName(usuario.getUsuario());
        SessionManager.getInstance().setUserPerfil(usuario.getIdPerfil().getNombre());

        try {
            Screen screen=Screen.getPrimary();
            Rectangle2D rectangle2D=screen.getVisualBounds();
            AppContext appContext=AppContext.getInstance();
            FXMLLoader fxmlLoader = new FXMLLoader(SysVentas.class.getResource("/view/maingui.fxml"));
            fxmlLoader.setControllerFactory(appContext::getBean);
            Scene scene = new Scene(fxmlLoader.load(), rectangle2D.getWidth(), rectangle2D.getHeight());
            scene.getStylesheets().add(SysVentas.class.getResource("/css/style.css").toExternalForm());
            Stage stage=(Stage) ((Node)e.getSource()).getScene().getWindow();
            stage.setTitle("Hello!");
            stage.setScene(scene);
            stage.setX(rectangle2D.getMinX());
            stage.setY(rectangle2D.getMinY());
            stage.setResizable(true);
            stage.show();

        }catch (Exception ex){ ex.printStackTrace(); }

    }






}
