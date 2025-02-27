package mg.razherana.tests;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import mg.razherana.database.DatabaseConnection;
import mg.razherana.tests.models.User;

public class ModelTests {
  public static void main(String[] args) {
    System.out.println("ModelTests");

    try (Connection conn = DatabaseConnection.fromDotEnv("database.xml").getConnection();) {

      boolean isInsert = false;

      if (isInsert) {
        try (FileWriter fileWriter = new FileWriter("data.sql")) {
          int count = 500;
          fileWriter.append("INSERT INTO user VALUES");
          for (int i = 1; i < count; i++) {
            fileWriter.append(" (" + i + ", 'User " + i + "'),");
          }
          fileWriter.append(" (" + count + ", 'User " + count + "');\n");
          fileWriter.append("INSERT INTO post VALUES");
          for (int i = 1, p = 1; i < count; i++) {
            for (int j = 1; j <= count; j++, p++) {
              fileWriter.append(" (" + p + ", " + i + ", 'Title " + j + "', 'test'),");
            }
          }
          fileWriter.append(" (" + count * count + ", " + count + ", 'Title " + count + "', 'test');");
        } catch (Exception e) {
          e.printStackTrace();
        }
        return;
      }

      int testCount = 100;
      double[] nums = new double[testCount];

      for (int i = 0; i < testCount; i++) {

        long start = System.nanoTime();
        System.out.println("Request started !!!");

        var ex = new User();
        ex.addNestedEagerLoad("posts");
        var exes = ex.all(conn);

        for (var exel : exes) {
          exel.getPosts(null);
        }

        nums[i] = ((float) (System.nanoTime() - start)) / 1e6;
      }

      Arrays.sort(nums);

      System.out.printf("Min = %.2f ms\n", nums[0]);
      System.out.printf("Max = %.2f ms\n", nums[testCount - 1]);

      double avg = Arrays.stream(nums).average().orElse(0);
      System.out.printf("Avg = %.2f ms\n", avg);

      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
