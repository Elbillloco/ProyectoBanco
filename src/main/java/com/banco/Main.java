package com.banco;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.banco.config.DatabaseConfig;
import com.banco.gui.VentanaLogin;

public class Main {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConfig.closePool();
        }));

        SwingUtilities.invokeLater(() -> {
            new VentanaLogin().setVisible(true);
        });
    }
}