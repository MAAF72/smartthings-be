package io.github.maaf72.smartthings.domain.common.dto;

public class PaginationRequest {
  private static final int DEFAULT_PAGE = 1;
  private static final int DEFAULT_SIZE = 10;

  public final int page;
  public final int size;

  public PaginationRequest() {
    this.page = DEFAULT_PAGE; // default page
    this.size = DEFAULT_SIZE; // default size
  }

  public PaginationRequest(Integer page, Integer size) {
    this.page = (page == null) ? DEFAULT_PAGE : Math.max(1, page);
    this.size = (size == null) ? DEFAULT_SIZE : Math.max(1, size);
  }
  
  public int offset() {
    return (page - 1) * size;
  }

  public static PaginationRequest of(String pageParam, String sizeParam) {
    Integer page = tryParse(pageParam);
    Integer size = tryParse(sizeParam);

    return new PaginationRequest(page, size);
  }

  private static Integer tryParse(String s) {
    try {
      return Integer.parseInt(s);
    } catch (Exception e) {
      return null;
    }
  }
}
