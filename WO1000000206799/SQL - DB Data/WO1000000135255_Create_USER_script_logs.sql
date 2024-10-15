create table USER_script_logs
(
id_number nvarchar(255),
script_name nvarchar(255),
task_name nvarchar(255),
user_name nvarchar(255),
machine_name nvarchar(255),
module_name nvarchar(255),
log_type nvarchar(255),
time_stamp datetime,
message nvarchar(4000)
);

grant select, insert, update, delete on USER_script_logs to olf_user;
grant select on USER_script_logs to olf_readonly;

