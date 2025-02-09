package mg.razherana.generator.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

import mg.razherana.generator.exceptions.ConfigNotInitializedException;
import mg.razherana.generator.exceptions.InvalidConfigFileException;
import mg.razherana.generator.exceptions.InvalidConfigValueException;
import mg.razherana.generator.relations.RelationConfigs;

public class Config {
  private Properties properties;

  private String outputDir;
  private String outputPackage;
  private String databaseConfig;
  private String prefixClass;
  private String prefixField;
  private ArrayList<String> removePrefixClass;
  private ArrayList<String> removePrefixField;
  private String classConvention;
  private String fieldConvention;
  private HashMap<String, String> tableToClass = new HashMap<>();
  private HashMap<String, String> columnToField = new HashMap<>();
  private HashSet<RelationConfigs> relationConfigs = new HashSet<>();

  public Config(String file) {
    properties = new Properties();

    if (file != null && new java.io.File(file).exists()) {
      try (FileInputStream fileInputStream = new FileInputStream(file)) {
        properties.loadFromXML(fileInputStream);
      } catch (Exception e) {
        throw new InvalidConfigFileException();
      }
      parseConfigs();
    } else {
      initPropertiesDefaults();
      saveProperties(file);
      throw new ConfigNotInitializedException();
    }
  }

  public HashSet<RelationConfigs> getRelationConfigs() {
    return relationConfigs;
  }

  public void setRelationConfigs(HashSet<RelationConfigs> relationConfigs) {
    this.relationConfigs = relationConfigs;
  }

  public String getDatabaseConfig() {
    return databaseConfig;
  }

  public void setDatabaseConfig(String databaseConfig) {
    this.databaseConfig = databaseConfig;
  }

  public String getOutputDir() {
    return outputDir;
  }

  public void setOutputDir(String outputDir) {
    this.outputDir = outputDir;
  }

  public String getOutputPackage() {
    return outputPackage;
  }

  public void setOutputPackage(String outputPackage) {
    this.outputPackage = outputPackage;
  }

  public String get(String key) {
    return properties.getProperty(key);
  }

  public Properties getProperties() {
    return properties;
  }

  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  public String getPrefixClass() {
    return prefixClass;
  }

  public void setPrefixClass(String prefixClass) {
    this.prefixClass = prefixClass;
  }

  public String getPrefixField() {
    return prefixField;
  }

  public void setPrefixField(String prefixField) {
    this.prefixField = prefixField;
  }

  public HashMap<String, String> getTableToClass() {
    return tableToClass;
  }

  public void setTableToClass(HashMap<String, String> tableToClass) {
    this.tableToClass = tableToClass;
  }

  public HashMap<String, String> getColumnToField() {
    return columnToField;
  }

  public void setColumnToField(HashMap<String, String> columnToField) {
    this.columnToField = columnToField;
  }

  public ArrayList<String> getRemovePrefixClass() {
    return removePrefixClass;
  }

  public void setRemovePrefixClass(ArrayList<String> removePrefixClass) {
    this.removePrefixClass = removePrefixClass;
  }

  public ArrayList<String> getRemovePrefixField() {
    return removePrefixField;
  }

  public void setRemovePrefixField(ArrayList<String> removePrefixField) {
    this.removePrefixField = removePrefixField;
  }

  public String getClassConvention() {
    return classConvention;
  }

  public void setClassConvention(String classConvention) {
    this.classConvention = classConvention;
  }

  public String getFieldConvention() {
    return fieldConvention;
  }

  public void setFieldConvention(String fieldConvention) {
    this.fieldConvention = fieldConvention;
  }

  private void parseConfigs() {
    // Output dir
    String outputDir = properties.getProperty("outputDir");

    var outputDirFile = new File(outputDir);

    if (!outputDirFile.exists() || !outputDirFile.isDirectory())
      throw new InvalidConfigValueException("The outputDir doesn't exist or isn't a directory");

    outputDir = outputDirFile.getAbsolutePath();
    setOutputDir(outputDir);

    System.out.println("[INFO] -> Using outputDir of " + outputDir);

    String outputPackage = properties.getProperty("outputPackage");

    if (outputPackage == null)
      throw new InvalidConfigValueException("The outputPackage is not set");

    // Check if matches package convention
    if (!outputPackage.matches("([a-z]+[.])*[a-z]+"))
      throw new InvalidConfigValueException("The outputPackage doesn't match the package convention");

    setOutputPackage(outputPackage);
    System.out.println("[INFO] -> Using outputPackage of " + outputPackage);

    // Database config
    String databaseConfig = properties.getProperty("databaseConfig");

    if (databaseConfig == null)
      throw new InvalidConfigValueException("The databaseConfig is not set");

    var databaseConfigFile = new File(databaseConfig);

    if (!databaseConfigFile.exists() || !databaseConfigFile.isFile())
      throw new InvalidConfigValueException("The databaseConfig doesn't exist or isn't a file");

    setDatabaseConfig(databaseConfig);

    System.out.println("[INFO] -> Using databaseConfig of " + databaseConfig);

    // Prefix class
    String prefixClass = properties.getProperty("prefixClass");
    setPrefixClass(prefixClass);

    // Prefix field
    String prefixField = properties.getProperty("prefixField");
    setPrefixField(prefixField);

    // Remove prefix class
    String removePrefixClass = properties.getProperty("removePrefixClass");

    // Parse into array
    if (removePrefixClass != null) {
      ArrayList<String> removePrefixList = new ArrayList<>();
      for (String string : removePrefixClass.split(","))
        removePrefixList.add(string.trim());
      setRemovePrefixClass(removePrefixList);
    }

    // Remove prefix field
    String removePrefixField = properties.getProperty("removePrefixField");
    if (removePrefixField != null) {
      ArrayList<String> removePrefixList = new ArrayList<>();
      for (String string : removePrefixField.split(","))
        removePrefixList.add(string.trim());
      setRemovePrefixField(removePrefixList);
    }

    // Class convention
    String classConvention = properties.getProperty("classConvention");
    setClassConvention(classConvention);

    System.out.println("[INFO] -> Using classConvention of " + classConvention);

    // Field convention
    String fieldConvention = properties.getProperty("fieldConvention");
    setFieldConvention(fieldConvention);

    System.out.println("[INFO] -> Using fieldConvention of " + fieldConvention);

    // Table to class
    String tableToClass = properties.getProperty("tableToClass");

    if (tableToClass != null && !tableToClass.isEmpty()) {
      String[] tableToClassArray = tableToClass.split(",");
      for (String table : tableToClassArray) {
        table = table.trim();
        String[] tableToClassPair = table.split(":");

        if (tableToClassPair.length != 2)
          throw new InvalidConfigValueException("The tableToClass is not correctly set");

        this.tableToClass.put(tableToClassPair[0].trim(), tableToClassPair[1].trim());
      }
    }

    // Column to field
    String columnToField = properties.getProperty("columnToField");

    if (columnToField != null && !columnToField.isEmpty()) {
      String[] columnToFieldArray = columnToField.split(",");
      for (String column : columnToFieldArray) {
        column = column.trim();
        String[] columnToFieldPair = column.split(":");

        if (columnToFieldPair.length != 3)
          throw new InvalidConfigValueException("The columnToField is not correctly set");

        this.columnToField.put(columnToFieldPair[0].trim() + ":" + columnToFieldPair[1].trim(),
            columnToFieldPair[2].trim());
      }
    }

    // Relations
    String relationStrings = properties.getProperty("relations");
    if (relationStrings != null && !relationStrings.isBlank()) {
      String[] relationStringSplitted = relationStrings.split(",");
      for (String relationString : relationStringSplitted) {
        relationString = relationString.trim();
        String[] parts = relationString.split(":");

        if (parts.length < 5 || parts.length > 7)
          throw new InvalidConfigValueException("The relations is not correctly set");

        RelationConfigs relationConfigs = new RelationConfigs(parts[0].trim(), parts[1].trim(), parts[2].trim(),
            parts[3].trim(), parts[4].trim(), parts.length >= 6 ? parts[5].trim() : "", parts.length >= 7 ? parts[6].trim() : "");
        getRelationConfigs().add(relationConfigs);
      }
    }
  }

  private void initPropertiesDefaults() {
    properties.setProperty("outputDir", "{Add your output directory here}");
    properties.setProperty("outputPackage", "{Add your package here}");
    properties.setProperty("databaseConfig", "{Add your database.xml config here}");
    properties.setProperty("prefixClass", "{If you want to add a prefix to your classes, eg : table}");
    properties.setProperty("prefixField", "{If you want to add a prefix to your fields, eg : table}");
    properties.setProperty("removePrefixClass",
        "{If you want to remove a prefix from your classes, eg : tbl, tb, t, ...}");
    properties.setProperty("removePrefixField",
        "{If you want to remove a prefix from your fields, eg : tbl, tb, t, ...}");
    properties.setProperty("classConvention",
        "{If you want to change the class naming convention, eg : PascalCase, camelCase, snake_case}");
    properties.setProperty("fieldConvention",
        "{If you want to change the field naming convention, eg : PascalCase, camelCase, snake_case}");
    properties.setProperty("tableToClass",
        "{If you want to change the table to class name specifically `table:name` separated by commas. users:User, posts:Post}");
    properties.setProperty("columnToField",
        "{If you want to change the column to field name specifically `table:col:name` separated by commas. users:id:identifier, users:id2:identifier2}");
    properties.setProperty("relations",
        "{If you want to define relations like hasmany belongsto onetoone. It will create automatically the reciprocating relation, eg : users:id:hasmany:posts:user_id[:optional_relation_name1:optional_relation_name2], posts:id:hasmany:comments:post_id[:optional_relation_name1:optional_relation_name2]}");
  }

  private void saveProperties(String file) {
    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
      properties.storeToXML(fileOutputStream, "Generator configuration");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
