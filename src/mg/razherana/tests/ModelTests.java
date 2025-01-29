package mg.razherana.tests;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import mg.razherana.database.DatabaseConnection;
import mg.razherana.lorm.Lorm;
import mg.razherana.lorm.annot.columns.Column;
import mg.razherana.lorm.annot.columns.ForeignColumn;
import mg.razherana.lorm.annot.general.Table;

public class ModelTests {

  @Table("user")
  static class User extends Lorm<User> {
    @Column(primaryKey = true, getter = "getId", setter = "setId")
    private int id;

    @Column
    private String name;

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  @Table("post")
  static class Post extends Lorm<Post> {
    @Column(primaryKey = true)
    private int id;

    @Column
    @ForeignColumn(model = User.class)
    private int user;

    @Column
    private String title;

    @Column
    private String description;

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public int getUser() {
      return user;
    }

    public void setUser(int user) {
      this.user = user;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }
  }

  public static void main(String[] args) {
    System.out.println("ModelTests");

    try {
      DatabaseConnection connection = DatabaseConnection.fromDotEnv("database.xml");
      Connection conn = connection.getConnection();

      ArrayList<User> users = new User().all(conn);

      for (User user : users)
        System.out.println(user.getId() + " - " + user.getName());

      // User user = new User();
      // user.setName("Herana");
      // user.save(conn);

      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
