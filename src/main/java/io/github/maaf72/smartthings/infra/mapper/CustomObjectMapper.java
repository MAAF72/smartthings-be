package io.github.maaf72.smartthings.infra.mapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CustomObjectMapper {
  private static final ObjectMapper objectMapper = buildObjectMapper();

  private static ObjectMapper buildObjectMapper() {
    return new ObjectMapper()
      .registerModules(new JavaTimeModule(), new Hibernate6Module())
      .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
      .setSerializationInclusion(JsonInclude.Include.NON_NULL)
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      // .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  }

  public static ObjectMapper getObjectMapper() {
    return objectMapper;
  }
}