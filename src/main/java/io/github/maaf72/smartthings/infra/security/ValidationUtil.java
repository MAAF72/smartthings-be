package io.github.maaf72.smartthings.infra.security;

import java.util.Set;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;

@ApplicationScoped
public class ValidationUtil {
  private static final Validator validator = buildValidator();

  private static Validator buildValidator() {
    return Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Produces
  public static Validator getValidator() {
    return validator;
  }

  public static <T> void validateOrThrow(T object) {
    Set<ConstraintViolation<T>> violations = validator.validate(object);

    if (!violations.isEmpty()) {
      String errorMessages = violations.stream()
        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
        .collect(Collectors.joining("; "));

      throw new ValidationException(errorMessages);
    }
  }
}
