drop view if exists "table_stats";

create or replace view table_stats as
select to_timestamp(cast(min(created_at) as bigint))::date as oldest,
       to_timestamp(cast(max(created_at) as bigint))::date as newest,
       count(*) as entries,
       'api_token' as tab
       from data_donation.api_token
union
select min(submitted_at),
       max(submitted_at),
       count(*),
       'client_metadata'
       from data_donation.client_metadata
union
select to_timestamp(cast(min(created_at)/1000 as bigint))::date,
       to_timestamp(cast(max(created_at)/1000 as bigint))::date,
       count(*),
       'device_token'
       from data_donation.device_token
union
select min(submitted_at),
       max(submitted_at),
       count(*),
       'exposure_risk_metadata'
       from data_donation.exposure_risk_metadata
union
select min(submitted_at),
       max(submitted_at),
       count(*),
       'exposure_window'
       from data_donation.exposure_window
union
select min(submitted_at),
       max(submitted_at),
       count(*),
       'scan_instance'
       from data_donation.scan_instance
union
select min(submitted_at),
       max(submitted_at),
       count(*),
       'key_submission_metadata_with_client_metadata'
       from data_donation.key_submission_metadata_with_client_metadata
union
select min(submitted_at),
       max(submitted_at),
       count(*),
       'key_submission_metadata_with_user_metadata'
       from data_donation.key_submission_metadata_with_user_metadata
union
select to_timestamp(cast(min(expiration_timestamp) as bigint))::date,
       to_timestamp(cast(max(expiration_timestamp) as bigint))::date,
       count(*),
       'one_time_password'
       from data_donation.one_time_password
union
select to_timestamp(cast(min(created_at)/1000 as bigint))::date,
       to_timestamp(cast(max(created_at)/1000 as bigint))::date,
       count(*),
       'salt'
       from data_donation.salt
union
select min(submitted_at),
       max(submitted_at),
       count(*),
       'test_result_metadata'
       from data_donation.test_result_metadata
union
select min(submitted_at),
       max(submitted_at),
       count(*),
       'user_metadata'
       from data_donation.user_metadata
union
select to_timestamp(cast(min(expiration_timestamp) as bigint))::date,
       to_timestamp(cast(max(expiration_timestamp) as bigint))::date,
       count(*),
       'els_one_time_password'
       from data_donation.els_one_time_password
union
select min(submitted_at),
       max(submitted_at),
       count(*),
       'exposure_window_test_result'
       from data_donation.exposure_window_test_result
union
select min(submitted_at),
       max(submitted_at),
       count(*),
       'exposure_windows_at_test_registration'
       from data_donation.exposure_windows_at_test_registration
union
select min(submitted_at),
       max(submitted_at),
       count(*),
       'scan_instances_at_test_registration'
       from data_donation.scan_instances_at_test_registration
union
select min(submitted_at),
       max(submitted_at),
       count(*),
       'summarized_exposure_windows_with_user_metadata'
       from data_donation.summarized_exposure_windows_with_user_metadata
;

-- now you can get a quick overview about what's in the tables with:
-- select * from table_stats order by tab;
