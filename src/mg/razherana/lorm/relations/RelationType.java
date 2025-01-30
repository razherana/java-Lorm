package mg.razherana.lorm.relations;

import mg.razherana.lorm.annot.relations.BelongsTo;
import mg.razherana.lorm.annot.relations.HasMany;
import mg.razherana.lorm.annot.relations.OneToOne;

public enum RelationType {
  HASMANY(HasMany.class),
  BELONGSTO(BelongsTo.class),
  ONETOONE(OneToOne.class);

  final private Class<?> annotation;

  public Class<?> getAnnotation() {
    return annotation;
  }

  RelationType(Class<?> annotation) {
    this.annotation = annotation;
  }
}
