package com.banco.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.banco.model.Transaccion;
import com.banco.model.Usuario;
import com.banco.service.TransaccionService;
import com.banco.service.UsuarioService;
import com.banco.utils.SessionManager;

public class VentanaPrincipal extends JFrame {
    private Usuario usuarioActual;
    private UsuarioService usuarioService;
    private TransaccionService transaccionService;
    private JLabel lblSaldo;
    private JTable tablaTransacciones;
    private DefaultTableModel tableModel;
    private JButton btnTransferir;
    private JButton btnRecargar;

    public VentanaPrincipal(Usuario usuario) {
        this.usuarioActual = usuario;
        this.usuarioService = new UsuarioService();
        this.transaccionService = new TransaccionService();
        initComponents();
        cargarDatos();
        setVisible(true);
    }

    private void initComponents() {
        setTitle("Banco Digital - Panel de Control");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 1100, 700, 15, 15));

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 242, 245));

        // Sidebar izquierdo
        JPanel sidebar = crearSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        // Contenido principal
        JPanel contentPanel = crearContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Barra de título
        crearTitleBar(mainPanel);

        add(mainPanel);
    }

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setBackground(new Color(25, 118, 210));
        sidebar.setBorder(BorderFactory.createEmptyBorder(60, 20, 20, 20));

        // Avatar del usuario
        JPanel avatarPanel = new JPanel(new BorderLayout());
        avatarPanel.setOpaque(false);
        avatarPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel lblAvatar = new JLabel();
        lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        lblAvatar.setIcon(crearAvatarInicial(usuarioActual.getNombreUsuario()));
        avatarPanel.add(lblAvatar, BorderLayout.CENTER);

        JLabel lblNombre = new JLabel(usuarioActual.getNombreCompleto() != null ?
                usuarioActual.getNombreCompleto() : usuarioActual.getNombreUsuario());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setHorizontalAlignment(SwingConstants.CENTER);
        avatarPanel.add(lblNombre, BorderLayout.SOUTH);

        sidebar.add(avatarPanel);
        sidebar.add(Box.createVerticalStrut(20));

        // Menú de navegación
        String[][] menuItems = {
                {"Inicio", "home"},
                {"Transferencias", "transfer"},
                {"Mis Cuentas", "accounts"},
                {"Historial", "history"},
                {"Configuración", "settings"},
                {"Cerrar Sesión", "logout"}
        };

        for (String[] item : menuItems) {
            JButton menuBtn = crearBotonMenu(item[0] + "  " + item[1], item[2]);
            sidebar.add(menuBtn);
            sidebar.add(Box.createVerticalStrut(10));
        }

        return sidebar;
    }

    private JButton crearBotonMenu(String texto, String accion) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(25, 118, 210));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(33, 150, 243));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(25, 118, 210));
            }
        });

        btn.addActionListener(e -> {
            switch (accion) {
                case "logout":
                    cerrarSesion();
                    break;
                case "transfer":
                    mostrarDialogoTransferencia();
                    break;
                case "history":
                    cargarHistorialCompleto();
                    break;
            }
        });

        return btn;
    }

    private JPanel crearContentPanel() {
        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBackground(new Color(240, 242, 245));
        content.setBorder(BorderFactory.createEmptyBorder(60, 20, 20, 20));

        JPanel headerPanel = crearHeaderSaldo();
        content.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        centerPanel.setOpaque(false);

        JPanel accionesPanel = crearPanelAccionesRapidas();
        centerPanel.add(accionesPanel);

        JPanel transaccionesPanel = crearPanelUltimasTransacciones();
        centerPanel.add(transaccionesPanel);

        content.add(centerPanel, BorderLayout.CENTER);

        return content;
    }

    private JPanel crearHeaderSaldo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JLabel lblTitulo = new JLabel("Mi Balance Total");
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitulo.setForeground(new Color(117, 117, 117));

        lblSaldo = new JLabel();
        lblSaldo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblSaldo.setForeground(new Color(76, 175, 80));

        JLabel lblCuenta = new JLabel("Cuenta: ****" + usuarioActual.getId());
        lblCuenta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCuenta.setForeground(new Color(117, 117, 117));

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.add(lblTitulo, BorderLayout.NORTH);
        infoPanel.add(lblSaldo, BorderLayout.CENTER);
        infoPanel.add(lblCuenta, BorderLayout.SOUTH);

        // Botón recargar saldo
        btnRecargar = new JButton("🔄 Actualizar");
        btnRecargar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRecargar.setBackground(new Color(33, 150, 243));
        btnRecargar.setForeground(Color.WHITE);
        btnRecargar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnRecargar.setFocusPainted(false);
        btnRecargar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRecargar.addActionListener(e -> cargarDatos());

        panel.add(infoPanel, BorderLayout.WEST);
        panel.add(btnRecargar, BorderLayout.EAST);

        return panel;
    }

    private JPanel crearPanelAccionesRapidas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitulo = new JLabel("Acciones Rápidas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(new Color(33, 33, 33));
        panel.add(lblTitulo, BorderLayout.NORTH);

        JPanel botonesPanel = new JPanel(new GridLayout(3, 1, 10, 15));
        botonesPanel.setOpaque(false);
        botonesPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        String[][] acciones = {
                {"Transferir Dinero", "#4CAF50"},
                {"Pagar Servicios", "#2196F3"},
                {"Generar Estado de Cuenta", "#FF9800"}
        };

        for (String[] accion : acciones) {
            JButton btn = new JButton(accion[0] + "  " + accion[1]);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.setBackground(new Color(245, 245, 245));
            btn.setForeground(new Color(33, 33, 33));
            btn.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (accion[1].equals("Transferir Dinero")) {
                btn.addActionListener(e -> mostrarDialogoTransferencia());
            } else if (accion[1].equals("Generar Estado de Cuenta")) {
                btn.addActionListener(e -> generarEstadoCuenta());
            }

            botonesPanel.add(btn);
        }

        panel.add(botonesPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelUltimasTransacciones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitulo = new JLabel("Últimas Transacciones");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(lblTitulo, BorderLayout.NORTH);

        // Tabla de transacciones
        String[] columnas = {"Fecha", "Concepto", "Monto", "Estado"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaTransacciones = new JTable(tableModel);
        tablaTransacciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaTransacciones.setRowHeight(35);
        tablaTransacciones.setShowGrid(false);
        tablaTransacciones.setIntercellSpacing(new Dimension(0, 0));

        tablaTransacciones.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof String) {
                    String montoStr = (String) value;
                    if (montoStr.contains("+")) {
                        c.setForeground(new Color(76, 175, 80));
                    } else if (montoStr.contains("-")) {
                        c.setForeground(new Color(244, 67, 54));
                    }
                    setHorizontalAlignment(SwingConstants.RIGHT);
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaTransacciones);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void cargarDatos() {

        double saldo = usuarioService.obtenerSaldo(usuarioActual.getId());
        lblSaldo.setText(String.format("$%,.2f MXN", saldo));
        usuarioActual.setSaldo(saldo);

        cargarUltimasTransacciones();
    }

    private void cargarUltimasTransacciones() {
        tableModel.setRowCount(0);
        List<Transaccion> transacciones = transaccionService.getUltimasTransacciones(usuarioActual.getId(), 5);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (Transaccion t : transacciones) {
            String concepto;
            String monto;

            if (t.getEmisorId() == usuarioActual.getId()) {
                concepto = "Envío a " + t.getReceptorNombre();
                monto = String.format("-$%,.2f", t.getMonto());
            } else {
                concepto = "Recepción de " + t.getEmisorNombre();
                monto = String.format("+$%,.2f", t.getMonto());
            }

            Object[] row = {
                    sdf.format(t.getFecha()),
                    concepto,
                    monto,
                    "Completado"
            };
            tableModel.addRow(row);
        }
    }

    private void cargarHistorialCompleto() {

        JDialog dialog = new JDialog(this, "Historial Completo de Transacciones", true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnas = {"Fecha", "Emisor", "Receptor", "Monto", "Descripción"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);
        JTable table = new JTable(model);

        List<Transaccion> transacciones = transaccionService.getHistorialCompleto(usuarioActual.getId());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (Transaccion t : transacciones) {
            Object[] row = {
                    sdf.format(t.getFecha()),
                    t.getEmisorNombre(),
                    t.getReceptorNombre(),
                    String.format("$%,.2f", t.getMonto()),
                    t.getDescripcion() != null ? t.getDescripcion() : ""
            };
            model.addRow(row);
        }

        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dialog.dispose());
        panel.add(btnCerrar, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void mostrarDialogoTransferencia() {
        JDialog dialog = new JDialog(this, "Realizar Transferencia", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel lblDestino = new JLabel("Usuario Destino:");
        lblDestino.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblDestino, gbc);

        JTextField txtDestino = new JTextField();
        txtDestino.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDestino.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(txtDestino, gbc);

        JLabel lblMonto = new JLabel("Monto a Transferir:");
        lblMonto.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(lblMonto, gbc);

        JTextField txtMonto = new JTextField();
        txtMonto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMonto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(txtMonto, gbc);

        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lblDescripcion, gbc);

        JTextArea txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDescripcion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(scrollDesc, gbc);

        JLabel lblSaldoDisponible = new JLabel("Saldo disponible: $" + String.format("%,.2f", usuarioActual.getSaldo()));
        lblSaldoDisponible.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblSaldoDisponible.setForeground(new Color(100, 100, 100));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        panel.add(lblSaldoDisponible, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

        JButton btnTransferir = new JButton("Transferir");
        btnTransferir.setBackground(new Color(76, 175, 80));
        btnTransferir.setForeground(Color.WHITE);
        btnTransferir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnTransferir.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btnTransferir.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(158, 158, 158));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancelar.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> dialog.dispose());

        btnTransferir.addActionListener(e -> {
            String destino = txtDestino.getText().trim();
            String montoStr = txtMonto.getText().trim();
            String descripcion = txtDescripcion.getText().trim();

            if (destino.isEmpty() || montoStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Por favor, complete todos los campos",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double monto = Double.parseDouble(montoStr);
                if (monto <= 0) {
                    JOptionPane.showMessageDialog(dialog, "El monto debe ser mayor a 0",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (monto > usuarioActual.getSaldo()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Saldo insuficiente. Saldo disponible: $" + String.format("%,.2f", usuarioActual.getSaldo()),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(dialog,
                        "¿Confirmar transferencia de $" + String.format("%,.2f", monto) + " a " + destino + "?",
                        "Confirmar Transferencia",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    btnTransferir.setText("Procesando...");
                    btnTransferir.setEnabled(false);

                    SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                        @Override
                        protected Boolean doInBackground() throws Exception {
                            return transaccionService.realizarTransferencia(
                                    usuarioActual.getId(), destino, monto, descripcion);
                        }

                        @Override
                        protected void done() {
                            try {
                                boolean exito = get();
                                if (exito) {
                                    JOptionPane.showMessageDialog(dialog,
                                            "Transferencia realizada exitosamente",
                                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                                    cargarDatos();
                                    dialog.dispose();
                                } else {
                                    JOptionPane.showMessageDialog(dialog,
                                            "Error: Usuario destino no encontrado o fondos insuficientes",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                    btnTransferir.setText("Transferir");
                                    btnTransferir.setEnabled(true);
                                }
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(dialog,
                                        "Error al procesar la transferencia",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                                btnTransferir.setText("Transferir");
                                btnTransferir.setEnabled(true);
                            }
                        }
                    };
                    worker.execute();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Monto inválido",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(btnTransferir);
        buttonPanel.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void generarEstadoCuenta() {

        JDialog dialog = new JDialog(this, "Estado de Cuenta", true);
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);

        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(70)).append("\n");
        sb.append("                    ESTADO DE CUENTA BANCARIO\n");
        sb.append("=".repeat(70)).append("\n\n");
        sb.append("Cliente: ").append(usuarioActual.getNombreCompleto() != null ? usuarioActual.getNombreCompleto() : usuarioActual.getNombreUsuario()).append("\n");
        sb.append("Usuario: ").append(usuarioActual.getNombreUsuario()).append("\n");
        sb.append("Email: ").append(usuarioActual.getEmail() != null ? usuarioActual.getEmail() : "No registrado").append("\n");
        sb.append("Fecha: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date())).append("\n\n");
        sb.append("-".repeat(70)).append("\n");
        sb.append(String.format("SALDO ACTUAL: $%,.2f MXN\n", usuarioActual.getSaldo()));
        sb.append("-".repeat(70)).append("\n\n");
        sb.append("ÚLTIMAS TRANSACCIONES:\n\n");
        sb.append(String.format("%-20s %-30s %-15s\n", "FECHA", "CONCEPTO", "MONTO"));
        sb.append("-".repeat(70)).append("\n");

        List<Transaccion> transacciones = transaccionService.getUltimasTransacciones(usuarioActual.getId(), 10);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (Transaccion t : transacciones) {
            String concepto;
            String monto;
            if (t.getEmisorId() == usuarioActual.getId()) {
                concepto = "Envío a " + t.getReceptorNombre();
                monto = String.format("-$%,.2f", t.getMonto());
            } else {
                concepto = "Recepción de " + t.getEmisorNombre();
                monto = String.format("+$%,.2f", t.getMonto());
            }
            sb.append(String.format("%-20s %-30s %-15s\n", sdf.format(t.getFecha()), concepto, monto));
        }

        sb.append("\n").append("=".repeat(70)).append("\n");
        sb.append("Este documento es una representación digital de su estado de cuenta.\n");
        sb.append("Para cualquier aclaración, contacte al servicio al cliente.\n");
        sb.append("Email: soporte@bancodigital.com | Tel: 55 1234 5678\n");

        textArea.setText(sb.toString());

        JScrollPane scroll = new JScrollPane(textArea);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dialog.dispose());

        JButton btnExportar = new JButton("Exportar a TXT");
        btnExportar.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("estado_cuenta_" + usuarioActual.getNombreUsuario() + ".txt"));
            if (fileChooser.showSaveDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                try (java.io.FileWriter fw = new java.io.FileWriter(fileChooser.getSelectedFile())) {
                    fw.write(textArea.getText());
                    JOptionPane.showMessageDialog(dialog, "Archivo exportado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error al exportar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(btnExportar);
        buttonPanel.add(btnCerrar);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas cerrar sesión?",
                "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.cerrarSesion();
            new VentanaLogin().setVisible(true);
            dispose();
        }
    }

    private void crearTitleBar(JPanel parent) {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        titleBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));

        JLabel lblTitle = new JLabel("🏦 Banco Digital - Panel de Control");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(new Color(33, 33, 33));
        titleBar.add(lblTitle, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);

        JButton btnMinimize = new JButton("─");
        estilizarBotonControl(btnMinimize);
        btnMinimize.addActionListener(e -> setState(Frame.ICONIFIED));

        JButton btnClose = new JButton("✕");
        estilizarBotonControl(btnClose);
        btnClose.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Deseas cerrar la aplicación?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

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

    private void estilizarBotonControl(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(new Color(100, 100, 100));
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(Color.RED);
            }
            public void mouseExited(MouseEvent e) {
                btn.setForeground(new Color(100, 100, 100));
            }
        });
    }

    private ImageIcon crearAvatarInicial(String nombre) {

        String inicial = nombre.substring(0, 1).toUpperCase();
        int size = 80;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillOval(0, 0, size, size);
        g2d.setColor(new Color(25, 118, 210));
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 40));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (size - fm.stringWidth(inicial)) / 2;
        int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(inicial, x, y);
        g2d.dispose();

        return new ImageIcon(image);
    }
}