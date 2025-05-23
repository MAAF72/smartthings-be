package io.github.maaf72.smartthings.infra.middleware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.github.maaf72.smartthings.config.Config;
import io.github.maaf72.smartthings.infra.tracing.RequestTracer;
import io.github.maaf72.smartthings.itf.AppMiddlewareItf;
import io.opentelemetry.api.trace.Span;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import ratpack.core.handling.Context;
import ratpack.core.http.MutableHeaders;
import ratpack.exec.registry.Registry;

@ApplicationScoped
@Slf4j
public class TracerMiddleware implements AppMiddlewareItf {
  protected static final String REQUEST_ID_HEADER_KEY = "X-Request-Id";

  @Override
  public void handle(Context ctx) throws Exception {
    String uri = ctx.getRequest().getUri();

    for (String path : Config.APP_PUBLIC_PATHS) {
      if (uri.startsWith(path)) {
        ctx.next();

        return;
      }
    }
    
    String strRequestId = ctx.header(REQUEST_ID_HEADER_KEY).orElse(UUID.randomUUID().toString());

    MutableHeaders responseHeaders = ctx.getResponse().getHeaders();
    
    responseHeaders.set(REQUEST_ID_HEADER_KEY, strRequestId);

    Span span = Span.current();

    RequestTracer requestTracer = new RequestTracer(UUID.fromString(strRequestId), span);

    if (span.isRecording()) {
      String method = ctx.getRequest().getMethod().getName();
      String path = ctx.getRequest().getPath();
      Map<String, String> parameterPathMap = new HashMap<>();

      String parameterizedPath = UrlPathParser.parsePath(path, parameterPathMap);

      span.updateName("%s: %s".formatted(method, parameterizedPath));
      span.setAttribute("request.id", requestTracer.getId().toString());

      parameterPathMap.forEach((key, value) -> span.setAttribute("url.path.parameter." + key, value));
    }
    
    Registry registry = Registry.single(RequestTracer.class, requestTracer);

    ctx.next(registry);
  }

  private static class UrlPathParser {
    private static final Pattern UUID_PATTERN = Pattern.compile(
      "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    public static String parsePath(String input, Map<String, String> params) {
      List<String> parts = Arrays.stream(input.split("/"))
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toList());

      List<String> outputParts = new ArrayList<>();

      for (int i = 0; i < parts.size(); i++) {
        String part = parts.get(i);
        if (isUUID(part)) {
          if (i > 0) { // Ensure there is a preceding segment
            String resource = parts.get(i - 1);
            String paramName = resource + "_id";
            params.put(paramName, part);
            outputParts.add("{" + paramName + "}");
          }
        } else {
          outputParts.add(part);
        }
      }

      return "/" + String.join("/", outputParts);
    }

     private static boolean isUUID(String s) {
      return UUID_PATTERN.matcher(s).matches();
    }
  }

}