CREATE TABLE "devices" (
  "deleted" BOOLEAN NOT NULL,
  "value" INTEGER,
  "created_at" TIMESTAMP(6),
  "registered_at" TIMESTAMP(6),
  "updated_at" TIMESTAMP(6),
  "created_by_id" UUID,
  "id" UUID NOT NULL,
  "registered_by_id" UUID,
  "brand_name" VARCHAR(255),
  "device_description" VARCHAR(255),
  "device_name" VARCHAR(255),
  "device_configuration" JSONB,
  PRIMARY KEY ("id"),
  FOREIGN KEY ("created_by_id") REFERENCES users("id"),
  FOREIGN KEY ("registered_by_id") REFERENCES users("id")
);

CREATE INDEX idx_devices_created_by_id ON devices (created_by_id);
CREATE INDEX idx_devices_registered_by_id ON devices (registered_by_id);