package com.garinternal.stl.export;

/*
File Name:                      GarOutputDmstoFileSystemOutput.java

Script Type:                    OUTPUT
Parameter Script:               None
Display Script:                 None

Description:
This is an output script to show the status of exported documents on the Settlement Desktop screen to the File System

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000206799 |             | Khalique   | Initial Version
---------------------------------------------------------------------------
 */

import com.garinternal.basic.GarBasicScript;
import com.garinternal.common.util.GarHasAValue;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.Ask;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.OUTPUT_SCRIPT)

public class GarOutputDmstoFileSystemOutput extends GarBasicScript {

    /**
     * Constructor
     *
     * @throws OException {@link OException}
     */
    public GarOutputDmstoFileSystemOutput() throws OException {
        super();
    }

    @Override
    public void execute(Table argt, Table returnt) throws Exception {
        Table  tblResults = argt.getTable("status_table", 1);
        String message    = argt.getString("status_message", 1);

        if (!GarHasAValue.isTableEmpty(tblResults)) {
            tblResults.viewTable();
        }

        if (GarHasAValue.hasAValue(message, true)) {
            Ask.ok(message);
        }

    }
}
