package com.banco.gui;

import com.banco.model.Usuario;
import com.banco.service.UsuarioService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardAdmin extends JFrame {
    private UsuarioService usuarioService;
    private JTable tablaUsuarios;
    private DefaultTableModel tableModel;

    public DashboardAdmin() {
        this.usuarioService = new UsuarioService();
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setTitle("Dashboard Administrador - Banco Java");
        setSize(1000, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Estadísticas", crearPanelEstadisticas());

        tabbedPane.addTab("Gestión de Usuarios", crearPanelUsuarios());

        tabbedPane.addTab("Reportes", crearPanelReportes());

        add(tabbedPane);
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultPieDataset dataset = new DefaultPieDataset();
        List<Usuario> usuarios = usuarioService.listarTodosUsuarios();
        for (Usuario u : usuarios) {
            dataset.setValue(u.getNombreUsuario(), u.getSaldo());
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Distribución de Saldos por Usuario",
                dataset,
                true, true, false
        );
        ChartPanel piePanel = new ChartPanel(pieChart);

        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        usuarios.stream()
                .sorted((a, b) -> Double.compare(b.getSaldo(), a.getSaldo()))
                .limit(5)
                .forEach(u -> barDataset.addValue(u.getSaldo(), "Saldo", u.getNombreUsuario()));

        JFreeChart barChart = ChartFactory.createBarChart(
                "Top 5 Usuarios con Mayor Saldo",
                "Usuario",
                "Saldo ($)",
                barDataset
        );
        ChartPanel barPanel = new ChartPanel(barChart);

        panel.add(piePanel);
        panel.add(barPanel);

        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        statsPanel.add(crearTarjetaEstadistica("Total Usuarios", String.valueOf(usuarios.size()), new Color(52, 152, 219)));
        statsPanel.add(crearTarjetaEstadistica("Saldo Total", "$" + String.format("%,.2f", usuarios.stream().mapToDouble(Usuario::getSaldo).sum()), new Color(46, 204, 113)));
        statsPanel.add(crearTarjetaEstadistica("Promedio Saldo", "$" + String.format("%,.2f", usuarios.stream().mapToDouble(Usuario::getSaldo).average().orElse(0)), new Color(241, 196, 15)));
        statsPanel.add(crearTarjetaEstadistica("Saldo Máximo", "$" + String.format("%,.2f", usuarios.stream().mapToDouble(Usuario::getSaldo).max().orElse(0)), new Color(231, 76, 60)));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panel, BorderLayout.CENTER);
        wrapper.add(statsPanel, BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel crearTarjetaEstadistica(String titulo, String valor, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        JLabel tituloLabel = new JLabel(titulo, SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel valorLabel = new JLabel(valor, SwingConstants.CENTER);
        valorLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valorLabel.setForeground(color);

        card.add(tituloLabel, BorderLayout.NORTH);
        card.add(valorLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tabla de usuarios
        String[] columnas = {"ID", "Usuario", "Nombre Completo", "Email", "Saldo", "Admin", "Activo"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaUsuarios = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnDesactivar = new JButton("Desactivar Usuario");
        JButton btnExportar = new JButton("Exportar a Excel");

        btnRefrescar.addActionListener(e -> cargarDatos());
        btnDesactivar.addActionListener(e -> desactivarUsuario());


        buttonPanel.add(btnRefrescar);
        buttonPanel.add(btnDesactivar);
        buttonPanel.add(btnExportar);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelReportes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JButton btnGenerarReporte = new JButton("Generar Reporte General");
        btnGenerarReporte.addActionListener(e -> {
            StringBuilder reporte = new StringBuilder();
            reporte.append("=== REPORTE GENERAL DEL BANCO ===\n");
            reporte.append("Fecha: ").append(new java.util.Date()).append("\n\n");

            List<Usuario> usuarios = usuarioService.listarTodosUsuarios();
            reporte.append("RESUMEN DE USUARIOS:\n");
            reporte.append(String.format("%-20s %-20s %-15s\n", "Usuario", "Email", "Saldo"));
            reporte.append("=".repeat(60)).append("\n");

            for (Usuario u : usuarios) {
                reporte.append(String.format("%-20s %-20s $%-14.2f\n",
                        u.getNombreUsuario(),
                        u.getEmail() != null ? u.getEmail() : "N/A",
                        u.getSaldo()));
            }

            double totalSaldo = usuarios.stream().mapToDouble(Usuario::getSaldo).sum();
            reporte.append("\n".repeat(60)).append("\n");
            reporte.append("TOTAL GENERAL: $").append(String.format("%,.2f", totalSaldo));

            reportArea.setText(reporte.toString());
        });

        panel.add(btnGenerarReporte, BorderLayout.NORTH);
        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);

        return panel;
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);
        List<Usuario> usuarios = usuarioService.listarTodosUsuarios();

        for (Usuario u : usuarios) {
            Object[] row = {
                    u.getId(),
                    u.getNombreUsuario(),
                    u.getNombreCompleto() != null ? u.getNombreCompleto() : "N/A",
                    u.getEmail() != null ? u.getEmail() : "N/A",
                    String.format("$%,.2f", u.getSaldo()),
                    u.isEsAdmin() ? "Sí" : "No",
                    u.isActivo() ? "Activo" : "Inactivo"
            };
            tableModel.addRow(row);
        }
    }

    private void desactivarUsuario() {
        int selectedRow = tablaUsuarios.getSelectedRow();
        if (selectedRow >= 0) {
            String usuario = (String) tableModel.getValueAt(selectedRow, 1);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Desactivar usuario: " + usuario + "?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Usuario desactivado (funcionalidad pendiente de implementar en DAO)");
                cargarDatos();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario primero");
        }
    }
}
