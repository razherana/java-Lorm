package mg.razherana.generator.utils;

import java.sql.Types;

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
      case Types.DATE:
        return "java.sql.Date";
      case Types.TIMESTAMP:
        return "java.sql.Timestamp";
      case Types.BLOB:
        return "byte[]";
      case Types.BOOLEAN:
        return "boolean";
      default:
        return "Object";
    }
  }
}
