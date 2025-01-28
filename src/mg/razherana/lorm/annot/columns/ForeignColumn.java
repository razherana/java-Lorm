package mg.razherana.lorm.annot.columns;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import mg.razherana.lorm.Lorm;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignColumn {
  /**
   * The name of the foreign <b>field</b> in the other class.
   */
  public String name() default "";

  /**
   * The other Lorm
   */
  public Class<? extends Lorm<?>> model();
}
