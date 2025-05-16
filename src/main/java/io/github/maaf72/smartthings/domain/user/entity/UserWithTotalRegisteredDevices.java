package io.github.maaf72.smartthings.domain.user.entity;

import java.io.Serializable;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserWithTotalRegisteredDevices implements Serializable {
    private final User user;
    private final Long totalRegisteredDevices;
}
