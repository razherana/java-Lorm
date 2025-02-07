package mg.razherana.lorm.exceptions;

public class RelationNotFoundException extends RuntimeException {
  public RelationNotFoundException(String message) {
    super(message);
  }
}
