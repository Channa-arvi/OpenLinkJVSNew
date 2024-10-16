package com.garinternal.mo.bbg.priceimport;

/*
File Name:                      GarMOBloombergPriceImportParam.java

Script Type:                    PARAM
Parameter Script:               None
Display Script:                 None

Description:
This is param script to import prices from Bloomberg Price file in excel format

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import java.io.File;

import com.garinternal.basic.GarBasicScript;
import com.garinternal.common.enums.GAR_FILE_EXTENSION_ENUM;
import com.garinternal.common.util.GarSafe;
import com.olf.openjvs.Ask;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.enums.ASK_SELECT_TYPES;
import com.olf.openjvs.enums.ASK_TEXT_DATA_TYPES;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.PARAM_SCRIPT)

public final class GarMOBloombergPriceImportParam extends GarBasicScript {

    /**
     * Constructor
     *
     * @throws OException {@link OException}
     */
    public GarMOBloombergPriceImportParam() throws OException {
        super();
    }

    @Override
    public void execute(Table argt, Table returnt) throws OException {
        Table tblAsk       = Table.tableNew();
        Table tblSelection = Table.tableNew();

        try {
            boolean retry = true;

            while (retry) {
                this.getUserSelectionTable(tblSelection);
                tblAsk.clearRows();
                Ask.setTextEdit(tblAsk, "File Path", "", ASK_TEXT_DATA_TYPES.ASK_FILENAME);
                Ask.setAvsTable(tblAsk, tblSelection, "Select Action", tblSelection.getColNum("action"),
                    ASK_SELECT_TYPES.ASK_SINGLE_SELECT.toInt(), tblSelection.getColNum("s_no"));

                int retVal = Ask.viewTable(tblAsk, "Bloomberg Price Import", "Select Bloomberg Price Import File");

                if (retVal == 0) {
                    throw new OException("Exit by User");
                }

                String path = tblAsk.getTable("return_value", 1).getString("ted_str_value", 1);

                boolean isFileValid    = true;
                String  fileInvalidMsg = "";
                String  allowedFileExt = GAR_FILE_EXTENSION_ENUM.XLSX.toString();

                if (null == path || path.trim().isEmpty() || ! (new File(path).exists())) {
                    isFileValid    = false;
                    fileInvalidMsg = "Invalid file path selected.\nPress Ok to re-select or Cancel to exit";
                } else if (!path.toLowerCase().endsWith("." + allowedFileExt)) {
                    isFileValid    = false;
                    fileInvalidMsg = "Only ." + allowedFileExt + " files are allowed.\nPress Ok to re-select or Cancel to exit";
                }

                if (!isFileValid) {
                    retVal = Ask.okCancel(fileInvalidMsg);

                    if (retVal == 0) {
                        throw new OException("Exit by User");
                    } else {
                        continue;
                    }

                }

                final int rowNumUserSelection = 2;
                int       selection           = tblAsk.getTable("return_value", rowNumUserSelection).getInt("return_value", 1);
                this.setValuesToArgt(argt, path, selection);
                retry = false;
            }

        } finally {
            GarSafe.destroy(tblAsk, tblSelection);
        }

    }

    /**
     * Get User Selection Table
     *
     * @param tblSelection Table to select action from
     * @throws OException
     */
    private void getUserSelectionTable(Table tblSelection) throws OException {
        tblSelection.addCol("s_no", COL_TYPE_ENUM.COL_INT);
        tblSelection.addCol("action", COL_TYPE_ENUM.COL_STRING);

        int rowNum = tblSelection.addRow();
        tblSelection.setString("action", rowNum, "Upload Prices");
        tblSelection.setInt("s_no", rowNum, GAR_MO_BBG_USER_SELECTION.IMPORT_PRICES.toInt());

        rowNum = tblSelection.addRow();
        tblSelection.setString("action", rowNum, "View Prices to be uploaded");
        tblSelection.setInt("s_no", rowNum, GAR_MO_BBG_USER_SELECTION.GENERATE_REPORT.toInt());
    }

    /**
     * Set Values to be sent to main script
     *
     * @param argt      Argt
     * @param path      File Path to set
     * @param selection User selection
     * @throws OException {@link OException}
     */
    private void setValuesToArgt(Table argt, String path, int selection) throws OException {
        argt.addCol("file_path", COL_TYPE_ENUM.COL_STRING);
        argt.addCol("user_selection", COL_TYPE_ENUM.COL_INT);
        argt.addRow();

        argt.setString("file_path", 1, path);
        argt.setInt("user_selection", 1, selection);
    }
}
