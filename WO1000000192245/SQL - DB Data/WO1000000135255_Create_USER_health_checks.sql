create table USER_health_checks
(
id_number int
, active_flag int
, name nvarchar(255)
, description nvarchar(1000)
, sql nvarchar(4000)
, message nvarchar(255)
, row_change_reason nvarchar(255) NOT NULL
, TimeStart datetime2 (2) GENERATED ALWAYS AS ROW START
, TimeEnd datetime2 (2) GENERATED ALWAYS AS ROW END
, PERIOD FOR SYSTEM_TIME (TimeStart, TimeEnd)
)
WITH (SYSTEM_VERSIONING = ON (HISTORY_TABLE = dbo.USER_health_checks_hist));

grant select, insert, update, delete on USER_health_checks to olf_user;
grant select on USER_health_checks to olf_readonly;
