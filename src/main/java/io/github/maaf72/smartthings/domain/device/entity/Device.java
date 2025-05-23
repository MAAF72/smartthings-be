package io.github.maaf72.smartthings.domain.device.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.github.maaf72.smartthings.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "devices")
@ToString(exclude = { "createdBy", "registeredBy" })
@DynamicUpdate
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@SoftDelete
public class Device implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private String brandName;
  private String deviceName;
  private String deviceDescription;
  @JdbcTypeCode(SqlTypes.JSON)
  private DeviceConfiguration deviceConfiguration;
  private Integer value;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "created_by_id")
  @JsonIgnoreProperties({ "created_devices", "registered_devices" })
  private User createdBy;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "registered_by_id")
  @JsonIgnoreProperties({ "created_devices", "registered_devices" })
  private User registeredBy;

  @Column(updatable = false)
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime registeredAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  @Data
  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  public static class DeviceConfiguration implements Serializable {
    private Integer minValue;
    private Integer maxValue;
    private Integer defaultValue;
  }
}
