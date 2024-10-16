package com.garinternal.common.enums.util;

/*
File Name:                      GarEnumGeneratorMain.java

Script Type:                    MAIN
Parameter Script:               None
Display Script:                 None

Description:
This script is used to generated enum body for the enums representing DB static data

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import java.util.Arrays;
import java.util.List;

import com.garinternal.basic.GarBasicScript;
import com.garinternal.common.util.GarHasAValue;
import com.garinternal.common.util.GarLogger;
import com.garinternal.common.util.GarSafe;
import com.garinternal.common.util.GarStrict;
import com.olf.openjvs.Ask;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.SEARCH_CASE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)

public class GarEnumGeneratorMain extends GarBasicScript {

    private GarLogger logger;

    /**
     * Constructor
     *
     * @throws OException {@link OException}
     */
    public GarEnumGeneratorMain() throws OException {
        super();
        this.logger = super.getLoggerInstance();
    }

    @Override
    public void execute(Table argt, Table returnt) throws Exception {

        if (GarHasAValue.isTableEmpty(argt)) {
            throw new OException("Script " + this.getClass().getName() + " is meant to be run with a param script");
        }

        Table tblData    = Table.tableNew();
        Table tblColType = Table.tableNew();

        try {
            String tableName = argt.getString("table_name", 1);

            StringBuilder sqlColTypes =
                new StringBuilder().append("\n     SELECT     t.name table_name, c.name column_name, y.name col_type") //
                    .append("\n              , IIF(y.name IN('char', 'nchar', 'ntext', 'nvarchar', 'text', 'varchar'), 1, 0)")//
                    .append("\n                   AS is_col_type_str") //
                    .append("\n     FROM       sys.tables t ") //
                    .append("\n     INNER JOIN sys.columns c ON t.object_id = c.object_id") //
                    .append("\n     INNER JOIN sys.types y ON y.user_type_id = c.user_type_id") //
                    .append("\n     WHERE      t.name = '" + tableName + "'");

            GarStrict.query(tblColType, sqlColTypes.toString(), this.logger);

            if (GarHasAValue.isTableEmpty(tblColType)) {
                String msg = "Could not get column type data from database";

                Ask.ok(msg);
                throw new OException(msg);
            }

            String otherColNames    = argt.getString("other_col_name", 1);
            String nameColName      = argt.getString("name_col_name", 1);
            String nameColNameAlias = nameColName + "_alias";

            StringBuilder sqlInitDataSelect = new StringBuilder().append("\n     SELECT UPPER(REPLACE(") //
                .append("\n                          REPLACE(") //
                .append("\n                                  REPLACE(") //
                .append("\n                                         REPLACE(" + nameColName + ", ' ', '_')") //
                .append("\n                                , '-', '_')") //
                .append("\n                        , '/', '_')") //
                .append("\n                 , '___', '_')) AS " + nameColNameAlias);

            StringBuilder lengthSelect = new StringBuilder() //
                .append("\n     SELECT MAX(LEN(" + nameColNameAlias + ")) len_" + nameColNameAlias);

            StringBuilder finalSelect =
                new StringBuilder().append("\n SELECT CONCAT(t." + nameColNameAlias + ", SPACE(2 + l.len_" + nameColNameAlias //
                    + " - LEN(t." + nameColNameAlias + ")), '('");

            List<String> extraColNames = Arrays.asList(otherColNames.split(","));
            int          len           = extraColNames.size();
            int          count         = 0;

            for (String extraColName : extraColNames) {
                count++;

                if (GarHasAValue.hasAValue(extraColName, true)) {
                    extraColName = extraColName.trim();
                    sqlInitDataSelect.append("," + extraColName);
                    lengthSelect.append(", MAX(LEN(" + extraColName + ")) AS len_" + extraColName);

                    int rowNum = tblColType.unsortedFindString("column_name", extraColName, SEARCH_CASE_ENUM.CASE_INSENSITIVE);

                    if (rowNum > 0 && tblColType.getInt("is_col_type_str", rowNum) == 1) {
                        finalSelect.append(", CONCAT('\"', t." + extraColName + ", '\"')");
                    } else {
                        finalSelect.append(", t." + extraColName);
                    }

                    if (count < len) {
                        finalSelect.append("\n      , ',', SPACE(2 + l.len_" + extraColName + " - LEN(t." + extraColName + "))");
                    }

                }

            }

            finalSelect.append(", '),') AS final_str");

            StringBuilder sql = new StringBuilder().append("\n WITH initial_data AS (").append(sqlInitDataSelect)
                .append("\n     FROM   " + tableName).append("\n )").append("\n , col_types AS (").append(sqlColTypes).append("\n )")
                .append("\n , length AS (").append(lengthSelect).append("\n     FROM   initial_data").append("\n )").append("\n ")
                .append(finalSelect).append("\n FROM   initial_data t, length l");

            GarStrict.query(tblData, sql.toString(), this.logger);

            tblData.viewTable();
        } finally {
            GarSafe.destroy(tblData, tblColType);
        }

    }
}
