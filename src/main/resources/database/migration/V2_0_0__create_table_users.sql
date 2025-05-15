CREATE TABLE "users" (
  "date_of_birth" DATE,
  "deleted" BOOLEAN NOT NULL,
  "created_at" TIMESTAMP(6),
  "updated_at" TIMESTAMP(6),
  "id" UUID NOT NULL,
  "address" VARCHAR(255),
  "country" VARCHAR(255),
  "email" VARCHAR(255) UNIQUE,
  "hash" VARCHAR(255),
  "name" VARCHAR(255),
  "role" VARCHAR(255) CHECK (
    "role" IN ('ST_USERS', 'ST_ADMINISTRATOR', 'DEVICE_VENDOR')
  ),
  PRIMARY KEY ("id")
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_role ON users (role);