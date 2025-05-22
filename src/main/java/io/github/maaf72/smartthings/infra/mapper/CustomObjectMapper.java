package io.github.maaf72.smartthings.infra.mapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CustomObjectMapper {
  private static final ObjectMapper objectMapper = buildObjectMapper();

  private static ObjectMapper buildObjectMapper() {
    return new ObjectMapper()
      .registerModules(new JavaTimeModule())
      .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  }

  public static ObjectMapper getObjectMapper() {
    return objectMapper;
  }
}