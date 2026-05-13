package com.banco.gui;

import com.banco.service.UsuarioService;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.regex.Pattern;

public class VentanaRegistro extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JPasswordField txtConfirmPass;
    private JTextField txtNombreCompleto;
    private JTextField txtEmail;
    private JTextField txtSaldo;
    private UsuarioService usuarioService;

    public VentanaRegistro() {
        this.usuarioService = new UsuarioService();
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        setTitle("Banco Digital - Crear Cuenta");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setSize(500, 650);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 500, 650, 15, 15));

        // Panel principal con gradiente
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(25, 118, 210),
                        0, getHeight(), new Color(21, 101, 192));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        JPanel formPanel = crearFormularioRegistro();
        mainPanel.add(formPanel, BorderLayout.CENTER);

        crearTitleBar(mainPanel);
        add(mainPanel);
    }

    private JPanel crearFormularioRegistro() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(60, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);

        // Título
        JLabel lblTitle = new JLabel("Crear Nueva Cuenta", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        gbc.gridy = 1;
        panel.add(Box.createVerticalStrut(20), gbc);

        // Campos del formulario
        gbc.gridwidth = 1;

        // Usuario
        addField(panel, gbc, "👤 Usuario:", 2, txtUser = new JTextField());
        addField(panel, gbc, "🔒 Contraseña:", 3, txtPass = new JPasswordField());
        addField(panel, gbc, "✓ Confirmar Contraseña:", 4, txtConfirmPass = new JPasswordField());
        addField(panel, gbc, "📝 Nombre Completo:", 5, txtNombreCompleto = new JTextField());
        addField(panel, gbc, "📧 Correo Electrónico:", 6, txtEmail = new JTextField());
        addField(panel, gbc, "💰 Saldo Inicial:", 7, txtSaldo = new JTextField("500.00"));

        // Checkbox términos
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        JCheckBox chkTerminos = new JCheckBox("Acepto los Términos y Condiciones");
        chkTerminos.setForeground(Color.WHITE);
        chkTerminos.setOpaque(false);
        chkTerminos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(chkTerminos, gbc);

        // Botones
        gbc.gridy = 9;
        gbc.insets = new Insets(20, 10, 10, 10);
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);

        JButton btnRegistrar = crearBoton("Registrarse", new Color(76, 175, 80));
        JButton btnCancelar = crearBoton("Cancelar", new Color(158, 158, 158));

        btnRegistrar.addActionListener(e -> registrarUsuario(chkTerminos.isSelected()));
        btnCancelar.addActionListener(e -> {
            new VentanaLogin().setVisible(true);
            dispose();
        });

        buttonPanel.add(btnRegistrar);
        buttonPanel.add(btnCancelar);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String label, int y, JTextField field) {
        JLabel lbl = new JLabel(label);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(lbl, gbc);

        estilizarCampoTexto(field);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void estilizarCampoTexto(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        campo.setBackground(new Color(255, 255, 255, 220));
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });

        return btn;
    }

    private void registrarUsuario(boolean aceptaTerminos) {
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword());
        String confirmPassword = new String(txtConfirmPass.getPassword());
        String nombreCompleto = txtNombreCompleto.getText().trim();
        String email = txtEmail.getText().trim();
        String saldoStr = txtSaldo.getText().trim();

        // Validaciones
        if (username.isEmpty() || password.isEmpty() || nombreCompleto.isEmpty()) {
            mostrarError("Por favor, complete todos los campos obligatorios");
            return;
        }

        if (username.length() < 3) {
            mostrarError("El usuario debe tener al menos 3 caracteres");
            return;
        }

        if (password.length() < 4) {
            mostrarError("La contraseña debe tener al menos 4 caracteres");
            return;
        }

        if (!password.equals(confirmPassword)) {
            mostrarError("Las contraseñas no coinciden");
            return;
        }

        if (!email.isEmpty() && !Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(email).matches()) {
            mostrarError("Correo electrónico inválido");
            return;
        }

        if (!aceptaTerminos) {
            mostrarError("Debe aceptar los Términos y Condiciones");
            return;
        }

        double saldoInicial;
        try {
            saldoInicial = Double.parseDouble(saldoStr);
            if (saldoInicial < 0) {
                mostrarError("El saldo inicial no puede ser negativo");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarError("Saldo inválido");
            return;
        }

        // Mostrar loading
        JButton btnRegistrar = (JButton) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(0)).getComponent(9);
        String originalText = btnRegistrar.getText();
        btnRegistrar.setText("Registrando...");
        btnRegistrar.setEnabled(false);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return usuarioService.registrarUsuario(username, password, nombreCompleto, email, saldoInicial);
            }

            @Override
            protected void done() {
                try {
                    boolean exito = get();
                    if (exito) {
                        JOptionPane.showMessageDialog(VentanaRegistro.this,
                                "✅ ¡Cuenta creada exitosamente!\n\n" +
                                        "Ya puedes iniciar sesión con tus credenciales.",
                                "Registro Exitoso",
                                JOptionPane.INFORMATION_MESSAGE);
                        new VentanaLogin().setVisible(true);
                        dispose();
                    } else {
                        mostrarError("El nombre de usuario ya existe. Por favor, elige otro.");
                        btnRegistrar.setText(originalText);
                        btnRegistrar.setEnabled(true);
                    }
                } catch (Exception ex) {
                    mostrarError("Error al registrar: " + ex.getMessage());
                    btnRegistrar.setText(originalText);
                    btnRegistrar.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de Registro", JOptionPane.ERROR_MESSAGE);
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
        btnClose.addActionListener(e -> {
            new VentanaLogin().setVisible(true);
            dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnClose);
        titleBar.add(buttonPanel, BorderLayout.EAST);

        parent.add(titleBar, BorderLayout.NORTH);
    }
}