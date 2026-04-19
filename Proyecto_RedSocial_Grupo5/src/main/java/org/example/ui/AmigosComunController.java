package org.example.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.model.Usuario;
import org.example.service.RedSocialService;

import java.util.List;
import java.util.Set;

// Controlador de la vista "Amigos en común".
// Permite seleccionar dos usuarios y visualizar los amigos que ambos tienen en común.
public class AmigosComunController {

    // ComboBox para seleccionar el primer usuario
    @FXML
    private ComboBox<Usuario> cbUsuario1;

    // ComboBox para seleccionar el segundo usuario
    @FXML
    private ComboBox<Usuario> cbUsuario2;

    // Tabla donde se mostrarán los amigos en común
    @FXML
    private TableView<Usuario> tablaComunes;

    // Columna para mostrar el username
    @FXML
    private TableColumn<Usuario, String> colUsername;

    // Columna para mostrar el nombre completo
    @FXML
    private TableColumn<Usuario, String> colNombre;

    // Servicio que contiene la lógica de la red social
    private final RedSocialService service = AppState.getService();

    @FXML
    public void initialize() {

        // Obtener todos los usuarios registrados
        List<Usuario> usuarios = service.getAllUsers();

        // Cargar los usuarios en ambos ComboBox
        cbUsuario1.getItems().setAll(usuarios);
        cbUsuario2.getItems().setAll(usuarios);

        // Configurar la columna de username
        colUsername.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getUsername()
                )
        );

        // Configurar la columna de nombre completo
        colNombre.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getPrimerNombre() + " " +
                                data.getValue().getPrimerApellido() +
                                (data.getValue().getSegundoApellido() != null
                                        ? " " + data.getValue().getSegundoApellido()
                                        : "")
                )
        );

        // Ajustar columnas para evitar espacio vacío
        tablaComunes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Mensaje cuando no hay datos en la tabla
        tablaComunes.setPlaceholder(new Label("No hay amigos en común."));

        // Mostrar solo el username en los ComboBox
        cbUsuario1.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Usuario item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getUsername());
            }
        });

        cbUsuario1.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Usuario item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getUsername());
            }
        });

        cbUsuario2.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Usuario item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getUsername());
            }
        });

        cbUsuario2.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Usuario item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getUsername());
            }
        });
    }

    // Método que maneja la acción de buscar amigos en común
    @FXML
    private void onBuscar() {

        // Obtener los usuarios seleccionados
        Usuario u1 = cbUsuario1.getValue();
        Usuario u2 = cbUsuario2.getValue();

        // Validar que ambos usuarios estén seleccionados
        if (u1 == null || u2 == null) {
            showError("Debe seleccionar ambos usuarios.");
            return;
        }

        // Validar que no sean el mismo usuario
        if (u1.equals(u2)) {
            showError("Debe seleccionar dos usuarios diferentes.");
            return;
        }

        try {
            // Obtener los usernames de los amigos en común
            Set<String> comunes = service.amigosEnComun(
                    u1.getUsername(),
                    u2.getUsername()
            );

            // Convertir los usernames a objetos Usuario
            List<Usuario> lista = comunes.stream()
                    .map(username -> service.findByUsername(username).orElse(null))
                    .filter(u -> u != null)
                    .toList();

            // Mostrar los resultados en la tabla
            tablaComunes.getItems().clear();
            tablaComunes.getItems().addAll(lista);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // Método para volver al menú principal
    @FXML
    private void onVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);

            Stage stage = (Stage) cbUsuario1.getScene().getWindow();
            stage.setScene(scene);

        } catch (Exception e) {
            showError("No se pudo volver.");
        }
    }

    // Método auxiliar para mostrar mensajes de error
    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setContentText(msg);
        a.showAndWait();
    }
}