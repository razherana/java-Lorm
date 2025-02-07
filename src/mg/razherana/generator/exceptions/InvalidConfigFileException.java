package mg.razherana.generator.exceptions;

public class InvalidConfigFileException extends RuntimeException {
  public InvalidConfigFileException() {
    super("The config file is invalid");
  }
}
