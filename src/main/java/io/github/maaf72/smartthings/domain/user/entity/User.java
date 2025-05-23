package io.github.maaf72.smartthings.domain.user.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SoftDelete;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.github.maaf72.smartthings.domain.device.entity.Device;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "users")
@ToString(exclude = { "createdDevices", "registeredDevices" })
@DynamicUpdate
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@SoftDelete
public class User implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  @Column(unique = true)
  private String email;
  private String name;
  @JsonIgnore
  private String hash;
  private LocalDate dateOfBirth;
  private String address;
  private String country;
  @Enumerated(EnumType.STRING)
  private Role role;

  @OneToMany(mappedBy = "createdBy", fetch = FetchType.EAGER)
  @OrderBy("createdAt ASC")
  @JsonIgnoreProperties({ "created_by", "registered_by" })
  @SQLRestriction("deleted = false")
  private List<Device> createdDevices;

  @OneToMany(mappedBy = "registeredBy", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
  @OrderBy("registeredAt ASC")
  @JsonIgnoreProperties({ "created_by", "registered_by" })
  @SQLRestriction("deleted = false")
  private List<Device> registeredDevices;

  @Column(updatable = false)
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public enum Role {
    ST_USERS, ST_ADMINISTRATOR, DEVICE_VENDOR;
  }

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}