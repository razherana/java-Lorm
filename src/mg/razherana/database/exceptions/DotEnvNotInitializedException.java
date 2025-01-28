package mg.razherana.database.exceptions;

public class DotEnvNotInitializedException extends RuntimeException {
  public DotEnvNotInitializedException(String message) {
    super(message);
  }

}
