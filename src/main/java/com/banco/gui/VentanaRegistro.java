package com.banco.gui;

import com.banco.datos.ExcelManager;
import javax.swing.*;
import java.awt.*;

public class VentanaRegistro extends JFrame {
    private JTextField txtUser = new JTextField();
    private JPasswordField txtPass = new JPasswordField();
    private JTextField txtSaldo = new JTextField("500.0"); // Saldo inicial sugerido

    public VentanaRegistro() {
        setTitle("Registro de Nuevo Usuario");
        setSize(300, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Usuario:")); add(txtUser);
        add(new JLabel("Contraseña:")); add(txtPass);
        add(new JLabel("Saldo Inicial:")); add(txtSaldo);

        JButton btnGuardar = new JButton("Registrar");
        JButton btnVolver = new JButton("Volver");

        add(btnGuardar); add(btnVolver);

        btnGuardar.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());
            try {
                double saldo = Double.parseDouble(txtSaldo.getText());
                if (ExcelManager.registrarUsuario(user, pass, saldo)) {
                    JOptionPane.showMessageDialog(this, "Usuario registrado con éxito");
                    new VentanaLogin().setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "El usuario ya existe");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Saldo inválido");
            }
        });

        btnVolver.addActionListener(e -> {
            new VentanaLogin().setVisible(true);
            this.dispose();
        });
    }
}
