package io.github.maaf72.smartthings.infra.middleware;

import java.util.Date;
import java.util.Optional;

import io.github.maaf72.smartthings.config.Config;
import io.github.maaf72.smartthings.infra.security.JwtUtil;
import io.github.maaf72.smartthings.itf.AppMiddlewareItf;
import io.jsonwebtoken.Claims;
import jakarta.enterprise.context.ApplicationScoped;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;
import ratpack.core.http.Status;

@ApplicationScoped
public class JwtAuthMiddleware implements AppMiddlewareItf, Handler {
  protected static final String AUTH_HEADER_KEY = "Authorization";
  protected static final String AUTH_HEADER_PREFIX = "Bearer ";

  @Override
  public void handle(Context ctx) throws Exception {
    String uri = ctx.getRequest().getUri();

    for (String path : Config.APP_PUBLIC_PATHS) {
      if (uri.startsWith(path)) {
        ctx.next();

        return;
      }
    }

    Optional<String> authHeader = ctx.header(AUTH_HEADER_KEY);

    try {
      String authHeaderStr = authHeader.get();

      if (!authHeaderStr.startsWith(AUTH_HEADER_PREFIX)) {
        throw new Exception("Invalid authorization header");
      }

      String authToken = authHeaderStr.substring(AUTH_HEADER_PREFIX.length());

      Claims claims = JwtUtil.parseJWTToken(authToken);

      if (claims == null || claims.getExpiration().before(new Date())) {
        throw new Exception("Invalid authorization token");  
      }
    } catch (Exception e) {
        ctx.getResponse().status(Status.UNAUTHORIZED).send(Status.UNAUTHORIZED.getMessage());

        return;
    }

    ctx.next();
  }
}
