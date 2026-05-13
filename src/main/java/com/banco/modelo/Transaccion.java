package com.banco.model;

import java.sql.Timestamp;

public class Transaccion {
    private int id;
    private int emisorId;
    private int receptorId;
    private double monto;
    private String descripcion;
    private String tipoTransaccion;
    private Timestamp fecha;

    private String emisorNombre;  // Para consultas JOIN
    private String receptorNombre;


    public Transaccion() {}

    public Transaccion(int emisorId, int receptorId, double monto, String descripcion) {
        this.emisorId = emisorId;
        this.receptorId = receptorId;
        this.monto = monto;
        this.descripcion = descripcion;
        this.tipoTransaccion = "TRANSFERENCIA";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEmisorId() { return emisorId; }
    public void setEmisorId(int emisorId) { this.emisorId = emisorId; }

    public int getReceptorId() { return receptorId; }
    public void setReceptorId(int receptorId) { this.receptorId = receptorId; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipoTransaccion() { return tipoTransaccion; }
    public void setTipoTransaccion(String tipoTransaccion) { this.tipoTransaccion = tipoTransaccion; }

    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }

    public String getEmisorNombre() { return emisorNombre; }
    public void setEmisorNombre(String emisorNombre) { this.emisorNombre = emisorNombre; }

    public String getReceptorNombre() { return receptorNombre; }
    public void setReceptorNombre(String receptorNombre) { this.receptorNombre = receptorNombre; }
}