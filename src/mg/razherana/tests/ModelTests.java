package mg.razherana.tests;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import mg.razherana.database.DatabaseConnection;
import mg.razherana.lorm.Lorm;
import mg.razherana.lorm.annot.columns.Column;
import mg.razherana.lorm.annot.columns.ForeignColumn;
import mg.razherana.lorm.annot.general.Table;
import mg.razherana.lorm.annot.relations.EagerLoad;
import mg.razherana.lorm.annot.relations.HasMany;
import mg.razherana.lorm.annot.relations.OneToOne;

public class ModelTests {

  @Table("user")
  @HasMany(model = Post.class)
  @EagerLoad("posts")
  static class User extends Lorm<User> {
    @Column(primaryKey = true)
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

    public ArrayList<Post> getPosts(Connection conn) throws SQLException {
      return hasMany("posts", conn);
    }
  }

  @Table("post")
  @OneToOne(model = User.class)
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

    public User getUser(Connection conn) throws SQLException {
      return oneToOne("user", conn);
    }

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

      long start = System.nanoTime();
      System.out.println("Request started !!!");

      User ex = new User();
      ex.addNestedEagerLoad("posts").addEagerLoad("user");
      ArrayList<User> users = ex.all(conn);

      for (User user : users) {
        System.out.println(user.getId() + " - " + user.getName());
        for (Post post : user.getPosts(null)) {
          System.out.println("  " + post.getId() + " - " + post.getTitle());
          System.out.println(post.getUser(null).getName() + " - " +
          post.getDescription());
        }
      }

      System.out.println("Request finished in " + (System.nanoTime() - start) / 1E6 + "ms");

      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
