// Generated Model using mg.razherana.generator
// Goood Luck coding!

package mg.razherana.tests.models;

import mg.razherana.lorm.annot.columns.Column;
import mg.razherana.lorm.annot.general.Table;
import mg.razherana.lorm.Lorm;
import mg.razherana.lorm.annot.columns.ForeignColumn;

@Table("comments")
public class Comment extends Lorm<Comment> { 
	@Column(value = "id", primaryKey = true)
	private int id;

	@Column(value = "post")
	@ForeignColumn(name = "id", model = Post.class)
	private int post;

	@Column(value = "user")
	@ForeignColumn(name = "id", model = User.class)
	private int user;

	@Column(value = "content")
	private String content;

	public int getId() { return id; }

	public void setId(int id) { this.id = id; }

	public int getPost() { return post; }

	public void setPost(int post) { this.post = post; }

	public int getUser() { return user; }

	public void setUser(int user) { this.user = user; }

	public String getContent() { return content; }

	public void setContent(String content) { this.content = content; }
}