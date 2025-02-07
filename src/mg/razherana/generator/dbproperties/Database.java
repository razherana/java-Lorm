package mg.razherana.generator.dbproperties;

import java.util.HashMap;

public class Database {
  private HashMap<String, Table> tables = new HashMap<>();

  public HashMap<String, Table> getTables() {
    return tables;
  }

  public void setTables(HashMap<String, Table> tables) {
    this.tables = tables;
  }

  public void addTable(Table table) {
    tables.put(table.getName(), table);
  }

  public Table getTable(String name) {
    return tables.get(name);
  }

  public boolean hasTable(String name) {
    return tables.containsKey(name);
  }

  public void removeTable(String name) {
    tables.remove(name);
  }

  public void clearTables() {
    tables.clear();
  }

  public int size() {
    return tables.size();
  }

  public boolean isEmpty() {
    return tables.isEmpty();
  }

  public void remove(Table table) {
    tables.remove(table.getName());
  }

  // Debug functions
  public void printTables() {
    for (Table table : getTables().values()) {
      System.out.println("Table: " + table.getName());
      for (Column column : table.getColumns().values()) {
        System.out.println("  Column: " + column.getName());
        System.out.println("    Type: " + column.getType());
        System.out.println("    JavaType: " + column.getJavaType());
        System.out.println("    PrimaryKey: " + column.isPrimaryKey());
        System.out.println("    ForeignKey: " + column.isForeignKey());
        if (column.isForeignKey()) {
          System.out.println("    ForeignKeyTable: " + column.getForeignTable().getName());
          System.out.println("    ForeignKeyColumn: " + column.getForeignColumn().getName());
        }
      }
    }
  }
}
