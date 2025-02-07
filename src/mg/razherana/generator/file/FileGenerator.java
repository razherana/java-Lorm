package mg.razherana.generator.file;

import java.io.File;
import java.util.ArrayList;

import mg.razherana.generator.dbproperties.Column;
import mg.razherana.generator.dbproperties.Table;

public class FileGenerator {
  public static StringBuilder newLineAndTab(StringBuilder stringBuilder, int tabs, int newLines) {
    stringBuilder.append("\n".repeat(newLines));
    stringBuilder.append("\t".repeat(tabs));
    return stringBuilder;
  }

  private File file;
  private Table table;
  private String packageName;
  private ArrayList<String> imports = new ArrayList<>();

  public FileGenerator(File file, Table table, String packageName) {
    this.table = table;
    this.packageName = packageName;
    this.file = file;

    fillImports();
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public String makeGetterSetters(int tab) {
    StringBuilder stringBuilder = new StringBuilder();

    newLineAndTab(stringBuilder, tab, 0);

    int size = table.getColumns().size();
    int i = 1;

    for (Column column : table.getColumns().values()) {
      String colGetSetName = column.getName().substring(0, 1).toUpperCase()
          + column.getName().substring(1, column.getName().length());
      stringBuilder
          .append(
              "public " + column.getJavaType() + " get" + colGetSetName + "() { return " + column.getName() + "; }");

      newLineAndTab(stringBuilder, tab, 2);

      stringBuilder
          .append(
              "public void set" + colGetSetName + "(" + column.getJavaType() + " " + column.getName() + ") { this."
                  + column.getName() + " = " + column.getName() + "; }");

      if (i < size)
        newLineAndTab(stringBuilder, tab, 2);
      i++;
    }

    return stringBuilder.toString();
  }

  public String makeColumn(Column column, int tab) {
    String type = column.getJavaType();
    String colName = column.getName();

    StringBuilder stringBuilder = new StringBuilder();

    // Add annot for column
    newLineAndTab(stringBuilder, tab, 0);
    stringBuilder.append("@Column(value = \"" + column.getOgName() + "\"");

    if (column.isPrimaryKey())
      stringBuilder.append(", primaryKey = true");

    stringBuilder.append(")");

    newLineAndTab(stringBuilder, tab, 1);

    // Add Foreigns
    if (column.isForeignKey()) {
      stringBuilder.append("@ForeignColumn(name = \"" + column.getForeignColumn().getName() + "\", model = "
          + column.getForeignTable().getName() + ".class)");
      newLineAndTab(stringBuilder, tab, 1);
    }

    // Add column
    stringBuilder.append("private " + type + " " + colName + ";");

    return stringBuilder.toString();
  }

  public String generate() {
    StringBuilder all = new StringBuilder(makeHeader());
    newLineAndTab(all, 0, 1);

    // Add class
    all.append(makeClass());
    newLineAndTab(all, 0, 1);

    // Add columns
    int tab = 1;
    for (Column column : table.getColumns().values()) {
      all.append(makeColumn(column, tab));
      newLineAndTab(all, 0, 2);
    }

    // Add getters setters
    all.append(makeGetterSetters(tab));

    newLineAndTab(all, 0, 1);
    all.append("}");

    return all.toString();
  }

  private void fillImports() {
    imports.add("mg.razherana.lorm.annot.columns.Column");
    imports.add("mg.razherana.lorm.annot.general.Table");
    imports.add("mg.razherana.lorm.Lorm");

    for (Column column : table.getColumns().values())
      if (column.isForeignKey()) {
        imports.add("mg.razherana.lorm.annot.columns.ForeignColumn");
        break;
      }
  }

  private String makeHeader() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("// Generated Model using mg.razherana.generator");
    stringBuilder.append("\n");
    stringBuilder.append("// Goood Luck coding!");
    stringBuilder.append("\n\n");

    // Package
    stringBuilder.append("package " + packageName + ";");
    stringBuilder.append("\n\n");

    // Imports
    for (String string : imports) {
      stringBuilder.append("import " + string + ";");
      stringBuilder.append("\n");
    }

    return stringBuilder.toString();
  }

  private String makeClass() {
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append("@Table(\"" + table.getOgName() + "\")");
    newLineAndTab(stringBuilder, 0, 1);
    stringBuilder.append("public class " + table.getName() + " extends Lorm<" + table.getName() + "> { ");

    return stringBuilder.toString();
  }

  public void write() {
    try (java.io.FileWriter fileWriter = new java.io.FileWriter(file)) {
      fileWriter.write(generate());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
