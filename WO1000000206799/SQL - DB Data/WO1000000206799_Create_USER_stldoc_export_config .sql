create table USER_stldoc_export_config
(
internal_lentity nvarchar(255),
file_name_part nvarchar(255)
)

grant select, insert, update, delete on USER_stldoc_export_config to olf_user

grant select on USER_stldoc_export_config to olf_readonly

