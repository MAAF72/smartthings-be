package io.github.maaf72.smartthings.domain.user.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserWithTotalRegisteredDevices implements Serializable {
    private final User user;
    private final Long totalRegisteredDevices;
}
