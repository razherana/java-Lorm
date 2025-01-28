package mg.razherana.tests;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import mg.razherana.database.DatabaseConnection;
import mg.razherana.lorm.Lorm;
import mg.razherana.lorm.annot.columns.Column;
import mg.razherana.lorm.annot.general.Table;

public class ModelTests {

  @Table("test_user")
  static class User extends Lorm<User> {
    @Column(primaryKey = true, getter = "getId2", setter = "setId")
    private int id;

    public int getId2() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }
  }

  public static void main(String[] args) {
    System.out.println("ModelTests");

    try {
      DatabaseConnection connection = DatabaseConnection.fromDotEnv("database.xml");
      Connection conn = connection.getConnection();

      ArrayList<User> users = new User().all(conn);
      for (User user : users) {
        System.out.println(user.getId2());
      }

      User user = new User();
      user.setId(2);
      user.save(conn);

      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
