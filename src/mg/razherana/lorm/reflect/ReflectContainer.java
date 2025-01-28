package mg.razherana.lorm.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import mg.razherana.lorm.Lorm;
import mg.razherana.lorm.annot.columns.Column;
import mg.razherana.lorm.annot.columns.ForeignColumn;
import mg.razherana.lorm.annot.general.Table;
import mg.razherana.lorm.exceptions.AnnotationException;

public class ReflectContainer {
  public static final HashMap<Class<? extends Lorm>, ReflectContainer> INSTANCES = new HashMap<>();

  private String table = "";
  private ArrayList<ColumnInfo> columns = new ArrayList<>();
  private Map<String, Function<Object, ?>> beforeIn = new HashMap<>();
  private Map<String, Function<Object, ?>> beforeOut = new HashMap<>();

  public static ReflectContainer loadAnnotations(Lorm lorm) {
    ReflectContainer reflectContainer = new ReflectContainer();

    Objects.requireNonNull(lorm, "Lorm cannot be null");

    if (INSTANCES.containsKey(lorm.getClass()))
      return INSTANCES.get(lorm.getClass());

    // Load the annotations
    Table table = lorm.getClass().getDeclaredAnnotation(Table.class);

    if (table != null)
      reflectContainer.setTable(table.value());
    else
      reflectContainer.setTable(lorm.getClass().getSimpleName());

    reflectContainer.setBeforeIn(lorm.beforeIn());
    reflectContainer.setBeforeOut(lorm.beforeOut());

    for (Field field : lorm.getClass().getDeclaredFields()) {
      Column column = field.getDeclaredAnnotation(Column.class);
      if (column != null) {
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.columnName = column.value().isEmpty() ? field.getName() : column.value();
        columnInfo.field = field;
        columnInfo.primaryKey = column.primaryKey();
        columnInfo.getterName = column.getter().isEmpty()
            ? "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1)
            : column.getter();
        columnInfo.setterName = column.setter().isEmpty()
            ? "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1)
            : column.setter();

        try {
          Method getter = lorm.getClass().getDeclaredMethod(columnInfo.getterName);
          Method setter = lorm.getClass().getDeclaredMethod(columnInfo.setterName, field.getType());

          columnInfo.getter = getter;
          columnInfo.setter = setter;
        } catch (NoSuchMethodException e) {
          throw new AnnotationException(
              "Getter or setter not found for field " + columnInfo.field.getName() + " using " + columnInfo.getterName
                  + " and " + columnInfo.setterName + " methods");
        }

        ForeignColumn foreignColumn = field.getDeclaredAnnotation(ForeignColumn.class);
        if (foreignColumn != null) {
          columnInfo.foreignKey = true;
          columnInfo.foreignName = foreignColumn.name();

          if (foreignColumn.model() == null)
            throw new AnnotationException("Foreign model is null for field " + field.getName());

          columnInfo.foreignModel = foreignColumn.model();
        }

        reflectContainer.columns.add(columnInfo);
      }
    }

    INSTANCES.put(lorm.getClass(), reflectContainer);
    return reflectContainer;
  }

  public String getTable() {
    return table;
  }

  public void setTable(String table) {
    this.table = table;
  }

  public Map<String, Function<Object, ?>> getBeforeIn() {
    return beforeIn;
  }

  public void setBeforeIn(Map<String, Function<Object, ?>> beforeIn) {
    this.beforeIn = beforeIn;
  }

  public Map<String, Function<Object, ?>> getBeforeOut() {
    return beforeOut;
  }

  public void setBeforeOut(Map<String, Function<Object, ?>> beforeOut) {
    this.beforeOut = beforeOut;
  }

  public void setValueFromResultSet(Lorm lorm, ResultSet resultSet) {
    for (ColumnInfo columnInfo : columns) {
      try {
        Object value = resultSet.getObject(columnInfo.columnName);

        if (lorm.getBeforeIn().containsKey(columnInfo.columnName))
          value = lorm.getBeforeIn().get(columnInfo.columnName).apply(value);

        if (!columnInfo.setter.canAccess(this))
          columnInfo.setter.setAccessible(true);
        columnInfo.setter.invoke(lorm, value);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  public HashMap<String, Object> getBeforeOutValues(Lorm lorm) {
    HashMap<String, Object> values = new HashMap<>();

    for (ColumnInfo columnInfo : columns) {
      try {
        Object value = columnInfo.getter.invoke(lorm);

        if (lorm.getBeforeOut().containsKey(columnInfo.columnName))
          value = lorm.getBeforeOut().get(columnInfo.columnName).apply(value);

        values.put(columnInfo.columnName, value);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return values;
  }
}
