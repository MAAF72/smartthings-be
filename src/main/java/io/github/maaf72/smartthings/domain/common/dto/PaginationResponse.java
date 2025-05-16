package io.github.maaf72.smartthings.domain.common.dto;

import java.io.Serializable;
import java.util.List;

public class PaginationResponse<T> extends BaseResponse<List<T>> {
  public final Metadata<T> metadata;
  
  public PaginationResponse(boolean success, String message, List<T> items, int page, int size, long totalItems) {
    super(success, message, items); 
    this.metadata = new Metadata<>(page, size, totalItems);
  }
  
  public static class Metadata<T> implements Serializable {
    public final int page;
    public final int size;
    public final long totalItems;
    public final int totalPages;

    public Metadata(int page, int size, long totalItems) {
      this.page = page;
      this.size = size;
      this.totalItems = totalItems;
      this.totalPages = (int) Math.ceil((double) totalItems / size);
    }
  }

  public static <T> PaginationResponse<T> of(boolean success, String message, List<T> items, long totalItems, PaginationRequest request) {
    return new PaginationResponse<>(success, message, items, request.page, request.size, totalItems);
  }
}
