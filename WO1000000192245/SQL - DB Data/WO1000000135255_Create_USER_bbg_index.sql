create table USER_bbg_index
(
id_number int IDENTITY(1,1)
, index_name nvarchar(255) NOT NULL PRIMARY KEY
, endur_index_name nvarchar(255)  NOT NULL
, ticker_code nvarchar(255) NOT NULL
, row_change_reason nvarchar(255)  NOT NULL
, TimeStart datetime2 (2) GENERATED ALWAYS AS ROW START
, TimeEnd datetime2 (2) GENERATED ALWAYS AS ROW END
, PERIOD FOR SYSTEM_TIME (TimeStart, TimeEnd)  
 )
WITH (SYSTEM_VERSIONING = ON (HISTORY_TABLE = dbo.USER_bbg_index_hist));

grant select, insert, update, delete on USER_bbg_index to olf_user;
grant select on USER_bbg_index to olf_readonly;

