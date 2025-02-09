package mg.razherana.tests;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import mg.razherana.database.DatabaseConnection;
import mg.razherana.tests.models.Like;
import mg.razherana.tests.models.User;

public class ModelTests {
  public static void main(String[] args) {
    System.out.println("ModelTests");

    try {
      DatabaseConnection connection = DatabaseConnection.fromDotEnv("database.xml");
      Connection conn = connection.getConnection();

      long start = System.nanoTime();
      System.out.println("Request started !!!");

      User ex = new User();
      ex.addNestedEagerLoad("likes").addEagerLoad("user");
      ArrayList<User> users = ex.all(conn);

      for (User user : users) {
        System.out.println(user.getId() + " - " + user.getName());
        for (Like like : user.getLikes(conn)) {
          System.out.println("  " + like.getId() + " - " + like.getPost());
          System.out.println(like.getUser(conn).getName());
          // System.out.println(post.getUser(null).getName() + " - " +
          // post.getDescription());
        }
      }

      System.out.println("Request finished in " + (System.nanoTime() - start) / 1E6 + "ms");

      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
