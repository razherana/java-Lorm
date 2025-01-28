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

  public String getter() default "";

  public String setter() default "";
}
