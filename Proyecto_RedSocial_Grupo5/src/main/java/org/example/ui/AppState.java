package org.example.ui;

import org.example.service.RedSocialService;

// Clase que mantiene el estado global de la aplicación.
// Permite que todos los controladores accedan a los mismos datos
// (usuarios, amistades, grupos) sin crear múltiples instancias.
public final class AppState {

    //Instancia única del servicio de la red social
    private static final RedSocialService SERVICE = new RedSocialService();

    // Constructor privado para evitar la creación de instancias. Esta clase no debe ser instanciada.
    private AppState() {}

    // Retorna la instancia única del servicio.
    // Permite que cualquier controlador acceda a la misma información.
    public static RedSocialService getService() {
        return SERVICE;
    }
}