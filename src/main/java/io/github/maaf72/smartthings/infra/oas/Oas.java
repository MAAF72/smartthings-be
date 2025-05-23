package io.github.maaf72.smartthings.infra.oas;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.jboss.jandex.Index;
import org.jboss.jandex.IndexReader;

import io.github.maaf72.smartthings.infra.oas.annotation.AnnotationConverter;
import io.github.maaf72.smartthings.infra.oas.annotation.ApiDoc;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;

public class Oas {
  private static final OpenAPI oas = buildOas();

  private static OpenAPI buildOas() {
    OpenAPI openAPI = new OpenAPI();

    Info info = new Info()
      .title("SmartThings API")
      .version("1.0.0")
      .description("SmartThings API");

    Paths paths = new Paths();

    Components components = new Components();
      
    // Load Jandex index
    Index index;
    try {
      InputStream is = Thread
        .currentThread()
        .getContextClassLoader()
        .getResourceAsStream("META-INF/jandex.idx");
        
      index = new IndexReader(is).read();
    } catch (IOException e) {
      throw new RuntimeException("Failed to read Jandex index", e);
    }

    Map<String, Map<String, Operation>> pathMethodOperation = new HashMap<>();
    
    index
      .getAnnotations(ApiDoc.class)
      .forEach(annotInstance -> {
        String className = annotInstance.target().asClass().name().toString();
        try {
          Class<?> clazz = Class.forName(className);
          ApiDoc apiDocAnn = clazz.getAnnotation(ApiDoc.class);
          io.swagger.v3.oas.annotations.Operation operationAnn = apiDocAnn.operation();

          Operation operation = AnnotationConverter.fromAnnotation(operationAnn, components);

          if (!pathMethodOperation.containsKey(apiDocAnn.path())) {
            pathMethodOperation.put(apiDocAnn.path(), new HashMap<>());
          }

          pathMethodOperation.get(apiDocAnn.path()).put(operationAnn.method().toUpperCase(), operation);
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
      });

    pathMethodOperation.forEach((path, methodOperation) -> {
      PathItem pathItem = new PathItem();

      methodOperation.forEach((method, operation) -> {
        switch (method) {
          case "GET":
                pathItem.get(operation);
                break;
            case "POST":
                pathItem.post(operation);
                break;
            case "PUT":
                pathItem.put(operation);
                break;
            case "DELETE":
                pathItem.delete(operation);
                break;
            case "PATCH":
                pathItem.patch(operation);
                break;
            case "HEAD":
                pathItem.head(operation);
                break;
            case "OPTIONS":
                pathItem.options(operation);
                break;
            case "TRACE":
                pathItem.trace(operation);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
      });

      paths.addPathItem(path, pathItem);
    });

    openAPI.setInfo(info);
    openAPI.setPaths(paths);
    openAPI.setComponents(components);


    return openAPI;
  }

  static String getOasAsJsonString() {
    return Json.pretty(oas);
  }

  static OpenAPI getOas() {
    return oas;
  }
}