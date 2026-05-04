package com.banco.main;

import com.banco.gui.VentanaLogin;

public class Main {
    public static void main(String[] args) {
        // Ejecutar la interfaz en el hilo de despacho de eventos
        javax.swing.SwingUtilities.invokeLater(() -> {
            new VentanaLogin().setVisible(true);
        });
    }
}