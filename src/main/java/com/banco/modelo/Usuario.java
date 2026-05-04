package com.banco.modelo;

public class Usuario {
    private String nombre;
    private String numeroCuenta;
    private double saldo;

    public Usuario(String nombre, String numeroCuenta, double saldo) {
        this.nombre = nombre;
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
    }


    public String getNombre() { return nombre; }
    public String getNumeroCuenta() { return numeroCuenta; }
    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
}
