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

@Table("test_like")
@BelongsTo(model = User.class, foreignKey = "user_id", relationName = "user")
public class Like extends Lorm<Like> { 
	@Column(value = "date", getter = "getDate", setter = "setDate")
	private java.time.LocalDateTime date;

	@Column(value = "id", primaryKey = true, getter = "getId", setter = "setId")
	private int id;

	@Column(value = "post", getter = "getPost", setter = "setPost")
	@ForeignColumn(name = "id", model = Post.class)
	private int post;

	@Column(value = "user_id", getter = "getUserid", setter = "setUserid")
	@ForeignColumn(name = "id", model = User.class)
	private int userId;

	public java.time.LocalDateTime getDate() { return date; }

	public void setDate(java.time.LocalDateTime date) { this.date = date; }

	public int getId() { return id; }

	public void setId(int id) { this.id = id; }

	public int getPost() { return post; }

	public void setPost(int post) { this.post = post; }

	public int getUserid() { return userId; }

	public void setUserid(int userId) { this.userId = userId; }

	public User getUser(Connection connection) throws SQLException { return belongsTo("user", connection); }
}