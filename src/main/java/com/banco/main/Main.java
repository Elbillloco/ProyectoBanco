package com.banco.main;

import com.banco.gui.VentanaLogin;

public class Main {
    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(() -> {
            new VentanaLogin().setVisible(true);
        });
    }
}