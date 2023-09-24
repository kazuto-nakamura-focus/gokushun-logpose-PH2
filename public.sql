/*
 Navicat PostgreSQL Data Transfer

 Source Server         : loghose
 Source Server Type    : PostgreSQL
 Source Server Version : 150002 (150002)
 Source Host           : localhost:5432
 Source Catalog        : public
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 150002 (150002)
 File Encoding         : 65001

 Date: 20/09/2023 09:29:40
*/

-- ----------------------------
-- Sequence structure for ph2_device_day_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ph2_device_day_seq";
CREATE SEQUENCE "public"."ph2_device_day_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ph2_devices_id2_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ph2_devices_id2_seq";
CREATE SEQUENCE "public"."ph2_devices_id2_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1200
CACHE 1;

-- ----------------------------
-- Sequence structure for ph2_devices_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ph2_devices_id_seq";
CREATE SEQUENCE "public"."ph2_devices_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ph2_fields_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ph2_fields_id_seq";
CREATE SEQUENCE "public"."ph2_fields_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 14
CACHE 1;

-- ----------------------------
-- Sequence structure for ph2_fields_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ph2_fields_seq";
CREATE SEQUENCE "public"."ph2_fields_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ph2_paramset_catalog_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ph2_paramset_catalog_seq";
CREATE SEQUENCE "public"."ph2_paramset_catalog_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 10
CACHE 1;

-- ----------------------------
-- Sequence structure for ph2_real_growth_f_stage_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ph2_real_growth_f_stage_seq";
CREATE SEQUENCE "public"."ph2_real_growth_f_stage_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ph2_rel_base_data_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ph2_rel_base_data_seq";
CREATE SEQUENCE "public"."ph2_rel_base_data_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ph2_sensor_contents_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ph2_sensor_contents_id_seq";
CREATE SEQUENCE "public"."ph2_sensor_contents_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ph2_sensor_sizes_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ph2_sensor_sizes_id_seq";
CREATE SEQUENCE "public"."ph2_sensor_sizes_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ph2_sensors_id2_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ph2_sensors_id2_seq";
CREATE SEQUENCE "public"."ph2_sensors_id2_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 400
CACHE 1;

-- ----------------------------
-- Sequence structure for ph2_sensors_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ph2_sensors_id_seq";
CREATE SEQUENCE "public"."ph2_sensors_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sensor_contents_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sensor_contents_id_seq";
CREATE SEQUENCE "public"."sensor_contents_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sensor_models_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sensor_models_id_seq";
CREATE SEQUENCE "public"."sensor_models_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sensor_sizes_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sensor_sizes_id_seq";
CREATE SEQUENCE "public"."sensor_sizes_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sensors_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sensors_id_seq";
CREATE SEQUENCE "public"."sensors_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for users_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."users_id_seq";
CREATE SEQUENCE "public"."users_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Table structure for ph2_base_data
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_base_data";
CREATE TABLE "public"."ph2_base_data" (
  "relation_id" int8 NOT NULL,
  "sensor_id" int8 NOT NULL,
  "temperature" float4 NOT NULL
)
;
COMMENT ON COLUMN "public"."ph2_base_data"."relation_id" IS '関係テーブルID';
COMMENT ON COLUMN "public"."ph2_base_data"."sensor_id" IS 'センサーID';
COMMENT ON COLUMN "public"."ph2_base_data"."temperature" IS '気温';

-- ----------------------------
-- Table structure for ph2_brand_master
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_brand_master";
CREATE TABLE "public"."ph2_brand_master" (
  "id" int4 NOT NULL,
  "name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."ph2_brand_master"."id" IS 'ブランド番号';
COMMENT ON COLUMN "public"."ph2_brand_master"."name" IS 'ブランド名';
COMMENT ON COLUMN "public"."ph2_brand_master"."created_at" IS '作成日時';
COMMENT ON COLUMN "public"."ph2_brand_master"."updated_at" IS '更新日時';

-- ----------------------------
-- Table structure for ph2_daily_base_data
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_daily_base_data";
CREATE TABLE "public"."ph2_daily_base_data" (
  "day_id" int8 NOT NULL,
  "tm" float4,
  "average" float4,
  "cdd" float8,
  "par" float8
)
;
COMMENT ON COLUMN "public"."ph2_daily_base_data"."day_id" IS '日付ID';
COMMENT ON COLUMN "public"."ph2_daily_base_data"."tm" IS 'Tm値';
COMMENT ON COLUMN "public"."ph2_daily_base_data"."average" IS '平均気温';
COMMENT ON COLUMN "public"."ph2_daily_base_data"."cdd" IS 'CDD値';
COMMENT ON COLUMN "public"."ph2_daily_base_data"."par" IS 'PAR値';

-- ----------------------------
-- Table structure for ph2_dashboard
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_dashboard";
CREATE TABLE "public"."ph2_dashboard" (
  "device_id" int8 NOT NULL,
  "casted_at" timestamp(6) NOT NULL,
  "content_id" int8 NOT NULL,
  "value" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "sensor_id" int8 NOT NULL
)
;

-- ----------------------------
-- Table structure for ph2_dashboards_rel
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_dashboards_rel";
CREATE TABLE "public"."ph2_dashboards_rel" (
  "user_id" int8 NOT NULL,
  "field_id" int8 NOT NULL
)
;
COMMENT ON COLUMN "public"."ph2_dashboards_rel"."user_id" IS 'ユーザーID';
COMMENT ON COLUMN "public"."ph2_dashboards_rel"."field_id" IS '圃場ID';

-- ----------------------------
-- Table structure for ph2_default_model_key_values
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_default_model_key_values";
CREATE TABLE "public"."ph2_default_model_key_values" (
  "model_id" int4 NOT NULL,
  "key" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "value" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."ph2_default_model_key_values"."model_id" IS 'モデルID';
COMMENT ON COLUMN "public"."ph2_default_model_key_values"."key" IS 'キー値';
COMMENT ON COLUMN "public"."ph2_default_model_key_values"."value" IS '値';
COMMENT ON COLUMN "public"."ph2_default_model_key_values"."created_at" IS '作成日時';
COMMENT ON COLUMN "public"."ph2_default_model_key_values"."updated_at" IS '更新日時';

-- ----------------------------
-- Table structure for ph2_default_paramset
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_default_paramset";
CREATE TABLE "public"."ph2_default_paramset" (
  "model_id" int4,
  "created_at" timestamp(6),
  "updated_at" timestamp(6),
  "key" varchar(64) COLLATE "pg_catalog"."default",
  "value" varchar(128) COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- Table structure for ph2_device_day
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_device_day";
CREATE TABLE "public"."ph2_device_day" (
  "id" int8 NOT NULL,
  "device_id" int8 NOT NULL,
  "date" date NOT NULL,
  "year" int2 NOT NULL,
  "lapse_day" int2 NOT NULL,
  "has_real" bool NOT NULL DEFAULT false
)
;
COMMENT ON COLUMN "public"."ph2_device_day"."id" IS '日付ID';
COMMENT ON COLUMN "public"."ph2_device_day"."device_id" IS 'デバイスID';
COMMENT ON COLUMN "public"."ph2_device_day"."date" IS '日付';
COMMENT ON COLUMN "public"."ph2_device_day"."year" IS '対象年度';
COMMENT ON COLUMN "public"."ph2_device_day"."lapse_day" IS '経過日数';
COMMENT ON COLUMN "public"."ph2_device_day"."has_real" IS '実測値があるかどうか';

-- ----------------------------
-- Table structure for ph2_devices
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_devices";
CREATE TABLE "public"."ph2_devices" (
  "id" int8 NOT NULL DEFAULT nextval('ph2_devices_id2_seq'::regclass),
  "field_id" int8 NOT NULL,
  "name" varchar COLLATE "pg_catalog"."default" NOT NULL,
  "location" varchar COLLATE "pg_catalog"."default",
  "placed_on" date,
  "client" varchar COLLATE "pg_catalog"."default",
  "sigfox_device_id" varchar COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "base_date" date,
  "brand" varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."ph2_devices"."id" IS 'デバイスID';
COMMENT ON COLUMN "public"."ph2_devices"."field_id" IS '圃場ID';
COMMENT ON COLUMN "public"."ph2_devices"."name" IS 'デバイス名';

-- ----------------------------
-- Table structure for ph2_el_stage_master
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_el_stage_master";
CREATE TABLE "public"."ph2_el_stage_master" (
  "id" int4 NOT NULL,
  "lapsed_day" int4 NOT NULL,
  "name" varchar(64) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."ph2_el_stage_master"."id" IS 'ELStage番号';
COMMENT ON COLUMN "public"."ph2_el_stage_master"."lapsed_day" IS '経過日';
COMMENT ON COLUMN "public"."ph2_el_stage_master"."name" IS '名前';

-- ----------------------------
-- Table structure for ph2_fdata
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_fdata";
CREATE TABLE "public"."ph2_fdata" (
  "fstage_id" int8 NOT NULL,
  "date" date,
  "fvalue" float8,
  "is_effective" bool,
  "f_sig_value" float8
)
;
COMMENT ON COLUMN "public"."ph2_fdata"."fvalue" IS 'f値';
COMMENT ON COLUMN "public"."ph2_fdata"."is_effective" IS '有効値';
COMMENT ON COLUMN "public"."ph2_fdata"."f_sig_value" IS '積算F値';

-- ----------------------------
-- Table structure for ph2_field_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_field_info";
CREATE TABLE "public"."ph2_field_info" (
  "id" int8 NOT NULL,
  "field_id" int8 NOT NULL,
  "device_id" int8 NOT NULL
)
;

-- ----------------------------
-- Table structure for ph2_field_status_by_device
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_field_status_by_device";
CREATE TABLE "public"."ph2_field_status_by_device" (
  "device_id" int8 NOT NULL,
  "stage" int2,
  "year" int2,
  "stage_date" date
)
;

-- ----------------------------
-- Table structure for ph2_fields
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_fields";
CREATE TABLE "public"."ph2_fields" (
  "id" int8 NOT NULL,
  "name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "location" varchar(512) COLLATE "pg_catalog"."default",
  "latitude" float8,
  "longitude" float8 DEFAULT nextval('ph2_fields_seq'::regclass),
  "client" varchar(255) COLLATE "pg_catalog"."default",
  "commence_date" varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."ph2_fields"."id" IS '圃場ID';
COMMENT ON COLUMN "public"."ph2_fields"."name" IS '圃場名';
COMMENT ON COLUMN "public"."ph2_fields"."location" IS '住所';
COMMENT ON COLUMN "public"."ph2_fields"."latitude" IS '緯度';
COMMENT ON COLUMN "public"."ph2_fields"."longitude" IS '経度';
COMMENT ON COLUMN "public"."ph2_fields"."client" IS '取引先';
COMMENT ON COLUMN "public"."ph2_fields"."commence_date" IS '統計開始日';
COMMENT ON COLUMN "public"."ph2_fields"."created_at" IS '作成日';
COMMENT ON COLUMN "public"."ph2_fields"."updated_at" IS '更新日';

-- ----------------------------
-- Table structure for ph2_insolation_data
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_insolation_data";
CREATE TABLE "public"."ph2_insolation_data" (
  "relation_id" int8 NOT NULL,
  "sensor_id" int8 NOT NULL,
  "insolation_intensity" float8 NOT NULL
)
;

-- ----------------------------
-- Table structure for ph2_model_data
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_model_data";
CREATE TABLE "public"."ph2_model_data" (
  "day_id" int8 NOT NULL,
  "f_value" float8,
  "culmitive_cnopy_ps" float8,
  "crown_leaf_area" float8,
  "leaf_count" float8
)
;
COMMENT ON COLUMN "public"."ph2_model_data"."day_id" IS '日付ID';
COMMENT ON COLUMN "public"."ph2_model_data"."f_value" IS 'fValue';
COMMENT ON COLUMN "public"."ph2_model_data"."culmitive_cnopy_ps" IS '推定積算樹冠光合成量';
COMMENT ON COLUMN "public"."ph2_model_data"."crown_leaf_area" IS '樹冠葉面積';
COMMENT ON COLUMN "public"."ph2_model_data"."leaf_count" IS '葉枚数';

-- ----------------------------
-- Table structure for ph2_model_master
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_model_master";
CREATE TABLE "public"."ph2_model_master" (
  "id" int4 NOT NULL,
  "name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "display_order" int2 NOT NULL,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."ph2_model_master"."id" IS 'モデル番号';
COMMENT ON COLUMN "public"."ph2_model_master"."name" IS 'モデル名';
COMMENT ON COLUMN "public"."ph2_model_master"."display_order" IS '表示順位';
COMMENT ON COLUMN "public"."ph2_model_master"."created_at" IS '作成日';
COMMENT ON COLUMN "public"."ph2_model_master"."updated_at" IS '更新日';

-- ----------------------------
-- Table structure for ph2_paramset_catalog
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_paramset_catalog";
CREATE TABLE "public"."ph2_paramset_catalog" (
  "id" int8 NOT NULL DEFAULT nextval('ph2_paramset_catalog_seq'::regclass),
  "name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "default_flag" bool NOT NULL DEFAULT false,
  "model_id" int4 NOT NULL,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "device_id" int8 NOT NULL,
  "year" int2
)
;
COMMENT ON COLUMN "public"."ph2_paramset_catalog"."id" IS 'パラメータセット番号';
COMMENT ON COLUMN "public"."ph2_paramset_catalog"."name" IS 'パラメータセット名';
COMMENT ON COLUMN "public"."ph2_paramset_catalog"."default_flag" IS '現在のパラメータ指定の真偽値';
COMMENT ON COLUMN "public"."ph2_paramset_catalog"."model_id" IS 'このパラメータセットが属するモデル';
COMMENT ON COLUMN "public"."ph2_paramset_catalog"."created_at" IS '作成日';
COMMENT ON COLUMN "public"."ph2_paramset_catalog"."updated_at" IS '更新日';
COMMENT ON COLUMN "public"."ph2_paramset_catalog"."device_id" IS 'このパラメータが属するデバイスID';

-- ----------------------------
-- Table structure for ph2_paramset_fruit_field
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_paramset_fruit_field";
CREATE TABLE "public"."ph2_paramset_fruit_field" (
  "paramset_id" int8 NOT NULL,
  "value_f" float8 NOT NULL,
  "value_g" float8 NOT NULL,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."ph2_paramset_fruit_field"."paramset_id" IS 'このパラメータセットが所属するカタログID';
COMMENT ON COLUMN "public"."ph2_paramset_fruit_field"."value_f" IS 'f値	
';
COMMENT ON COLUMN "public"."ph2_paramset_fruit_field"."value_g" IS 'g値';
COMMENT ON COLUMN "public"."ph2_paramset_fruit_field"."created_at" IS '作成日';
COMMENT ON COLUMN "public"."ph2_paramset_fruit_field"."updated_at" IS '更新日';

-- ----------------------------
-- Table structure for ph2_paramset_fruit_weibull
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_paramset_fruit_weibull";
CREATE TABLE "public"."ph2_paramset_fruit_weibull" (
  "paramset_id" int8 NOT NULL,
  "value_a" float8 NOT NULL,
  "value_b" float8 NOT NULL,
  "value_l" float8 NOT NULL,
  "created_at" timetz(6) NOT NULL,
  "updated_at" timetz(6) NOT NULL
)
;
COMMENT ON COLUMN "public"."ph2_paramset_fruit_weibull"."paramset_id" IS 'このパラメータセットが所属するカタログID';
COMMENT ON COLUMN "public"."ph2_paramset_fruit_weibull"."value_a" IS 'α値';
COMMENT ON COLUMN "public"."ph2_paramset_fruit_weibull"."value_b" IS 'β値';
COMMENT ON COLUMN "public"."ph2_paramset_fruit_weibull"."value_l" IS 'λ値';
COMMENT ON COLUMN "public"."ph2_paramset_fruit_weibull"."created_at" IS '作成日';
COMMENT ON COLUMN "public"."ph2_paramset_fruit_weibull"."updated_at" IS '更新日';

-- ----------------------------
-- Table structure for ph2_paramset_growth
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_paramset_growth";
CREATE TABLE "public"."ph2_paramset_growth" (
  "paramset_id" int8 NOT NULL,
  "before__d" float8 NOT NULL,
  "before_e" float8 NOT NULL,
  "after__d" float8 NOT NULL,
  "after_e" float8 NOT NULL,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."ph2_paramset_growth"."paramset_id" IS 'このパラメータセットが所属するカタログID';
COMMENT ON COLUMN "public"."ph2_paramset_growth"."before__d" IS '萌芽前d値';
COMMENT ON COLUMN "public"."ph2_paramset_growth"."before_e" IS '萌芽前e値';
COMMENT ON COLUMN "public"."ph2_paramset_growth"."after__d" IS '萌芽後d値';
COMMENT ON COLUMN "public"."ph2_paramset_growth"."after_e" IS '萌芽後e値';
COMMENT ON COLUMN "public"."ph2_paramset_growth"."created_at" IS '作成日';
COMMENT ON COLUMN "public"."ph2_paramset_growth"."updated_at" IS '更新日';

-- ----------------------------
-- Table structure for ph2_paramset_history
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_paramset_history";
CREATE TABLE "public"."ph2_paramset_history" (
  "paramset_id" int8 NOT NULL,
  "user_id" int8,
  "comment" varchar(512) COLLATE "pg_catalog"."default" NOT NULL,
  "latest" bool NOT NULL,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."ph2_paramset_history"."paramset_id" IS 'パラメータセットID';
COMMENT ON COLUMN "public"."ph2_paramset_history"."user_id" IS '編集したユーザーのID';
COMMENT ON COLUMN "public"."ph2_paramset_history"."comment" IS '説明';
COMMENT ON COLUMN "public"."ph2_paramset_history"."latest" IS '最新フラグ';
COMMENT ON COLUMN "public"."ph2_paramset_history"."created_at" IS '作成日';
COMMENT ON COLUMN "public"."ph2_paramset_history"."updated_at" IS '更新日';

-- ----------------------------
-- Table structure for ph2_paramset_leaf_area
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_paramset_leaf_area";
CREATE TABLE "public"."ph2_paramset_leaf_area" (
  "paramset_id" int8 NOT NULL,
  "value_a" float8 NOT NULL,
  "value_b" float8 NOT NULL,
  "value_c" float8 NOT NULL,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."ph2_paramset_leaf_area"."paramset_id" IS 'このパラメータセットが所属するカタログID';
COMMENT ON COLUMN "public"."ph2_paramset_leaf_area"."value_a" IS 'a値';
COMMENT ON COLUMN "public"."ph2_paramset_leaf_area"."value_b" IS 'b値';
COMMENT ON COLUMN "public"."ph2_paramset_leaf_area"."value_c" IS 'c値';
COMMENT ON COLUMN "public"."ph2_paramset_leaf_area"."created_at" IS '作成日';
COMMENT ON COLUMN "public"."ph2_paramset_leaf_area"."updated_at" IS '更新日';

-- ----------------------------
-- Table structure for ph2_paramset_leaf_count
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_paramset_leaf_count";
CREATE TABLE "public"."ph2_paramset_leaf_count" (
  "paramset_id" int8 NOT NULL,
  "value_c" float8 NOT NULL,
  "value_d" float8 NOT NULL,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."ph2_paramset_leaf_count"."paramset_id" IS 'このパラメータセットが所属するカタログID';
COMMENT ON COLUMN "public"."ph2_paramset_leaf_count"."value_c" IS 'c値	
';
COMMENT ON COLUMN "public"."ph2_paramset_leaf_count"."value_d" IS 'd値';
COMMENT ON COLUMN "public"."ph2_paramset_leaf_count"."created_at" IS '作成日';
COMMENT ON COLUMN "public"."ph2_paramset_leaf_count"."updated_at" IS '更新日';

-- ----------------------------
-- Table structure for ph2_paramset_ps_field
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_paramset_ps_field";
CREATE TABLE "public"."ph2_paramset_ps_field" (
  "paramset_id" int8 NOT NULL,
  "value_f" float8 NOT NULL,
  "value_g" float8 NOT NULL,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."ph2_paramset_ps_field"."paramset_id" IS 'このパラメータセットが所属するカタログID';
COMMENT ON COLUMN "public"."ph2_paramset_ps_field"."value_f" IS 'f値	
';
COMMENT ON COLUMN "public"."ph2_paramset_ps_field"."value_g" IS 'g値';
COMMENT ON COLUMN "public"."ph2_paramset_ps_field"."created_at" IS '作成日';
COMMENT ON COLUMN "public"."ph2_paramset_ps_field"."updated_at" IS '更新日';

-- ----------------------------
-- Table structure for ph2_paramset_ps_weibull
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_paramset_ps_weibull";
CREATE TABLE "public"."ph2_paramset_ps_weibull" (
  "paramset_id" int8 NOT NULL,
  "value_a" float8 NOT NULL,
  "value_b" float8 NOT NULL,
  "value_l" float8 NOT NULL,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."ph2_paramset_ps_weibull"."paramset_id" IS 'このパラメータセットが所属するカタログID';
COMMENT ON COLUMN "public"."ph2_paramset_ps_weibull"."value_a" IS 'α値';
COMMENT ON COLUMN "public"."ph2_paramset_ps_weibull"."value_b" IS 'β値';
COMMENT ON COLUMN "public"."ph2_paramset_ps_weibull"."value_l" IS 'λ値';
COMMENT ON COLUMN "public"."ph2_paramset_ps_weibull"."created_at" IS '作成日';
COMMENT ON COLUMN "public"."ph2_paramset_ps_weibull"."updated_at" IS '更新日';

-- ----------------------------
-- Table structure for ph2_raw_data
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_raw_data";
CREATE TABLE "public"."ph2_raw_data" (
  "sensor_id" int8 NOT NULL,
  "epoch_time" timestamp(6) NOT NULL,
  "start_channel" int2 NOT NULL,
  "channel1" int8 NOT NULL,
  "channel2" int8,
  "channel3" int8,
  "channel4" int8
)
;
COMMENT ON COLUMN "public"."ph2_raw_data"."sensor_id" IS '紐づくセンサーIDデバイスIDにも紐づけられる';
COMMENT ON COLUMN "public"."ph2_raw_data"."epoch_time" IS '全てのチャンネルのデータが揃った時刻';
COMMENT ON COLUMN "public"."ph2_raw_data"."start_channel" IS 'チャネルのスタート位置';
COMMENT ON COLUMN "public"."ph2_raw_data"."channel1" IS 'チャネル１のデータ';
COMMENT ON COLUMN "public"."ph2_raw_data"."channel2" IS 'チャネル２のデータ';
COMMENT ON COLUMN "public"."ph2_raw_data"."channel3" IS 'チャネル３のデータ';
COMMENT ON COLUMN "public"."ph2_raw_data"."channel4" IS 'チャネル４のデータ';

-- ----------------------------
-- Table structure for ph2_real_fruits_bearing
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_real_fruits_bearing";
CREATE TABLE "public"."ph2_real_fruits_bearing" (
  "device_id" int8 NOT NULL,
  "year" int2 NOT NULL,
  "bearing" int2 NOT NULL,
  "amount" float8 NOT NULL,
  "area" float8 NOT NULL,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."ph2_real_fruits_bearing"."device_id" IS 'このパラメータセットが所属するデバイスID';
COMMENT ON COLUMN "public"."ph2_real_fruits_bearing"."year" IS '対象年度';
COMMENT ON COLUMN "public"."ph2_real_fruits_bearing"."bearing" IS '着果負担';
COMMENT ON COLUMN "public"."ph2_real_fruits_bearing"."amount" IS '積算推定樹冠光合成量あたりの着果量';
COMMENT ON COLUMN "public"."ph2_real_fruits_bearing"."area" IS '実測着果数/樹冠葉面積';
COMMENT ON COLUMN "public"."ph2_real_fruits_bearing"."created_at" IS '作成日';
COMMENT ON COLUMN "public"."ph2_real_fruits_bearing"."updated_at" IS '更新日';

-- ----------------------------
-- Table structure for ph2_real_fruits_data
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_real_fruits_data";
CREATE TABLE "public"."ph2_real_fruits_data" (
  "device_id" int8 NOT NULL,
  "event_id" int2 NOT NULL,
  "target_date" date NOT NULL,
  "average" float4 NOT NULL,
  "count" int4 NOT NULL,
  "year" int2 NOT NULL,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."ph2_real_fruits_data"."device_id" IS 'このパラメータセットが所属するデバイスID';
COMMENT ON COLUMN "public"."ph2_real_fruits_data"."event_id" IS 'イベントID(1～3)';
COMMENT ON COLUMN "public"."ph2_real_fruits_data"."target_date" IS '実測日';
COMMENT ON COLUMN "public"."ph2_real_fruits_data"."average" IS '平均房重';
COMMENT ON COLUMN "public"."ph2_real_fruits_data"."count" IS '実測着果数';
COMMENT ON COLUMN "public"."ph2_real_fruits_data"."created_at" IS '作成日';
COMMENT ON COLUMN "public"."ph2_real_fruits_data"."updated_at" IS '更新日';

-- ----------------------------
-- Table structure for ph2_real_growth_f_date
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_real_growth_f_date";
CREATE TABLE "public"."ph2_real_growth_f_date" (
  "device_id" int8 NOT NULL,
  "value_f" float8 NOT NULL,
  "target_date" varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."ph2_real_growth_f_date"."device_id" IS 'このパラメータセットが所属するデバイスID';
COMMENT ON COLUMN "public"."ph2_real_growth_f_date"."value_f" IS 'f値';
COMMENT ON COLUMN "public"."ph2_real_growth_f_date"."target_date" IS '対象となる日付';

-- ----------------------------
-- Table structure for ph2_real_growth_f_stage
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_real_growth_f_stage";
CREATE TABLE "public"."ph2_real_growth_f_stage" (
  "device_id" int8 NOT NULL,
  "year" int2 NOT NULL,
  "stage_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "stage_start" int2 NOT NULL,
  "stage_end" int2 NOT NULL,
  "interval_f" float8 NOT NULL,
  "accumulated_f" float8 NOT NULL,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "id" int8 NOT NULL
)
;
COMMENT ON COLUMN "public"."ph2_real_growth_f_stage"."device_id" IS 'このパラメータセットが所属するデバイスID';
COMMENT ON COLUMN "public"."ph2_real_growth_f_stage"."year" IS '年度';
COMMENT ON COLUMN "public"."ph2_real_growth_f_stage"."stage_name" IS '生育期名';
COMMENT ON COLUMN "public"."ph2_real_growth_f_stage"."stage_start" IS 'EL Stage開始番号';
COMMENT ON COLUMN "public"."ph2_real_growth_f_stage"."stage_end" IS 'EL Stage終了番号';
COMMENT ON COLUMN "public"."ph2_real_growth_f_stage"."interval_f" IS 'F値間隔';
COMMENT ON COLUMN "public"."ph2_real_growth_f_stage"."accumulated_f" IS '累積F値';

-- ----------------------------
-- Table structure for ph2_real_leaf_shoots_area
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_real_leaf_shoots_area";
CREATE TABLE "public"."ph2_real_leaf_shoots_area" (
  "device_id" int8 NOT NULL,
  "target_date" date NOT NULL,
  "count" int4 NOT NULL,
  "average_area" float4 NOT NULL,
  "real_area" float8,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."ph2_real_leaf_shoots_area"."device_id" IS 'このパラメータセットが所属するデバイスID';
COMMENT ON COLUMN "public"."ph2_real_leaf_shoots_area"."target_date" IS '実測日';
COMMENT ON COLUMN "public"."ph2_real_leaf_shoots_area"."count" IS '新梢辺り葉枚数';
COMMENT ON COLUMN "public"."ph2_real_leaf_shoots_area"."average_area" IS '平均個葉面積';
COMMENT ON COLUMN "public"."ph2_real_leaf_shoots_area"."real_area" IS '実測新梢辺り葉面積';
COMMENT ON COLUMN "public"."ph2_real_leaf_shoots_area"."created_at" IS '作成日';
COMMENT ON COLUMN "public"."ph2_real_leaf_shoots_area"."updated_at" IS '更新日';

-- ----------------------------
-- Table structure for ph2_real_leaf_shoots_count
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_real_leaf_shoots_count";
CREATE TABLE "public"."ph2_real_leaf_shoots_count" (
  "device_id" int8 NOT NULL,
  "target_date" date NOT NULL,
  "count" int4 NOT NULL,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "year" int2 NOT NULL
)
;
COMMENT ON COLUMN "public"."ph2_real_leaf_shoots_count"."device_id" IS 'このパラメータセットが所属するデバイスID';
COMMENT ON COLUMN "public"."ph2_real_leaf_shoots_count"."target_date" IS '対象日';
COMMENT ON COLUMN "public"."ph2_real_leaf_shoots_count"."count" IS '新梢数';
COMMENT ON COLUMN "public"."ph2_real_leaf_shoots_count"."created_at" IS '作成日時';
COMMENT ON COLUMN "public"."ph2_real_leaf_shoots_count"."updated_at" IS '更新日時';
COMMENT ON COLUMN "public"."ph2_real_leaf_shoots_count"."year" IS '対象年度';

-- ----------------------------
-- Table structure for ph2_real_ps_amount
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_real_ps_amount";
CREATE TABLE "public"."ph2_real_ps_amount" (
  "device_id" int8 NOT NULL,
  "value_f" float4 NOT NULL,
  "value_g" float4 NOT NULL,
  "created_at" timestamp(6) NOT NULL,
  "updated_at" timestamp(6) NOT NULL,
  "year" int2 NOT NULL,
  "date" date NOT NULL
)
;
COMMENT ON COLUMN "public"."ph2_real_ps_amount"."device_id" IS 'このパラメータセットが所属するデバイスID';
COMMENT ON COLUMN "public"."ph2_real_ps_amount"."value_f" IS 'F値';
COMMENT ON COLUMN "public"."ph2_real_ps_amount"."value_g" IS 'G値';
COMMENT ON COLUMN "public"."ph2_real_ps_amount"."created_at" IS '作成日時';
COMMENT ON COLUMN "public"."ph2_real_ps_amount"."updated_at" IS '更新日時';
COMMENT ON COLUMN "public"."ph2_real_ps_amount"."year" IS '対象年度';
COMMENT ON COLUMN "public"."ph2_real_ps_amount"."date" IS '対象日時';

-- ----------------------------
-- Table structure for ph2_rel_base_data
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_rel_base_data";
CREATE TABLE "public"."ph2_rel_base_data" (
  "id" int8 NOT NULL,
  "device_id" int8 NOT NULL,
  "casted_at" timestamp(6) NOT NULL
)
;
COMMENT ON COLUMN "public"."ph2_rel_base_data"."id" IS '関係ID';

-- ----------------------------
-- Table structure for ph2_rel_field_device
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_rel_field_device";
CREATE TABLE "public"."ph2_rel_field_device" (
  "id" int8 NOT NULL,
  "field_id" int8 NOT NULL,
  "device_id" int8 NOT NULL,
  "brand_id" int4 NOT NULL,
  "commenced_at" timestamp(0),
  "closed_at" timestamp(0)
)
;
COMMENT ON COLUMN "public"."ph2_rel_field_device"."id" IS '関係ID';
COMMENT ON COLUMN "public"."ph2_rel_field_device"."field_id" IS '圃場ID';
COMMENT ON COLUMN "public"."ph2_rel_field_device"."device_id" IS 'デバイスID';
COMMENT ON COLUMN "public"."ph2_rel_field_device"."brand_id" IS 'ブランドID';
COMMENT ON COLUMN "public"."ph2_rel_field_device"."commenced_at" IS '運用開始日時';
COMMENT ON COLUMN "public"."ph2_rel_field_device"."closed_at" IS '運用終了日時';

-- ----------------------------
-- Table structure for ph2_sensor_contents
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_sensor_contents";
CREATE TABLE "public"."ph2_sensor_contents" (
  "id" int8 NOT NULL DEFAULT nextval('ph2_sensor_contents_id_seq'::regclass),
  "name" varchar COLLATE "pg_catalog"."default" NOT NULL,
  "channel_size" int4 NOT NULL,
  "code" varchar COLLATE "pg_catalog"."default" NOT NULL,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."ph2_sensor_contents"."id" IS 'センサー対象項目マスターID';
COMMENT ON COLUMN "public"."ph2_sensor_contents"."name" IS 'センサー対象項目名';
COMMENT ON COLUMN "public"."ph2_sensor_contents"."channel_size" IS 'SigFoxデータのチャネルサイズ';
COMMENT ON COLUMN "public"."ph2_sensor_contents"."code" IS 'センサー対象項目コード';

-- ----------------------------
-- Table structure for ph2_sensor_models
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_sensor_models";
CREATE TABLE "public"."ph2_sensor_models" (
  "id" int8 NOT NULL DEFAULT nextval('sensor_models_id_seq'::regclass),
  "code" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."ph2_sensor_models"."id" IS 'センサーモデルID';
COMMENT ON COLUMN "public"."ph2_sensor_models"."code" IS 'センサーモデル型番';

-- ----------------------------
-- Table structure for ph2_sensor_sizes
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_sensor_sizes";
CREATE TABLE "public"."ph2_sensor_sizes" (
  "id" int8 NOT NULL DEFAULT nextval('ph2_sensor_sizes_id_seq'::regclass),
  "size" int4 NOT NULL,
  "dxd" float8 NOT NULL,
  "dxu" float8 NOT NULL,
  "dt" float8 NOT NULL,
  "rs" int4 NOT NULL,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;

-- ----------------------------
-- Table structure for ph2_sensors
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_sensors";
CREATE TABLE "public"."ph2_sensors" (
  "id" int8 NOT NULL DEFAULT nextval('ph2_sensors_id2_seq'::regclass),
  "device_id" int8 NOT NULL,
  "channel" int4 NOT NULL,
  "sensor_size_id" int8 NOT NULL,
  "sensor_content_id" int8 NOT NULL,
  "plant" varchar COLLATE "pg_catalog"."default" NOT NULL,
  "kst" float8,
  "sm" float8,
  "lf" float8,
  "cb" float8,
  "sensor_model_id" int8,
  "name" varchar COLLATE "pg_catalog"."default",
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;

-- ----------------------------
-- Table structure for ph2_system_status
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_system_status";
CREATE TABLE "public"."ph2_system_status" (
  "table_name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "last_time" timestamp(6)
)
;
COMMENT ON COLUMN "public"."ph2_system_status"."table_name" IS 'テーブル名';
COMMENT ON COLUMN "public"."ph2_system_status"."last_time" IS '最終処理レコードの受信時刻';

-- ----------------------------
-- Table structure for ph2_users
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_users";
CREATE TABLE "public"."ph2_users" (
  "id" int8 NOT NULL DEFAULT nextval('users_id_seq'::regclass),
  "username" varchar COLLATE "pg_catalog"."default" NOT NULL,
  "email" varchar COLLATE "pg_catalog"."default" NOT NULL,
  "password_digest" varchar COLLATE "pg_catalog"."default" NOT NULL,
  "created_at" timestamp(6) NOT NULL,
  "updated_at" timestamp(6) NOT NULL
)
;
COMMENT ON COLUMN "public"."ph2_users"."id" IS 'ユーザーID';
COMMENT ON COLUMN "public"."ph2_users"."username" IS 'ユーザー名';
COMMENT ON COLUMN "public"."ph2_users"."email" IS 'ユーザーのemail';
COMMENT ON COLUMN "public"."ph2_users"."password_digest" IS 'パスワード';

-- ----------------------------
-- Table structure for ph2_voltage_data
-- ----------------------------
DROP TABLE IF EXISTS "public"."ph2_voltage_data";
CREATE TABLE "public"."ph2_voltage_data" (
  "relation_id" int8 NOT NULL,
  "content_id" int8 NOT NULL,
  "sensor_id" int8 NOT NULL,
  "channel_no" int2 NOT NULL,
  "voltage" float8 NOT NULL
)
;
COMMENT ON COLUMN "public"."ph2_voltage_data"."relation_id" IS '関係ID';
COMMENT ON COLUMN "public"."ph2_voltage_data"."content_id" IS 'センサーオブジェクトのコンテントID';
COMMENT ON COLUMN "public"."ph2_voltage_data"."sensor_id" IS 'センサー';
COMMENT ON COLUMN "public"."ph2_voltage_data"."channel_no" IS 'チャンネル番号';
COMMENT ON COLUMN "public"."ph2_voltage_data"."voltage" IS '電圧';

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."ph2_device_day_seq"', 1647135, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ph2_devices_id2_seq"
OWNED BY "public"."ph2_devices"."id";
SELECT setval('"public"."ph2_devices_id2_seq"', 1206, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ph2_devices_id_seq"
OWNED BY "public"."ph2_devices"."id";
SELECT setval('"public"."ph2_devices_id_seq"', 4, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ph2_fields_id_seq"
OWNED BY "public"."ph2_fields"."id";
SELECT setval('"public"."ph2_fields_id_seq"', 14, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ph2_fields_seq"
OWNED BY "public"."ph2_fields"."id";
SELECT setval('"public"."ph2_fields_seq"', 14, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ph2_paramset_catalog_seq"
OWNED BY "public"."ph2_paramset_catalog"."id";
SELECT setval('"public"."ph2_paramset_catalog_seq"', 6872400, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."ph2_real_growth_f_stage_seq"', 132, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."ph2_rel_base_data_seq"', 5009848, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ph2_sensor_contents_id_seq"
OWNED BY "public"."ph2_sensor_contents"."id";
SELECT setval('"public"."ph2_sensor_contents_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ph2_sensor_sizes_id_seq"
OWNED BY "public"."ph2_sensor_sizes"."id";
SELECT setval('"public"."ph2_sensor_sizes_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ph2_sensors_id2_seq"
OWNED BY "public"."ph2_sensors"."id";
SELECT setval('"public"."ph2_sensors_id2_seq"', 406, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ph2_sensors_id_seq"
OWNED BY "public"."ph2_sensors"."id";
SELECT setval('"public"."ph2_sensors_id_seq"', 8, true);

-- ----------------------------
-- Indexes structure for table ph2_base_data
-- ----------------------------
CREATE INDEX "i_ph2_base_data_on_relation_id" ON "public"."ph2_base_data" USING btree (
  "relation_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table ph2_brand_master
-- ----------------------------
ALTER TABLE "public"."ph2_brand_master" ADD CONSTRAINT "brand_master_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table ph2_daily_base_data
-- ----------------------------
CREATE UNIQUE INDEX "i_ph2_daily_base_data_on_day_id" ON "public"."ph2_daily_base_data" USING btree (
  "day_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Indexes structure for table ph2_dashboard
-- ----------------------------
CREATE INDEX "i_ph2_dashbord_on_device_id" ON "public"."ph2_dashboard" USING btree (
  "device_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "i_ph2_dashbord_on_did_cid" ON "public"."ph2_dashboard" USING btree (
  "device_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "content_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Indexes structure for table ph2_dashboards_rel
-- ----------------------------
CREATE INDEX "i_ph2_dashboards_rel_on_user_id" ON "public"."ph2_dashboards_rel" USING btree (
  "user_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Indexes structure for table ph2_default_model_key_values
-- ----------------------------
CREATE INDEX "i_ph2_default_model_key_values_on_model_id" ON "public"."ph2_default_model_key_values" USING btree (
  "model_id" "pg_catalog"."int4_ops" ASC NULLS LAST
);

-- ----------------------------
-- Indexes structure for table ph2_default_paramset
-- ----------------------------
CREATE INDEX "index_on_ph2_default_paramset" ON "public"."ph2_default_paramset" USING btree (
  "model_id" "pg_catalog"."int4_ops" ASC NULLS LAST
);

-- ----------------------------
-- Indexes structure for table ph2_device_day
-- ----------------------------
CREATE INDEX "i_ph2_device_day_on_device_id" ON "public"."ph2_device_day" USING btree (
  "device_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "i_ph2_device_day_on_m1" ON "public"."ph2_device_day" USING btree (
  "device_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "year" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE INDEX "i_ph2_device_day_on_m2" ON "public"."ph2_device_day" USING btree (
  "device_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "date" "pg_catalog"."date_ops" ASC NULLS LAST
);
CREATE INDEX "i_ph2_device_day_on_m3" ON "public"."ph2_device_day" USING btree (
  "device_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "year" "pg_catalog"."int2_ops" ASC NULLS LAST,
  "lapse_day" "pg_catalog"."int2_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table ph2_device_day
-- ----------------------------
ALTER TABLE "public"."ph2_device_day" ADD CONSTRAINT "ph2_device_day_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table ph2_devices
-- ----------------------------
ALTER TABLE "public"."ph2_devices" ADD CONSTRAINT "devices_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table ph2_el_stage_master
-- ----------------------------
ALTER TABLE "public"."ph2_el_stage_master" ADD CONSTRAINT "el_stage_master_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table ph2_fdata
-- ----------------------------
CREATE INDEX "i_ph2_f_data_on_date" ON "public"."ph2_fdata" USING btree (
  "date" "pg_catalog"."date_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table ph2_field_info
-- ----------------------------
ALTER TABLE "public"."ph2_field_info" ADD CONSTRAINT "ph2_field_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table ph2_field_status_by_device
-- ----------------------------
ALTER TABLE "public"."ph2_field_status_by_device" ADD CONSTRAINT "ph2_field_status_by_device_pkey" PRIMARY KEY ("device_id");

-- ----------------------------
-- Uniques structure for table ph2_fields
-- ----------------------------
ALTER TABLE "public"."ph2_fields" ADD CONSTRAINT "u_name_on_ph2_fields" UNIQUE ("name");

-- ----------------------------
-- Primary Key structure for table ph2_fields
-- ----------------------------
ALTER TABLE "public"."ph2_fields" ADD CONSTRAINT "ph2_fields_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table ph2_insolation_data
-- ----------------------------
CREATE INDEX "i_ph2_insolaion_data_on_relation_id" ON "public"."ph2_insolation_data" USING btree (
  "relation_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Indexes structure for table ph2_model_data
-- ----------------------------
CREATE INDEX "i_ph2_model_data_on_day_id" ON "public"."ph2_model_data" USING btree (
  "day_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table ph2_model_data
-- ----------------------------
ALTER TABLE "public"."ph2_model_data" ADD CONSTRAINT "ph2_model_data_pkey" PRIMARY KEY ("day_id");

-- ----------------------------
-- Primary Key structure for table ph2_model_master
-- ----------------------------
ALTER TABLE "public"."ph2_model_master" ADD CONSTRAINT "ph2_model_master_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table ph2_paramset_catalog
-- ----------------------------
CREATE INDEX "i_ph2_paramset_catalog_on_default_flag" ON "public"."ph2_paramset_catalog" USING btree (
  "default_flag" "pg_catalog"."bool_ops" ASC NULLS LAST
);
CREATE INDEX "i_ph2_paramset_catalog_on_model_id" ON "public"."ph2_paramset_catalog" USING btree (
  "model_id" "pg_catalog"."int4_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table ph2_paramset_catalog
-- ----------------------------
ALTER TABLE "public"."ph2_paramset_catalog" ADD CONSTRAINT "ph2_brand_master_copy1_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table ph2_paramset_fruit_field
-- ----------------------------
ALTER TABLE "public"."ph2_paramset_fruit_field" ADD CONSTRAINT "ph2_paramset_fruit_field_pkey" PRIMARY KEY ("paramset_id");

-- ----------------------------
-- Primary Key structure for table ph2_paramset_fruit_weibull
-- ----------------------------
ALTER TABLE "public"."ph2_paramset_fruit_weibull" ADD CONSTRAINT "ph2_paramset_fruit_weibull_pkey" PRIMARY KEY ("paramset_id");

-- ----------------------------
-- Primary Key structure for table ph2_paramset_growth
-- ----------------------------
ALTER TABLE "public"."ph2_paramset_growth" ADD CONSTRAINT "ph2_paramset_growth_pkey" PRIMARY KEY ("paramset_id");

-- ----------------------------
-- Indexes structure for table ph2_paramset_history
-- ----------------------------
CREATE INDEX "i_ph2_paramset_history_on_latest" ON "public"."ph2_paramset_history" USING btree (
  "latest" "pg_catalog"."bool_ops" ASC NULLS LAST
);
CREATE INDEX "i_ph2_paramset_history_on_paramset_id" ON "public"."ph2_paramset_history" USING btree (
  "paramset_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table ph2_paramset_leaf_area
-- ----------------------------
ALTER TABLE "public"."ph2_paramset_leaf_area" ADD CONSTRAINT "ph2_paramset_leaf_area_pkey" PRIMARY KEY ("paramset_id");

-- ----------------------------
-- Primary Key structure for table ph2_paramset_leaf_count
-- ----------------------------
ALTER TABLE "public"."ph2_paramset_leaf_count" ADD CONSTRAINT "ph2_paramset_leaf_count_pkey" PRIMARY KEY ("paramset_id");

-- ----------------------------
-- Primary Key structure for table ph2_paramset_ps_field
-- ----------------------------
ALTER TABLE "public"."ph2_paramset_ps_field" ADD CONSTRAINT "ph2_paramset_ps_field_pkey" PRIMARY KEY ("paramset_id");

-- ----------------------------
-- Primary Key structure for table ph2_paramset_ps_weibull
-- ----------------------------
ALTER TABLE "public"."ph2_paramset_ps_weibull" ADD CONSTRAINT "ph2_paramset_ps_weibull_pkey" PRIMARY KEY ("paramset_id");

-- ----------------------------
-- Indexes structure for table ph2_raw_data
-- ----------------------------
CREATE INDEX "i_ph2_raw_data_on_epoch_time" ON "public"."ph2_raw_data" USING btree (
  "epoch_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "i_ph2_raw_data_on_sensor_id" ON "public"."ph2_raw_data" USING btree (
  "sensor_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Indexes structure for table ph2_real_fruits_bearing
-- ----------------------------
CREATE INDEX "i_ph2_real_fruits_bearing_on_device_id" ON "public"."ph2_real_fruits_bearing" USING btree (
  "device_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table ph2_real_fruits_bearing
-- ----------------------------
ALTER TABLE "public"."ph2_real_fruits_bearing" ADD CONSTRAINT "ph2_real_fruits_bearing_pkey" PRIMARY KEY ("device_id");

-- ----------------------------
-- Indexes structure for table ph2_real_fruits_data
-- ----------------------------
CREATE INDEX "i_ph2_real_fruits_data_on_device_id" ON "public"."ph2_real_fruits_data" USING btree (
  "device_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Indexes structure for table ph2_real_growth_f_date
-- ----------------------------
CREATE INDEX "i_ph2_real_growth_f_date_on_device_id" ON "public"."ph2_real_growth_f_date" USING btree (
  "device_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "target_date" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Indexes structure for table ph2_real_growth_f_stage
-- ----------------------------
CREATE INDEX "i_ph2_real_growth_f_stage_on_device_id
" ON "public"."ph2_real_growth_f_stage" USING btree (
  "device_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "i_ph2_real_growth_f_stage_on_mlt1
" ON "public"."ph2_real_growth_f_stage" USING btree (
  "device_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "year" "pg_catalog"."int2_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table ph2_real_growth_f_stage
-- ----------------------------
ALTER TABLE "public"."ph2_real_growth_f_stage" ADD CONSTRAINT "ph2_real_growth_f_stage_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table ph2_real_leaf_shoots_area
-- ----------------------------
CREATE INDEX "i_ph2_real_leaf_shoots_area_on_device_id" ON "public"."ph2_real_leaf_shoots_area" USING btree (
  "device_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "i_ph2_real_leaf_shoots_area_on_target_date" ON "public"."ph2_real_leaf_shoots_area" USING btree (
  "target_date" "pg_catalog"."date_ops" ASC NULLS LAST
);

-- ----------------------------
-- Indexes structure for table ph2_real_leaf_shoots_count
-- ----------------------------
CREATE INDEX "i_ph2_real_leaf_shoots_count_on_target_date" ON "public"."ph2_real_leaf_shoots_count" USING btree (
  "device_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "target_date_in_ph2_real_leaf_shoots_count" ON "public"."ph2_real_leaf_shoots_count" USING btree (
  "target_date" "pg_catalog"."date_ops" ASC NULLS LAST
);

-- ----------------------------
-- Indexes structure for table ph2_real_ps_amount
-- ----------------------------
CREATE INDEX "i_ph2_real_leaf_shoots_count_on_device_id" ON "public"."ph2_real_ps_amount" USING btree (
  "device_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Indexes structure for table ph2_rel_base_data
-- ----------------------------
CREATE INDEX "i_ph2_rel_base_data_on_casted_at" ON "public"."ph2_rel_base_data" USING btree (
  "casted_at" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "i_ph2_rel_base_data_on_device_id" ON "public"."ph2_rel_base_data" USING btree (
  "device_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table ph2_rel_base_data
-- ----------------------------
ALTER TABLE "public"."ph2_rel_base_data" ADD CONSTRAINT "ph2_rel_base_data_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table ph2_rel_field_device
-- ----------------------------
CREATE INDEX "i_ph2_rel_field_device_on_device_id" ON "public"."ph2_rel_field_device" USING btree (
  "device_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "i_ph2_rel_field_device_on_field_id" ON "public"."ph2_rel_field_device" USING btree (
  "field_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table ph2_rel_field_device
-- ----------------------------
ALTER TABLE "public"."ph2_rel_field_device" ADD CONSTRAINT "ph2_rel_field_device_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table ph2_sensor_contents
-- ----------------------------
ALTER TABLE "public"."ph2_sensor_contents" ADD CONSTRAINT "sensor_contents_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table ph2_sensor_models
-- ----------------------------
ALTER TABLE "public"."ph2_sensor_models" ADD CONSTRAINT "sensor_models_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table ph2_sensor_sizes
-- ----------------------------
ALTER TABLE "public"."ph2_sensor_sizes" ADD CONSTRAINT "sensor_sizes_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table ph2_sensors
-- ----------------------------
CREATE INDEX "index_sensors_on_device_id" ON "public"."ph2_sensors" USING btree (
  "device_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "index_sensors_on_sensor_content_id" ON "public"."ph2_sensors" USING btree (
  "sensor_content_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "index_sensors_on_sensor_model_id" ON "public"."ph2_sensors" USING btree (
  "sensor_model_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "index_sensors_on_sensor_size_id" ON "public"."ph2_sensors" USING btree (
  "sensor_size_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table ph2_sensors
-- ----------------------------
ALTER TABLE "public"."ph2_sensors" ADD CONSTRAINT "sensors_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table ph2_system_status
-- ----------------------------
ALTER TABLE "public"."ph2_system_status" ADD CONSTRAINT "ph2_system_status_pkey" PRIMARY KEY ("table_name");

-- ----------------------------
-- Primary Key structure for table ph2_users
-- ----------------------------
ALTER TABLE "public"."ph2_users" ADD CONSTRAINT "users_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table ph2_voltage_data
-- ----------------------------
CREATE INDEX "i_ph2_voltage_data_on_relation_id" ON "public"."ph2_voltage_data" USING btree (
  "relation_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);



-- ----------------------------
-- Foreign Keys structure for table ph2_base_data
-- ----------------------------
ALTER TABLE "public"."ph2_base_data" ADD CONSTRAINT "f_ph2_base_data_on_relation_id" FOREIGN KEY ("relation_id") REFERENCES "public"."ph2_rel_base_data" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "public"."ph2_base_data" ADD CONSTRAINT "f_ph2_base_data_on_sensor_id" FOREIGN KEY ("sensor_id") REFERENCES "public"."ph2_sensors" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table ph2_daily_base_data
-- ----------------------------
ALTER TABLE "public"."ph2_daily_base_data" ADD CONSTRAINT "f_ph2_daily_base_on_day_id" FOREIGN KEY ("day_id") REFERENCES "public"."ph2_device_day" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table ph2_dashboard
-- ----------------------------
ALTER TABLE "public"."ph2_dashboard" ADD CONSTRAINT "f_ph2_dashbord_on_device_id" FOREIGN KEY ("device_id") REFERENCES "public"."ph2_devices" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table ph2_dashboards_rel
-- ----------------------------
ALTER TABLE "public"."ph2_dashboards_rel" ADD CONSTRAINT "f_ph2_dashboards_rel_on_field_id" FOREIGN KEY ("field_id") REFERENCES "public"."ph2_fields" ("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "public"."ph2_dashboards_rel" ADD CONSTRAINT "f_ph2_dashboards_rel_on_user_id" FOREIGN KEY ("user_id") REFERENCES "public"."ph2_users" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table ph2_default_model_key_values
-- ----------------------------
ALTER TABLE "public"."ph2_default_model_key_values" ADD CONSTRAINT "f_ph2_default_model_key_values_on_model_id" FOREIGN KEY ("model_id") REFERENCES "public"."ph2_model_master" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table ph2_device_day
-- ----------------------------
ALTER TABLE "public"."ph2_device_day" ADD CONSTRAINT "f_ph2_device_day_on_device_id_fkey" FOREIGN KEY ("device_id") REFERENCES "public"."ph2_devices" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table ph2_fdata
-- ----------------------------
ALTER TABLE "public"."ph2_fdata" ADD CONSTRAINT "f_ph2_f_data_on_fstage_id" FOREIGN KEY ("fstage_id") REFERENCES "public"."ph2_real_growth_f_stage" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table ph2_insolation_data
-- ----------------------------
ALTER TABLE "public"."ph2_insolation_data" ADD CONSTRAINT "f_ph2_insolation_data_on_relation_id" FOREIGN KEY ("relation_id") REFERENCES "public"."ph2_rel_base_data" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "public"."ph2_insolation_data" ADD CONSTRAINT "f_ph2_insolation_data_on_sensor_id" FOREIGN KEY ("sensor_id") REFERENCES "public"."ph2_sensors" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table ph2_model_data
-- ----------------------------
ALTER TABLE "public"."ph2_model_data" ADD CONSTRAINT "f_ph2_model_data_on_day_id" FOREIGN KEY ("day_id") REFERENCES "public"."ph2_device_day" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table ph2_paramset_catalog
-- ----------------------------
ALTER TABLE "public"."ph2_paramset_catalog" ADD CONSTRAINT "f_ph2_paramset_catalog_on_model_id" FOREIGN KEY ("model_id") REFERENCES "public"."ph2_model_master" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table ph2_paramset_fruit_field
-- ----------------------------
ALTER TABLE "public"."ph2_paramset_fruit_field" ADD CONSTRAINT "f_ph2_paramset_fruit_field_on_paramset_id" FOREIGN KEY ("paramset_id") REFERENCES "public"."ph2_paramset_catalog" ("id") ON DELETE CASCADE ON UPDATE CASCADE;
COMMENT ON CONSTRAINT "f_ph2_paramset_fruit_field_on_paramset_id" ON "public"."ph2_paramset_fruit_field" IS '共通パラメータセットカタログの外部キー';

-- ----------------------------
-- Foreign Keys structure for table ph2_paramset_fruit_weibull
-- ----------------------------
ALTER TABLE "public"."ph2_paramset_fruit_weibull" ADD CONSTRAINT "f_ph2_paramset_fruit_weibull_on_paramset_id" FOREIGN KEY ("paramset_id") REFERENCES "public"."ph2_paramset_catalog" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table ph2_paramset_growth
-- ----------------------------
ALTER TABLE "public"."ph2_paramset_growth" ADD CONSTRAINT "f_ph2_paramset_growth_on_paramset_id" FOREIGN KEY ("paramset_id") REFERENCES "public"."ph2_paramset_catalog" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table ph2_paramset_history
-- ----------------------------
ALTER TABLE "public"."ph2_paramset_history" ADD CONSTRAINT "f_ph2_paramset_history_on_user_id" FOREIGN KEY ("user_id") REFERENCES "public"."ph2_users" ("id") ON DELETE SET NULL ON UPDATE SET NULL;
ALTER TABLE "public"."ph2_paramset_history" ADD CONSTRAINT "i_ph2_paramset_history_on_latest" FOREIGN KEY ("paramset_id") REFERENCES "public"."ph2_paramset_catalog" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table ph2_paramset_leaf_area
-- ----------------------------
ALTER TABLE "public"."ph2_paramset_leaf_area" ADD CONSTRAINT "f_ph2_paramset_leaf_area_on_paramset_id" FOREIGN KEY ("paramset_id") REFERENCES "public"."ph2_paramset_catalog" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table ph2_paramset_leaf_count
-- ----------------------------
ALTER TABLE "public"."ph2_paramset_leaf_count" ADD CONSTRAINT "f_ph2_paramset_leaf_count_on_paramset_id" FOREIGN KEY ("paramset_id") REFERENCES "public"."ph2_paramset_catalog" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table ph2_paramset_ps_field
-- ----------------------------
ALTER TABLE "public"."ph2_paramset_ps_field" ADD CONSTRAINT "f_ph2_paramset_ps_field_on_paramset_id" FOREIGN KEY ("paramset_id") REFERENCES "public"."ph2_paramset_catalog" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table ph2_paramset_ps_weibull
-- ----------------------------
ALTER TABLE "public"."ph2_paramset_ps_weibull" ADD CONSTRAINT "f_ph2_paramset_ps_weibull_on_paramset_id" FOREIGN KEY ("paramset_id") REFERENCES "public"."ph2_paramset_catalog" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table ph2_real_fruits_bearing
-- ----------------------------
ALTER TABLE "public"."ph2_real_fruits_bearing" ADD CONSTRAINT "f_ph2_real_fruits_bearing_on_device_id" FOREIGN KEY ("device_id") REFERENCES "public"."ph2_devices" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table ph2_real_fruits_data
-- ----------------------------
ALTER TABLE "public"."ph2_real_fruits_data" ADD CONSTRAINT "f_ph2_real_fruits_data_on_device_id" FOREIGN KEY ("device_id") REFERENCES "public"."ph2_devices" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table ph2_real_growth_f_date
-- ----------------------------
ALTER TABLE "public"."ph2_real_growth_f_date" ADD CONSTRAINT "f_ph2_real_growth_f_date_on_device_id
" FOREIGN KEY ("device_id") REFERENCES "public"."ph2_devices" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table ph2_real_growth_f_stage
-- ----------------------------
ALTER TABLE "public"."ph2_real_growth_f_stage" ADD CONSTRAINT "f_ph2_real_growth_f_stage_on_device_id
f_ph2_real_growth_f_sta" FOREIGN KEY ("device_id") REFERENCES "public"."ph2_devices" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table ph2_real_leaf_shoots_area
-- ----------------------------
ALTER TABLE "public"."ph2_real_leaf_shoots_area" ADD CONSTRAINT "f_ph2_real_leaf_shoots_area_on_device_id
f_ph2_real_leaf_shoot" FOREIGN KEY ("device_id") REFERENCES "public"."ph2_devices" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table ph2_real_leaf_shoots_count
-- ----------------------------
ALTER TABLE "public"."ph2_real_leaf_shoots_count" ADD CONSTRAINT "f_ph2_real_leaf_shoots_count_on_device_id" FOREIGN KEY ("device_id") REFERENCES "public"."ph2_devices" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table ph2_real_ps_amount
-- ----------------------------
ALTER TABLE "public"."ph2_real_ps_amount" ADD CONSTRAINT "f_ph2_real_leaf_shoots_count_on_device_id" FOREIGN KEY ("device_id") REFERENCES "public"."ph2_paramset_catalog" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table ph2_rel_base_data
-- ----------------------------
ALTER TABLE "public"."ph2_rel_base_data" ADD CONSTRAINT "f_ph2_rel_base_data_on_device_id" FOREIGN KEY ("device_id") REFERENCES "public"."ph2_devices" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table ph2_rel_field_device
-- ----------------------------
ALTER TABLE "public"."ph2_rel_field_device" ADD CONSTRAINT "f_ph2_rel_field_device_on_device_id" FOREIGN KEY ("device_id") REFERENCES "public"."ph2_devices" ("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "public"."ph2_rel_field_device" ADD CONSTRAINT "f_ph2_rel_field_device_on_field_id" FOREIGN KEY ("field_id") REFERENCES "public"."ph2_fields" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table ph2_sensors
-- ----------------------------
ALTER TABLE "public"."ph2_sensors" ADD CONSTRAINT "fk_rails_76c31c3d93" FOREIGN KEY ("sensor_content_id") REFERENCES "public"."ph2_sensor_contents" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."ph2_sensors" ADD CONSTRAINT "fk_rails_92e56bf2fb" FOREIGN KEY ("device_id") REFERENCES "public"."ph2_devices" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."ph2_sensors" ADD CONSTRAINT "fk_rails_a39885339e" FOREIGN KEY ("sensor_size_id") REFERENCES "public"."ph2_sensor_sizes" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."ph2_sensors" ADD CONSTRAINT "fk_rails_cd16a62e8a" FOREIGN KEY ("sensor_model_id") REFERENCES "public"."ph2_sensor_models" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table ph2_voltage_data
-- ----------------------------
ALTER TABLE "public"."ph2_voltage_data" ADD CONSTRAINT "f_ph2_voltage_data_on_relation_id" FOREIGN KEY ("relation_id") REFERENCES "public"."ph2_rel_base_data" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "public"."ph2_voltage_data" ADD CONSTRAINT "f_ph2_voltage_data_on_sensor_id" FOREIGN KEY ("sensor_id") REFERENCES "public"."ph2_sensors" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;

