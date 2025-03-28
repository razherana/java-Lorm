package mg.razherana.lorm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

import mg.razherana.lorm.exceptions.LormException;
import mg.razherana.lorm.exceptions.RelationNotFoundException;
import mg.razherana.lorm.queries.WhereContainer;
import mg.razherana.lorm.reflect.ReflectContainer;
import mg.razherana.lorm.relations.NestedEagerLoad;
import mg.razherana.lorm.relations.Relation;

abstract public class Lorm<T extends Lorm<T>> {
  final private ReflectContainer reflectContainer;
  private Map<String, Function<Object, ?>> beforeOut = new HashMap<>();
  private Map<String, Function<Object, ?>> beforeIn = new HashMap<>();

  final private Map<String, Object> oldValues = new HashMap<>();

  public Map<String, Object> getOldValues() {
    return oldValues;
  }

  private boolean loaded = false;

  public boolean isLoaded() {
    return loaded;
  }

  public void setLoaded(boolean loaded) {
    this.loaded = loaded;
  }

  public Lorm() {
    reflectContainer = ReflectContainer.loadAnnotations(this);
    for (String eagerLoad : reflectContainer.getEagerLoads())
      eagerLoads.add(new NestedEagerLoad(eagerLoad));
  }

  protected HashSet<NestedEagerLoad> eagerLoads = new HashSet<>();

  public HashSet<NestedEagerLoad> getEagerLoads() {
    return eagerLoads;
  }

  public void setEagerLoads(HashSet<NestedEagerLoad> eagerLoads) {
    this.eagerLoads = eagerLoads;
  }

  /**
   * Checks if exists first
   * 
   * @param eagerLoad
   * @return
   */
  public Lorm<T> addEagerLoad(String eagerLoad) {
    for (NestedEagerLoad nestedEagerLoad : eagerLoads)
      if (eagerLoad.equals(nestedEagerLoad.getRelation()))
        return this;
    eagerLoads.add(new NestedEagerLoad(eagerLoad));
    return this;
  }

  /**
   * There are no checking or this
   * 
   * @param eagerLoad
   * @return
   */
  public Lorm<T> addEagerLoad(String[] eagerLoad) {
    for (String string : eagerLoad)
      eagerLoads.add(new NestedEagerLoad(string));
    return this;
  }

  /**
   * Checks if exist then get the existant else create one
   * 
   * @param eagerLoad
   * @return
   */
  public NestedEagerLoad addNestedEagerLoad(String eagerLoad) {
    for (NestedEagerLoad nestedEagerLoad : eagerLoads)
      if (eagerLoad.equals(nestedEagerLoad.getRelation()))
        return nestedEagerLoad;

    NestedEagerLoad nestedEagerLoad = new NestedEagerLoad(eagerLoad);
    eagerLoads.add(nestedEagerLoad);
    return nestedEagerLoad;
  }

  public Map<String, Function<Object, ?>> beforeIn() {
    return new HashMap<>();
  }

  public Map<String, Function<Object, ?>> beforeOut() {
    return new HashMap<>();
  }

  public ReflectContainer getReflectContainer() {
    return reflectContainer;
  }

  public Map<String, Function<Object, ?>> getBeforeOut() {
    return beforeOut;
  }

  public void setBeforeOut(Map<String, Function<Object, ?>> beforeOut) {
    this.beforeOut = beforeOut;
  }

  public Map<String, Function<Object, ?>> getBeforeIn() {
    return beforeIn;
  }

  public void setBeforeIn(Map<String, Function<Object, ?>> beforeIn) {
    this.beforeIn = beforeIn;
  }

  void setValueFromResultSet(ResultSet resultSet) {
    reflectContainer.setValueFromResultSet(this, resultSet);
  }

  public ArrayList<T> query(String query, Object[] queryParams, Connection connection) throws SQLException {
    PreparedStatement preparedStatement = connection.prepareStatement(query);

    System.out.println("[" + getClass() + ":query] -> " + query);

    for (int i = 0; i < queryParams.length; i++)
      preparedStatement.setObject(i + 1, queryParams[i]);

    ResultSet resultSet = preparedStatement.executeQuery();

    ArrayList<T> result = new ArrayList<>();

    var constructor = reflectContainer.getConstructor();

    while (resultSet.next()) {
      try {
        @SuppressWarnings("unchecked")
        T instance = (T) constructor.newInstance();
        instance.setEagerLoads(eagerLoads);
        instance.setValueFromResultSet(resultSet);
        result.add(instance);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    resultSet.close();
    preparedStatement.close();

    loadEagerLoads(result, connection);

    return result;
  }

  public ArrayList<T> query(String query, Connection connection) throws SQLException {
    return query(query, new Object[] {}, connection);
  }

  public T find(Object id, Connection connection) throws SQLException {
    var primaryKey = reflectContainer.getPrimaryKey();
    if (primaryKey == null)
      return null;

    ArrayList<T> ts = where(
        new ArrayList<>(
            List.of(
                new WhereContainer(primaryKey.getColumnName(), id, "="))),
        connection);

    return ts.size() > 0 ? ts.get(0) : null;
  }

  public ArrayList<T> where(String extraQuery, ArrayList<WhereContainer> whereContainers, Connection connection)
      throws SQLException {
    String query = "SELECT * FROM " + reflectContainer.getTable();
    var where = WhereContainer.toConditionClause(whereContainers);

    Object[] queryParams = where.getValue();

    if (!where.getKey().isEmpty())
      query += " WHERE " + where.getKey();

    if (!extraQuery.isEmpty())
      query += " " + extraQuery;

    return query(query, queryParams, connection);
  }

  public ArrayList<T> where(ArrayList<WhereContainer> whereContainers, Connection connection) throws SQLException {
    return where("", whereContainers, connection);
  }

  public ArrayList<T> all(Connection connection) throws SQLException {
    return where("", new ArrayList<>(), connection);
  }

  public void save(Connection connection) throws SQLException {
    String query = "INSERT INTO " + reflectContainer.getTable() + " (";
    String values = " VALUES (";
    ArrayList<Object> queryParams = new ArrayList<>();

    HashMap<String, Object> beforeOutValues = reflectContainer.getBeforeOutInsertValues(this);

    for (Map.Entry<String, Object> entry : beforeOutValues.entrySet()) {
      query += entry.getKey() + ", ";
      values += "?, ";
      queryParams.add(entry.getValue());
    }

    // Remove the last comma and space
    // Add the values
    query = (beforeOutValues.size() > 0 ? query.substring(0, query.length() - 2) : query) + ")"
        + (beforeOutValues.size() > 0 ? values.substring(0, values.length() - 2) : values) + ")";

    System.out.println("[" + getClass() + ":save] -> " + query);

    PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

    for (int i = 0; i < queryParams.size(); i++)
      preparedStatement.setObject(i + 1, queryParams.get(i));

    preparedStatement.executeUpdate();

    var prim = reflectContainer.getPrimaryKey();
    try {
      if (prim != null) {
        var last = preparedStatement.getGeneratedKeys();
        if (last.next())
          prim.setter.invoke(this, last.getInt(1));
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    preparedStatement.close();
  }

  public void update(Connection connection) throws SQLException {
    String query = "UPDATE " + reflectContainer.getTable() + " SET ";

    ArrayList<Object> queryParams = new ArrayList<>();

    HashMap<String, Object> beforeOutValues = reflectContainer.getBeforeOutInsertValues(this);

    int c = 0;
    for (Map.Entry<String, Object> entry : beforeOutValues.entrySet()) {
      query += entry.getKey() + " = ?";
      queryParams.add(entry.getValue());
      if (c++ != beforeOutValues.size() - 1)
        query += ", ";
    }

    System.out.println(query);

    query += " WHERE ";

    if (reflectContainer.getPrimaryKey() != null)
      query += reflectContainer.getPrimaryKey().getColumnName() + " = ?";
    else if (beforeOutValues.size() > 0) {
      int c1 = 0;
      for (String colName : beforeOutValues.keySet()) {
        query += colName + " = ?";
        queryParams.add(beforeOutValues.get(colName));

        if (c1++ != beforeOutValues.size() - 1)
          query += " AND ";
      }
    } else
      throw new LormException("No columns found to make the where query...");

    System.out.println("[" + getClass() + ":update] -> " + query);

    PreparedStatement preparedStatement = connection.prepareStatement(query);

    for (int i = 0; i < queryParams.size(); i++)
      preparedStatement.setObject(i + 1, queryParams.get(i));

    preparedStatement.close();
  }

  public Map<String, Relation<? extends Lorm<?>, ? extends Lorm<?>>> relations() {
    return new HashMap<>();
  }

  // #region [RelationMethods]

  private void loadEagerLoads(ArrayList<T> models, Connection connection) throws SQLException {
    var relationMap = reflectContainer.getRelationMap();
    for (var nested : getEagerLoads()) {
      String eagerLoad = nested.getRelation();
      if (!relationMap.containsKey(eagerLoad))
        throw new RelationNotFoundException(
            "The relation of name '" + eagerLoad + "' doesn't exist in " + getClass());

      @SuppressWarnings("unchecked")
      var relation = (Relation<T, ?>) relationMap.get(eagerLoad);

      switch (relation.getRelationType()) {
        case HASMANY:
          hasManyStatic(models, relation, connection, nested.getNestedLoads());
          break;
        case BELONGSTO:
          belongsToStatic(models, relation, connection, nested.getNestedLoads());
          break;
        default:
          break;
      }
    }
  }

  public HashMap<String, ArrayList<Lorm<?>>> hasMany = new HashMap<>();

  protected <U extends Lorm<U>> ArrayList<U> hasMany(String relationName, Connection connection)
      throws SQLException {
    @SuppressWarnings("unchecked")
    Relation<T, U> relation = (Relation<T, U>) getReflectContainer().getRelationMap().get(relationName);
    if (relation == null)
      throw new RelationNotFoundException("The relation of name '" + relationName + "' doesn't exist");
    return hasMany(relation, connection);
  }

  @SuppressWarnings("unchecked")
  protected <U extends Lorm<U>> ArrayList<U> hasMany(Relation<T, U> relation, Connection connection)
      throws SQLException {
    if (hasMany.containsKey(relation.getRelationName()))
      return (ArrayList<U>) hasMany.get(relation.getRelationName());
    return this.<U>hasManyInstance(relation, connection);
  }

  private <U extends Lorm<U>> ArrayList<U> hasManyInstance(Relation<T, U> relation, Connection connection)
      throws SQLException {
    Objects.requireNonNull(connection, "Not loaded relation, cannot use null as connection");
    U other;
    try {
      var constr = relation.getModel2().getDeclaredConstructor();
      constr.setAccessible(true);
      var model = constr.newInstance();
      other = model;
      other.setEagerLoads(
          getEagerLoads().stream().filter((e) -> e.getRelation().equals(relation.getRelationName()))
              .findFirst().orElse(new NestedEagerLoad(relation.getRelationName())).getNestedLoads());
    } catch (Exception e) {
      throw new RuntimeException("Error instanciating the other model...", e);
    }

    ArrayList<U> otherModels = other.all(connection);
    @SuppressWarnings("unchecked")
    final T thisT = (T) this;
    otherModels.removeIf((o) -> !relation.getCondition().test(thisT, o));

    return otherModels;
  }

  @SuppressWarnings("unchecked")
  private static <U extends Lorm<U>, T extends Lorm<T>> void hasManyStatic(ArrayList<T> models,
      Relation<T, U> relation,
      Connection connection, HashSet<NestedEagerLoad> nestedLoads) throws SQLException {
    U other;
    try {
      var constr = relation.getModel2().getDeclaredConstructor();
      constr.setAccessible(true);
      var model = constr.newInstance();
      other = model;
      other.setEagerLoads(nestedLoads);
    } catch (Exception e) {
      throw new RuntimeException("Error instanciating the other model...", e);
    }

    ArrayList<U> otherModels = other.all(connection);

    relation.getRelationSetter().apply((ArrayList<Lorm<?>>) models, (ArrayList<Lorm<?>>) otherModels,
        (BiPredicate<Lorm<?>, Lorm<?>>) relation.getCondition(),
        relation.getRelationName());
  }

  public HashMap<String, Lorm<?>> oneToOne = new HashMap<>();

  protected <U extends Lorm<U>> U belongsTo(String relationName, Connection connection)
      throws SQLException {
    @SuppressWarnings("unchecked")
    Relation<T, U> relation = (Relation<T, U>) getReflectContainer().getRelationMap().get(relationName);
    if (relation == null)
      throw new RelationNotFoundException("The relation of name '" + relationName + "' doesn't exist");
    return belongsTo(relation, connection);
  }

  @SuppressWarnings("unchecked")
  protected <U extends Lorm<U>> U belongsTo(Relation<T, U> relation, Connection connection)
      throws SQLException {
    if (oneToOne.containsKey(relation.getRelationName()))
      return (U) oneToOne.get(relation.getRelationName());
    return this.<U>belongsToInstance(relation, connection);
  }

  protected <U extends Lorm<U>> U oneToOne(String relationName, Connection connection)
      throws SQLException {
    @SuppressWarnings("unchecked")
    Relation<T, U> relation = (Relation<T, U>) getReflectContainer().getRelationMap().get(relationName);
    if (relation == null)
      throw new RelationNotFoundException("The relation of name '" + relationName + "' doesn't exist");
    return oneToOne(relation, connection);
  }

  @SuppressWarnings("unchecked")
  protected <U extends Lorm<U>> U oneToOne(Relation<T, U> relation, Connection connection)
      throws SQLException {
    if (oneToOne.containsKey(relation.getRelationName()))
      return (U) oneToOne.get(relation.getRelationName());
    return this.<U>belongsToInstance(relation, connection);
  }

  private <U extends Lorm<U>> U belongsToInstance(Relation<T, U> relation, Connection connection)
      throws SQLException {
    Objects.requireNonNull(connection, "Not loaded relation, cannot use null as connection");
    U other;
    try {
      var constr = relation.getModel2().getDeclaredConstructor();
      constr.setAccessible(true);
      var model = constr.newInstance();
      other = model;
      other.setEagerLoads(
          getEagerLoads().stream().filter((e) -> e.getRelation().equals(relation.getRelationName()))
              .findFirst().orElse(new NestedEagerLoad(relation.getRelationName())).getNestedLoads());
    } catch (Exception e) {
      throw new RuntimeException("Error instanciating the other model...", e);
    }

    ArrayList<U> otherModels = other.all(connection);
    @SuppressWarnings("unchecked")
    final T thisT = (T) this;
    otherModels.removeIf((o) -> !relation.getCondition().test(thisT, o));

    return otherModels.size() > 0 ? otherModels.get(0) : null;
  }

  @SuppressWarnings("unchecked")
  private static <U extends Lorm<U>, T extends Lorm<T>> void belongsToStatic(ArrayList<T> models,
      Relation<T, U> relation, Connection connection, HashSet<NestedEagerLoad> nestedLoads) throws SQLException {
    U other;
    try {
      var constr = relation.getModel2().getDeclaredConstructor();
      constr.setAccessible(true);
      var model = constr.newInstance();
      other = model;
      other.setEagerLoads(nestedLoads);
    } catch (Exception e) {
      throw new RuntimeException("Error instanciating the other model...", e);
    }

    ArrayList<U> otherModels = other.all(connection);

    relation.getRelationSetter().apply((ArrayList<Lorm<?>>) models, (ArrayList<Lorm<?>>) otherModels,
        (BiPredicate<Lorm<?>, Lorm<?>>) relation.getCondition(),
        relation.getRelationName());
  }

  // #endregion [RelationMethods]
}
