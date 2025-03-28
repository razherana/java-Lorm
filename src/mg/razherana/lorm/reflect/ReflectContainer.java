package mg.razherana.lorm.reflect;

import java.lang.reflect.Constructor;
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
import mg.razherana.lorm.annot.relations.BelongsTo;
import mg.razherana.lorm.annot.relations.EagerLoad;
import mg.razherana.lorm.annot.relations.HasMany;
import mg.razherana.lorm.annot.relations.OneToOne;
import mg.razherana.lorm.exceptions.AnnotationException;
import mg.razherana.lorm.relations.Relation;
import mg.razherana.lorm.relations.RelationType;

public class ReflectContainer {
  public static final HashMap<Class<? extends Lorm<?>>, ReflectContainer> INSTANCES = new HashMap<>();

  @SuppressWarnings("unchecked")
  public static ReflectContainer loadAnnotations(Lorm<?> lorm) {
    ReflectContainer reflectContainer = new ReflectContainer();

    Objects.requireNonNull(lorm, "Lorm cannot be null");

    if (INSTANCES.containsKey(lorm.getClass()))
      return INSTANCES.get(lorm.getClass());

    System.out.println("[Lorm:info] -> Load annot for " + lorm.getClass().getSimpleName());

    // Load constructor
    try {
      reflectContainer.setConstructor((Constructor<? extends Lorm<?>>) lorm.getClass().getDeclaredConstructor());
      reflectContainer.getConstructor().setAccessible(true);
    } catch (NoSuchMethodException e) {
      throw new AnnotationException("No default constructor found for " + lorm.getClass().getSimpleName());
    }

    // Load the annotations
    Table table = lorm.getClass().getDeclaredAnnotation(Table.class);

    if (table != null)
      reflectContainer.setTable(table.value());
    else
      reflectContainer.setTable(lorm.getClass().getSimpleName());

    reflectContainer.setBeforeIn(lorm.beforeIn());
    reflectContainer.setBeforeOut(lorm.beforeOut());
    reflectContainer.setRelationMap(lorm.relations());

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
        boolean indexed = column.indexed();
        columnInfo.indexed = indexed;

        if (columnInfo.primaryKey) {
          if (reflectContainer.primaryKey != null)
            throw new AnnotationException(
                "Multiple primary keys found for " + lorm.getClass().getSimpleName());
          reflectContainer.primaryKey = columnInfo;
        }

        if (indexed || column.primaryKey())
          reflectContainer.indexedColumns.add(columnInfo);

        try {
          Method getter = lorm.getClass().getDeclaredMethod(columnInfo.getterName);
          Method setter = lorm.getClass().getDeclaredMethod(columnInfo.setterName, field.getType());

          columnInfo.getter = getter;
          columnInfo.setter = setter;
          columnInfo.setter.setAccessible(true);
          columnInfo.getter.setAccessible(true);
        } catch (NoSuchMethodException e) {
          throw new AnnotationException(
              "Getter or setter not found for field " + columnInfo.field.getName() + " using "
                  + columnInfo.getterName
                  + " and " + columnInfo.setterName + " methods");
        }

        ForeignColumn foreignColumn = field.getDeclaredAnnotation(ForeignColumn.class);
        if (foreignColumn != null) {
          columnInfo.foreignKey = true;
          columnInfo.foreignName = foreignColumn.name();
          if (columnInfo.foreignName.isEmpty())
            columnInfo.foreignName = "id";

          if (foreignColumn.model() == null)
            throw new AnnotationException("Foreign model is null for field " + field.getName());

          columnInfo.foreignModel = foreignColumn.model();
        }

        reflectContainer.columns.add(columnInfo);
      }
    }

    // Load eagerLoads

    EagerLoad eagerLoads = lorm.getClass().getDeclaredAnnotation(EagerLoad.class);

    if (eagerLoads != null)
      for (String eagerLoad : eagerLoads.value())
        reflectContainer.getEagerLoads().add(eagerLoad);

    Class<? extends Lorm<?>> clazz = (Class<? extends Lorm<?>>) lorm.getClass();
    INSTANCES.put(clazz, reflectContainer);

    // Load relation annots
    loadRelationsAnnotations(lorm, reflectContainer);

    return reflectContainer;
  }

  @SuppressWarnings("unchecked")
  private static void loadRelationsAnnotations(Lorm<?> lorm, ReflectContainer reflectContainer) {
    HasMany[] hasManies = lorm.getClass().getDeclaredAnnotationsByType(HasMany.class);

    for (HasMany hasMany : hasManies) {
      String relationName = hasMany.relationName();
      if (relationName.isEmpty())
        relationName = hasMany.model().getSimpleName().toLowerCase() + "s";

      String foreignKey = hasMany.foreignKey();
      if (foreignKey.isEmpty())
        foreignKey = lorm.getClass().getSimpleName().toLowerCase();

      Method getter1 = null;
      Method getter2 = null;

      // Get the getter of the other model
      if (INSTANCES.get(hasMany.model()) == null) {
        try {
          var constr = hasMany.model().getDeclaredConstructor();
          constr.setAccessible(true);
          constr.newInstance();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }

      ReflectContainer refRelation = INSTANCES.get(hasMany.model());

      if (refRelation == null)
        throw new AnnotationException("Could not load annot for " + hasMany.model().getSimpleName());

      // Get the getter from the other Lorm
      var columnInfo1 = refRelation.getColumn(foreignKey);
      if (columnInfo1 == null)
        throw new AnnotationException("Column not found for foreign key " + foreignKey);
      if (!columnInfo1.foreignKey)
        throw new AnnotationException("Column is not a foreign key for " + foreignKey);
      getter1 = columnInfo1.getter;

      // Get the getter from this Lorm
      var columnInfo2 = reflectContainer.getColumn(columnInfo1.foreignName);
      if (columnInfo2 == null)
        throw new AnnotationException("Column not found for foreign key " + columnInfo1.foreignName);
      getter2 = columnInfo2.getter;

      final var getterFinal1 = getter1;
      final var getterFinal2 = getter2;

      @SuppressWarnings({ "rawtypes" })
      var relation = new Relation((Class<Lorm<?>>) lorm.getClass(),
          (Class<Lorm<?>>) hasMany.model(), (a, b) -> {
            try {
              return getterFinal2.invoke(a).equals(getterFinal1.invoke(b));
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }, RelationType.HASMANY,
          relationName, (models1, models2, cond, relName) -> {
            try {
              HashMap<Object, ArrayList<Lorm<?>>> map2 = new HashMap<>();
              for (Lorm<?> model : models2) {
                var el = getterFinal1.invoke(model);
                map2.putIfAbsent(el, new ArrayList<>());
                map2.get(el).add(model);
              }
              for (Lorm<?> model : models1) {
                var el = getterFinal2.invoke(model);
                model.hasMany.put(relName, map2.getOrDefault(el, new ArrayList<>()));
              }
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          });

      reflectContainer.relationMap.put(relationName, relation);
    }

    BelongsTo[] belongsTos = lorm.getClass().getDeclaredAnnotationsByType(BelongsTo.class);
    for (BelongsTo belongsTo : belongsTos) {
      String relationName = belongsTo.relationName();
      if (relationName.isEmpty())
        relationName = belongsTo.model().getSimpleName().toLowerCase();

      String foreignKey = belongsTo.foreignKey();
      if (foreignKey.isEmpty())
        foreignKey = belongsTo.model().getSimpleName().toLowerCase();

      Method getter1 = null;
      Method getter2 = null;

      if (INSTANCES.get(belongsTo.model()) == null) {
        try {
          var constr = belongsTo.model().getDeclaredConstructor();
          constr.setAccessible(true);
          constr.newInstance();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }

      // Get the getter of the other model
      ReflectContainer refRelation = INSTANCES.get(belongsTo.model());
      if (refRelation == null)
        throw new AnnotationException("Could not load annot for " + belongsTo.model().getSimpleName());

      // Get the getter from the other Lorm
      var columnInfo1 = reflectContainer.getColumn(foreignKey);
      if (columnInfo1 == null)
        throw new AnnotationException("Column not found for foreign key " + foreignKey);
      if (!columnInfo1.foreignKey)
        throw new AnnotationException("Column is not a foreign key for " + foreignKey);

      getter1 = columnInfo1.getter;

      // Get the getter from this Lorm
      var columnInfo2 = refRelation.getColumn(columnInfo1.foreignName);
      if (columnInfo2 == null)
        throw new AnnotationException("Column not found for foreign key " + columnInfo1.foreignName);
      getter2 = columnInfo2.getter;

      final var getterFinal1 = getter1;
      final var getterFinal2 = getter2;

      @SuppressWarnings({ "rawtypes" })
      var relation = new Relation(lorm.getClass(),
          belongsTo.model(), (a, b) -> {
            try {
              return getterFinal1.invoke(a).equals(getterFinal2.invoke(b));
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }, RelationType.BELONGSTO,
          relationName, (models1, models2, cond, relName) -> {
            try {
              HashMap<Object, Lorm<?>> map2 = new HashMap<>();
              for (Lorm<?> model : models2) {
                var el = getterFinal2.invoke(model);
                map2.put(el, model);
              }
              for (Lorm<?> model : models1) {
                var el = getterFinal1.invoke(model);
                model.oneToOne.put(relName, map2.get(el));
              }
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          });

      reflectContainer.relationMap.put(relationName, relation);
    }

    OneToOne[] oneToOnes = lorm.getClass().getDeclaredAnnotationsByType(OneToOne.class);
    for (OneToOne belongsTo : oneToOnes) {
      String relationName = belongsTo.relationName();
      if (relationName.isEmpty())
        relationName = belongsTo.model().getSimpleName().toLowerCase();

      String foreignKey = belongsTo.foreignKey();
      if (foreignKey.isEmpty())
        foreignKey = belongsTo.model().getSimpleName().toLowerCase();

      Method getter1 = null;
      Method getter2 = null;

      if (INSTANCES.get(belongsTo.model()) == null) {
        try {
          var constr = belongsTo.model().getDeclaredConstructor();
          constr.setAccessible(true);
          constr.newInstance();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }

      // Get the getter of the other model
      ReflectContainer refRelation = INSTANCES.get(belongsTo.model());
      if (refRelation == null)
        throw new AnnotationException("Could not load annot for " + belongsTo.model().getSimpleName());

      // Get the getter from the other Lorm
      var columnInfo1 = reflectContainer.getColumn(foreignKey);
      if (columnInfo1 == null)
        throw new AnnotationException("Column not found for foreign key " + foreignKey);
      if (!columnInfo1.foreignKey)
        throw new AnnotationException("Column is not a foreign key for " + foreignKey);

      getter1 = columnInfo1.getter;

      // Get the getter from this Lorm
      var columnInfo2 = refRelation.getColumn(columnInfo1.foreignName);
      if (columnInfo2 == null)
        throw new AnnotationException("Column not found for foreign key " + columnInfo1.foreignName);
      getter2 = columnInfo2.getter;

      final var getterFinal1 = getter1;
      final var getterFinal2 = getter2;

      @SuppressWarnings({ "rawtypes" })
      var relation = new Relation(lorm.getClass(),
          belongsTo.model(), (a, b) -> {
            try {
              return getterFinal1.invoke(a).equals(getterFinal2.invoke(b));
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }, RelationType.ONETOONE,
          relationName, (models1, models2, cond, relName) -> {
            try {
              HashMap<Object, Lorm<?>> map2 = new HashMap<>();
              for (Lorm<?> model : models2) {
                var el = getterFinal2.invoke(model);
                map2.put(el, model);
              }
              for (Lorm<?> model : models1) {
                var el = getterFinal1.invoke(model);
                model.oneToOne.put(relName, map2.get(el));
              }
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          });

      reflectContainer.relationMap.put(relationName, relation);
    }
  }

  private String table = "";
  private ArrayList<ColumnInfo> columns = new ArrayList<>();
  private ArrayList<ColumnInfo> indexedColumns = new ArrayList<>();
  private Map<String, Function<Object, ?>> beforeIn = new HashMap<>();
  private Map<String, Function<Object, ?>> beforeOut = new HashMap<>();
  private Map<String, Relation<? extends Lorm<?>, ? extends Lorm<?>>> relationMap = new HashMap<>();
  private ArrayList<String> eagerLoads = new ArrayList<>();
  private Constructor<? extends Lorm<?>> constructor;

  private ColumnInfo primaryKey;

  public ArrayList<ColumnInfo> getColumns() {
    return columns;
  }

  public ArrayList<ColumnInfo> getIndexedColumns() {
    return indexedColumns;
  }

  public void setIndexedColumns(ArrayList<ColumnInfo> indexedColumns) {
    this.indexedColumns = indexedColumns;
  }

  public ArrayList<String> getEagerLoads() {
    return eagerLoads;
  }

  public void setEagerLoads(ArrayList<String> eagerLoads) {
    this.eagerLoads = eagerLoads;
  }

  public ColumnInfo getColumn(String name) {
    for (ColumnInfo columnInfo : columns) {
      if (name.equals(columnInfo.columnName))
        return columnInfo;
    }
    return null;
  }

  public Constructor<? extends Lorm<?>> getConstructor() {
    return constructor;
  }

  public void setConstructor(Constructor<? extends Lorm<?>> constructor) {
    this.constructor = constructor;
  }

  public Map<String, Relation<? extends Lorm<?>, ? extends Lorm<?>>> getRelationMap() {
    return relationMap;
  }

  public void setRelationMap(Map<String, Relation<? extends Lorm<?>, ? extends Lorm<?>>> relationMap) {
    this.relationMap = relationMap;
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

  public void setValueFromResultSet(Lorm<?> lorm, ResultSet resultSet) {
    for (ColumnInfo columnInfo : columns) {
      try {
        Object value = resultSet.getObject(columnInfo.columnName);

        if (lorm.getBeforeIn().containsKey(columnInfo.columnName))
          value = lorm.getBeforeIn().get(columnInfo.columnName).apply(value);

        columnInfo.setter.invoke(lorm, value);

        lorm.getOldValues().put(columnInfo.columnName, value);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    lorm.setLoaded(true);
  }

  public HashMap<String, Object> getBeforeOutValues(Lorm<?> lorm) {
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

  public HashMap<String, Object> getBeforeOutInsertValues(Lorm<?> lorm) {
    var beforeOutValues = getBeforeOutValues(lorm);

    columns.stream().filter(ColumnInfo::isPrimaryKey)
        .forEach(columnInfo -> beforeOutValues.remove(columnInfo.columnName));

    return beforeOutValues;
  }

  public HashMap<String, Object> getBeforeOutPrimaryKeyOrAll(Lorm<?> lorm) {
    var beforeOutValues = getBeforeOutValues(lorm);

    // We get all primary keys
    var primaryKeys = columns.stream().filter(ColumnInfo::isPrimaryKey).map(ColumnInfo::getColumnName)
        .toArray(String[]::new);

    // If we have primary keys, we return only them
    if (primaryKeys.length > 0) {
      var primaryKeyValues = new HashMap<String, Object>();
      for (String primaryKey : primaryKeys)
        primaryKeyValues.put(primaryKey, beforeOutValues.get(primaryKey));
      return primaryKeyValues;
    }

    return beforeOutValues;
  }

  public ColumnInfo getPrimaryKey() {
    return primaryKey;
  }
}
