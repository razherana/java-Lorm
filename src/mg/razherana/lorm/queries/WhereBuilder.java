package mg.razherana.lorm.queries;

import java.util.ArrayList;

public class WhereBuilder {
  private ArrayList<WhereContainer> whereContainers = new ArrayList<>();

  private WhereBuilder() {
  }

  public static WhereBuilder create() {
    return new WhereBuilder();
  }

  public WhereBuilder add(String element1, Object element2, String operator) {
    whereContainers.add(new WhereContainer(element1, element2, operator));
    return this;
  }

  public WhereBuilder add(String element1, Object element2, String operator, boolean intoParameters) {
    whereContainers.add(new WhereContainer(element1, element2, operator, intoParameters));
    return this;
  }

  public WhereBuilder add(String element1, Object element2, String operator, String next) {
    whereContainers.add(new WhereContainer(element1, element2, operator, next));
    return this;
  }

  public WhereBuilder add(String element1, Object element2, String operator, String next, boolean intoParameters) {
    whereContainers.add(new WhereContainer(element1, element2, operator, next, intoParameters));
    return this;
  }

  public WhereBuilder add(WhereContainer whereContainer) {
    whereContainers.add(whereContainer);
    return this;
  }

  public WhereBuilder andWhere(String element1, Object element2, String operator) {
    whereContainers.add(new WhereContainer(element1, element2, operator, "AND"));
    return this;
  }

  public WhereBuilder orWhere(String element1, Object element2, String operator) {
    whereContainers.add(new WhereContainer(element1, element2, operator, "OR"));
    return this;
  }

  public ArrayList<WhereContainer> getWhereContainers() {
    return whereContainers;
  }

  public ArrayList<WhereContainer> then() {
    return whereContainers;
  }
}
