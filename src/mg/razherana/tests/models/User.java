// Generated Model using mg.razherana.generator
// Happy Codingg!

package mg.razherana.tests.models;

import java.sql.SQLException;
import mg.razherana.lorm.annot.columns.Column;
import java.sql.Connection;
import mg.razherana.lorm.annot.relations.HasMany;
import mg.razherana.lorm.annot.general.Table;
import mg.razherana.lorm.Lorm;
import java.util.ArrayList;

@Table("user")
@HasMany(model = Comment.class, foreignKey = "user", relationName = "comments")
@HasMany(model = Post.class, foreignKey = "user", relationName = "posts")
@HasMany(model = Like.class, foreignKey = "user_id", relationName = "likes")
public class User extends Lorm<User> { 
	@Column(value = "name", getter = "getName", setter = "setName")
	private String name;

	@Column(value = "id", primaryKey = true, getter = "getId", setter = "setId")
	private int id;

	public String getName() { return name; }

	public void setName(String name) { this.name = name; }

	public int getId() { return id; }

	public void setId(int id) { this.id = id; }

	public ArrayList<Comment> getComments(Connection connection) throws SQLException { return hasMany("comments", connection); }

	public ArrayList<Post> getPosts(Connection connection) throws SQLException { return hasMany("posts", connection); }

	public ArrayList<Like> getLikes(Connection connection) throws SQLException { return hasMany("likes", connection); }
}