package com.mycompany.proyectogimnasio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    
    // --- NUEVA CONFIGURACIÓN ---
    // Host: trolley.proxy.rlwy.net
    // Puerto: 54218
    // Base de datos: railway
private static final String URL = "jdbc:mysql://yamanote.proxy.rlwy.net:12109/railway?useSSL=false&allowPublicKeyRetrieval=true";
private static final String PASSWORD = "JssENrLuDmthJZiPTjpkXVovNBaZdUWS";    
private static final String USER = "root";

    static {
        try {
            // Carga del driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver MySQL no encontrado.");
            e.printStackTrace();
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}