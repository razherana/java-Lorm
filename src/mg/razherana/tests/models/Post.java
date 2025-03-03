package mg.razherana.tests.models;

import mg.razherana.lorm.Lorm;
import mg.razherana.lorm.annot.columns.Column;
import mg.razherana.lorm.annot.columns.ForeignColumn;
import mg.razherana.lorm.annot.general.Table;

@Table("post")
public class Post extends Lorm<Post> {
    @Column(value = "id", primaryKey = true)
    private int id;

    @Column("user")
    @ForeignColumn(name = "id", model = User.class)
    private int user;

    @Column("title")
    private String title;

    @Column("description")
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