package mg.razherana.lorm;

import java.util.Map;
import java.util.function.Function;

abstract public class Lorm {
  private String table;
  private Map<String, Function<Object, ?>> beforeIn;
  private Map<String, Function<Object, ?>> beforeOut;

  public Lorm() {
    loadAnnotations();
  }

  private void loadAnnotations() {
    // Load the annotations
    
  }
} 
