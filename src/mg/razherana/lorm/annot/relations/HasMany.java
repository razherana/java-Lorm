package mg.razherana.lorm.annot.relations;

import mg.razherana.lorm.Lorm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(HasManyContainer.class)
public @interface HasMany {
  Class<? extends Lorm<?>> model();

  String foreignKey() default "";

  String relationName() default "";
}
