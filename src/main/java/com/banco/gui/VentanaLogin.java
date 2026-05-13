package com.banco.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import com.banco.model.Usuario;
import com.banco.service.UsuarioService;

public class VentanaLogin extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnEntrar;
    private JButton btnRegistrar;
    private UsuarioService usuarioService;

    public VentanaLogin() {
        this.usuarioService = new UsuarioService();
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        setTitle("Banco Digital - Acceso Seguro");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 900, 600, 20, 20));

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(25, 118, 210),
                        getWidth(), getHeight(), new Color(21, 101, 192));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout()); // ← CORREGIDO: usar BorderLayout en lugar de GridBagLayout

        JPanel loginPanel = crearLoginPanel();
        mainPanel.add(loginPanel, BorderLayout.CENTER);

        crearTitleBar(mainPanel);

        add(mainPanel);
    }

    private JPanel crearLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel lblTitle = new JLabel("Banco Digital", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        JLabel lblSubtitle = new JLabel("Bienvenido a tu banca en línea", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(200, 200, 200));
        gbc.gridy = 1;
        panel.add(lblSubtitle, gbc);

        gbc.gridy = 2;
        panel.add(Box.createVerticalStrut(30), gbc);

        JLabel lblUser = new JLabel("Usuario");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(Color.WHITE);
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(lblUser, gbc);

        txtUser = new JTextField(20);
        estilizarCampoTexto(txtUser);
        gbc.gridy = 4;
        panel.add(txtUser, gbc);

        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(Color.WHITE);
        gbc.gridy = 5;
        panel.add(lblPass, gbc);

        txtPass = new JPasswordField(20);
        estilizarCampoTexto(txtPass);
        gbc.gridy = 6;
        panel.add(txtPass, gbc);

        JCheckBox chkMostrarPass = new JCheckBox("Mostrar contraseña");
        chkMostrarPass.setForeground(Color.WHITE);
        chkMostrarPass.setOpaque(false);
        chkMostrarPass.addActionListener(e -> {
            if (chkMostrarPass.isSelected()) {
                txtPass.setEchoChar((char) 0);
            } else {
                txtPass.setEchoChar('•');
            }
        });
        gbc.gridy = 7;
        panel.add(chkMostrarPass, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);

        btnEntrar = new JButton("Iniciar Sesión");
        estilizarBoton(btnEntrar, new Color(76, 175, 80));
        btnEntrar.addActionListener(e -> login());

        btnRegistrar = new JButton("Crear Cuenta");
        estilizarBoton(btnRegistrar, new Color(33, 150, 243));
        btnRegistrar.addActionListener(e -> {
            new VentanaRegistro().setVisible(true);
            dispose();
        });

        buttonPanel.add(btnEntrar);
        buttonPanel.add(btnRegistrar);

        gbc.gridy = 8;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(buttonPanel, gbc);

        JLabel lblForgot = new JLabel("¿Olvidaste tu contraseña?", SwingConstants.CENTER);
        lblForgot.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblForgot.setForeground(new Color(200, 200, 255));
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblForgot.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(VentanaLogin.this,
                        "Contacta al administrador para restablecer tu contraseña.\nEmail: soporte@bancodigital.com",
                        "Recuperar Contraseña",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            public void mouseEntered(MouseEvent e) {
                lblForgot.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                lblForgot.setForeground(new Color(200, 200, 255));
            }
        });
        gbc.gridy = 9;
        panel.add(lblForgot, gbc);

        return panel;
    }

    private void estilizarCampoTexto(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        campo.setBackground(new Color(255, 255, 255, 220));
    }

    private void estilizarBoton(JButton boton, Color color) {
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                boton.setBackground(color);
            }
        });
    }

    private void crearTitleBar(JPanel parent) {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        titleBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));

        JButton btnClose = new JButton("✕");
        btnClose.setFont(new Font("Arial", Font.BOLD, 16));
        btnClose.setForeground(Color.WHITE);
        btnClose.setBackground(new Color(0, 0, 0, 0));
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> System.exit(0));

        JButton btnMinimize = new JButton("─");
        btnMinimize.setFont(new Font("Arial", Font.BOLD, 16));
        btnMinimize.setForeground(Color.WHITE);
        btnMinimize.setBackground(new Color(0, 0, 0, 0));
        btnMinimize.setBorderPainted(false);
        btnMinimize.setFocusPainted(false);
        btnMinimize.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMinimize.addActionListener(e -> setState(Frame.ICONIFIED));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnMinimize);
        buttonPanel.add(btnClose);

        titleBar.add(buttonPanel, BorderLayout.EAST);

        MouseAdapter ma = new MouseAdapter() {
            int x, y;
            public void mousePressed(MouseEvent e) {
                x = e.getX();
                y = e.getY();
            }
            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - x, e.getYOnScreen() - y);
            }
        };
        titleBar.addMouseListener(ma);
        titleBar.addMouseMotionListener(ma);

        parent.add(titleBar, BorderLayout.NORTH);
    }

    private void login() {
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            mostrarMensajeError("Por favor, ingresa tus credenciales");
            return;
        }

        btnEntrar.setText("Cargando...");
        btnEntrar.setEnabled(false);

        SwingWorker<Usuario, Void> worker = new SwingWorker<>() {
            @Override
            protected Usuario doInBackground() throws Exception {
                return usuarioService.autenticarUsuario(username, password);
            }

            @Override
            protected void done() {
                try {
                    Usuario usuario = get();
                    if (usuario != null) {
                        JOptionPane.showMessageDialog(VentanaLogin.this,
                                " ¡Bienvenido " + usuario.getNombreUsuario() + "!",
                                "Login Exitoso",
                                JOptionPane.INFORMATION_MESSAGE);

                        if (usuario.isEsAdmin()) {
                            new DashboardAdmin().setVisible(true);
                        } else {
                            new VentanaPrincipal(usuario).setVisible(true);
                        }
                        dispose();
                    } else {
                        mostrarMensajeError("Usuario o contraseña incorrectos");
                        txtPass.setText("");
                        btnEntrar.setText("Iniciar Sesión");
                        btnEntrar.setEnabled(true);
                    }
                } catch (Exception ex) {
                    mostrarMensajeError("Error de conexión: " + ex.getMessage());
                    btnEntrar.setText("Iniciar Sesión");
                    btnEntrar.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void mostrarMensajeError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
    }
}