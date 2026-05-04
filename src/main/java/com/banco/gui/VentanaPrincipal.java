package com.banco.gui;

import com.banco.datos.ExcelManager;
import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
    private String usuarioActual;

    public VentanaPrincipal(String usuario) {
        this.usuarioActual = usuario;
        setTitle("Panel de Control - " + usuario);
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        JButton btnSaldo = new JButton("Ver Estado de Cuenta");
        JButton btnTransf = new JButton("Realizar Transferencia");
        JButton btnCerrar = new JButton("Cerrar Sesión");

        add(btnSaldo); add(btnTransf); add(btnCerrar);

        btnSaldo.addActionListener(e -> {
            double saldo = ExcelManager.obtenerSaldo(usuarioActual);
            JOptionPane.showMessageDialog(this, "Tu saldo es: $" + saldo);
        });

        btnTransf.addActionListener(e -> {
            String dest = JOptionPane.showInputDialog(this, "Usuario destino:");
            String montoStr = JOptionPane.showInputDialog(this, "Monto a transferir:");
            if (dest != null && montoStr != null) {
                double monto = Double.parseDouble(montoStr);
                if (ExcelManager.procesarTransferencia(usuarioActual, dest, monto)) {
                    com.banco.datos.GestionSesiones.registrarEvento(usuarioActual,
                            "TRANSFERENCIA ENVIADA a " + dest + " por $" + monto);
                    JOptionPane.showMessageDialog(this, "Transferencia Exitosa");
                } else {
                    JOptionPane.showMessageDialog(this, "Error: Fondos insuficientes o usuario no existe");
                }
            }
        });


        btnCerrar.addActionListener(e -> {


            com.banco.datos.GestionSesiones.registrarEvento(usuarioActual, "CIERRE DE SESIÓN");

            new VentanaLogin().setVisible(true);
            this.dispose();
        });
    }
}
