package com.garinternal.common.enums.util;

/*
File Name:                      GarEnumGeneratorParam.java

Script Type:                    PARAM
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

import com.garinternal.basic.GarBasicScript;
import com.garinternal.common.util.GarSafe;
import com.olf.openjvs.Ask;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.enums.ASK_TEXT_DATA_TYPES;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.PARAM_SCRIPT)

public class GarEnumGeneratorParam extends GarBasicScript {

    /**
     * Constructor
     * 
     * @throws OException {@link OException}
     */
    public GarEnumGeneratorParam() throws OException {
        super();
    }

    @Override
    public void execute(Table argt, Table returnt) throws Exception {
        Table tblAsk = Table.tableNew();

        try {
            boolean retry = true;

            while (retry) {
                tblAsk.clearRows();
                Ask.setTextEdit(tblAsk, "Table Name:", "", ASK_TEXT_DATA_TYPES.ASK_STRING);
                Ask.setTextEdit(tblAsk, "Col Name for Enum Name:", "", ASK_TEXT_DATA_TYPES.ASK_STRING);
                Ask.setTextEdit(tblAsk, "Other Col Names(Comma Separated):", "", ASK_TEXT_DATA_TYPES.ASK_STRING);

                int retVal = Ask.viewTable(tblAsk, "Enum Creator", "Input DB Values to create enum data");

                if (retVal == 0) {
                    throw new OException("Exit by User");
                }

                final int tableNameRowNum     = 1;
                final int nameColNameRowNum   = 2;
                final int stringColNameRowNum = 3;

                String tableName    = tblAsk.getTable("return_value", tableNameRowNum).getString("ted_str_value", 1);
                String nameColName  = tblAsk.getTable("return_value", nameColNameRowNum).getString("ted_str_value", 1);
                String otherColName = tblAsk.getTable("return_value", stringColNameRowNum).getString("ted_str_value", 1);

                this.setValuesToArgt(argt, tableName, nameColName, otherColName);
                retry = false;
            }

        } finally {
            GarSafe.destroy(tblAsk);
        }

    }

    /**
     * Set Values to argt
     * 
     * @param argt         Argt
     * @param tableName    DB Table Name
     * @param nameColName  DB Name Column Name
     * @param otherColName DB Other Column Name
     * @throws OException
     */
    private void setValuesToArgt(Table argt, String tableName, String nameColName, String otherColName) throws OException {
        argt.addCols("S(table_name) S(name_col_name) S(other_col_name)");

        int rowNum = argt.addRow();
        argt.setString("table_name", rowNum, tableName);
        argt.setString("name_col_name", rowNum, nameColName);
        argt.setString("other_col_name", rowNum, otherColName);
    }
}
