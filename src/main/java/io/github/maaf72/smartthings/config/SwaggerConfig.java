package io.github.maaf72.smartthings.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexReader;

import io.github.maaf72.smartthings.annotation.ApiDoc;
import io.github.maaf72.smartthings.annotation.OperationAnnotationConverter;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SwaggerConfig {
  public static String toJsonString(OpenAPI openApi) {
    return Json.pretty(openApi);
  }

  public static OpenAPI openAPI() throws Exception {
    OpenAPI openAPI = new OpenAPI();

    Info info = new Info()
      .title("SmartThings API")
      .version("1.0.0")
      .description("SmartThings API");

    Paths paths = new Paths();
      
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

    Collection<AnnotationInstance> listAnnotationInstance = index.getAnnotations(ApiDoc.class);
    listAnnotationInstance.forEach(annotInstance -> {
      String className = annotInstance.target().asClass().name().toString();
      try {
        Class<?> clazz = Class.forName(className);
        ApiDoc apiDocAnn = clazz.getAnnotation(ApiDoc.class);
        Operation operationAnn = apiDocAnn.operation();

        io.swagger.v3.oas.models.Operation operation = OperationAnnotationConverter.fromAnnotation(operationAnn);

        PathItem pathItem = new PathItem();

        switch (operationAnn.method().toUpperCase()) {
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
              throw new IllegalArgumentException("Unsupported HTTP method: " + operationAnn.method());
        }

        paths.addPathItem(apiDocAnn.path(), pathItem);
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    });

    openAPI.setInfo(info);
    openAPI.setPaths(paths);

    // SwaggerConfiguration oasConfig = new SwaggerConfiguration().openAPI(openAPI).
    // OpenApiContext builder = new GenericOpenApiContextBuilder<>().openApiConfiguration(oasConfig).buildContext(true);

    // return builder.read();
    return openAPI;
  }
}