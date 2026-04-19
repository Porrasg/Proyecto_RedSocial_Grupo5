package org.example.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import org.example.model.AvatarType;
import org.example.model.Usuario;
import org.example.service.RedSocialService;
import org.example.validation.UserValidator;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import org.example.ui.AvatarUtils;
import java.io.IOException;
import java.time.LocalDate;

// Controlador de la vista Nuevo Usuario
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

    // Servicio que maneja la lógica de la aplicación
    private final RedSocialService service = AppState.getService();

    // Inicializa la vista al cargarse.
    @FXML
    public void initialize() {
        // Opciones del ComboBox de avatar
        cbAvatar.getItems().setAll(AvatarType.MASCULINO, AvatarType.FEMENINO, AvatarType.PREDETERMINADO);
        // Valor por defecto
        cbAvatar.setValue(AvatarType.PREDETERMINADO);
        lblStatus.setText("");

        actualizarPreviewAvatar();

        // Actualiza la imagen cuando cambia el avatar
        cbAvatar.setOnAction(event -> actualizarPreviewAvatar());
    }

    // Maneja el botón Guardar
    @FXML
    private void onGuardar() {

        try {
            // 1. Leer datos
            String username = txtUsername.getText();
            String password = txtPassword.getText();
            String primerNombre = txtPrimerNombre.getText();
            String primerApellido = txtPrimerApellido.getText();
            String segundoApellido = txtSegundoApellido.getText();
            LocalDate fechaNacimiento = dpFechaNacimiento.getValue();
            AvatarType avatar = cbAvatar.getValue();

            // 2. VALIDAR TODO (si algo falla lanza error y se detiene)
            UserValidator.requireNotBlank(username, "Usuario");
            UserValidator.validatePassword(password);
            UserValidator.requireNotBlank(primerNombre, "Primer Nombre");
            UserValidator.requireNotBlank(primerApellido, "Primer Apellido");
            UserValidator.requireNotBlank(segundoApellido, "Segundo Apellido");
            UserValidator.validateBirthDate(fechaNacimiento);

            if (service.existsUsername(username)) {
                throw new IllegalArgumentException("Ese usuario ya existe.");
            }

            // 3. CREAR solo si TODO está bien
            Usuario u = new Usuario(
                    username.trim(),
                    password,
                    primerNombre.trim(),
                    primerApellido.trim(),
                    segundoApellido.trim(),
                    fechaNacimiento,
                    avatar
            );

            service.addUser(u);

            showInfo("Usuario creado", "Se creó el usuario: " + u.getUsername());
            lblStatus.setText("Usuario creado correctamente.");
            onLimpiar();

        } catch (IllegalArgumentException ex) {
            showError("Validación", ex.getMessage());
            lblStatus.setText(ex.getMessage());
        }
    }

    // Limpia todos los campos del formulario.
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

    // Regresa al menú principal.
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

    // Muestra mensaje informativo.
    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    // Actualiza la imagen de vista previa del avatar.
    private void actualizarPreviewAvatar() {
        imgAvatarPreview.setImage(AvatarUtils.getAvatarImage(cbAvatar.getValue()));
    }

    // Muestra mensaje de error.
    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}