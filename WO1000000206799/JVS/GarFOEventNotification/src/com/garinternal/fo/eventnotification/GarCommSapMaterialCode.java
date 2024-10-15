package com.garinternal.fo.eventnotification;

/*
File Name:                      GarCommSapMaterialCode.java

Script Type:                    MAIN
Parameter Script:               None
Display Script:                 None

Description:
This script is an event notification script  that populate sap material code in the drop down
based on the selected commodity subgroup.

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
WO1000000118549  |             |Channa Arvi | Initial Version
                 |             |            |
---------------------------------------------------------------------------
**/

import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.Transaction;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.TRANF_FIELD;

import com.garinternal.basic.GarBasicScript;
import com.garinternal.common.enums.GAR_SAP_SYSTEM_TYPE;
import com.garinternal.common.enums.GAR_USER_TABLE_ENUM;
import com.garinternal.common.util.GarLogger;
import com.garinternal.common.util.GarSafe;
import com.garinternal.common.util.GarStrict;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_EVENT_NOTIFICATION)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)

public class GarCommSapMaterialCode extends GarBasicScript {

    private GarLogger logger;

    /**
     *
     * Constructor
     *
     * @throws OException {@link OException}
     */
    public GarCommSapMaterialCode() throws OException {
        super();
        this.logger = super.getLoggerInstance();
    }

    @Override
    public void execute(Table argt, Table returnt) throws OException {

        Table sapMaterialCodeTblList = Util.NULL_TABLE;
        sapMaterialCodeTblList = Table.tableNew();

        try {
            Transaction tranPointer = argt.getTran("tran", 1);
            this.logger.info("Tran number" + tranPointer);

            // Get current selected Idx_subgroup
            String commoditySubgroup = tranPointer.getField(TRANF_FIELD.TRANF_IDX_SUBGROUP.toInt(), 1, "Commodity Sub-Group", -1, -1);
            this.logger.info("commoditySubgroup" + commoditySubgroup);
            sapMaterialCodeTblList = this.getSapMaterialCodeList(commoditySubgroup);

            int numrows = sapMaterialCodeTblList.getNumRows();
            this.logger.info("No of Rows: " + numrows);

            if (numrows > 0) {
                returnt.setTable("table_value", 1, sapMaterialCodeTblList);
            }

        } finally {
            GarSafe.destroy(sapMaterialCodeTblList);
        }

    }

    /**
     * Get SAP Material Code List Data
     *
     * @param commoditySubgroup
     * @return
     * @throws OException
     */
    private Table getSapMaterialCodeList(String commoditySubgroup) throws OException {
        String sqlSapMaterialCodeIds = "\n SELECT  id, name AS label " //
            + "\n FROM    " + GAR_USER_TABLE_ENUM.USER_SAP_MD_MATERIAL_MAPPING.toString() //
            + "\n WHERE  type = '" + GAR_SAP_SYSTEM_TYPE.MD.toString() + "'" //
            + "\n   AND  idx_subgroup_name = '" + commoditySubgroup + "'";//

        String errorMsg = "Unable to find a id value with supplied selected Commodity Subgroup: ";
        this.logger.info("sqlSapMaterialCodeIds" + sqlSapMaterialCodeIds);
        return GarStrict.query(sqlSapMaterialCodeIds, errorMsg, this.logger);
    }

}
