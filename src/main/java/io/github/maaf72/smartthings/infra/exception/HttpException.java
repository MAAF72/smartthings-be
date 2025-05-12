package io.github.maaf72.smartthings.infra.exception;

public class HttpException extends RuntimeException {
  private final int statusCode;

  public HttpException(int statusCode, String message)  {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
