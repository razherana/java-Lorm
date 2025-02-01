package mg.razherana.generator.dbproperties;

public class Column {
  private String name;
  private String ogName;
  private String type;
  private boolean isPrimaryKey;
  private boolean isForeignKey;
  private Table foreignTable;
  private Column foreignColumn;
  private String javaType;

  public Column(String name) {
    this.name = name;
    setOgName(name);
  }

  public String getOgName() {
    return ogName;
  }

  public void setOgName(String ogName) {
    this.ogName = ogName;
  }

  public String getJavaType() {
    return javaType;
  }

  public void setJavaType(String javaType) {
    this.javaType = javaType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isPrimaryKey() {
    return isPrimaryKey;
  }

  public void setPrimaryKey(boolean isPrimaryKey) {
    this.isPrimaryKey = isPrimaryKey;
  }

  public boolean isForeignKey() {
    return isForeignKey;
  }

  public void setForeignKey(boolean isForeignKey) {
    this.isForeignKey = isForeignKey;
  }

  public Table getForeignTable() {
    return foreignTable;
  }

  public void setForeignTable(Table foreignTable) {
    this.foreignTable = foreignTable;
  }

  public Column getForeignColumn() {
    return foreignColumn;
  }

  public void setForeignColumn(Column foreignColumn) {
    this.foreignColumn = foreignColumn;
  }

}
