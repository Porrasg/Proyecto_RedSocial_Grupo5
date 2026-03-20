package org.example.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {

    @FXML
    private void irCrearUsuario(ActionEvent event) throws IOException {
        cambiarPantalla(event, "/fxml/new_user.fxml");
    }

    @FXML
    private void irRedSocial(ActionEvent event) throws IOException {
        cambiarPantalla(event, "/fxml/red_social.fxml");
    }

    @FXML
    private void salir() {
        System.exit(0);
    }

    private void cambiarPantalla(ActionEvent event, String rutaFXML) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}