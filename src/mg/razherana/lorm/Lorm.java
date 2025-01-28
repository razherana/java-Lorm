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

    for (int i = 0; i < queryParams.length; i++)
      preparedStatement.setObject(i + 1, queryParams[i]);

    ResultSet resultSet = preparedStatement.executeQuery();

    ArrayList<T> result = new ArrayList<>();

    Constructor<?> constructor;
    try {
      constructor = getClass().getDeclaredConstructor();
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

    return result;
  }

  
}
