package mg.razherana.lorm.relations;

import java.util.ArrayList;
import java.util.function.BiPredicate;

import mg.razherana.lorm.Lorm;
import mg.razherana.lorm.annot.relations.BelongsTo;
import mg.razherana.lorm.annot.relations.HasMany;
import mg.razherana.lorm.annot.relations.OneToOne;

public enum RelationType {
  HASMANY(HasMany.class, (a, b, cond, name) -> {
    for (Lorm<?> t : a) {
      ArrayList<Lorm<?>> joins = new ArrayList<>();
      for (Lorm<?> u : b)
        if (cond.test(t, u))
          joins.add(u);
      t.hasMany.put(name, joins);
    }
  }),

  BELONGSTO(BelongsTo.class, (a, b, cond, name) -> {
    for (Lorm<?> t : a) {
      Lorm<?> join = null;

      for (Lorm<?> u : b)
        if (cond.test(t, u)) {
          join = u;
          break;
        }

      if (join != null)
        t.oneToOne.put(name, join);
    }
  }),

  ONETOONE(OneToOne.class, BELONGSTO.getRelationSetter());

  final private Class<?> annotation;

  @FunctionalInterface
  public static interface CheckAndAddToData {
    public void apply(ArrayList<Lorm<?>> models1, ArrayList<Lorm<?>> models2, BiPredicate<Lorm<?>, Lorm<?>> condition,
        String relationName);
  }

  final private CheckAndAddToData relationSetter;

  public CheckAndAddToData getRelationSetter() {
    return relationSetter;
  }

  public Class<?> getAnnotation() {
    return annotation;
  }

  RelationType(Class<?> annotation, CheckAndAddToData relationSetter) {
    this.annotation = annotation;
    this.relationSetter = relationSetter;
  }
}
