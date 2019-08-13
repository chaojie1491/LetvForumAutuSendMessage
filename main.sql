/*
 navicat premium data transfer

 source server         : letv
 source server type    : sqlite
 source server version : 3021000
 source schema         : main

 target server type    : sqlite
 target server version : 3021000
 file encoding         : 65001

 date: 04/08/2019 14:11:41
*/

pragma foreign_keys = false;

-- ----------------------------
-- table structure for letv_config
-- ----------------------------
drop table if exists "letv_config";
create table "letv_config" (
  "config_id" integer(11) not null,
  "config_message_text" text(50) not null,
  primary key ("config_id")
);

-- ----------------------------
-- table structure for letv_cookie
-- ----------------------------
drop table if exists "letv_cookie";
create table "letv_cookie" (
  "cookie_id" integer(11) not null,
  "cookie_key" text(20),
  "cookie_value" text(20),
  primary key ("cookie_id")
);

-- ----------------------------
-- table structure for letv_link
-- ----------------------------
drop table if exists "letv_link";
create table "letv_link" (
  "letv_link_id" integer(11) not null,
  "letv_link_name" text(40) not null,
  "letv_link" text(50) not null,
  "letv_last_time" text,
  primary key ("letv_link_id")
);

-- ----------------------------
-- table structure for letv_user
-- ----------------------------
drop table if exists "letv_user";
create table "letv_user" (
  "letv_user_id" integer(11) not null,
  "letv_user_uid" text(11),
  "letv_user_link" text(40),
  primary key ("letv_user_id")
);

-- ----------------------------
-- table structure for sqlite_sequence
-- ----------------------------
drop table if exists "sqlite_sequence";
create table "sqlite_sequence" (
  "name",
  "seq"
);

pragma foreign_keys = true;
