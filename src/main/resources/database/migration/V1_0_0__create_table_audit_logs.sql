create table "audit_logs" (
  "created_at" timestamp(6),
  "id" uuid not null,
  "action" varchar(255),
  "module" varchar(255),
  "object" varchar(255),
  "subject" varchar(255),
  "metadata" jsonb,
  primary key ("id")
);