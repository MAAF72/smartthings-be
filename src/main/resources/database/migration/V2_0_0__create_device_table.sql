create table Device (
  id uuid not null,
  brandName varchar(255),
  deviceDescription varchar(255),
  deviceName varchar(255),
  value integer,
  primary key (id)
)