-- exposure_window
INSERT INTO data_donation.exposure_window (
    date,
    report_type,
    infectiousness,
    callibration_confidence,
    transmission_risk_level,
    normalized_time,
    cwa_version_major,
    cwa_version_minor,
    cwa_version_patch,
    app_config_etag,
    submitted_at
)
SELECT
    date_trunc('day', NOW() - interval '1 month'),
    1, -- report_type integer NOT NULL
    1, -- infectiousness integer NOT NULL,
    1, -- callibration_confidence integer NOT NULL,
    1, -- transmission_risk_level integer NOT NULL,
    12341, -- normalized_time numeric NOT NULL,
    1, -- cwa_version_major integer NOT NULL,
    1, -- cwa_version_minor integer NOT NULL,
    1, -- cwa_version_patch integer NOT NULL,
    left(md5(i::text), 100), -- app_config_etag character varying(100) COLLATE pg_catalog."default" NOT NULL,
    date_trunc('day', NOW() - interval '1 month') -- submitted_at date NOT NULL DEFAULT CURRENT_DATE,
FROM generate_series(1, 10000000) AS s(i)
-- results in: INSERT 0 10000000, Query returned successfully in 37 secs 371 msec.

-- scan_instance
INSERT INTO data_donation.scan_instance (
    exposure_window_id,
    typical_attenuation,
    minimum_attenuation,
    seconds_since_last_scan,
    submitted_at
)
SELECT
    (SELECT id FROM data_donation.exposure_window ORDER BY RANDOM() LIMIT 1), -- exposure_window_id integer NOT NULL,
    1, -- typical_attenuation integer NOT NULL,
    1, -- minimum_attenuation integer NOT NULL,
    1, -- seconds_since_last_scan integer NOT NULL,
    date_trunc('day', NOW() - interval '1 month') -- submitted_at date,
FROM generate_series(1, 100000000) AS s(i)
-- with FK: results in: INSERT 0 100000000, Query returned successfully in 14 min 29 secs.
-- without FK: results in: INSERT 0 100000000, Query returned successfully in 3 min 45 secs.


select
(select count(*) from data_donation.exposure_window) as exposure_window,
(select count(*) from data_donation.scan_instance) as scan_instance

