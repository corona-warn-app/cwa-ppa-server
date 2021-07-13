ALTER SEQUENCE scan_instance_id_seq AS bigint;

ALTER TABLE scan_instance ALTER COLUMN id TYPE bigint;
