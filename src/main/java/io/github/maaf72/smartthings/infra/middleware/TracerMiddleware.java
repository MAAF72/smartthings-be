package io.github.maaf72.smartthings.infra.middleware;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import javax.net.ssl.SSLSession;

import com.google.common.net.HostAndPort;
import com.google.common.reflect.TypeToken;

import io.github.maaf72.smartthings.infra.tracing.RequestTracer;
import io.github.maaf72.smartthings.itf.AppMiddlewareItf;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.cookie.Cookie;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import ratpack.core.handling.Context;
import ratpack.core.http.Headers;
import ratpack.core.http.HttpMethod;
import ratpack.core.http.MediaType;
import ratpack.core.http.MutableHeaders;
import ratpack.core.http.Request;
import ratpack.core.http.TypedData;
import ratpack.exec.Promise;
import ratpack.exec.registry.NotInRegistryException;
import ratpack.exec.registry.Registry;
import ratpack.exec.stream.TransformablePublisher;
import ratpack.func.Block;
import ratpack.func.MultiValueMap;

@ApplicationScoped
@Slf4j
public class TracerMiddleware implements AppMiddlewareItf {
  protected static final String REQUEST_ID_HEADER_KEY = "X-Request-Id";

  @Override
  public void handle(Context ctx) throws Exception {
    String strRequestId = ctx.header(REQUEST_ID_HEADER_KEY).orElse(UUID.randomUUID().toString());

    MutableHeaders responseHeaders = ctx.getResponse().getHeaders();
    
    responseHeaders.set(REQUEST_ID_HEADER_KEY, strRequestId);

    Span span = Span.current();

    RequestTracer requestTracer = new RequestTracer(UUID.fromString(strRequestId), span);

    if (false) {
      ctx.getRequest().getBody().cache().then(body -> {
        if (span.isRecording()) {
          String method = ctx.getRequest().getMethod().getName();
          String path = ctx.getRequest().getPath();

          span.updateName("%s: %s".formatted(method, path));
          span.setAttribute("request.id", requestTracer.getId().toString());
          span.addEvent("http.request", Attributes.of(
            AttributeKey.stringKey("http.request.body"), body.getText()
          ));

          log.info(body.getText());
        }
        
        ctx.getRequest().add(body);

        Registry registry = Registry.single(RequestTracer.class, requestTracer);

        ctx.next(registry);
      });
    }

    // Get and cache the body promise
    Promise<TypedData> cachedBody = ctx.getRequest().getBody().cache();

    // Create a new request wrapper with the cached body
    Request wrappedRequest = new RequestWrapper(ctx.getRequest(), cachedBody);

    // Process the cached body
    cachedBody.then(body -> {
      if (span.isRecording()) {
          String method = wrappedRequest.getMethod().getName();
          String path = wrappedRequest.getPath();
          
          span.updateName("%s: %s".formatted(method, path));
          span.setAttribute("request.id", requestTracer.getId().toString());
          span.addEvent("http.request", Attributes.of(
              AttributeKey.stringKey("http.request.body"), body.getText()
          ));

          log.info("Request body: {}", body.getText());
      }

      
      // Create the registry with both the tracer and the original request
      Registry registry = Registry.builder()
        .add(RequestTracer.class, requestTracer)
        .add(Request.class, wrappedRequest)
        .build();

      // Continue with the wrapped request and custom registry
      ctx.next(registry);
    });
  }

  private static class RequestWrapper implements Request {
    private final Request delegate;
    private final Promise<TypedData> bodyPromise;

    RequestWrapper(Request delegate, Promise<TypedData> bodyPromise) {
        this.delegate = delegate;
        this.bodyPromise = bodyPromise;
    }

    @Override
    public Promise<TypedData> getBody() {
        return bodyPromise;
    }

    @Override
    public <T> void remove(TypeToken<T> type) throws NotInRegistryException {
      delegate.remove(type);
    }

    @Override
    public <O> Optional<O> maybeGet(TypeToken<O> type) {
      return delegate.maybeGet(type);
    }

    @Override
    public <O> Iterable<? extends O> getAll(TypeToken<O> type) {
      return delegate.getAll(type);
    }

    @Override
    public HttpMethod getMethod() {
      return delegate.getMethod();
    }

    @Override
    public String getProtocol() {
      return delegate.getProtocol();
    }

    @Override
    public String getRawUri() {
      return delegate.getRawUri();
    }

    @Override
    public String getUri() {
      return delegate.getUri();
    }

    @Override
    public String getQuery() {
      return delegate.getQuery();
    }

    @Override
    public String getPath() {
      return delegate.getPath();
    }

    @Override
    public MultiValueMap<String, String> getQueryParams() {
      return delegate.getQueryParams();
    }

    @Override
    public Set<Cookie> getCookies() {
      return delegate.getCookies();
    }

    @Override
    public String oneCookie(String name) {
      return delegate.oneCookie(name);
    }

    @Override
    public void setIdleTimeout(Duration idleTimeout) {
      delegate.setIdleTimeout(idleTimeout);
    }

    @Override
    public Promise<TypedData> getBody(Block onTooLarge) {
      return delegate.getBody(onTooLarge);
    }

    @Override
    public Promise<TypedData> getBody(long maxContentLength) {
      return delegate.getBody(maxContentLength);
    }

    @Override
    public Promise<TypedData> getBody(long maxContentLength, Block onTooLarge) {
      return delegate.getBody(maxContentLength, onTooLarge);
    }

    @Override
    public TransformablePublisher<? extends ByteBuf> getBodyStream() {
     return delegate.getBodyStream();
    }

    @Override
    public TransformablePublisher<? extends ByteBuf> getBodyStream(long maxContentLength) {
      return delegate.getBodyStream(maxContentLength);
    }

    @Override
    public Headers getHeaders() {
      return delegate.getHeaders();
    }

    @Override
    public MediaType getContentType() {
      return delegate.getContentType();
    }

    @Override
    public HostAndPort getRemoteAddress() {
      return delegate.getRemoteAddress();
    }

    @Override
    public HostAndPort getLocalAddress() {
      return delegate.getLocalAddress();
    }

    @Override
    public boolean isAjaxRequest() {
      return delegate.isAjaxRequest();
    }

    @Override
    public boolean isExpectsContinue() {
      return delegate.isExpectsContinue();
    }

    @Override
    public boolean isChunkedTransfer() {
      return delegate.isChunkedTransfer();
    }

    @Override
    public long getContentLength() {
      return delegate.getContentLength();
    }

    @Override
    public Instant getTimestamp() {
      return delegate.getTimestamp();
    }

    @Override
    public void setMaxContentLength(long maxContentLength) {
      delegate.setMaxContentLength(maxContentLength);
    }

    @Override
    public long getMaxContentLength() {
      return delegate.getMaxContentLength();
    }

    @Override
    public Optional<SSLSession> getSslSession() {
      return delegate.getSslSession();
    }

    @Override
    public <O> Request add(Class<O> type, O object) {
     return delegate.add(type, object);
    }

    @Override
    public <O> Request add(TypeToken<O> type, O object) {
      return delegate.add(type, object);
    }

    @Override
    public Request add(Object object) {
      return delegate.add(object);
    }

    @Override
    public <O> Request addLazy(Class<O> type, Supplier<? extends O> supplier) {
      return delegate.addLazy(type, supplier);
    }

    @Override
    public <O> Request addLazy(TypeToken<O> type, Supplier<? extends O> supplier) {
     return delegate.addLazy(type, supplier);
    }
  }
}
