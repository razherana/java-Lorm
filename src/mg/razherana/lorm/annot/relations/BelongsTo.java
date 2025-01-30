package mg.razherana.lorm.annot.relations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import mg.razherana.lorm.Lorm;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(BelongsToContainer.class)
public @interface BelongsTo {
  Class<? extends Lorm<?>> model();

  String foreignKey() default "";

  String relationName() default "";
}
