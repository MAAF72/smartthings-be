package io.github.maaf72.smartthings.infra.oas.annotation;

import java.util.stream.Stream;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

public class AnnotationConverter {
  public static Operation fromAnnotation(io.swagger.v3.oas.annotations.Operation ann, Components components) {
    return new Operation()
      .operationId(emptyToNull(ann.summary()))
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
          (a, b) -> {}
        )
      );
  }

  public static Parameter fromAnnotation(io.swagger.v3.oas.annotations.Parameter ann, Components components) {
    return new Parameter()
      .name(emptyToNull(ann.name()))
      .in(emptyToNull(ann.in().name().toLowerCase()))
      .description(emptyToNull(ann.description()))
      .required(ann.required())
      .allowEmptyValue(ann.allowEmptyValue())
      .example(emptyToNull(ann.example()))
      .schema(fromAnnotation(ann.schema(), components));
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
    return Stream.of(ann).
      collect(
        Content::new,
        (contentModel, contentAnn) -> contentModel.addMediaType(contentAnn.mediaType(), new MediaType().schema(fromAnnotation(contentAnn.schema(), components))),
        (a, b) -> {}
      );
  }

  public static Schema<?> fromAnnotation(io.swagger.v3.oas.annotations.media.Schema ann, Components components) {
    Class<?> clImpl = ann.implementation();
    Class<?>[] clAllOf = ann.allOf();
    Class<?>[] clAnyOf = ann.anyOf();
    Class<?>[] clOneOf = ann.oneOf();

    if (clImpl != null && !clImpl.equals(Void.class)) {
      return resolveSchema(clImpl, components);
    }

    if (clAllOf != null && clAllOf.length > 0) {
      ComposedSchema composedSchema = new ComposedSchema();
      for (Class<?> c : clAllOf) {
        composedSchema.addAllOfItem(resolveSchema(c, components));
      }

      return composedSchema;
    }

    if (clAnyOf != null && clAnyOf.length > 0) {
      ComposedSchema composedSchema = new ComposedSchema();
      for (Class<?> c : clAnyOf) {
        composedSchema.addAnyOfItem(resolveSchema(c, components));
      }

      return composedSchema;
    }

    if (clOneOf != null && clOneOf.length > 0) {
      ComposedSchema composedSchema = new ComposedSchema();
      for (Class<?> c : clOneOf) {
        composedSchema.addOneOfItem(resolveSchema(c, components));
      }

      return composedSchema;
    }

    switch (ann.type()) {
      case "string": return new StringSchema();
      case "integer": return new IntegerSchema();
      case "boolean": return new BooleanSchema();
      case "number": return new NumberSchema();
      case "array": return new ArraySchema();
      case "object": return new Schema<>().type("object");
    }

    return null;
  }

  private static Schema<?> resolveSchema(Class<?> clazz, Components components) {
    // Resolve the schema and its dependencies
    ResolvedSchema resolvedSchema = ModelConverters.getInstance().resolveAsResolvedSchema(new AnnotatedType(clazz));
    
    // Register referenced schemas to components
    if (components != null && resolvedSchema.referencedSchemas != null) {
        resolvedSchema.referencedSchemas.forEach(components::addSchemas);
    }
    
    return resolvedSchema.schema;
  }

  private static String emptyToNull(String s) {
    return (s == null || s.trim().isEmpty()) ? null : s;
  }
}
