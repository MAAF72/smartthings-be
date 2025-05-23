package io.github.maaf72.smartthings.infra.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.maaf72.smartthings.domain.common.dto.BaseResponse;
import io.github.maaf72.smartthings.infra.tracing.RequestTracer;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import ratpack.core.error.ServerErrorHandler;
import ratpack.core.handling.Context;
import ratpack.core.jackson.Jackson;

@Slf4j
public class ExceptionHandler implements ServerErrorHandler {
  @Override
  public void error(Context context, Throwable throwable) throws Exception {
    switch (throwable) {
      case null:
        break;
      case Error e:
        renderError(context, 500, e);
        break;
      case Exception ex:
        switch (ex) {
          case ValidationException ve:
            Map<String, Object> details = Stream.of(ve.getMessage().split("; "))
              .map(pair -> pair.split(": ", 2))     
              .collect(Collectors.toMap(
                parts -> parts[0].replaceAll("([A-Z])(?=[A-Z])", "$1_").replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(), // convert to snake case           
                parts -> parts[1],                 
                (a, _) -> a,                       
                LinkedHashMap::new                 
              ));

            renderError(context, 400, new Exception("Bad Request"), details);
            break;
          case HttpException he:
            renderError(context, he.getStatusCode(), he);
            break;
          default:
            renderError(context, 500, ex);
            break;
        }
        break;
      default:
        renderError(context, 500, throwable);
        break;
    }}

  private static void renderError(Context context, int statusCode, Throwable throwable) {
    renderError(context, statusCode, throwable, null);
  }

  private static void renderError(Context context, int statusCode, Throwable throwable, Map<String, Object> details) {
    context.maybeGet(RequestTracer.class).ifPresent(
      requestTracer -> {
        Span span = requestTracer.getRootSpan();

        if (!span.isRecording()) {
          return;
        }

        span.setStatus(StatusCode.ERROR);

        AttributesBuilder attributesBuilder = Attributes.builder();

        attributesBuilder.put("error.message", throwable.getMessage());

        if (details != null) {
          attributesBuilder.put("error.details", details.toString());
        }

        span.addEvent("error", attributesBuilder.build());
      }
    );

    String message = throwable.getMessage();

    if (statusCode == 500) {
      log.error("Server Internal Error: {} -> {}", throwable.getClass().getCanonicalName(), message);
      log.debug(throwable.getStackTrace()[0].toString());

      message = "Server Internal Error";
    }

    context.getResponse().status(statusCode);
    context.render(Jackson.json(BaseResponse.of(false, message, details)));
  }
}
