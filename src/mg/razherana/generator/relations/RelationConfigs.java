package mg.razherana.generator.relations;

import java.util.ArrayList;
import java.util.Map;

import mg.razherana.generator.dbproperties.Table;
import mg.razherana.generator.exceptions.InvalidConfigValueException;
import mg.razherana.lorm.relations.RelationType;

public class RelationConfigs {
  final static Map<RelationType, RelationType> relationTypeRecipr = Map.of(RelationType.HASMANY, RelationType.BELONGSTO,
      RelationType.BELONGSTO, RelationType.HASMANY, RelationType.ONETOONE, RelationType.ONETOONE);

  static RelationType toRelationType(String relation) {
    switch (relation.toLowerCase()) {
      case "hasmany":
        return RelationType.HASMANY;
      case "belongsto":
        return RelationType.BELONGSTO;
      case "onetoone":
        return RelationType.ONETOONE;
      default:
        throw new InvalidConfigValueException("The relation " + relation + " is unsupported...");
    }
  }

  private String table1;
  private String col1;
  private RelationType relation;
  private String table2;
  private String col2;
  private String relationName1;
  private String relationName2;

  public String getRelationName2() {
    return relationName2;
  }

  public void setRelationName2(String relationName2) {
    this.relationName2 = relationName2;
  }

  public String getRelationName1() {
    return relationName1;
  }

  public void setRelationName(String relationName) {
    this.relationName1 = relationName;
  }

  public RelationConfigs(String table1, String col1, RelationType relation, String table2, String col2,
      String relationName1, String relationName2) {
    this.table1 = table1;
    this.col1 = col1;
    this.relation = relation;
    this.table2 = table2;
    this.col2 = col2;
    this.relationName1 = relationName1.toLowerCase();
    if (relationName1.isBlank()) {
      this.relationName1 = table2.toLowerCase();
    }
    this.relationName2 = relationName2.toLowerCase();
    if (relationName2.isBlank()) {
      this.relationName2 = table1.toLowerCase();
    }
  }

  public boolean matches(String table) {
    return table1.equals(table) || table2.equals(table);
  }

  public String getRelationName(String table) {
    if (table.equals(table1))
      return relationName1;
    return relationName2;
  }

  public Class<?> getRelationType(String table) {
    if (table.equals(table1))
      return getRelation().getAnnotation();
    return getReciprocate().getAnnotation();
  }

  public RelationType getRelation(String table) {
    if (table.equals(table1))
      return getRelation();
    return getReciprocate();
  }

  public RelationConfigs(String table1, String col1, String relation, String table2, String col2, String relationName1,
      String relationName2) {
    this(table1, col1, toRelationType(relation), table2, col2, relationName1, relationName2);
  }

  public String getTable1() {
    return table1;
  }

  public void setTable1(String table1) {
    this.table1 = table1;
  }

  public String getCol1() {
    return col1;
  }

  public void setCol1(String col1) {
    this.col1 = col1;
  }

  public RelationType getRelation() {
    return relation;
  }

  public void setRelation(RelationType relation) {
    this.relation = relation;
  }

  public String getTable2() {
    return table2;
  }

  public void setTable2(String table2) {
    this.table2 = table2;
  }

  public String getCol2() {
    return col2;
  }

  public void setCol2(String col2) {
    this.col2 = col2;
  }

  public RelationType getReciprocate() {
    return relationTypeRecipr.get(getRelation());
  }

  public String getRelationTable(String ogName) {
    if (table1.equals(ogName))
      return table2;
    return table1;
  }

  public String getRelationColumn(String ogName) {
    var relation = getRelation(ogName);
    switch (relation) {
      case HASMANY:
        if (table1.equals(ogName))
          return col2;
        return col1;
      case BELONGSTO:
      case ONETOONE:
        if (table1.equals(ogName))
          return col1;
        return col2;
      default:
        throw new InvalidConfigValueException("The relation " + relation + " is unsupported...");
    }
  }

  static String relationToMethod(RelationType relationType) {
    switch (relationType) {
      case HASMANY:
        return "hasMany";
      case BELONGSTO:
        return "belongsTo";
      case ONETOONE:
        return "oneToOne";
      default:
        // this shouldn't happen...
        throw new RuntimeException("Relation unsupported");
    }
  }

  public String getReturnTypeString(String table, String otherModel) {
    if (table.equals(table1))
      return getReturnTypeString(getRelation(), otherModel);
    return getReturnTypeString(getReciprocate(), otherModel);
  }

  public String getImport(RelationType relationType) {
    switch (relationType) {
      case HASMANY:
        return ArrayList.class.getName();
      default:
        return "";
    }
  }

  public String getImport(String table) {
    if (table.equals(table1))
      return getImport(getRelation());
    return getImport(getReciprocate());
  }

  public String getReturnTypeString(RelationType relationType, String otherModel) {
    switch (relationType) {
      case HASMANY:
        return "ArrayList<" + otherModel + ">";
      case BELONGSTO, ONETOONE:
        return otherModel;
      default:
        // this shouldn't happen...
        throw new RuntimeException("Relation unsupported");
    }
  }

  public String getRelationMethodName(String table) {
    if (table1.equals(table))
      return relationToMethod(getRelation());
    return relationToMethod(getReciprocate());
  }

  public Table getTableToCheck(Table table, Table relationTable) {
    var relation = getRelation(table.getOgName());
    switch (relation) {
      case HASMANY:
        return relationTable;
      case BELONGSTO:
      case ONETOONE:
        return table;
      default:
        throw new InvalidConfigValueException("The relation " + relation + " is unsupported...");
    }
  }
}
