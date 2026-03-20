package org.example.ui;

import javafx.scene.image.Image;
import org.example.model.AvatarType;

import java.util.Objects;

public final class AvatarUtils {

    private AvatarUtils() {}

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

        return new Image(Objects.requireNonNull(
                AvatarUtils.class.getResourceAsStream(path),
                "No se encontró la imagen: " + path
        ));
    }
}