// Generated Model using mg.razherana.generator
// Goood Luck coding!

package mg.razherana.tests.models;

import mg.razherana.lorm.annot.columns.Column;
import mg.razherana.lorm.annot.general.Table;
import mg.razherana.lorm.Lorm;
import mg.razherana.lorm.annot.columns.ForeignColumn;

@Table("post")
public class Post extends Lorm<Post> { 
	@Column(value = "description")
	private String description;

	@Column(value = "id", primaryKey = true)
	private int id;

	@Column(value = "title")
	private String title;

	@Column(value = "user")
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
}