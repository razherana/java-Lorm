// Generated Model using mg.razherana.generator
// Goood Luck coding!

package mg.razherana.tests.models;

import mg.razherana.lorm.annot.columns.Column;
import mg.razherana.lorm.annot.general.Table;
import mg.razherana.lorm.annot.relations.HasMany;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import mg.razherana.lorm.Lorm;

@Table("user")
@HasMany(model = Post.class, foreignKey = "user")
// Can be shorthanded : @HasMany(model = Post.class)
public class User extends Lorm<User> {
	@Column(value = "name")
	private String name;

	@Column(value = "id", primaryKey = true)
	private int id;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<Post> getPosts(Connection conn) throws SQLException {
		return hasMany("posts", conn);
	}
}