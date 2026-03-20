package org.example.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.example.model.Grupo;
import org.example.service.RedSocialService;

public class AddGroupController {

    // Campos de la interfaz
    private TextField txtNombreGrupo;
    private ColorPicker cpColor;
    private Label lblStatus;

    // Obtengo la instancia del servicio que maneja la lógica de la red social.
    private final RedSocialService service = AppState.getService();

    // Preparo el estado inicial de la pantalla.
    @FXML
    public void initialize() {
        lblStatus.setText(""); // Limpio cualquier mensaje que pudiera aparecer al iniciar la vista
        cpColor.setValue(Color.GRAY);  // Defino un color por defecto para el grupo
    }

    @FXML
    private void onGuardarGrupo() {
        try {

            // Obtengo el texto que escribió el usuario en el TextField
            String nombre = txtNombreGrupo.getText();

            // Valido que el nombre no esté vacío ni sea solo espacios
            // Si está vacío lanzo una excepción para mostrar el error
            if (nombre == null || nombre.isBlank()) {
                throw new IllegalArgumentException("El nombre del grupo es obligatorio.");
            }

            // Obtengo el color seleccionado en el ColorPicker
            Color color = cpColor.getValue();

            // Creo el objeto Grupo con los datos ingresados
            // trim() lo uso para eliminar espacios al inicio o final
            Grupo grupo = new Grupo(nombre.trim(), color);

            // Llamo al service para agregar el grupo a la red social
            // Aquí realmente se guarda el grupo en la estructura de datos
            service.agregarGrupo(grupo);

            // Muestro un Alert informando que el grupo se creó correctamente
            showInfo("Grupo creado", "Se creó el grupo: " + nombre);

            // También actualizo el label de estado en la interfaz
            lblStatus.setText("Grupo creado correctamente.");

            // Limpio el campo de texto para que el usuario pueda crear otro grupo
            txtNombreGrupo.clear();

        } catch (IllegalArgumentException ex) {
            // Este catch maneja errores de validación
            // (por ejemplo cuando el nombre está vacío)
            showError("Validación", ex.getMessage());
            lblStatus.setText(ex.getMessage());

        } catch (Exception ex) {

            // Este catch captura cualquier otro error inesperado
            // que pueda ocurrir al crear o guardar el grupo
            showError("Error", "Error inesperado: " + ex.getMessage());
            lblStatus.setText("Error inesperado.");
        }
    }

    // Método auxiliar para mostrar mensajes informativos
    // Uso un Alert de tipo INFORMATION
    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    // Método auxiliar para mostrar mensajes de error
    // Uso un Alert de tipo ERROR
    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
