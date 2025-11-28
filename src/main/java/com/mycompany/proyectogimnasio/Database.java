package com.mycompany.proyectogimnasio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    // URL corregida para evitar el error SSLHandshakeException:
    private static final String URL = "jdbc:mysql://gondola.proxy.rlwy.net:51831/railway?useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "dZLeazCTzEKkPnAQFANrKCxyZlNywudL";

    static {
        // Mantenemos la carga explícita, ya que ayudó a avanzar en el diagnóstico.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver MySQL no encontrado.");
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}