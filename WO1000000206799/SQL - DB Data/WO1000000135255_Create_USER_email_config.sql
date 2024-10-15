create table USER_email_config
(
id_number int IDENTITY(1,1)
, active_flag nvarchar(255)
, mail_context nvarchar(255)
, mail_sub_context nvarchar(255)
, mail_subject nvarchar(255)
, mail_to nvarchar(255)
, mail_cc nvarchar(255)
, mail_bcc nvarchar(255)
, mail_body nvarchar(4000)
, row_change_reason nvarchar(255) NOT NULL
, TimeStart datetime2 (2) GENERATED ALWAYS AS ROW START
, TimeEnd datetime2 (2) GENERATED ALWAYS AS ROW END
, PERIOD FOR SYSTEM_TIME (TimeStart, TimeEnd)
)
WITH (SYSTEM_VERSIONING = ON (HISTORY_TABLE = dbo.USER_email_config_hist));

grant select, insert, update, delete on USER_email_config to olf_user;
grant select on USER_email_config to olf_readonly;

