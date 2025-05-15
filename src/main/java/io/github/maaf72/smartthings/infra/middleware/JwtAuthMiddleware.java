package io.github.maaf72.smartthings.infra.middleware;

import java.util.Date;
import java.util.Optional;

import io.github.maaf72.smartthings.config.Config;
import io.github.maaf72.smartthings.infra.mapper.CustomObjectMapper;
import io.github.maaf72.smartthings.infra.security.JwtUtil;
import io.github.maaf72.smartthings.infra.security.UserClaims;
import io.github.maaf72.smartthings.itf.AppMiddlewareItf;
import io.jsonwebtoken.Claims;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import ratpack.core.handling.Context;
import ratpack.exec.registry.Registry;

@ApplicationScoped
@Slf4j
public class JwtAuthMiddleware implements AppMiddlewareItf {
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
      String authHeaderStr = authHeader.orElseThrow(() -> new Exception("missing authorization header"));
      
      if (!authHeaderStr.startsWith(AUTH_HEADER_PREFIX)) {
        throw new Exception("invalid authorization header");
      }

      String authToken = authHeaderStr.substring(AUTH_HEADER_PREFIX.length());

      Claims claims = JwtUtil.parseJWTToken(authToken);

      if (claims == null || claims.getExpiration().before(new Date())) {
        throw new Exception("invalid authorization token");
      }
      
      UserClaims userClaims = CustomObjectMapper.getObjectMapper().convertValue(claims, UserClaims.class);

      Registry userRegistry = Registry.single(UserClaims.class, userClaims);
      
      ctx.next(userRegistry);
    } catch (Exception e) {
      ctx.getResponse().status(401).send("Unauthorized");
      log.error("error: " + e.getMessage());

      return;
    }
  }
}
