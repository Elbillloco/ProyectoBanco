package com.banco.model;

import java.sql.Timestamp;

public class Usuario {
    private int id;
    private String nombreUsuario;
    private String passwordHash;
    private String nombreCompleto;
    private String email;
    private double saldo;
    private boolean esAdmin;
    private boolean activo;
    private Timestamp fechaCreacion;
    private Timestamp ultimoAcceso;

    public Usuario() {}

    public Usuario(String nombreUsuario, String passwordHash, String nombreCompleto, String email, double saldo) {
        this.nombreUsuario = nombreUsuario;
        this.passwordHash = passwordHash;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.saldo = saldo;
        this.esAdmin = false;
        this.activo = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }

    public boolean isEsAdmin() { return esAdmin; }
    public void setEsAdmin(boolean esAdmin) { this.esAdmin = esAdmin; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Timestamp getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(Timestamp ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }
}
