package org.example.ui;

import org.example.service.RedSocialService;

public final class AppState {
    private static final RedSocialService SERVICE = new RedSocialService();

    private AppState() {}

    public static RedSocialService getService() {
        return SERVICE;
    }
}