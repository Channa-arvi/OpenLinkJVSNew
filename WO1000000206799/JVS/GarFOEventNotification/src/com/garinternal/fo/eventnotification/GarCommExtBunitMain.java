package com.garinternal.fo.eventnotification;

/*
File Name:                      GarCommExtBunitMain.java

Script Type:                    MAIN
Parameter Script:               None
Display Script:                 None

Description:
This script is an Instrument Builder's Event Notifications script that overrides
external business unit list based on the selected Internal Legal Entity. It will
check which corresponding business unit based on the user table USER_filter_phys_extbu table.

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000120464 |             | Willyam    | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import com.garinternal.basic.GarBasicScript;
import com.garinternal.common.enums.GAR_USER_TABLE_ENUM;
import com.garinternal.common.util.GarHasAValue;
import com.garinternal.common.util.GarLogger;
import com.garinternal.common.util.GarSafe;
import com.garinternal.common.util.GarStrict;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.Transaction;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.SHM_USR_TABLES_ENUM;
import com.olf.openjvs.enums.TRANF_FIELD;
import com.olf.openjvs.fnd.RefBase;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_EVENT_NOTIFICATION)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)

public class GarCommExtBunitMain extends GarBasicScript {
    private GarLogger logger;

    /**
     *
     * Constructor
     *
     * @throws OException {@link OException}
     */
    public GarCommExtBunitMain() throws OException {
        super();
        this.logger = super.getLoggerInstance();
    }

    @Override
    public void execute(Table argt, Table returnt) throws OException {

        if (GarHasAValue.isTableEmpty(argt)) {
            this.logger.info("Either the param script failed or was not set in the task");
            return;
        }

        Table       tblList  = Util.NULL_TABLE;
        Table       tblExtbu = Util.NULL_TABLE;
        Transaction tran;

        try {
            // Get current transaction number
            tran = argt.getTran("tran", 1);

            // Get current selected Internal Legal Entity
            String selectedInternalLE = tran.getField(TRANF_FIELD.TRANF_INTERNAL_LENTITY);

            // Get the selected Internal Legal Entity Party ID
            int selectedInternalLECode = RefBase.getValue(SHM_USR_TABLES_ENUM.PARTY_TABLE, selectedInternalLE);

            if (selectedInternalLECode <= 0) {
                String errorMsg = "Unable to find a party_id value with supplied party short name: " + selectedInternalLE + "";
                throw new OException(errorMsg);
            }

            // Check if selected internal LE's Party Id exists in USER_filter_phys_extbu
            boolean hasCustomBU = this.hasCustomBU(selectedInternalLECode);

            // Check if event is Option Field (16) and selected Internal LE has custom BU list
            if (hasCustomBU) {
                // Get list of ext BU and its number of rows for selected internal LE Code
                tblExtbu = this.getExtBUlistTbl(selectedInternalLECode);

                // Get list of complete ext BU from return table col 2 row 1
                tblList = Table.tableNew();
                tblList = returnt.getTable("table_value", 1);

                GarStrict.select(tblList, tblExtbu, "id(filter_col)", "id EQ $id");
                tblList.deleteWhereValue("filter_col", 0);
                tblList.delCol("filter_col");

                argt.select(tblList, "DISTINCT, *", "id GT 0");
            }

        } finally {
            GarSafe.destroy(tblList, tblExtbu);

        }

    }

    /**
     * Get distinct number of row of a given int legal entity id.
     *
     * @param intLECode Internal Legal Entity id
     * @return
     * @throws OException
     */
    private boolean hasCustomBU(int intLECode) throws OException {
        Table   tblIndexName = Util.NULL_TABLE;
        boolean hasCustomBU  = false;

        try {
            String sql = "\n SELECT DISTINCT int_lentity " //
                + "\n FROM   " + GAR_USER_TABLE_ENUM.USER_FILTER_PHYS_EXTBU.toString() //
                + "\n WHERE int_lentity = '" + intLECode + "'";//

            String errorMsg = "Unable to find supplied internal entity in USER_filter_phys_extbu: " + intLECode + "";

            tblIndexName = GarStrict.query(sql, errorMsg, this.logger);

            if (!GarHasAValue.isTableEmpty(tblIndexName)) {
                hasCustomBU = tblIndexName.getNumRows() > 0;

                if (!hasCustomBU) {
                    throw new OException(errorMsg);
                }

            } else {
                throw new OException(errorMsg);
            }

        } finally {
            GarSafe.destroy(tblIndexName);
        }

        return hasCustomBU;
    }

    /**
     * Return a table with ext_bunit from USER_filter_phys_extbu table based on internal legal entity id
     *
     * @param intLECode Internal Legal Entity id
     * @return
     * @throws OException
     */
    private Table getExtBUlistTbl(int intLECode) throws OException {
        String sql = "\n SELECT ext_bunit " //
            + "\n FROM   " + GAR_USER_TABLE_ENUM.USER_FILTER_PHYS_EXTBU.toString() //
            + "\n WHERE int_lentity = '" + intLECode + "'";//

        String errorMsg =
            "Unable to find supplied internal entity in " + GAR_USER_TABLE_ENUM.USER_FILTER_PHYS_EXTBU.toString() + ": " + intLECode + "";

        Table tblExtBu = GarStrict.query(sql, errorMsg, this.logger);

        if (GarHasAValue.isTableEmpty(tblExtBu)) {
            throw new OException(errorMsg);
        }

        return tblExtBu;

    }

}
