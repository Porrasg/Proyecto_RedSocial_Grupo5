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
public class RedSocialController implements Initializable {

    @FXML
    private AnchorPane graphPane;

    private final RedSocialService service = AppState.getService();

    private final Map<String, Circle> nodeByUsername = new HashMap<>();
    private String selectedUser = null;
    private MouseButton selectedMode = null;

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
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        renderGraph();
    }

    public void renderGraph() {
        graphPane.getChildren().clear();
        nodeByUsername.clear();

        List<Usuario> users = service.getAllUsers();

        if (users == null || users.isEmpty()) {
            return;
        }

        double width = graphPane.getPrefWidth() > 0 ? graphPane.getPrefWidth() : 900;
        double height = graphPane.getPrefHeight() > 0 ? graphPane.getPrefHeight() : 600;

        double centerX = width / 2;
        double centerY = height / 2;
        double radiusLayout = Math.min(width, height) / 2.8;
        double nodeRadius = 35;

        // Crear nodos
        for (int i = 0; i < users.size(); i++) {
            Usuario user = users.get(i);

            double angle = (2 * Math.PI / users.size()) * i;
            double x = centerX + radiusLayout * Math.cos(angle);
            double y = centerY + radiusLayout * Math.sin(angle);

            Group node = createUserNode(user, x, y, nodeRadius);
            Circle circle = (Circle) node.getChildren().get(0);

            nodeByUsername.put(user.getUsername(), circle);
            graphPane.getChildren().add(node);
        }

        // Dibujar conexiones
        for (Usuario user : users) {
            String fromUser = user.getUsername();
            Set<Object> friends = service.getFriends(fromUser);

            if (friends == null || friends.isEmpty()) {
                continue;
            }

            for (Object toUser : friends) {
                Circle from = nodeByUsername.get(fromUser);
                Circle to = nodeByUsername.get(toUser);

                if (from == null || to == null) {
                    continue;
                }

                Line line = new Line(
                        from.getCenterX(),
                        from.getCenterY(),
                        to.getCenterX(),
                        to.getCenterY()
                );

                line.setStrokeWidth(2);
                graphPane.getChildren().add(0, line);
            }
        }
    }

    private Group createUserNode(Usuario user, double x, double y, double radius) {
        Color borderColor = getUserColor(user);

        Circle circle = new Circle(x, y, radius);
        circle.setFill(Color.WHITE);
        circle.setStroke(borderColor);
        circle.setStrokeWidth(2);

        ImageView avatar = createAvatar(user, x, y, radius);

        Label label = new Label(user.getUsername());
        label.setLayoutX(x - 30);
        label.setLayoutY(y + radius + 10);

        Group group = new Group(circle, avatar, label);

        group.setOnMouseClicked(e -> handleClick(user.getUsername(), e.getButton()));

        return group;
    }

    private ImageView createAvatar(Usuario user, double x, double y, double radius) {
        Image img = loadAvatar(user);

        ImageView view = new ImageView(img);
        view.setFitWidth(radius * 1.9);
        view.setFitHeight(radius * 1.9);

        view.setLayoutX(x - (view.getFitWidth() / 2));
        view.setLayoutY(y - (view.getFitHeight() / 2));

        Circle clip = new Circle(
                view.getFitWidth() / 2,
                view.getFitHeight() / 2,
                radius * 0.78
        );

        view.setClip(clip);
        view.setMouseTransparent(true);

        return view;
    }

    private Image loadAvatar(Usuario user) {
        return AvatarUtils.getAvatarImage(user.getAvatarType());
    }

    private void handleClick(String clicked, MouseButton button) {
        try {
            if (selectedUser == null) {
                selectedUser = clicked;
                selectedMode = button;
                highlight(clicked);
                return;
            }

            if (selectedUser.equals(clicked)) {
                clearSelection();
                showAlert(Alert.AlertType.WARNING, "Aviso", "No puedes seleccionarte a ti mismo.");
                return;
            }

            if (selectedMode == MouseButton.PRIMARY) {
                service.addFriend(selectedUser, clicked);
                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Amistad creada",
                        selectedUser + " ahora eres amigo de " + clicked
                );
            } else {
                service.removeFriend(selectedUser, clicked);
                showAlert(
                  Alert.AlertType.INFORMATION,
                  "Amistad eliminada",
                  selectedUser + " amistad eliminada con " + clicked
                );
            }

            clearSelection();
            renderGraph();

        } catch (Exception e) {
            clearSelection();
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void highlight(String username) {
        clearHighlights();
        Circle c = nodeByUsername.get(username);
        if (c != null) {
            c.setStroke(Color.ORANGE);
            c.setStrokeWidth(5);
        }
    }

    private void clearHighlights() {
        for (String username : nodeByUsername.keySet()) {
            Usuario user = findUser(username);
            Color color = user != null ? getUserColor(user) : Color.GRAY;

            Circle c = nodeByUsername.get(username);
            c.setStroke(color);
            c.setStrokeWidth(3);
        }
    }

    private void clearSelection() {
        selectedUser = null;
        selectedMode = null;
        clearHighlights();
    }

    private Usuario findUser(String username) {
        return service.getAllUsers()
                .stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    private Color getUserColor(Usuario user) {
        try {
            if (user.getGrupo() != null && user.getGrupo().getColor() != null) {
                return Color.web(String.valueOf(user.getGrupo().getColor()));
            }
        } catch (Exception ignored) {}

        return Color.GRAY;
    }

    private String getAvatarPath(Usuario user) {
        if (user.getAvatarType() != null) {
            return "/images/" + user.getAvatarType().name().toLowerCase() + ".png";
        }
        return "/images/default.png";
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}