/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectogimnasio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 *
 * @author julia
 */

public class ConexionBD {
    private static final String URL = "jdbc:mysql://localhost:3307/gimnasio"; 
    private static final String USER = "root";   // cambia por tu usuario
    private static final String PASSWORD = "";   // cambia por tu contraseña
    
    public static void probarConexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // fuerza carga del driver
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conectadooo a la BD gimnasioo");
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
