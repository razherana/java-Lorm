package mg.razherana.lorm.queries;

import java.util.ArrayList;
import java.util.Map;

public class WhereContainer {
  final private String element1;
  final private Object element2;
  final private String operator;
  final private String next;
  final private boolean intoParameters;

  public boolean isIntoParameters() {
    return intoParameters;
  }

  public WhereContainer(String element1, Object element2, String operator) {
    this(element1, element2, operator, "AND", true);
  }

  public WhereContainer(String element1, Object element2, String operator, boolean intoParameters) {
    this(element1, element2, operator, "AND", intoParameters);
  }

  public WhereContainer(String element1, Object element2, String operator, String next) {
    this(element1, element2, operator, next, true);
  }

  public WhereContainer(String element1, Object element2, String operator, String next, boolean intoParameters) {
    this.element1 = element1;
    this.element2 = element2;
    this.operator = operator;
    this.next = next;
    this.intoParameters = intoParameters;
  }

  public static Map.Entry<String, Object[]> toConditionClause(ArrayList<WhereContainer> whereContainers) {
    if (whereContainers == null || whereContainers.size() == 0) 
      return Map.entry("", new Object[] {});
    
    var listWheres = new ArrayList<>();
    var listWhereNexts = new ArrayList<>();
    ArrayList<Object> queryParams = new ArrayList<>();

    for (WhereContainer whereContainer : whereContainers) {
      var where = whereContainer.getElement1() + " " + whereContainer.getOperator() + " ";
      listWhereNexts.add(whereContainer.getNext());

      if (whereContainer.isIntoParameters()) {
        where += "?";
        queryParams.add(whereContainer.getElement2());
      } else {
        where += whereContainer.getElement2();
      }

      listWheres.add(where);
    }

    if (listWheres.size() > 0) {
      var where = "";
      for (int i = 0; i < listWheres.size(); i++) {
        where += listWheres.get(i) + " ";
        if (i < listWheres.size() - 1 && i > 0)
          where += listWhereNexts.get(i) + " ";
      }

      return Map.entry(where, queryParams.toArray(Object[]::new));
    }

    return Map.entry("", new Object[] {});
  }

  public String getNext() {
    return next;
  }

  public String getElement1() {
    return element1;
  }

  public Object getElement2() {
    return element2;
  }

  public String getOperator() {
    return operator;
  }

}
