package com.banco.service;

import com.banco.dao.TransaccionDAO;
import com.banco.dao.UsuarioDAO;
import com.banco.model.Transaccion;
import com.banco.model.Usuario;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransaccionService {
    private TransaccionDAO transaccionDAO;
    private UsuarioDAO usuarioDAO;

    public TransaccionService() {
        this.transaccionDAO = new TransaccionDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    public List<Transaccion> getUltimasTransacciones(int usuarioId, int limite) {
        try {
            return transaccionDAO.getUltimasTransacciones(usuarioId, limite);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Transaccion> getHistorialCompleto(int usuarioId) {
        try {
            return transaccionDAO.getTransaccionesByUsuario(usuarioId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public boolean realizarTransferencia(int emisorId, String receptorUsername, double monto, String descripcion) {
        try {

            if (monto <= 0) return false;

            Usuario emisor = usuarioDAO.findById(emisorId);
            Usuario receptor = usuarioDAO.findByUsername(receptorUsername);

            if (emisor == null || receptor == null) return false;
            if (emisor.getSaldo() < monto) return false;

            double nuevoSaldoEmisor = emisor.getSaldo() - monto;
            double nuevoSaldoReceptor = receptor.getSaldo() + monto;

            usuarioDAO.updateSaldo(emisorId, nuevoSaldoEmisor);
            usuarioDAO.updateSaldo(receptor.getId(), nuevoSaldoReceptor);

            Transaccion transaccion = new Transaccion(emisorId, receptor.getId(), monto, descripcion);
            return transaccionDAO.createTransaccion(transaccion);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}