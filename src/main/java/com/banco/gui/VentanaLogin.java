package com.banco.gui;

import com.banco.datos.ExcelManager;
import javax.swing.*;
import java.awt.*;

public class VentanaLogin extends JFrame {
    private JTextField txtUser = new JTextField();
    private JPasswordField txtPass = new JPasswordField();

    public VentanaLogin() {
        setTitle("Banco Java - Acceso");
        setSize(1350, 1280);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 1, 10, 5));

        add(new JLabel("Usuario:", SwingConstants.CENTER));
        add(txtUser);
        add(new JLabel("Contraseña:", SwingConstants.CENTER));
        add(txtPass);

        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 0));
        JButton btnEntrar = new JButton("Login");
        JButton btnRegistrar = new JButton("Registrarse");

        panelBotones.add(btnEntrar);
        panelBotones.add(btnRegistrar);
        add(panelBotones);


        btnEntrar.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());

            if (ExcelManager.validarUsuario(user, pass)) {

                com.banco.datos.GestionSesiones.registrarEvento(user, "INICIO DE SESIÓN");

                new VentanaPrincipal(user).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales Incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        btnRegistrar.addActionListener(e -> {
            new VentanaRegistro().setVisible(true);
            this.dispose();
        });

    }
}
