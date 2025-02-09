package mg.razherana.generator.file;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import mg.razherana.generator.dbproperties.Column;
import mg.razherana.generator.dbproperties.Table;
import mg.razherana.generator.relations.RelationConfigs;
import mg.razherana.generator.utils.ConventionStringBuilder;
import mg.razherana.generator.utils.ConventionStringBuilder.Convention;
import mg.razherana.lorm.exceptions.AnnotationException;

public class FileGenerator {
  public static StringBuilder newLineAndTab(StringBuilder stringBuilder, int tabs, int newLines) {
    stringBuilder.append("\n".repeat(newLines));
    stringBuilder.append("\t".repeat(tabs));
    return stringBuilder;
  }

  private File file;
  private Table table;
  private ArrayList<Table> tables;
  private String packageName;
  private HashSet<String> imports = new HashSet<>();

  private ArrayList<RelationConfigs> relationConfig = new ArrayList<>();
  private boolean hasRelationConfig = false;

  public FileGenerator(File file, Table table, String packageName, HashSet<RelationConfigs> relationConfigs,
      ArrayList<Table> tables) {
    this.table = table;
    this.packageName = packageName;
    this.file = file;
    this.tables = tables;

    if (relationConfigs != null) {
      for (RelationConfigs relationConfigs2 : relationConfigs)
        if (relationConfigs2.matches(table.getOgName())) {
          this.relationConfig.add(relationConfigs2);
          hasRelationConfig = true;
        }
    }

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
      String colGetSetName = ConventionStringBuilder.toConvention(column.getName(), Convention.PASCAl);
      stringBuilder
          .append(
              "public " + column.getJavaType() + " get" + colGetSetName + "() { return " + column.getName() + "; }");

      newLineAndTab(stringBuilder, tab, 2);

      stringBuilder
          .append(
              "public void set" + colGetSetName + "(" + column.getJavaType() + " " + column.getName() + ") { this."
                  + column.getName() + " = " + column.getName() + "; }");

      if (i < size || hasRelationConfig)
        newLineAndTab(stringBuilder, tab, 2);
      i++;
    }

    // Generating relation getter and setters
    i = 0;
    size = relationConfig.size();
    if (hasRelationConfig)
      for (RelationConfigs relationConfigs : relationConfig) {
        // Model name
        String otherModel = null;
        String relationTableName = relationConfigs.getRelationTable(table.getOgName());

        Table relationTable = tables.stream().filter((e) -> e.getOgName().equals(relationTableName)).findAny()
            .orElse(null);
        if (relationTable == null)
          throw new AnnotationException("The relationTable " + relationTableName + " doesn't exists...");

        otherModel = relationTable.getName();

        var relationName = relationConfigs.getRelationName(table.getOgName());

        String returnType = relationConfigs.getReturnTypeString(table.getOgName(), otherModel);

        stringBuilder
            .append(
                "public " + returnType + " get" + ConventionStringBuilder.toConvention(relationName, Convention.PASCAl)
                    + "(Connection connection) throws SQLException { return "
                    + relationConfigs.getRelationMethodName(table.getOgName())
                    + "(\"" + relationName + "\", connection); }");

        if (i < size - 1)
          newLineAndTab(stringBuilder, tab, 2);
        i++;
      }

    return stringBuilder.toString();
  }

  public String makeColumn(Column column, int tab) {
    String type = column.getJavaType();
    String colName = column.getName();
    String colGetSetName = ConventionStringBuilder.toConvention(column.getName(), Convention.PASCAl);

    StringBuilder stringBuilder = new StringBuilder();

    // Add annot for column
    newLineAndTab(stringBuilder, tab, 0);
    stringBuilder.append("@Column(value = \"" + column.getOgName() + "\"");

    if (column.isPrimaryKey())
      stringBuilder.append(", primaryKey = true");

    stringBuilder.append(", getter = \"get" + colGetSetName + "\", setter = \"set" + colGetSetName + "\")");

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

    if (hasRelationConfig) {
      imports.add("java.sql.Connection");
      imports.add("java.sql.SQLException");
      for (RelationConfigs relationConfig2 : relationConfig) {
        imports.add(relationConfig2.getRelationType(table.getOgName()).getName());
        String toImport = relationConfig2.getImport(table.getOgName());
        if (!toImport.isEmpty())
          imports.add(toImport);
      }
    }
  }

  private String makeHeader() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("// Generated Model using mg.razherana.generator");
    newLineAndTab(stringBuilder, 0, 1);
    stringBuilder.append("// Happy Codingg!");
    newLineAndTab(stringBuilder, 0, 2);

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

    // Add relations here :
    for (RelationConfigs relationConfig2 : relationConfig) {
      // Model name

      String otherModel = null;
      String relationTableName = relationConfig2.getRelationTable(table.getOgName());

      Table relationTable = tables.stream().filter((e) -> e.getOgName().equals(relationTableName)).findAny()
          .orElse(null);
      if (relationTable == null)
        throw new AnnotationException("The relationTable " + relationTableName + " doesn't exists...");

      otherModel = relationTable.getName();

      // Column name
      String relationColumnName = relationConfig2.getRelationColumn(table.getOgName());
      var relationColumn = relationConfig2.getTableToCheck(table, relationTable).getColumnOg(relationColumnName);
      String relationName = relationConfig2.getRelationName(table.getOgName());

      if (relationColumn == null)
        throw new AnnotationException("The relationColumn " + relationColumnName + " in the relationTable "
            + relationTable.getName() + " doesn't exists...");

      stringBuilder
          .append("@" + relationConfig2.getRelationType(table.getOgName()).getSimpleName() + "(model = " + otherModel
              + ".class, foreignKey = \"" + relationColumnName + "\", relationName = \"" + relationName + "\")");
      newLineAndTab(stringBuilder, 0, 1);
    }

    stringBuilder.append("public class " + table.getName() + " extends Lorm<" + table.getName() + "> { ");

    return stringBuilder.toString();
  }

  public void write() {
    if (file.exists()) {
      System.out.println("[WARNING] -> File already exists : " + file.getName());
      return;
    }
    try (java.io.FileWriter fileWriter = new java.io.FileWriter(file)) {
      fileWriter.write(generate());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
