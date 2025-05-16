package io.github.maaf72.smartthings.annotation;

import java.util.Arrays;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OperationAnnotationConverter {

  public static Operation fromAnnotation(io.swagger.v3.oas.annotations.Operation operationAnn) {
    if (operationAnn == null) return null;
  
    Operation operationModel = new Operation();

    // Basic metadata
    operationModel.setSummary(emptyToNull(operationAnn.summary()));
    operationModel.setDescription(emptyToNull(operationAnn.description()));
    operationModel.setOperationId(emptyToNull(operationAnn.operationId()));
    operationModel.setParameters(null);

    // Tags
    Arrays.stream(operationAnn.tags())
      .filter(tag -> !tag.isEmpty())
      .forEach(operationModel::addTagsItem);


    // Parameters
    Arrays.stream(operationAnn.parameters())
      .filter(parameter -> parameter != null)
      .map(x -> new Parameter()
        .name(emptyToNull(x.name()))
        .in(emptyToNull(x.in().name().toLowerCase()))
        .description(emptyToNull(x.description()))
        .required(x.required())
        .allowEmptyValue(x.allowEmptyValue())
        .example(emptyToNull(x.example()))
        .schema(resolveSchema(x.schema()))
      )
      .forEach(operationModel::addParametersItem);

    // Request Body
    io.swagger.v3.oas.annotations.parameters.RequestBody requestBodyAnn = operationAnn.requestBody();

    // AnnotationsUtils.getContent(requestBodyAnn.content(), new String[0], new String[0], null, null, null).ifPresent(null);;
    
    if (requestBodyAnn != null && requestBodyAnn.content().length > 0) {
      RequestBody requestBody = new RequestBody()
          .description(requestBodyAnn.description())
          .required(requestBodyAnn.required());

      Content content = new Content();
      for (io.swagger.v3.oas.annotations.media.Content c : requestBodyAnn.content()) {
        Schema<?> schema = resolveSchema(c.schema());
        MediaType mediaType = new MediaType().schema(schema);
        content.addMediaType(c.mediaType(), mediaType);
      }
      requestBody.setContent(content);

      operationModel.setRequestBody(requestBody);
    }

    // Responses
    ApiResponses responses = new ApiResponses();
    for (io.swagger.v3.oas.annotations.responses.ApiResponse apiResponseAnn : operationAnn.responses()) {
      ApiResponse responseModel = new ApiResponse();
      responseModel.setDescription(emptyToNull(apiResponseAnn.description()));

      Content respContent = new Content();
      for (io.swagger.v3.oas.annotations.media.Content c : apiResponseAnn.content()) {
        Schema<?> schema = resolveSchema(c.schema());
        MediaType mediaType = new MediaType().schema(schema);
        respContent.addMediaType(c.mediaType(), mediaType);
      }

      responseModel.setContent(respContent);
      responses.addApiResponse(apiResponseAnn.responseCode(), responseModel);
    }

    if (!responses.isEmpty()) {
      operationModel.setResponses(responses);
    }

    return operationModel;
  }

  private static Schema<?> resolveSchema(io.swagger.v3.oas.annotations.media.Schema schemaAnn) {
    Class<?> clImpl = schemaAnn.implementation();
    Class<?>[] clAllOf = schemaAnn.allOf();

    if (clImpl != null && !clImpl.equals(Void.class)) {
      return ModelConverters.getInstance().resolveAsResolvedSchema(new AnnotatedType(clImpl)).schema;
    }

    if (clAllOf != null && clAllOf.length > 0) {
      ComposedSchema composedSchema = new ComposedSchema();
      for (Class<?> c : clAllOf) {
        composedSchema.addAllOfItem(ModelConverters.getInstance().resolveAsResolvedSchema(new AnnotatedType(c)).schema);
      }

      return composedSchema;
    }

    switch (schemaAnn.type()) {
        case "string": return new StringSchema();
        case "integer": return new IntegerSchema();
        case "boolean": return new BooleanSchema();
        case "number": return new NumberSchema();
        case "array": return new ArraySchema();
        case "object": return new Schema<>().type("object");
    }

    return null;
  }

  private static String emptyToNull(String s) {
    return (s == null || s.trim().isEmpty()) ? null : s;
  }
}
