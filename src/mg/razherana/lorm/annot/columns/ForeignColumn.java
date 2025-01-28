package mg.razherana.lorm.annot.columns;

import mg.razherana.lorm.Lorm;

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
