/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author monic
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException; 
public class Conexion {
       // Configura tus datos de conexión aquí
    private static final String URL = "jdbc:postgresql://localhost:5434/proyecto";
    private static final String USER = "postgres";
    private static final String PASS = "hdp";  
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el Driver de PostgreSQL", e);
        }
    }
        
}
