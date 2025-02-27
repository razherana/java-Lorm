package mg.razherana.lorm.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import mg.razherana.lorm.Lorm;

public class ColumnInfo {
  String columnName;
  String getterName;
  String setterName;
  Field field;
  Method getter;
  public Method setter;
  boolean primaryKey;
  boolean foreignKey;
  String foreignName;
  Class<? extends Lorm<?>> foreignModel;
  boolean indexed;

  public String getColumnName() {
    return columnName;
  }

  public String getGetterName() {
    return getterName;
  }

  public String getSetterName() {
    return setterName;
  }

  public Field getField() {
    return field;
  }

  public Method getGetter() {
    return getter;
  }

  public Method getSetter() {
    return setter;
  }

  public boolean isPrimaryKey() {
    return primaryKey;
  }

  public boolean isForeignKey() {
    return foreignKey;
  }

  public String getForeignName() {
    return foreignName;
  }

  public Class<? extends Lorm<?>> getForeignModel() {
    return foreignModel;
  }
}