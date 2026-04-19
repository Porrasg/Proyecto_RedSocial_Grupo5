package org.example.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

// Controlador del menú principal de la aplicación.
public class MenuController {

    // Navega a la pantalla de creación de usuario.
    @FXML
    private void irCrearUsuario(ActionEvent event) throws IOException {
        cambiarPantalla(event, "/fxml/new_user.fxml");
    }

    // Navega a la pantalla de creación de grupo.
    @FXML
    private void irCrearGrupo(ActionEvent event) throws IOException {
        cambiarPantalla(event, "/fxml/add_group.fxml");
    }

    // Navega a la pantalla de visualización de la red social.
    @FXML
    private void irRedSocial(ActionEvent event) throws IOException {
        cambiarPantalla(event, "/fxml/red_social.fxml");
    }
    //Navega a la pantalla de visualización de usuario
    @FXML
    private void irVerUsuario(ActionEvent event) throws IOException {
        cambiarPantalla(event, "/fxml/ver_usuario.fxml");
    }

    // Navega a la pantalla de visualizacion de amigos en común
    @FXML
    private void irAmigosComun(ActionEvent event) throws IOException {
        cambiarPantalla(event, "/fxml/amigos_comun.fxml");
    }

    // Cierra completamente la aplicación.
    @FXML
    private void salir() {
        System.exit(0);
    }

    // Método reutilizable para cambiar de pantalla.
    private void cambiarPantalla(ActionEvent event, String rutaFXML) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));

        // Cargar el nuevo contenido
        javafx.scene.Parent root = loader.load();

        // Obtener el stage actual
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

        // Solo cambiar el contenido, no la escena
        stage.getScene().setRoot(root);
    }
}