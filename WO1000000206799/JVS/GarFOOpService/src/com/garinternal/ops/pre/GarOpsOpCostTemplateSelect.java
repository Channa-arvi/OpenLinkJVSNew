package com.garinternal.ops.pre;

/*
File Name:                      GarOpsOpCostTemplateSelect.java

Script Type:                    MAIN
Parameter Script:               None
Display Script:                 None

Description:
This script describes to select particular op cost template based on Lentity, index subgroup and the incoterms as well.

---------------------------------------------------------------------------
 REQ No          | Release Date| Author           | Changes
---------------------------------------------------------------------------
 WO1000000202785 |             | Channa Arvi      | initial
---------------------------------------------------------------------------
 */

import com.garinternal.basic.GarBasicScript;
import com.garinternal.common.enums.GAR_OP_COST_TEMPLATES_STATUS;
import com.garinternal.common.enums.GAR_TRAN_INFO_ENUM;
import com.garinternal.common.enums.GAR_USER_TABLE_ENUM;
import com.garinternal.common.util.GarHasAValue;
import com.garinternal.common.util.GarLogger;
import com.garinternal.common.util.GarSafe;
import com.garinternal.common.util.GarStrict;
import com.olf.openjvs.OException;
import com.olf.openjvs.OpService;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.Transaction;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.TRANF_FIELD;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_OPS_SERVICE)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)

public class GarOpsOpCostTemplateSelect extends GarBasicScript {
    private GarLogger logger;

    /**
     * Constructor
     *
     * @throws OException {@link OException}
     */
    public GarOpsOpCostTemplateSelect() throws OException {
        super();
        this.logger = super.getLoggerInstance();
    }

    @Override
    public void execute(Table argt, Table returnt) throws OException {

        int totalTrans = OpService.retrieveNumTrans();

        // Loop one time for each deal
        for (int tranCount = 1; tranCount <= totalTrans; tranCount++) {
            // Get a pointer to the Instrument
            Transaction tranPointer         = OpService.retrieveOriginalTran(tranCount);
            int         intLntity           = tranPointer.getFieldInt(TRANF_FIELD.TRANF_INTERNAL_LENTITY.toInt(), 0, "int_lentity", 0, 0);
            int         indexId             = tranPointer.getFieldInt(TRANF_FIELD.TRANF_PROJ_INDEX, 1, "Proj Index", -1, -1);
            int         incoTerms           =
                tranPointer.getFieldInt(TRANF_FIELD.TRANF_DELIVERY_TERM_INCOTERM.toInt(), 1, "incoterms", -1, -1);
            int         intCommoditySugroup = tranPointer.getFieldInt(TRANF_FIELD.TRANF_IDX_SUBGROUP, 1, "Commodity Sub-Group", -1, -1);
            this.logger.info("intCommoditySugroup" + intCommoditySugroup);
            // getting idx_subgroup id based on projection index
            int idxSubgroupId = this.getIdxSubGroupId(indexId);
            this.logger.info("idxSubgroupId" + idxSubgroupId);

            String opCostTemplateName = this.getOpsCostTemplateName(intLntity, intCommoditySugroup, incoTerms);

            this.setTranInfoValue(opCostTemplateName, tranPointer, GAR_TRAN_INFO_ENUM.T_OPERATION_COST_TEMPLATE);

        }

    }

    /**
     * Set Tran Info Values
     *
     * @param tranInfoValue
     * @param tranPointer
     * @param tranInfoEnum
     * @throws OException {@link OExcpetion}
     */
    private void setTranInfoValue(String tranInfoValue, Transaction tranPointer, GAR_TRAN_INFO_ENUM tranInfoEnum) throws OException {

        this.logger.info("Value for " + tranInfoEnum.toString() + ": " + tranInfoValue);

        tranPointer.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, GAR_TRAN_INFO_ENUM.T_OPERATION_COST_TEMPLATE.toString(), tranInfoValue,
            0, 0, -1, -1);

    }

    /**
     *
     * Get Ops Cost Template name
     *
     * @param intLntity
     * @param idxSubgroupId
     * @param incoTerms
     * @return
     * @throws OException
     */
    private String getOpsCostTemplateName(int intLntity, int idxSubgroupId, int incoTerms) throws OException {

        String opCostTemplateName = "";
        Table  tblOpCostTemplate  = Table.tableNew();

        try {
            String sqlOpCostTemplate = "SELECT uop.template_name AS template_name"//
                + " FROM " + GAR_USER_TABLE_ENUM.USER_OPERATIONCOST_TEMPLATES + " uop " //
                + " WHERE uop.status <> '" + GAR_OP_COST_TEMPLATES_STATUS.DELETED.toString() + "'" //
                + " AND uop.legal_entity_id = " + intLntity//
                + " AND uop.idx_subgroup = " + idxSubgroupId//
                + " AND uop.delivery_term_incoterm = " + incoTerms;//

            String errorMsg = "Unable to find opCostTemplateName for Lentity Name = " + intLntity + ", Index Subgroup Id = " + idxSubgroupId
                + ", delivery term incoterm  = " + incoTerms;

            GarStrict.query(tblOpCostTemplate, sqlOpCostTemplate, errorMsg, this.logger);

            if (!GarHasAValue.isTableEmpty(tblOpCostTemplate)) {
                opCostTemplateName = tblOpCostTemplate.getString("template_name", 1);
            }

        } finally {
            GarSafe.destroy(tblOpCostTemplate);
        }

        return opCostTemplateName;
    }

    /**
     * Get Index Sub Group Id
     *
     * @param indexId
     * @return
     * @throws OException
     */
    private int getIdxSubGroupId(int indexId) throws OException {
        int   idxSubGroupId  = 0;
        Table tblIdxSubGroup = Table.tableNew();

        try {
            String sqlQueryIdxSubgroup = "" //
                + "\n SELECT idx_subgroup" //
                + "\n FROM   idx_def" //
                + "\n WHERE  index_id = " + indexId;

            String errorMsg = "Unable to find idx_subgroup for selected index id";

            GarStrict.query(tblIdxSubGroup, sqlQueryIdxSubgroup, errorMsg, this.logger);

            if (!GarHasAValue.isTableEmpty(tblIdxSubGroup)) {
                idxSubGroupId = tblIdxSubGroup.getInt("idx_subgroup", 1);
            }

        } finally {
            GarSafe.destroy(tblIdxSubGroup);
        }

        return idxSubGroupId;
    }

}
