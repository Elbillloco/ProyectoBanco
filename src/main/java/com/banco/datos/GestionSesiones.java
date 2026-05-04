package com.banco.datos;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GestionSesiones {
    private static final String LOG_FILE = "log_sesiones.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void registrarEvento(String usuario, String accion) {
        String fechaHora = LocalDateTime.now().format(formatter);
        String linea = String.format("[%s] Usuario: %s - Acción: %s", fechaHora, usuario, accion);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(linea);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error al registrar sesión: " + e.getMessage());
        }
    }

}