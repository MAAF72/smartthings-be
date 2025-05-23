package io.github.maaf72.smartthings.infra.oas;

import io.github.maaf72.smartthings.itf.AppRoutesItf;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import ratpack.core.handling.Chain;

@ApplicationScoped
@Slf4j
public class OasRoutes implements AppRoutesItf {
  @Override
  public void Routes(Chain chain) {
    try {
      chain.files(f -> f.files("swagger-ui").indexFiles("index.html"));
      chain.get("swagger-ui/swagger.json", ctx -> {
        ctx.getResponse().contentType("application/json").send(Oas.getOasAsJsonString());
      });
    } catch (Exception e) {
      log.error("Error registering routes", e);
    }
  }
}
