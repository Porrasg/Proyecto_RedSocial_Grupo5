package org.example.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import org.example.model.AvatarType;
import org.example.model.Usuario;
import org.example.service.RedSocialService;
import org.example.validation.UserValidator;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import org.example.ui.AvatarUtils;
import java.io.IOException;

public class NewUserController {

    // Campos de UI (FXML IDs)
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtPrimerNombre;
    @FXML
    private TextField txtPrimerApellido;
    @FXML
    private TextField txtSegundoApellido;
    @FXML
    private DatePicker dpFechaNacimiento;
    @FXML
    private ComboBox<AvatarType> cbAvatar;
    @FXML
    private Label lblStatus;
    @FXML
    private ImageView imgAvatarPreview;


    private final RedSocialService service = AppState.getService();

    @FXML
    public void initialize() {
        cbAvatar.getItems().setAll(AvatarType.MASCULINO, AvatarType.FEMENINO, AvatarType.PREDETERMINADO);
        cbAvatar.setValue(AvatarType.PREDETERMINADO);
        lblStatus.setText("");

        actualizarPreviewAvatar();

        cbAvatar.setOnAction(event -> actualizarPreviewAvatar());
    }

    @FXML
    private void onGuardar() {
        try {
            // 1) Leer inputs
            String username = txtUsername.getText();
            String password = txtPassword.getText();
            String primerNombre = txtPrimerNombre.getText();
            String primerApellido = txtPrimerApellido.getText();
            String segundoApellido = txtSegundoApellido.getText();
            var fechaNacimiento = dpFechaNacimiento.getValue();
            AvatarType avatar = cbAvatar.getValue();

            // 2) Validar
            UserValidator.requireNotBlank(username, "Usuario");
            UserValidator.validatePassword(password);
            UserValidator.requireNotBlank(primerNombre, "Primer Nombre");
            UserValidator.requireNotBlank(primerApellido, "Primer Apellido");
            UserValidator.requireNotBlank(segundoApellido, "Segundo Apellido");
            UserValidator.validateBirthDate(fechaNacimiento);

            // 3) Crear usuario
            Usuario u = new Usuario(
                    username.trim(),
                    password,
                    primerNombre.trim(),
                    primerApellido.trim(),
                    segundoApellido.trim(),
                    fechaNacimiento,
                    avatar
            );

            // 4) Registrar en memoria
            service.addUser(u);

            // 5) Feedback
            showInfo("Usuario creado", "Se creó el usuario: " + u.getUsername());
            lblStatus.setText("Usuario creado correctamente.");
            onLimpiar();

        } catch (IllegalArgumentException ex) {
            showError("Validación", ex.getMessage());
            lblStatus.setText(" " + ex.getMessage());
        } catch (Exception ex) {
            showError("Error inesperado", "Ocurrió un error inesperado: " + ex.getMessage());
            lblStatus.setText("Error inesperado.");
        }
    }

    @FXML
    private void onLimpiar() {
        txtUsername.clear();
        txtPassword.clear();
        txtPrimerNombre.clear();
        txtPrimerApellido.clear();
        txtSegundoApellido.clear();
        dpFechaNacimiento.setValue(null);
        cbAvatar.setValue(AvatarType.PREDETERMINADO);
    }
    @FXML
    private void onVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600); // Tamaño fijo

            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            showError("Error", "No se pudo volver al menú: " + e.getMessage());
        }
    }

    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
    private void actualizarPreviewAvatar() {
        imgAvatarPreview.setImage(AvatarUtils.getAvatarImage(cbAvatar.getValue()));
    }
    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}