package io.github.maaf72.smartthings.infra.oas.annotation;

import java.util.stream.Stream;

import io.swagger.v3.core.util.AnnotationsUtils;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

public class AnnotationConverter {
  public static Operation fromAnnotation(io.swagger.v3.oas.annotations.Operation ann, Components components) {
    return new Operation()
      .operationId(emptyToNull(ann.operationId()))
      .description(emptyToNull(ann.description()))
      .summary(emptyToNull(ann.summary()))
      .tags(Stream.of(ann.tags())
        .filter(tag -> tag != null && !tag.isEmpty())
        .toList()
      )
      .parameters(Stream.of(ann.parameters())
        .filter(parameter -> parameter != null)
        .map(parameter -> fromAnnotation(parameter, components))
        .toList()
      )
      .requestBody(fromAnnotation(ann.requestBody(), components))
      .responses(Stream.of(ann.responses())
        .filter(response -> response != null)
        .collect(
          ApiResponses::new, 
          (responseModel, responseAnn) -> responseModel.addApiResponse(responseAnn.responseCode(), fromAnnotation(responseAnn, components)), 
          (_, _) -> {}
        )
      );
  }

  public static Parameter fromAnnotation(io.swagger.v3.oas.annotations.Parameter ann, Components components) {
    boolean isArraySchema = false;
    Class<?> schemaImplementation = ann.schema().implementation();
    if (schemaImplementation == Void.class) {
      schemaImplementation = ann.array().schema().implementation();
      if (schemaImplementation != Void.class) {
          isArraySchema = true;
      }
    }

    return new Parameter()
      .name(emptyToNull(ann.name()))
      .in(emptyToNull(ann.in().name().toLowerCase()))
      .description(emptyToNull(ann.description()))
      .required(ann.required())
      .allowEmptyValue(ann.allowEmptyValue())
      .example(emptyToNull(ann.example()))
      .schema(AnnotationsUtils.getSchema(
        ann.schema(), 
        ann.array(), 
        ann.schema().type() == "array" || isArraySchema, 
        schemaImplementation, 
        components, 
        null
      ).get());
  }

  public static RequestBody fromAnnotation(io.swagger.v3.oas.annotations.parameters.RequestBody ann, Components components) {
    if (ann == null || ann.content().length == 0) {
      return null;
    }

   return new RequestBody()
      .description(ann.description())
      .required(ann.required())
      .content(fromAnnotation(ann.content(), components));
  }

  public static ApiResponse fromAnnotation(io.swagger.v3.oas.annotations.responses.ApiResponse ann, Components components) {
    if (ann == null || ann.content().length == 0) {
      return null;
    }

    return new ApiResponse()
      .description(emptyToNull(ann.description()))
      .content(fromAnnotation(ann.content(), components));
  }

  public static Content fromAnnotation(io.swagger.v3.oas.annotations.media.Content[] ann, Components components) {
    return AnnotationsUtils.getContent(ann, null, null, null, components, null).get();
  }

  private static String emptyToNull(String s) {
    return (s == null || s.trim().isEmpty()) ? null : s;
  }
}
