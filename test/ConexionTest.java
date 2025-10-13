import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.sql.SQLException;

public class ConexionTest {

    @Test
    public void testGetConnection() {
        try (Connection conn = Conexion.getConnection()) {
            assertNotNull(conn, "La conexión no debe ser null");
            assertFalse(conn.isClosed(), "La conexión debe estar abierta");
        } catch (SQLException e) {
            fail("No se pudo establecer conexión: " + e.getMessage());
        }
    }
}
    
