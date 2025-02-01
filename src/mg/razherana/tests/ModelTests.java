package mg.razherana.tests;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import mg.razherana.database.DatabaseConnection;
import mg.razherana.tests.models.Post;
import mg.razherana.tests.models.User;

public class ModelTests {
  public static void main(String[] args) {
    System.out.println("ModelTests");

    // Generate the models
    // var gen = new Generator("generator.xml");
    // gen.generateFiles();

    try {
      DatabaseConnection connection = DatabaseConnection.fromDotEnv("database.xml");
      Connection conn = connection.getConnection();

      long start = System.nanoTime();
      System.out.println("Request started !!!");

      User ex = new User();
      ex.addNestedEagerLoad("posts");
      ArrayList<User> users = ex.all(conn);

      for (User user : users) {
        System.out.println(user.getId() + " - " + user.getName());
        for (Post post : user.getPosts(conn)) {
          System.out.println("  " + post.getId() + " - " + post.getTitle());
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
