package com.banco.utils;

import com.banco.model.Usuario;
import java.util.UUID;

public class SessionManager {
    private static Usuario usuarioActual;
    private static String sessionToken;

    public static void iniciarSesion(Usuario usuario) {
        usuarioActual = usuario;
        sessionToken = UUID.randomUUID().toString();
    }

    public static void cerrarSesion() {
        usuarioActual = null;
        sessionToken = null;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static boolean isLoggedIn() {
        return usuarioActual != null;
    }

    public static boolean isAdmin() {
        return usuarioActual != null && usuarioActual.isEsAdmin();
    }
}