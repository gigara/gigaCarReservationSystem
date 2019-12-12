# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table car (
  plate_number                  varchar(255) not null,
  make                          varchar(255),
  model                         varchar(255),
  no_of_seats                   integer not null,
  with_ac                       tinyint(1) default 0 not null,
  transmission                  varchar(6),
  constraint ck_car_transmission check ( transmission in ('Auto','Manual')),
  constraint pk_car primary key (plate_number)
);

create table motorbike (
  plate_number                  varchar(255) not null,
  make                          varchar(255),
  model                         varchar(255),
  is_with_abs                   tinyint(1) default 0 not null,
  no_of_gears                   integer not null,
  constraint pk_motorbike primary key (plate_number)
);

create table schedule (
  reservation_id                integer auto_increment not null,
  pick_up_date                  date,
  drop_off_date                 date,
  motorbike_plate_number        varchar(255),
  car_plate_number              varchar(255),
  constraint pk_schedule primary key (reservation_id)
);

create table task (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  done                          tinyint(1) default 0 not null,
  due_date                      datetime(6),
  constraint pk_task primary key (id)
);

create index ix_schedule_motorbike_plate_number on schedule (motorbike_plate_number);
alter table schedule add constraint fk_schedule_motorbike_plate_number foreign key (motorbike_plate_number) references motorbike (plate_number) on delete restrict on update restrict;

create index ix_schedule_car_plate_number on schedule (car_plate_number);
alter table schedule add constraint fk_schedule_car_plate_number foreign key (car_plate_number) references car (plate_number) on delete restrict on update restrict;


# --- !Downs

alter table schedule drop foreign key fk_schedule_motorbike_plate_number;
drop index ix_schedule_motorbike_plate_number on schedule;

alter table schedule drop foreign key fk_schedule_car_plate_number;
drop index ix_schedule_car_plate_number on schedule;

drop table if exists car;

drop table if exists motorbike;

drop table if exists schedule;

drop table if exists task;

