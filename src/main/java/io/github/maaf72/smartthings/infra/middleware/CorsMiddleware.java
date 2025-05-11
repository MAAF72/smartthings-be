package io.github.maaf72.smartthings.infra.middleware;

import io.github.maaf72.smartthings.itf.AppMiddlewareItf;
import jakarta.enterprise.context.ApplicationScoped;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;
import ratpack.core.http.MutableHeaders;
import ratpack.core.http.Status;

@ApplicationScoped
public class CorsMiddleware implements AppMiddlewareItf, Handler {
  @Override
  public void handle(Context ctx) throws Exception {
    MutableHeaders headers = ctx.getResponse().getHeaders();

    headers.set("Access-Control-Allow-Origin", "*");
    headers.set("Access-Control-Max-Age", "86400");
    headers.set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, PATCH");
    headers.set("Access-Control-Allow-Headers", "Content-Type, Authorization, X-App-Id, X-Client-Id, X-Client-Version");
    headers.set("Access-Control-Expose-Headers", "Content-Length");
    headers.set("Access-Control-Allow-Credentials", "true");

    if (ctx.getRequest().getMethod().isOptions()) {
      ctx.getResponse().status(Status.OK).send();

      return;
    }

    ctx.next();
  }
}
