package org.example.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.model.Usuario;
import org.example.model.Grupo;
import org.example.service.RedSocialService;

import java.util.List;

public class VerUsuarioController {

    // ComboBoxes
    @FXML
    private ComboBox<Usuario> cbUsuarios;
    @FXML
    private ComboBox<Grupo> cbGrupos;
    // Labels para la info
    @FXML
    private Label lblUsername;
    @FXML
    private Label lblNombre;
    @FXML
    private Label lblApellidos;
    @FXML
    private Label lblGrupoActual;
    @FXML
    private Label lblStatus;

    // Service compartido
    private final RedSocialService service = AppState.getService();

    @FXML
    public void initialize() {

        lblStatus.setText("");

        // Aquí cargamos los usuarios
        List<Usuario> usuarios = service.getAllUsers();
        cbUsuarios.getItems().setAll(usuarios);

        // Se muestra el username en ComboBox
        cbUsuarios.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Usuario item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getUsername());
            }
        });

        cbUsuarios.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Usuario item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getUsername());
            }
        });

        cbUsuarios.setOnAction(e -> mostrarUsuario());

        // Carga los grupos
        List<Grupo> grupos = service.getGrupos();
        cbGrupos.getItems().setAll(grupos);

        // Se muestra el nombre de grupo
        cbGrupos.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Grupo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });

        cbGrupos.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Grupo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
    }

    // Para enseñar la información del usuario seleccionado
    private void mostrarUsuario() {
        Usuario u = cbUsuarios.getValue();

        if (u == null) return;

        lblUsername.setText(u.getUsername());
        lblNombre.setText(u.getPrimerNombre());
        lblApellidos.setText(u.getPrimerApellido() + " " + u.getSegundoApellido());

        if (u.getGrupo() != null) {
            lblGrupoActual.setText(u.getGrupo().getNombre());
            cbGrupos.setValue(u.getGrupo());
        } else {
            lblGrupoActual.setText("Sin grupo");
            cbGrupos.setValue(null);
        }
    }

    // Guardar cambios (asignar grupo)
    @FXML
    private void onGuardar() {
        try {
            Usuario usuario = cbUsuarios.getValue();
            Grupo grupo = cbGrupos.getValue();

            if (usuario == null) {
                throw new IllegalArgumentException("Debe seleccionar un usuario.");
            }

            if (grupo == null) {
                throw new IllegalArgumentException("Debe seleccionar un grupo.");
            }

            // Llamar al service
            service.asignarGrupo(usuario.getUsername(), grupo.getNombre());

            lblStatus.setText("Grupo asignado correctamente.");
            mostrarUsuario();

            showInfo("Éxito", "Grupo asignado a " + usuario.getUsername());

        } catch (IllegalArgumentException ex) {
            showError("Validación", ex.getMessage());
            lblStatus.setText(ex.getMessage());

        } catch (Exception ex) {
            showError("Error", "Error inesperado: " + ex.getMessage());
            lblStatus.setText("Error inesperado.");
        }
    }

    // Volver al menú
    @FXML
    private void onVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);

            Stage stage = (Stage) cbUsuarios.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            showError("Error", "No se pudo volver al menú: " + e.getMessage());
        }
    }

    // Alerta, muestra la info
    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    // Alerta, muestra el error
    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}