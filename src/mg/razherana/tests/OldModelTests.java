package mg.razherana.tests;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mg.dao.annotation.Column;
import mg.dao.annotation.Table;
import mg.daoherana.DaoHerana;
import mg.daoherana.relations.BelongsTo;
import mg.daoherana.relations.HasMany;
import mg.razherana.database.DatabaseConnection;

public class OldModelTests {

  @Table(name = "user")
  @HasMany(model = Post.class, parentKeyGetter = "getId", foreignKeyGetter = "getUser", relationName = "posts")
  static class User extends DaoHerana {
    public User() {
    }

    @Column(isPK = true)
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

  @Table(name = "post")
  @BelongsTo(model = User.class, parentKeyGetter = "getUser", foreignKeyGetter = "getId", relationName = "user")
  static class Post extends DaoHerana {
    public Post() {
    }

    @Column(isPK = true)
    private int id;

    @Column
    private int user;

    @Column
    private String title;

    @Column
    private String description;

    public User getUser(Connection conn) throws SQLException {
      return belongsTo("user", conn);
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
    System.out.println("Old Model Tests");

    try {
      DatabaseConnection connection = DatabaseConnection.fromDotEnv("database.xml");
      Connection conn = connection.getConnection();

      long start = System.nanoTime();
      System.out.println("Request started !!!");

      User ex = new User();
      ex.setMapLoads(Map.of(User.class.getName(), List.of("posts"), Post.class.getName(), List.of("user")));
      User[] users = ex.getAll(conn);

      for (User user : users) {
        System.out.println(user.getId() + " - " + user.getName());
        for (Post post : user.getPosts(conn)) {
          System.out.println("  " + post.getId() + " - " + post.getTitle());
          System.out.println(post.getUser(conn).getName() + " - " + post.getDescription());
        }
      }

      System.out.println("Request finished in " + (System.nanoTime() - start) / 1E6 + "ms");

      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
