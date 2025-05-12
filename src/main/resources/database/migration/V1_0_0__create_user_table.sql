create table "User" (
  id uuid not null,
  address varchar(255),
  country varchar(255),
  dateOfBirth timestamp(6),
  email varchar(255),
  hash varchar(255),
  name varchar(255),
  primary key (id)
)