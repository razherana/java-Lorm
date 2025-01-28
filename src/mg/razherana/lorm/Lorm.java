package mg.razherana.lorm;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import mg.razherana.lorm.queries.WhereContainer;
import mg.razherana.lorm.reflect.ReflectContainer;

abstract public class Lorm<T extends Lorm<T>> {
  final private ReflectContainer reflectContainer;
  private Map<String, Function<Object, ?>> beforeOut = new HashMap<>();
  private Map<String, Function<Object, ?>> beforeIn = new HashMap<>();

  public Lorm() {
    reflectContainer = ReflectContainer.loadAnnotations(this);
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

    Constructor<?> constructor;
    try {
      constructor = getClass().getDeclaredConstructor();
      constructor.setAccessible(true);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    while (resultSet.next()) {
      try {
        @SuppressWarnings("unchecked")
        T instance = (T) constructor.newInstance();
        instance.setValueFromResultSet(resultSet);
        result.add(instance);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    resultSet.close();
    preparedStatement.close();

    return result;
  }

  public ArrayList<T> query(String query, Connection connection) throws SQLException {
    return query(query, new Object[] {}, connection);
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

    PreparedStatement preparedStatement = connection.prepareStatement(query);

    for (int i = 0; i < queryParams.size(); i++)
      preparedStatement.setObject(i + 1, queryParams.get(i));

    preparedStatement.executeUpdate();
  }
}
