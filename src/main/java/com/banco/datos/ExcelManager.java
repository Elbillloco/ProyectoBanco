package com.banco.datos;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;

public class ExcelManager {
    private static final String FILE_PATH = "usuarios.xlsx";

    static { inicializarExcel(); }

    private static void inicializarExcel() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try (Workbook wb = new XSSFWorkbook(); FileOutputStream out = new FileOutputStream(FILE_PATH)) {
                Sheet s = wb.createSheet("Usuarios");
                Row r = s.createRow(0);
                r.createCell(0).setCellValue("Usuario");
                r.createCell(1).setCellValue("Password");
                r.createCell(2).setCellValue("Saldo");

                // Usuario de prueba inicial
                Row user = s.createRow(1);
                user.createCell(0).setCellValue("user123");
                user.createCell(1).setCellValue("pass123");
                user.createCell(2).setCellValue(5000.0);

                wb.write(out);
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public static boolean validarUsuario(String user, String pass) {
        try (FileInputStream fis = new FileInputStream(FILE_PATH); Workbook wb = new XSSFWorkbook(fis)) {
            Sheet s = wb.getSheetAt(0);
            for (Row r : s) {
                if (r.getRowNum() == 0) continue;
                if (r.getCell(0).getStringCellValue().equals(user) &&
                        r.getCell(1).getStringCellValue().equals(pass)) return true;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public static double obtenerSaldo(String user) {
        try (FileInputStream fis = new FileInputStream(FILE_PATH); Workbook wb = new XSSFWorkbook(fis)) {
            Sheet s = wb.getSheetAt(0);
            for (Row r : s) {
                if (r.getCell(0).getStringCellValue().equals(user)) return r.getCell(2).getNumericCellValue();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }


    public static boolean registrarUsuario(String user, String pass, double saldoInicial) {
        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             Workbook wb = new XSSFWorkbook(fis)) {

            Sheet s = wb.getSheetAt(0);


            for (Row r : s) {
                if (r.getRowNum() == 0) continue;
                if (r.getCell(0).getStringCellValue().equals(user)) return false;
            }

            // Crear nueva fila al final
            int lastRow = s.getLastRowNum();
            Row newRow = s.createRow(lastRow + 1);
            newRow.createCell(0).setCellValue(user);
            newRow.createCell(1).setCellValue(pass);
            newRow.createCell(2).setCellValue(saldoInicial);

            try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
                wb.write(fos);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean procesarTransferencia(String emisor, String receptor, double monto) {
        try (FileInputStream fis = new FileInputStream(FILE_PATH); Workbook wb = new XSSFWorkbook(fis)) {
            Sheet s = wb.getSheetAt(0);
            Row rowEmisor = null, rowReceptor = null;

            for (Row r : s) {
                if (r.getRowNum() == 0) continue;
                if (r.getCell(0).getStringCellValue().equals(emisor)) rowEmisor = r;
                if (r.getCell(0).getStringCellValue().equals(receptor)) rowReceptor = r;
            }

            if (rowEmisor != null && rowReceptor != null && rowEmisor.getCell(2).getNumericCellValue() >= monto) {
                rowEmisor.getCell(2).setCellValue(rowEmisor.getCell(2).getNumericCellValue() - monto);
                rowReceptor.getCell(2).setCellValue(rowReceptor.getCell(2).getNumericCellValue() + monto);
                try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) { wb.write(fos); return true; }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
