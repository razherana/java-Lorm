package mg.razherana.tests;

import java.sql.Connection;
import java.sql.SQLException;

import mg.razherana.database.DatabaseConnection;
import mg.razherana.generator.Generator;

public class ConnectionTest {
  public static void main(String[] args) {
    // Test the connection
    try {
      DatabaseConnection connection = DatabaseConnection.fromDotEnv("database.xml");
      Connection conn = connection.getConnection();
      conn.close();

      // Generate the models
      var gen = new Generator("generator.xml");
      gen.generateFiles();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
