package com.banco.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.banco.config.DatabaseConfig;
import com.banco.model.Transaccion;

public class TransaccionDAO {

    public boolean createTransaccion(Transaccion transaccion) throws SQLException {
        String sql = "INSERT INTO transacciones (emisor_id, receptor_id, monto, descripcion, tipo_transaccion) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, transaccion.getEmisorId());
            stmt.setInt(2, transaccion.getReceptorId());
            stmt.setDouble(3, transaccion.getMonto());
            stmt.setString(4, transaccion.getDescripcion());
            stmt.setString(5, transaccion.getTipoTransaccion());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    transaccion.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    public List<Transaccion> getTransaccionesByUsuario(int usuarioId) throws SQLException {
        List<Transaccion> transacciones = new ArrayList<>();
        String sql = "SELECT t.*, u1.nombre_usuario as emisor_nombre, u2.nombre_usuario as receptor_nombre " +
                "FROM transacciones t " +
                "JOIN usuarios u1 ON t.emisor_id = u1.id " +
                "JOIN usuarios u2 ON t.receptor_id = u2.id " +
                "WHERE t.emisor_id = ? OR t.receptor_id = ? " +
                "ORDER BY t.fecha DESC LIMIT 50";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaccion t = new Transaccion();
                t.setId(rs.getInt("id"));
                t.setEmisorId(rs.getInt("emisor_id"));
                t.setReceptorId(rs.getInt("receptor_id"));
                t.setMonto(rs.getDouble("monto"));
                t.setDescripcion(rs.getString("descripcion"));
                t.setTipoTransaccion(rs.getString("tipo_transaccion"));
                t.setFecha(rs.getTimestamp("fecha"));
                t.setEmisorNombre(rs.getString("emisor_nombre"));
                t.setReceptorNombre(rs.getString("receptor_nombre"));
                transacciones.add(t);
            }
        }
        return transacciones;
    }

    public List<Transaccion> getUltimasTransacciones(int usuarioId, int limite) throws SQLException {
        List<Transaccion> transacciones = new ArrayList<>();
        String sql = "SELECT t.*, u1.nombre_usuario as emisor_nombre, u2.nombre_usuario as receptor_nombre " +
                "FROM transacciones t " +
                "JOIN usuarios u1 ON t.emisor_id = u1.id " +
                "JOIN usuarios u2 ON t.receptor_id = u2.id " +
                "WHERE t.emisor_id = ? OR t.receptor_id = ? " +
                "ORDER BY t.fecha DESC LIMIT ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, usuarioId);
            stmt.setInt(3, limite);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaccion t = new Transaccion();
                t.setId(rs.getInt("id"));
                t.setEmisorId(rs.getInt("emisor_id"));
                t.setReceptorId(rs.getInt("receptor_id"));
                t.setMonto(rs.getDouble("monto"));
                t.setDescripcion(rs.getString("descripcion"));
                t.setTipoTransaccion(rs.getString("tipo_transaccion"));
                t.setFecha(rs.getTimestamp("fecha"));
                t.setEmisorNombre(rs.getString("emisor_nombre"));
                t.setReceptorNombre(rs.getString("receptor_nombre"));
                transacciones.add(t);
            }
        }
        return transacciones;
    }
}