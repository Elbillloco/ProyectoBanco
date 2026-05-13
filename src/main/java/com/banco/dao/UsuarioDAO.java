package com.banco.dao;

import com.banco.config.DatabaseConfig;
import com.banco.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Usuario findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE nombre_usuario = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUsuario(rs);
            }
        }
        return null;
    }

    public Usuario findById(int id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUsuario(rs);
            }
        }
        return null;
    }

    public boolean createUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre_usuario, password_hash, nombre_completo, email, saldo, es_admin) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNombreUsuario());
            stmt.setString(2, usuario.getPasswordHash());
            stmt.setString(3, usuario.getNombreCompleto());
            stmt.setString(4, usuario.getEmail());
            stmt.setDouble(5, usuario.getSaldo());
            stmt.setBoolean(6, usuario.isEsAdmin());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    public boolean updateSaldo(int userId, double nuevoSaldo) throws SQLException {
        String sql = "UPDATE usuarios SET saldo = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, nuevoSaldo);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Usuario> getAllUsuarios() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombre_usuario";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        }
        return usuarios;
    }

    public boolean authenticateUser(String username, String password) throws SQLException {
        Usuario usuario = findByUsername(username);
        if (usuario != null && usuario.isActivo()) {
            String storedPassword = usuario.getPasswordHash();
            if (storedPassword.startsWith("$2a$")) {
                return BCrypt.checkpw(password, storedPassword);
            } else {
                return password.equals(storedPassword);
            }
        }
        return false;
    }

    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNombreUsuario(rs.getString("nombre_usuario"));
        usuario.setPasswordHash(rs.getString("password_hash"));
        usuario.setNombreCompleto(rs.getString("nombre_completo"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSaldo(rs.getDouble("saldo"));
        usuario.setEsAdmin(rs.getBoolean("es_admin"));
        usuario.setActivo(rs.getBoolean("activo"));
        usuario.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        usuario.setUltimoAcceso(rs.getTimestamp("ultimo_acceso"));
        return usuario;
    }
}