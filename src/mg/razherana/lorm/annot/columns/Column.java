package mg.razherana.lorm.annot.columns;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
  public String value() default "";

  public boolean primaryKey() default false;

  /**
   * The getter's name
   */
  public String getter() default "";

  /**
   * The setter's name
   */
  public String setter() default "";

  /**
   * Tells that a column is indexed when using in relation.
   */
  public boolean indexed() default false;
}
