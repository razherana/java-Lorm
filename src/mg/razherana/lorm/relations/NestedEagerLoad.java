package mg.razherana.lorm.relations;

import java.util.HashSet;

public class NestedEagerLoad {
  private String relation;
  private HashSet<NestedEagerLoad> nestedLoads;

  public NestedEagerLoad(String relation) {
    this.relation = relation;
    this.nestedLoads = new HashSet<>();
  }

  /**
   * Adds a relation on the same level
   * 
   * @param relation
   * @return
   */
  public NestedEagerLoad addEagerLoad(String relation) {
    for (NestedEagerLoad nestedEagerLoad : nestedLoads)
      if (relation.equals(nestedEagerLoad.getRelation()))
        return nestedEagerLoad;

    NestedEagerLoad nestedLoad = new NestedEagerLoad(relation);
    this.nestedLoads.add(nestedLoad);
    return this;
  }

  /**
   * Adds a relation on the same level then returns that relation for nested
   * eagerLoads
   * 
   * @param relation
   * @return
   */
  public NestedEagerLoad addNestedEagerLoad(String relation) {
    for (NestedEagerLoad nestedEagerLoad : nestedLoads)
      if (relation.equals(nestedEagerLoad.getRelation()))
        return nestedEagerLoad;

    NestedEagerLoad nestedLoad = new NestedEagerLoad(relation);
    this.nestedLoads.add(nestedLoad);
    return nestedLoad;
  }

  public String getRelation() {
    return relation;
  }

  public HashSet<NestedEagerLoad> getNestedLoads() {
    return nestedLoads;
  }

  @Override
  public String toString() {
    return "NestedEagerLoad [relation=" + relation + ", nestedLoads=" + nestedLoads + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((relation == null) ? 0 : relation.hashCode());
    result = prime * result + ((nestedLoads == null) ? 0 : nestedLoads.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    NestedEagerLoad other = (NestedEagerLoad) obj;
    if (relation == null) {
      if (other.relation != null)
        return false;
    } else if (!relation.equals(other.relation))
      return false;
    if (nestedLoads == null) {
      if (other.nestedLoads != null)
        return false;
    } else if (!nestedLoads.equals(other.nestedLoads))
      return false;
    return true;
  }

}
