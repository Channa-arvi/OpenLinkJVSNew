package com.garinternal.fo.fieldnotification;

/*
File Name:                      GarFNPopulateSAPMaterialCode.java

Script Type:                    MAIN
Parameter Script:               None
Display Script:                 None

Description:
This script is field notification script to populate the Sap Materical code
based on the selected commodity subgroup .

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
WO1000000118549  |             |Channa Arvi | Initial Version
                 |             |            |
---------------------------------------------------------------------------
**/

import com.garinternal.basic.GarBasicScript;
import com.garinternal.common.enums.GAR_TRAN_INFO_ENUM;
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
import com.olf.openjvs.enums.TRANF_FIELD;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_FIELD_NOTIFICATION)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)

public class GarFNPopulateSAPMaterialCode extends GarBasicScript {
    private GarLogger logger;

    /**
     *
     * Constructor
     *
     * @throws OException {@link OException}
     */
    public GarFNPopulateSAPMaterialCode() throws OException {

        super();
        this.logger = super.getLoggerInstance();
    }

    @Override
    public void execute(Table argt, Table returnt) throws OException {

        Table tblSap = Util.NULL_TABLE;

        try {
            Transaction tranPointer = argt.getTran("tran", 1);

            String commodity           = tranPointer.getField(TRANF_FIELD.TRANF_IDX_SUBGROUP.toInt(), 1, "Commodity Sub-Group", -1, -1);
            String grade               = tranPointer.getField(TRANF_FIELD.TRANF_MEASURE_GROUP.toInt(), 1, "Measure Group", -1, -1);
            int    commoditySubgroupId = this.getCommoditySubGroupId(commodity);

            int gradeId = this.getGradeId(grade);

            tblSap = this.getSapData(commoditySubgroupId, gradeId);

            this.setTranInfoValues(tblSap, tranPointer);

        } finally {
            GarSafe.destroy(tblSap);

        }

    }

    /**
     * Set TranInfo Values
     *
     * @param tblSap
     * @param tranPointer
     * @throws OException
     */
    private void setTranInfoValues(Table tblSap, Transaction tranPointer) throws OException {

        String sapMaterialCode = "";

        if (!GarHasAValue.isTableEmpty(tblSap)) {
            sapMaterialCode = tblSap.getString("name", 1);
            this.logger.info("sapMaterialCode" + sapMaterialCode);

            if (!GarHasAValue.hasAValue(sapMaterialCode, true)) {
                throw new OException("Invalid Sap Data");
            }

        } else {
            throw new OException("No Sap Data found");
        }

        // setting sales org, distribution channel and division tran info fields
        tranPointer.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, GAR_TRAN_INFO_ENUM.T_SAP_MATERIAL_CODE.toString(), sapMaterialCode, 0,
            0, -1, -1);

    }

    /**
     * Get Commodity Sub Group Id
     *
     * @param commodity
     * @return
     * @throws OException
     */
    private int getCommoditySubGroupId(String commodity) throws OException {
        int   commoditySubGroupID    = 0;
        Table tblCommoditySubgroupId = Table.tableNew();
        this.logger.info("inside the method");

        try {
            String sqlCommoditySugroupId = "\n SELECT idxs.id_number CommoditySubgroup" //
                + "\n FROM   idx_subgroup idxs" //
                + "\n WHERE idxs.name = '" + commodity + "'";

            String errorMsg = "Unable to find supplied commodity : " + commodity + "";
            GarStrict.query(tblCommoditySubgroupId, sqlCommoditySugroupId, errorMsg, this.logger);

            if (!GarHasAValue.isTableEmpty(tblCommoditySubgroupId)) {
                commoditySubGroupID = tblCommoditySubgroupId.getInt("CommoditySubgroup", 1);
            }

        } finally {
            GarSafe.destroy(tblCommoditySubgroupId);
        }

        return commoditySubGroupID;
    }

    /**
     * Get grade Id
     *
     * @param grade
     * @return
     * @throws OException
     */
    private int getGradeId(String grade) throws OException {
        int   gradeId             = 0;
        Table tblCommodityGradeId = Table.tableNew();

        try {
            String sqlCommodityGradeId = "\nSELECT mg.id_number grade" //
                + "\n FROM   measure_group mg" //
                + "\n WHERE  mg.name = '" + grade + "'";

            String errorMsg = "Unable to find supplied grade : " + grade + "";

            GarStrict.query(tblCommodityGradeId, sqlCommodityGradeId, errorMsg, this.logger);

            if (!GarHasAValue.isTableEmpty(tblCommodityGradeId)) {
                gradeId = tblCommodityGradeId.getInt("grade", 1);
                this.logger.info("gradeId---------->" + gradeId);
            }

        } finally {
            GarSafe.destroy(tblCommodityGradeId);
        }

        return gradeId;
    }

    /**
     * Get grade Id
     *
     * @param grade
     * @param commoditySubgroupId
     * @return
     * @throws OException
     */
    private Table getSapData(int commoditySubgroupId, int gradeId) throws OException {
        this.logger.info("Inside getSAPData");
        String sqlSAPMaterialCode = "\n SELECT   usmm.name, usmc.CommoditySubgroup " //
            + "\n FROM     " + GAR_USER_TABLE_ENUM.USER_SAP_MD_MATERIAL_MAPPING.toString() + " usmm " //
            + "\n JOIN     " + GAR_USER_TABLE_ENUM.USER_SAP_MATERIAL_CODE.toString() + " usmc ON usmm.id = usmc.SAPMaterialCode " //
            + "\n  AND     usmc.CommoditySubgroup = " + commoditySubgroupId //
            + "\n  AND     usmc.Grade = " + gradeId //
            + "\n ORDER BY usmc.last_modified_on DESC";

        String errorMsg = "Unable to find supplied commodity : " + commoditySubgroupId + " and grad id " + gradeId + "";

        return GarStrict.query(sqlSAPMaterialCode, errorMsg, this.logger);

    }
}
