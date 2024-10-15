create table USER_email_status
(
id_number nvarchar(255)
, to_address nvarchar(255)
, cc_address nvarchar(255)
, bcc_address nvarchar(255)
, subject nvarchar(255)
, body nvarchar(4000)
, has_attachments int
, send_success int
, failure_reason nvarchar(4000)
, time_taken_in_millis bigint
, last_updated datetime default GETDATE()
, skip_health_check int
);

grant select, insert, update, delete on USER_email_status to olf_user;
grant select on USER_email_status to olf_readonly;

