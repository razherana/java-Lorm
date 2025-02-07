package mg.razherana.generator.utils;

import java.util.function.Function;

public class ConventionStringBuilder {
  public static enum Convention {
    CAMEL(ConventionStringBuilder::toCamelCase),
    SNAKE(ConventionStringBuilder::toSnakeCase),
    PASCAl(ConventionStringBuilder::toPascalCase);

    Convention(Function<String, String> function) {
      this.function = function;
    }

    final private Function<String, String> function;

    public Function<String, String> getFunction() {
      return function;
    }

    public static Convention parseString(String str) {
      switch (str) {
        case "PascalCase":
          return PASCAl;
        case "camelCase":
          return CAMEL;
        case "snake_case":
          return SNAKE;
        default:
          return null;
      }
    }
  }

  public static String toConvention(String str, Convention convention) {
    if (convention == null)
      return str;
    return convention.function.apply(str);
  }

  private static String toCamelCase(String str) {
    str = str.substring(0, 1).toLowerCase() + str.substring(1, str.length());
    StringBuilder camelCase = new StringBuilder();
    boolean toUpper = false;
    for (char c : str.toCharArray()) {
      if (c == '_')
        toUpper = true;
      else if (toUpper) {
        camelCase.append(Character.toUpperCase(c));
        toUpper = false;
      } else
        camelCase.append(Character.toLowerCase(c));
    }
    return camelCase.toString();
  }

  private static String toSnakeCase(String str) {
    str = str.substring(0, 1).toLowerCase() + str.substring(1, str.length());
    StringBuilder snakeCase = new StringBuilder();
    for (char c : str.toCharArray()) {
      if (Character.isUpperCase(c)) {
        snakeCase.append('_').append(Character.toLowerCase(c));
      } else {
        snakeCase.append(c);
      }
    }
    return snakeCase.toString();
  }

  private static String toPascalCase(String str) {
    str = toCamelCase(str);
    return str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
  }
}
