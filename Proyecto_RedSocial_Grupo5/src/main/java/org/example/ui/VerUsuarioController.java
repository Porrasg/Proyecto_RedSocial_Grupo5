package org.example.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.model.Usuario;
import org.example.model.Grupo;
import org.example.service.RedSocialService;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
    // Tabla que muestra los amigos del usuario
    @FXML
    private TableView<Usuario> tablaAmigos;

    // Tabla que muestra las sugerencias de amistad
    @FXML
    private TableView<Usuario> tablaSugerencias;

    // Columna que muestra el username de los amigos del usuario seleccionado
    @FXML
    private TableColumn<Usuario, String> colUsernameAmigos;

    // Columna que muestra el nombre completo de los amigos (nombre + apellidos)
    @FXML
    private TableColumn<Usuario, String> colNombreAmigos;

    // Columna que muestra el username de las sugerencias de amistad (resultado del BFS)
    @FXML
    private TableColumn<Usuario, String> colUsernameSugerencias;

    // Columna que muestra el nombre completo de las sugerencias de amistad
    @FXML
    private TableColumn<Usuario, String> colNombreSugerencias;


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

        // Configurar columna de username (amigos)
        colUsernameAmigos.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getUsername()
                )
        );

        // Configurar columna de nombre completo (amigos)
        colNombreAmigos.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getPrimerNombre() + " " +
                                data.getValue().getPrimerApellido() +
                                (data.getValue().getSegundoApellido() != null
                                        ? " " + data.getValue().getSegundoApellido()
                                        : "")
                )
        );

        // Configurar columna de username (sugerencias)
        colUsernameSugerencias.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getUsername()
                )
        );

        // Configurar columna de nombre completo (sugerencias)
        colNombreSugerencias.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getPrimerNombre() + " " +
                                data.getValue().getPrimerApellido() +
                                (data.getValue().getSegundoApellido() != null
                                        ? " " + data.getValue().getSegundoApellido()
                                        : "")
                )
        );

        // Mensaje para tablas vacías
        tablaSugerencias.setPlaceholder(new Label("No hay sugerencias disponibles."));
        tablaAmigos.setPlaceholder(new Label("No tiene amigos."));

        // Evitar este espacio, se puede ajustar el ancho de las columnas
        tablaAmigos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaSugerencias.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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

    // Método que carga y muestra toda la información del usuario seleccionado,
    // incluyendo sus amigos y sugerencias de amistad obtenidas mediante BFS
    private void mostrarUsuario() {
        // Obtenemos el usuario seleccionado del ComboBox
        Usuario u = cbUsuarios.getValue();

        // Si no hay usuario seleccionado, no hacemos nada
        if (u == null) return;

        // Obtenemos los usernames de los amigos del usuario
        Set<String> amigosUsernames = service.getFriends(u.getUsername());

        // Convertimos los usernames a objetos Usuario,
        // filtramos nulos y ordenamos alfabéticamente
        List<Usuario> amigos = amigosUsernames.stream()
                .map(username -> service.findByUsername(username).orElse(null))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Usuario::getPrimerNombre))
                .toList();

        // Mostramos los amigos en la tabla
        tablaAmigos.getItems().setAll(amigos);

        // Obtenemos sugerencias usando BFS (amigos de amigos)
        Set<String> sugerenciasUsernames = service.obtenerSugerenciasBFS(u.getUsername());

        // Convertimos a objetos Usuario, filtramos y ordenamos
        List<Usuario> sugerencias = sugerenciasUsernames.stream()
                .map(username -> service.findByUsername(username).orElse(null))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Usuario::getPrimerNombre))
                .toList();

        // Mostramos las sugerencias en la tabla
        tablaSugerencias.getItems().setAll(sugerencias);


        lblUsername.setText(u.getUsername());
        lblNombre.setText(u.getPrimerNombre());
        lblApellidos.setText(u.getPrimerApellido() + " " + u.getSegundoApellido());
        // Mostramos el grupo actual del usuario
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