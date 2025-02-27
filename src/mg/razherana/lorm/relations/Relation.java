package mg.razherana.lorm.relations;

import java.util.function.BiPredicate;

import mg.razherana.lorm.Lorm;

public class Relation<T extends Lorm<T>, U extends Lorm<U>> {
  final private Class<T> model1;
  final private Class<U> model2;
  final private BiPredicate<T, U> condition;
  final private RelationType relationType;
  final private String relationName;
  final private RelationType.CheckAndAddToData relationSetter;

  public Relation(Class<T> model1, Class<U> model2,
      BiPredicate<T, U> condition, RelationType relationType, String relationName) {
    this(model1, model2, condition, relationType, relationName, relationType.getRelationSetter());
  }

  public Relation(Class<T> model1, Class<U> model2,
      BiPredicate<T, U> condition, RelationType relationType, String relationName,
      RelationType.CheckAndAddToData relationSetter) {
    this.model1 = model1;
    this.model2 = model2;
    this.condition = condition;
    this.relationType = relationType;
    this.relationName = relationName;
    this.relationSetter = relationSetter;
  }

  public RelationType.CheckAndAddToData getRelationSetter() {
    return relationSetter;
  }

  public Class<T> getModel1() {
    return model1;
  }

  public Class<U> getModel2() {
    return model2;
  }

  public BiPredicate<T, U> getCondition() {
    return condition;
  }

  public RelationType getRelationType() {
    return relationType;
  }

  public String getRelationName() {
    return relationName;
  }
}
