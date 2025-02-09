package mg.razherana.generator;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

import mg.razherana.database.DatabaseConnection;
import mg.razherana.generator.dbproperties.Column;
import mg.razherana.generator.dbproperties.Database;
import mg.razherana.generator.dbproperties.Table;
import mg.razherana.generator.file.FileGenerator;
import mg.razherana.generator.utils.Config;
import mg.razherana.generator.utils.ConventionStringBuilder;
import mg.razherana.generator.utils.SqlTypeToJavaType;
import mg.razherana.generator.utils.ConventionStringBuilder.Convention;

public class Generator {
  Config configuration;
  final Database database;

  public Database getDatabase() {
    return database;
  }

  public Generator(String config) {
    // Load configuration
    configuration = new Config(config);
    database = new Database();

    getDatabaseProperties();

    // We format the names of tables based on config
    formatTableAndColumnNames();
  }

  public void generateFiles() {
    var tables = new ArrayList<>(database.getTables().values());
    for (Table table : tables) {
      File file = new File(configuration.getOutputDir() + File.separator + table.getName() + ".java");
      FileGenerator fileGenerator = new FileGenerator(file, table, configuration.getOutputPackage(),
          configuration.getRelationConfigs(), tables);
      fileGenerator.write();
    }
  }

  private void formatTableAndColumnNames() {
    Convention classConvention = Convention.parseString(configuration.getClassConvention());
    Convention fieldConvention = Convention.parseString(configuration.getFieldConvention());
    for (Table table : database.getTables().values()) {
      String tableName = table.getName();
      String className = tableName;

      // Format table name
      if (configuration.getTableToClass().get(tableName) != null) {
        className = configuration.getTableToClass().get(tableName);
      }
      // Remove prefix
      else if (configuration.getRemovePrefixClass() != null)
        for (String removePrefixClass : configuration.getRemovePrefixClass())
          if (tableName.startsWith(removePrefixClass)) {
            className = tableName.substring(removePrefixClass.length());
            break;
          }

      if (configuration.getPrefixClass() != null)
        className = configuration.getPrefixClass() + className;

      className = ConventionStringBuilder.toConvention(className, classConvention);
      table.setName(className);

      for (Column column : table.getColumns().values()) {
        String columnName = column.getName();
        String fieldName = columnName;

        // Format column name
        if (configuration.getColumnToField().get(className + ":" + columnName) != null) {
          fieldName = configuration.getColumnToField().get(className + ":" + columnName);
        }
        // Remove prefix
        else if (configuration.getRemovePrefixField() != null)
          for (String removePrefixField : configuration.getRemovePrefixField())
            if (columnName.startsWith(removePrefixField)) {
              fieldName = columnName.substring(removePrefixField.length());
              break;
            }

        if (configuration.getPrefixField() != null)
          fieldName = configuration.getPrefixField() + fieldName;

        fieldName = ConventionStringBuilder.toConvention(fieldName, fieldConvention);
        column.setName(fieldName);
      }

      table.setColumns(
          new HashMap<>(table.getColumns().values().stream().collect(Collectors.toMap(e -> e.getOgName(), e -> e))));
    }

    database.setTables(
        new HashMap<>(database.getTables().values().stream().collect(Collectors.toMap(e -> e.getOgName(), e -> e))));
  }

  public void getDatabaseProperties() {
    // Connect database
    DatabaseConnection connection = DatabaseConnection.fromDotEnv(configuration.get("databaseConfig"));

    try (Connection conn = connection.getConnection()) {
      // Get the metadata
      DatabaseMetaData metaData = conn.getMetaData();

      // Get all the tables
      String[] types = { "TABLE" };
      ResultSet tables = metaData.getTables(conn.getCatalog(), null, "%", types);

      while (tables.next()) {
        String tableName = tables.getString("TABLE_NAME");

        Table table = database.getTable(tableName);

        if (table == null) {
          table = new Table(tableName);
          database.addTable(table);
        }

        // Get the primaryKeys of the table and store them in a HashSet
        ResultSet primaryKeys = metaData.getPrimaryKeys(conn.getCatalog(), null, tableName);
        HashSet<String> primaryKeysSet = new HashSet<>();
        while (primaryKeys.next())
          primaryKeysSet.add(primaryKeys.getString("COLUMN_NAME"));
        primaryKeys.close();

        // Get all the foreignKeys and table column referenced by them
        ResultSet foreignKeys = metaData.getImportedKeys(conn.getCatalog(), null, tableName);
        while (foreignKeys.next()) {
          String fkTableName = foreignKeys.getString("PKTABLE_NAME");
          String fkColumnName = foreignKeys.getString("PKCOLUMN_NAME");

          // Get the table
          Table fkTable = database.getTable(fkTableName);
          if (fkTable == null) {
            fkTable = new Table(fkTableName);
            database.addTable(fkTable);
          }

          // Get the column
          Column fkColumn = fkTable.getColumn(fkColumnName);

          if (fkColumn == null) {
            fkColumn = new Column(fkColumnName);
            fkTable.addColumn(fkColumn);
          }

          // Get the column
          String columnName = foreignKeys.getString("FKCOLUMN_NAME");
          Column column = table.getColumn(columnName);

          if (column == null) {
            column = new Column(foreignKeys.getString("FKCOLUMN_NAME"));
            table.addColumn(column);
          }

          // Add column infos
          column.setForeignKey(true);
          column.setForeignTable(fkTable);
          column.setForeignColumn(fkColumn);

        }
        foreignKeys.close();

        // Get all the columns
        ResultSet columns = metaData.getColumns(conn.getCatalog(), null, tableName, null);

        while (columns.next()) {
          String columnName = columns.getString("COLUMN_NAME");

          Column column = table.getColumn(columnName);

          if (column == null) {
            column = new Column(columnName);
            table.addColumn(column);
          }

          // Add column infos
          column.setType(columns.getString("TYPE_NAME"));
          column.setJavaType(SqlTypeToJavaType.mapSqlTypeToJavaType(columns.getInt("DATA_TYPE")));
          column.setPrimaryKey(primaryKeysSet.contains(columnName));

          table.addColumn(column);
        }
      }

      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
