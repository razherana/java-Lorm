// Generated Model using mg.razherana.generator
// Happy Codingg!

package mg.razherana.tests.models;

import java.sql.SQLException;
import mg.razherana.lorm.annot.columns.Column;
import java.sql.Connection;
import mg.razherana.lorm.annot.relations.BelongsTo;
import mg.razherana.lorm.annot.general.Table;
import mg.razherana.lorm.annot.columns.ForeignColumn;
import mg.razherana.lorm.Lorm;

@Table("comments")
@BelongsTo(model = User.class, foreignKey = "user", relationName = "user")
@BelongsTo(model = Post.class, foreignKey = "post", relationName = "post")
public class Comment extends Lorm<Comment> { 
	@Column(value = "id", primaryKey = true, getter = "getId", setter = "setId")
	private int id;

	@Column(value = "post", getter = "getPost", setter = "setPost")
	@ForeignColumn(name = "id", model = Post.class)
	private int post;

	@Column(value = "user", getter = "getUser", setter = "setUser")
	@ForeignColumn(name = "id", model = User.class)
	private int user;

	@Column(value = "content", getter = "getContent", setter = "setContent")
	private String content;

	public int getId() { return id; }

	public void setId(int id) { this.id = id; }

	public int getPost() { return post; }

	public void setPost(int post) { this.post = post; }

	public int getUser() { return user; }

	public void setUser(int user) { this.user = user; }

	public String getContent() { return content; }

	public void setContent(String content) { this.content = content; }

	public User getUser(Connection connection) throws SQLException { return belongsTo("user", connection); }

	public Post getPost(Connection connection) throws SQLException { return belongsTo("post", connection); }
}