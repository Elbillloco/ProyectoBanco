// src/main/java/com/banco/service/UsuarioService.java
package com.banco.service;

import com.banco.dao.UsuarioDAO;
import com.banco.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.List;

public class UsuarioService {
    private UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public boolean registrarUsuario(String username, String password, String nombreCompleto, String email, double saldoInicial) {
        try {
            if (usuarioDAO.findByUsername(username) != null) {
                return false;
            }

            String storedPassword = password;

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreUsuario(username);
            nuevoUsuario.setPasswordHash(storedPassword);
            nuevoUsuario.setNombreCompleto(nombreCompleto);
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setSaldo(saldoInicial);
            nuevoUsuario.setEsAdmin(false);
            nuevoUsuario.setActivo(true);

            return usuarioDAO.createUsuario(nuevoUsuario);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Usuario autenticarUsuario(String username, String password) {
        try {
            if (usuarioDAO.authenticateUser(username, password)) {
                return usuarioDAO.findByUsername(username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double obtenerSaldo(int userId) {
        try {
            Usuario usuario = usuarioDAO.findById(userId);
            return usuario != null ? usuario.getSaldo() : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<Usuario> listarTodosUsuarios() {
        try {
            return usuarioDAO.getAllUsuarios();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }
}