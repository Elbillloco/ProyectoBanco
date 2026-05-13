// src/main/java/com/banco/Main.java
package com.banco;

import com.banco.config.DatabaseConfig;
import com.banco.gui.VentanaLogin;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Configurar Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Agregar hook para cerrar pool de conexiones
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConfig.closePool();
        }));

        SwingUtilities.invokeLater(() -> {
            new VentanaLogin().setVisible(true);
        });
    }
}