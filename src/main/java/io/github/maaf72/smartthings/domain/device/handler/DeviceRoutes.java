package io.github.maaf72.smartthings.domain.device.handler;

import io.github.maaf72.smartthings.domain.user.entity.User.Role;
import io.github.maaf72.smartthings.infra.middleware.RbacMiddleware;
import io.github.maaf72.smartthings.itf.AppRoutesItf;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import ratpack.core.handling.Chain;


@ApplicationScoped
@Slf4j
public class DeviceRoutes implements AppRoutesItf {

  @Inject
  ListAvailableVendorDeviceHandler listAvailableVendorDeviceHandler;

  @Inject
  ListVendorDeviceHandler listVendorDeviceHandler;

  @Inject
  CreateVendorDeviceHandler createVendorDeviceHandler;

  @Inject
  UpdateVendorDeviceHandler updateVendorDeviceHandler;

  @Inject
  DeleteVendorDeviceHandler deleteVendorDeviceHandler;

  @Inject
  ListUserDeviceHandler listUserDeviceHandler;

  @Inject
  RegisterUserDeviceHandler registerUserDeviceHandler;

  @Inject
  UnregisterUserDeviceHandler unregisterUserDeviceHandler;

  @Inject
  CommandUserDeviceHandler commandUserDeviceHandler;

  @Override
  public void Routes(Chain chain) {
    try {
      chain
        .prefix("devices", x -> x
          .get("", listAvailableVendorDeviceHandler)
        )
        .prefix("vendor", x -> x
          .all(new RbacMiddleware(Role.DEVICE_VENDOR))
          .path("devices", ctx -> ctx
            .byMethod(x2 -> x2
              .get(listVendorDeviceHandler)
              .post(createVendorDeviceHandler)
            )
          )
          .path("devices/:id", ctx -> ctx
            .byMethod(x2 -> x2
              .put(updateVendorDeviceHandler)
              .delete(deleteVendorDeviceHandler)
            )
          )
        )
        .prefix("users", x -> x
          .all(new RbacMiddleware(Role.ST_USERS))
          .get("me/devices", listUserDeviceHandler)
          .post("me/devices/:id/register", registerUserDeviceHandler)
          .post("me/devices/:id/unregister", unregisterUserDeviceHandler)
          .post("me/devices/:id/commands", commandUserDeviceHandler)
        );
    } catch (Exception e) {
      log.error("Error registering routes", e);
    }
  }
}
