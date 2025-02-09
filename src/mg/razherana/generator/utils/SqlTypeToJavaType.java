package mg.razherana.generator.utils;

import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SqlTypeToJavaType {
  public static String mapSqlTypeToJavaType(int sqlType) {
    switch (sqlType) {
      case Types.INTEGER:
        return "int";
      case Types.VARCHAR:
      case Types.CHAR:
      case Types.LONGVARCHAR:
      case Types.CLOB:
        return "String";
      case Types.DOUBLE:
        return "double";
      case Types.FLOAT:
        return "float";
      case Types.DECIMAL:
        return "java.math.BigDecimal";
      case Types.TIME:
        return LocalTime.class.getName();
      case Types.DATE:
        return LocalDate.class.getName();
      case Types.TIMESTAMP:
        return LocalDateTime.class.getName();
      case Types.BLOB:
        return "byte[]";
      case Types.BOOLEAN:
        return "boolean";
      default:
        return "Object";
    }
  }
}
