package mg.razherana.lorm.relations;

import java.util.ArrayList;
import java.util.List;

public class NestedEagerLoad {
  private String relation;
  private List<NestedEagerLoad> nestedLoads;

  public NestedEagerLoad(String relation) {
    this.relation = relation;
    this.nestedLoads = new ArrayList<>();
  }

  public NestedEagerLoad addEagerLoad(String relation) {
    NestedEagerLoad nestedLoad = new NestedEagerLoad(relation);
    this.nestedLoads.add(nestedLoad);
    return this;
  }

  public NestedEagerLoad addNestedEagerLoad(String relation) {
    NestedEagerLoad nestedLoad = new NestedEagerLoad(relation);
    this.nestedLoads.add(nestedLoad);
    return nestedLoad;
  }

  public String getRelation() {
    return relation;
  }

  public List<NestedEagerLoad> getNestedLoads() {
    return nestedLoads;
  }

  @Override
  public String toString() {
    return "NestedEagerLoad [relation=" + relation + ", nestedLoads=" + nestedLoads + "]";
  }
}
