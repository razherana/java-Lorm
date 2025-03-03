package mg.razherana.tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

import mg.razherana.tests.models.User;

public class ModelTests {

    static class Conn {
        private static Connection conn;

        public static Connection getConn() throws SQLException {
            return conn == null
                    ? (conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "razherana", ""))
                    : conn;
        }
    }

    public static void main(String[] args) {
        System.out.println("ModelTests");

        try (Connection conn = Conn.getConn()) {
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
