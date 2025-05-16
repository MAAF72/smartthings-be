package io.github.maaf72.smartthings.domain.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BaseResponse<T> {
  public final boolean success;
  public final String message;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public final T data;

  public static <T> BaseResponse<T> of(boolean success, String message, T data) {
    return new BaseResponse<>(success, message, data);
  }

  public static <T> BaseResponse<T> of(boolean success, String message) {
    return new BaseResponse<>(success, message, null);
  }
}
