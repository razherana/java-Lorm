package mg.razherana.tests;

import java.sql.Connection;
import java.sql.SQLException;

import mg.razherana.database.DatabaseConnection;

public class ConnectionTest {
  public static void main(String[] args) {
    // Test the connection
    try {
      DatabaseConnection connection = DatabaseConnection.fromDotEnv("database.xml");
      Connection conn = connection.getConnection();

      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
