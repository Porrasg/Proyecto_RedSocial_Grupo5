package org.example.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

import org.example.model.Usuario;
import org.example.service.RedSocialService;
import org.example.ui.AvatarUtils;

// Controlador de la vista de la red social.
// Se encarga de:
// - Dibujar los usuarios como nodos
// - Mostrar las conexiones (amistades)
// - Permitir crear o eliminar amistades con clicks
public class RedSocialController implements Initializable {

    // Panel donde se dibuja el grafo
    @FXML
    private AnchorPane graphPane;
    @FXML
    private Label lblInstrucciones;

    // Servicio que contiene la lógica de la red social
    private final RedSocialService service = AppState.getService();

    // Mapa para relacionar username con su nodo visual
    private final Map<String, Circle> nodeByUsername = new HashMap<>();

    // Usuario seleccionado actualmente
    private String selectedUser = null;

    // Tipo de click (izquierdo o derecho)
    private MouseButton selectedMode = null;

    // Regresa al menú principal.
    @FXML
    private void onVolverMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600); // Tamaño fijo

            Stage stage = (Stage) graphPane.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo volver al menú: " + e.getMessage());
        }
    }

    // Método que se ejecuta al cargar la vista.
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (lblInstrucciones != null) {
            lblInstrucciones.setText("Click izquierdo: crear amistad | Click derecho: eliminar amistad");
        }
        renderGraph();
    }

    // Dibuja el grafo completo en pantalla.
    public void renderGraph() {

        // Limpio el panel antes de dibujar
        graphPane.getChildren().clear();

        // Limpio el mapa de nodos
        nodeByUsername.clear();

        // Obtengo todos los usuarios
        List<Usuario> users = service.getAllUsers();

        // Si no hay usuarios, no hago nada
        if (users == null || users.isEmpty()) {
            return;
        }

        double width = graphPane.getPrefWidth() > 0 ? graphPane.getPrefWidth() : 900;
        double height = graphPane.getPrefHeight() > 0 ? graphPane.getPrefHeight() : 600;

        double centerX = width / 2;
        double centerY = height / 2;
        // Radio donde se colocan los nodos (forma circular)
        double radiusLayout = Math.min(width, height) / 2.8;
        // Tamaño de cada nodo
        double nodeRadius = 35;

        // Crear nodos
        for (int i = 0; i < users.size(); i++) {
            Usuario user = users.get(i);

            // Calcular posición circular usando ángulo
            double angle = (2 * Math.PI / users.size()) * i;

            double x = centerX + radiusLayout * Math.cos(angle);
            double y = centerY + radiusLayout * Math.sin(angle);

            // Crear nodo visual
            Group node = createUserNode(user, x, y, nodeRadius);

            // Obtener círculo principal
            Circle circle = (Circle) node.getChildren().get(0);

            // Guardar referencia
            nodeByUsername.put(user.getUsername(), circle);

            // Agregar al panel
            graphPane.getChildren().add(node);
        }

        // Dibujar conexiones
        Set<String> drawn = new HashSet<>();

        for (Usuario user : users) {
            String fromUser = user.getUsername();
            Set<String> friends = service.getFriends(fromUser);

            if (friends == null || friends.isEmpty()) continue;

            for (String toUser : friends) {

                //Clave única para evitar duplicados
                String key = fromUser.compareToIgnoreCase(toUser) < 0
                        ? fromUser + "-" + toUser
                        : toUser + "-" + fromUser;

                if (drawn.contains(key)) continue;

                Circle from = nodeByUsername.get(fromUser);
                Circle to = nodeByUsername.get(toUser);

                if (from == null || to == null) continue;

                Line line = new Line(
                        from.getCenterX(),
                        from.getCenterY(),
                        to.getCenterX(),
                        to.getCenterY()
                );

                line.setStrokeWidth(2);

                graphPane.getChildren().add(0, line);
                drawn.add(key);
            }
        }
    }

    // Crea el nodo visual de un usuario.
    private Group createUserNode(Usuario user, double x, double y, double radius) {
        // Obtiene el color según el grupo del usuario
        Color borderColor = getUserColor(user);

        // Círculo base del nodo
        Circle circle = new Circle(x, y, radius);
        circle.setFill(Color.WHITE);
        circle.setStroke(borderColor);
        circle.setStrokeWidth(2);

        // Imagen del avatar
        ImageView avatar = createAvatar(user, x, y, radius);

        // Etiqueta con usernam
        Label label = new Label(user.getUsername());
        label.setLayoutX(x - 30);
        label.setLayoutY(y + radius + 10);

        // Agrupar todo
        Group group = new Group(circle, avatar, label);

        // Evento de click para interactuar con el nodo
        group.setOnMouseClicked(e -> handleClick(user.getUsername(), e.getButton()));

        return group;
    }

    // Crea la imagen del avatar del usuario.
    private ImageView createAvatar(Usuario user, double x, double y, double radius) {
        // Carga la imagen del avatar
        Image img = loadAvatar(user);

        ImageView view = new ImageView(img);
        view.setFitWidth(radius * 1.9);
        view.setFitHeight(radius * 1.9);

        // Centrar imagen sobre el nodo
        view.setLayoutX(x - (view.getFitWidth() / 2));
        view.setLayoutY(y - (view.getFitHeight() / 2));

        // Recorte circular para que el avatar se vea redondo
        Circle clip = new Circle(
                view.getFitWidth() / 2,
                view.getFitHeight() / 2,
                radius * 0.78
        );

        view.setClip(clip);
        view.setMouseTransparent(true); // Permite que el click pase al nodo

        return view;
    }

    // Obtiene la imagen del avatar según el tipo.
    private Image loadAvatar(Usuario user) {
        return AvatarUtils.getAvatarImage(user.getAvatarType());
    }

    // Maneja los clicks sobre los nodos.
    // Click izquierdo → crear amistad
    // Click derecho → eliminar amistad
    private void handleClick(String clicked, MouseButton button) {
        try {
            // Primer click: seleccionar usuario
            if (selectedUser == null) {
                selectedUser = clicked;
                selectedMode = button;
                highlight(clicked);
                return;
            }
            // Evita seleccionar el mismo usuario
            if (selectedUser.equals(clicked)) {
                clearSelection();
                showAlert(Alert.AlertType.WARNING, "Aviso", "No puedes seleccionarte a ti mismo.");
                return;
            }
            // Click izquierdo → crear amistad
            if (selectedMode == MouseButton.PRIMARY) {
                service.addFriend(selectedUser, clicked);
                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Amistad creada",
                        selectedUser + " ahora eres amigo de " + clicked
                );
            } else { // Click derecho → eliminar amistad
                service.removeFriend(selectedUser, clicked);
                showAlert(
                  Alert.AlertType.INFORMATION,
                  "Amistad eliminada",
                        "Se eliminó la amistad entre " + selectedUser + " y " + clicked
                );
            }
            // Resetear selección y redibujar
            clearSelection();
            renderGraph();

        } catch (Exception e) {
            clearSelection();
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    // Resalta un nodo seleccionado.
    private void highlight(String username) {
        clearHighlights();
        Circle c = nodeByUsername.get(username);
        if (c != null) {
            c.setStroke(Color.ORANGE);
            c.setStrokeWidth(5);
        }
    }

    //Quita los resaltados de todos los nodos.
    private void clearHighlights() {
        for (String username : nodeByUsername.keySet()) {
            // Busca el usuario para recuperar su color original
            Usuario user = findUser(username);
            Color color = user != null ? getUserColor(user) : Color.GRAY;

            Circle c = nodeByUsername.get(username);
            // Restaurar color original
            c.setStroke(color);
            c.setStrokeWidth(3);
        }
    }

    // Limpia la selección actual.
    private void clearSelection() {
        selectedUser = null;
        selectedMode = null;
        clearHighlights();
    }

    // Busca un usuario por username.
    private Usuario findUser(String username) {
        return service.getAllUsers()
                .stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    // Obtiene el color del usuario según su grupo.
    private Color getUserColor(Usuario user) {
        try {
            if (user.getGrupo() != null && user.getGrupo().getColor() != null) {
                return Color.web(String.valueOf(user.getGrupo().getColor()));
            }
        } catch (Exception ignored) {}

        return Color.GRAY;
    }

    // Construye la ruta de la imagen del avatar según el tipo del usuario.
    private String getAvatarPath(Usuario user) {
        // Verifica si el usuario tiene un tipo de avatar asignado
        if (user.getAvatarType() != null) {
            return "/images/" + user.getAvatarType().name().toLowerCase() + ".png";
        }
        return "/images/default.png"; // Retorna una imagen por defecto si no hay avatar
    }

    // Muestra alertas al usuario.
    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
    @FXML
    private void mostrarUsuarioConMasAmigos() {
        Usuario u = service.obtenerUsuarioConMasAmigos();

        if (u == null) {
            showAlert(Alert.AlertType.INFORMATION, "Resultado", "No hay usuarios.");
            return;
        }

        showAlert(
                Alert.AlertType.INFORMATION,
                "Usuario con más amigos",
                u.getUsername() + " tiene " + u.getCantidadAmigos() + " amigos."
        );
    }
}