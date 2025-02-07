package mg.razherana.generator.dbproperties;

import java.util.HashMap;

public class Table {
  private HashMap<String, Column> columns = new HashMap<>();
  private String name;
  private String ogName;

  public String getOgName() {
    return ogName;
  }

  public void setOgName(String ogName) {
    this.ogName = ogName;
  }

  public Table(String name) {
    this.name = name;
    setOgName(name);
  }

  public void setColumns(HashMap<String, Column> columns) {
    this.columns = columns;
  }

  public HashMap<String, Column> getColumns() {
    return columns;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void addColumn(Column column) {
    columns.put(column.getOgName(), column);
  }

  public String getName() {
    return name;
  }

  public Column getColumn(String name) {
    return columns.get(name);
  }
}
