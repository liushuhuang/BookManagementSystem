drop table if exists fine_record;
drop table if exists borrow_record;
drop table if exists book_copy;
drop table if exists book_info;
drop table if exists book_category;
drop table if exists sys_user_role;
drop table if exists sys_role;
drop table if exists sys_user;
drop table if exists system_config;

create table sys_user (
  id bigint auto_increment primary key,
  username varchar(50) not null unique,
  password varchar(100) not null,
  real_name varchar(50) not null,
  phone varchar(20),
  email varchar(100),
  avatar varchar(255),
  reader_no varchar(50) unique,
  max_borrow_count int not null default 5,
  status tinyint not null default 1,
  last_login_time timestamp,
  create_time timestamp default current_timestamp,
  update_time timestamp default current_timestamp,
  is_deleted tinyint not null default 0
);

create table sys_role (
  id bigint auto_increment primary key,
  role_name varchar(50) not null,
  role_code varchar(50) not null unique,
  description varchar(255),
  status tinyint not null default 1,
  create_time timestamp default current_timestamp,
  update_time timestamp default current_timestamp,
  is_deleted tinyint not null default 0
);

create table sys_user_role (
  id bigint auto_increment primary key,
  user_id bigint not null,
  role_id bigint not null,
  create_time timestamp default current_timestamp,
  unique(user_id, role_id)
);

create table book_category (
  id bigint auto_increment primary key,
  category_name varchar(100) not null,
  category_code varchar(50) not null unique,
  parent_id bigint not null default 0,
  sort_order int not null default 0,
  description varchar(255),
  status tinyint not null default 1,
  create_time timestamp default current_timestamp,
  update_time timestamp default current_timestamp,
  is_deleted tinyint not null default 0
);

create table book_info (
  id bigint auto_increment primary key,
  isbn varchar(20) not null unique,
  book_name varchar(255) not null,
  author varchar(100) not null,
  publisher varchar(100) not null,
  publish_date date,
  category_id bigint not null,
  cover_url varchar(255),
  description clob,
  price decimal(10,2),
  pages int,
  status tinyint not null default 1,
  create_time timestamp default current_timestamp,
  update_time timestamp default current_timestamp,
  is_deleted tinyint not null default 0
);

create table book_copy (
  id bigint auto_increment primary key,
  book_id bigint not null,
  copy_code varchar(50) not null unique,
  location varchar(100),
  status tinyint not null default 1,
  in_time timestamp default current_timestamp,
  remark varchar(255),
  create_time timestamp default current_timestamp,
  update_time timestamp default current_timestamp,
  is_deleted tinyint not null default 0
);

create table borrow_record (
  id bigint auto_increment primary key,
  borrow_no varchar(50) not null unique,
  user_id bigint not null,
  book_id bigint not null,
  copy_id bigint not null,
  borrow_time timestamp default current_timestamp,
  due_time timestamp not null,
  return_time timestamp,
  status tinyint not null default 0,
  operator_id bigint,
  remark varchar(255),
  create_time timestamp default current_timestamp,
  update_time timestamp default current_timestamp
);

create table fine_record (
  id bigint auto_increment primary key,
  borrow_id bigint not null,
  user_id bigint not null,
  fine_type tinyint not null default 1,
  fine_amount decimal(10,2) not null default 0.00,
  fine_days int not null default 0,
  status tinyint not null default 0,
  paid_time timestamp,
  operator_id bigint,
  remark varchar(255),
  create_time timestamp default current_timestamp,
  update_time timestamp default current_timestamp,
  unique(borrow_id, fine_type)
);

create table system_config (
  id bigint auto_increment primary key,
  config_key varchar(100) not null unique,
  config_value varchar(255) not null,
  config_name varchar(100) not null,
  description varchar(255),
  status tinyint not null default 1,
  create_time timestamp default current_timestamp,
  update_time timestamp default current_timestamp
);
