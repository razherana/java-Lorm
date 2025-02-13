package mg.razherana.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import mg.razherana.database.exceptions.DotEnvNotInitializedException;
import mg.razherana.database.exceptions.DriverNotLoadedException;

public class DatabaseConnection {
  private String url;
  private String user;
  private String password;
  private String driver;

  public String getDriver() {
    return driver;
  }

  public void setDriver(String driver) {
    this.driver = driver;
  }

  public DatabaseConnection(String url, String user, String password, String driver) {
    setUrl(url);
    setUser(user);
    setPassword(password);
    setDriver(driver);
  }

  /**
   * Load the database connection from a .env file
   * 
   * @param fileName
   * @return
   */
  public static DatabaseConnection fromDotEnv(String fileName) {
    if (fileName == null)
      throw new IllegalArgumentException("File name cannot be null");

    Properties properties;
    if (new File(fileName).exists()) {
      System.out.println("[INFO] -> Using dotenv : " + new File(fileName).getAbsolutePath());
      try (FileInputStream fis = new FileInputStream(fileName)) {
        properties = new Properties();
        properties.loadFromXML(fis);
        return new DatabaseConnection(properties.getProperty("url"), properties.getProperty("user"),
            properties.getProperty("password"), properties.getProperty("driver"));
      } catch (IOException e) {
        e.printStackTrace();
        throw new RuntimeException("Cannot read the file " + fileName, e);
      }
    }

    properties = new Properties();
    properties.setProperty("url", "{url}");
    properties.setProperty("user", "{user}");
    properties.setProperty("password", "{password}");
    properties.setProperty("driver", "{driver}");

    try (FileOutputStream fos = new FileOutputStream(fileName)) {
      properties.storeToXML(fos, "Database connection properties");
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Cannot write to the file " + fileName, e);
    }

    throw new DotEnvNotInitializedException(
        "Please fill the file " + fileName + " with the correct values, then try again");
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Connection getConnection() throws SQLException {
    try {
      // Load the driver
      Class.forName(driver);
    } catch (ClassNotFoundException e) {
      throw new DriverNotLoadedException("Driver not loaded or does't exists: " + driver);
    }

    // Get the connection
    return DriverManager.getConnection(url, user, password);
  }
}