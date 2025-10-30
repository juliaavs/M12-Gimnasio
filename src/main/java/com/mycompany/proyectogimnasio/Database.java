package com.mycompany.proyectogimnasio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    // URL pública de Railway
    private static final String URL = "jdbc:mysql://gondola.proxy.rlwy.net:51831/railway";
    private static final String USER = "root";
    private static final String PASSWORD = "dZLeazCTzEKkPnAQFANrKCxyZlNywudL";

    // Método para obtener la conexión
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}