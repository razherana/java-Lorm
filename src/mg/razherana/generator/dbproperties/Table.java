package mg.razherana.generator.dbproperties;

import java.util.HashMap;

public class Table {
  private HashMap<String, Column> columns = new HashMap<>();
  private transient HashMap<String, Column> columnsOgName = new HashMap<>();
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

  private void updateOgName() {
    columnsOgName = new HashMap<>();
    for (var col : columns.values()) {
      columnsOgName.put(col.getOgName(), col);
    }
  }

  public void setColumns(HashMap<String, Column> columns) {
    this.columns = columns;
    updateOgName();
  }

  public HashMap<String, Column> getColumns() {
    return columns;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void addColumn(Column column) {
    columns.put(column.getOgName(), column);
    columnsOgName.put(column.getOgName(), column);
  }

  public String getName() {
    return name;
  }

  public Column getColumn(String name) {
    return columns.get(name);
  }

  public Column getColumnOg(String name) {
    return columnsOgName.get(name);
  }
}
