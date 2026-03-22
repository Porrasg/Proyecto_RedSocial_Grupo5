package org.example.ui;

import javafx.scene.image.Image;
import org.example.model.AvatarType;

import java.util.Objects;

// Clase utilitaria para manejar la carga de imágenes de avatares.
public final class AvatarUtils {

    // Constructor privado para evitar instanciación.
    private AvatarUtils() {}

    // Retorna la imagen correspondiente al tipo de avatar.
    // Si el avatar es null o no coincide con ninguno, se usa una imagen por defecto.
    public static Image getAvatarImage(AvatarType avatarType) {
        String path;

        if (avatarType == null) {
            path = "/images/avatar_default.png";
        } else {
            switch (avatarType) {
                case MASCULINO -> path = "/images/avatar_masculino.png";
                case FEMENINO -> path = "/images/avatar_femenino.png";
                case PREDETERMINADO -> path = "/images/avatar_default.jpg";
                default -> path = "/images/avatar_default.jpg";
            }
        }

        // Carga la imagen desde recursos
        return new Image(Objects.requireNonNull(
                AvatarUtils.class.getResourceAsStream(path),
                "No se encontró la imagen: " + path
        ));
    }
}