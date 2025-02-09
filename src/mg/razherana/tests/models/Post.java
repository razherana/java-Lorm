// Generated Model using mg.razherana.generator
// Happy Codingg!

package mg.razherana.tests.models;

import java.sql.SQLException;
import mg.razherana.lorm.annot.columns.Column;
import java.sql.Connection;
import mg.razherana.lorm.annot.relations.BelongsTo;
import mg.razherana.lorm.annot.relations.HasMany;
import mg.razherana.lorm.annot.general.Table;
import mg.razherana.lorm.annot.columns.ForeignColumn;
import mg.razherana.lorm.Lorm;
import java.util.ArrayList;

@Table("post")
@BelongsTo(model = User.class, foreignKey = "user", relationName = "user")
@HasMany(model = Comment.class, foreignKey = "post", relationName = "comments")
public class Post extends Lorm<Post> { 
	@Column(value = "description", getter = "getDescription", setter = "setDescription")
	private String description;

	@Column(value = "id", primaryKey = true, getter = "getId", setter = "setId")
	private int id;

	@Column(value = "title", getter = "getTitle", setter = "setTitle")
	private String title;

	@Column(value = "user", getter = "getUser", setter = "setUser")
	@ForeignColumn(name = "id", model = User.class)
	private int user;

	public String getDescription() { return description; }

	public void setDescription(String description) { this.description = description; }

	public int getId() { return id; }

	public void setId(int id) { this.id = id; }

	public String getTitle() { return title; }

	public void setTitle(String title) { this.title = title; }

	public int getUser() { return user; }

	public void setUser(int user) { this.user = user; }

	public User getUser(Connection connection) throws SQLException { return belongsTo("user", connection); }

	public ArrayList<Comment> getComments(Connection connection) throws SQLException { return hasMany("comments", connection); }
}