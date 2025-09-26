package com.mycompany.proyectogimnasio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    // URL pública de Railway
    private static final String URL = "jdbc:mysql://centerbeam.proxy.rlwy.net:23892/railway";
    private static final String USER = "root";
    private static final String PASSWORD = "ShlYFjtRmPFlizYSEyizwuhZgYpWHijg";

    // Método para obtener la conexión
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}