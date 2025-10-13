/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author monic
 */
import java.sql.Connection;

public class Prueba {

    // Cree esta clase para hacer una prueba si conectaba la base de datos ya que estaba teniendo problemas con ello
    public static void main(String[] args) {
        try {
            Connection con = Conexion.getConnection();
            System.out.println(" Conexión exitosa a PostgreSQL");
            con.close();
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

}
