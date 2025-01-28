package mg.razherana.lorm;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import mg.razherana.lorm.reflect.ReflectContainer;

abstract public class Lorm {
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

  
}
