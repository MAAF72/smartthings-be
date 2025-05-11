package io.github.maaf72.smartthings.config;


public class Config {
  public final static String APP_NAME = System.getenv().getOrDefault("APP_NAME", "smartthings-be");
  public final static boolean APP_DEVELOPMENT = Boolean.valueOf(System.getenv().getOrDefault("APP_DEVELOPMENT", "false"));
  public final static int APP_PORT = Integer.valueOf(System.getenv().getOrDefault("APP_PORT", "8080"));
  public final static String APP_API_PREFIX = System.getenv().getOrDefault("APP_API_PREFIX", "api/v1");
  public final static String[] APP_PUBLIC_PATHS = System.getenv().getOrDefault("APP_PUBLIC_PATHS", APP_API_PREFIX + "/auth").split(",");

  public final static String APP_JWT_SECRET_KEY = System.getenv().getOrDefault("APP_JWT_SECRET_KEY", "LoremIpsumDolorSitAmetConsecteturAdipiscingElitAliquamConsequatx");
  public final static Long APP_JWT_TOKEN_DURATION = Long.parseLong(System.getenv().getOrDefault("APP_JWT_TOKEN_DURATION", "86400000"));

  public final static String APP_DATABASE_DRIVER = System.getenv().getOrDefault("APP_DATABASE_DRIVER", "org.h2.Driver");
  public final static String APP_DATABASE_JDBC_URL = System.getenv().getOrDefault("APP_DATABASE_JDBC_URL", "jdbc:h2:file:./.db");
  public final static String APP_DATABASE_USERNAME = System.getenv().getOrDefault("APP_DATABASE_USERNAME", "root");
  public final static String APP_DATABASE_PASSWORD = System.getenv().getOrDefault("APP_DATABASE_PASSWORD", "");
}
