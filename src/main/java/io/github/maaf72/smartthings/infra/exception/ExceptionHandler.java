package io.github.maaf72.smartthings.infra.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.maaf72.smartthings.domain.common.dto.BaseResponse;
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
        log.error("Error occurred: " + e.getClass().getCanonicalName() + " : {}", e.getMessage());
        context.getResponse().status(500);
        context.render(Jackson.json(BaseResponse.of(false, "Server Internal Error")));
        break;
      case Exception ex:
        log.error("Exception occurred: " + ex.getClass().getCanonicalName() + " : {}", ex.getMessage());
        switch (ex) {
          case ValidationException ve:
            context.getResponse().status(400);

            Map<String, Object> details = Stream.of(ve.getMessage().split("; "))
              .map(pair -> pair.split(": ", 2))     
              .collect(Collectors.toMap(
                parts -> parts[0].replaceAll("([A-Z])(?=[A-Z])", "$1_").replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(), // convert to snake case           
                parts -> parts[1],                 
                (a, b) -> a,                       
                LinkedHashMap::new                 
              ));

            context.render(Jackson.json(BaseResponse.of(false, "Bad Request", details)));
            break;
          case HttpException he:
            context.getResponse().status(he.getStatusCode());
            context.render(Jackson.json(BaseResponse.of(false, he.getMessage())));
            break;
          default:
            context.getResponse().status(500);
            context.render(Jackson.json(BaseResponse.of(false, "Server Internal Error")));
            break;
        }
        break;
      default:
        log.error("Unknown throwable occurred: " + throwable.getClass().getCanonicalName() + " : {}", throwable.getMessage());
        context.getResponse().status(500);
        context.render(Jackson.json(BaseResponse.of(false, "Server Internal Error")));
        break;
    }}
}
