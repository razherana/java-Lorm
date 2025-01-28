package mg.razherana.lorm.annot.columns;

public @interface Column {
  public String value() default "";

  public boolean primaryKey() default false;

  public String getter() default "";

  public String setter() default "";
}
