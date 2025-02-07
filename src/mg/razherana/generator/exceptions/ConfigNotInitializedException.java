package mg.razherana.generator.exceptions;

public class ConfigNotInitializedException extends RuntimeException {
  public ConfigNotInitializedException() {
    super("The generator's config isn't initialized yet, please fill them...");
  }
}
