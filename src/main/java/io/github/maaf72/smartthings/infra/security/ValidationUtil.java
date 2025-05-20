package io.github.maaf72.smartthings.infra.security;

import java.util.Set;
import java.util.stream.Collectors;

import io.smallrye.mutiny.Uni;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;

public class ValidationUtil {
  private static final Validator validator = buildValidator();

  private static Validator buildValidator() {
    return Validation.buildDefaultValidatorFactory().getValidator();
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

  public static <T> Uni<Void> validateAsync(T object) {
    return Uni.createFrom().emitter(emitter -> {
      try {
        validateOrThrow(object);
        emitter.complete(null);
      } catch (ValidationException e) {
        emitter.fail(e);
      }
    });
  }
}
