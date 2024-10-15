package com.garinternal.ops.pre;

/*
File Name:                      GarOpsIndoPurchasesDataPrePopulate.java

Script Type:                    MAIN
Parameter Script:               None
Display Script:                 None

Description:
This script is an Operation service script  that populate the Partner PCA
based on the selected external/Internal Legal Entity, Project index and Zone. It will
check which corresponding partner pca based on the user table USER_SAP_MD_PARTNER_PCA table.

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
WO1000000118549  |             |Channa Arvi | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */
import com.garinternal.basic.GarBasicScript;
import com.garinternal.common.enums.GAR_TRAN_INFO_ENUM;
import com.garinternal.common.enums.GAR_USER_TABLE_ENUM;
import com.garinternal.common.util.GarHasAValue;
import com.garinternal.common.util.GarLogger;
import com.garinternal.common.util.GarSafe;
import com.garinternal.common.util.GarStrict;
import com.olf.openjvs.OException;
import com.olf.openjvs.OpService;
import com.olf.openjvs.Table;
import com.olf.openjvs.Transaction;
import com.olf.openjvs.enums.TRANF_FIELD;

public class GarOpsIndoSAPMaterialCodePrePopulate extends GarBasicScript {

    private GarLogger logger;

    /**
     * Constructor
     *
     * @throws OException {@link OException}
     */
    public GarOpsIndoSAPMaterialCodePrePopulate() throws OException {
        super();
        this.logger = super.getLoggerInstance();
    }

    @Override
    public void execute(Table argt, Table returnt) throws OException {

        Table tblSapMaterialCode              = Table.tableNew();
        Table tblCommoditySubgroupAndGradeIds = Table.tableNew();

        try {
            int numTrans = OpService.retrieveNumTrans();

            // Loop one time for each deal
            for (int tranCount = 1; tranCount <= numTrans; tranCount++) {
                this.logger.info("\nTransaction " + OpService.retrieveTran(tranCount));

                Transaction tran = OpService.retrieveOriginalTran(tranCount);
                this.logger.info("\nTransaction number " + tran.getTranNum());

                String commodity = tran.getField(TRANF_FIELD.TRANF_IDX_SUBGROUP.toInt(), 1, "", -1, -1);
                String grade     = tran.getField(TRANF_FIELD.TRANF_MEASURE_GROUP.toInt(), 1, "", -1, -1);
                this.logger.info("Commodity ---->" + commodity);
                this.logger.info("Grade ---->" + grade);

                this.populateCommoditySubGroupAndGrade(tblCommoditySubgroupAndGradeIds, commodity, grade);

                int commoditySubgroupId = tblCommoditySubgroupAndGradeIds.getInt("commoditysubgroup", 1);
                this.logger.info(" CommoditySubgroupId" + commoditySubgroupId);
                int gradeId = tblCommoditySubgroupAndGradeIds.getInt("grade", 1);
                this.logger.info("GradeId" + gradeId);

                this.populateSapMaterialcode(tblSapMaterialCode, commoditySubgroupId, gradeId);
                String errorMsg = "Unable to find an CommoditySubgroup value with supplied selected CommoditySubgroupId and gradeId: ";
                String sapMaterialCode;

                if (!GarHasAValue.isTableEmpty(tblSapMaterialCode)) {
                    sapMaterialCode = tblSapMaterialCode.getString("sap_material_code", 1);

                    if (!GarHasAValue.hasAValue(sapMaterialCode, true)) {
                        throw new OException(errorMsg);
                    }

                } else {
                    throw new OException(errorMsg);
                }

                tran.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, GAR_TRAN_INFO_ENUM.T_SAP_MATERIAL_CODE.toString(), sapMaterialCode, 0,
                    0, -1, -1);

                this.logger.info("\nCommodity= \t" + commodity);
                this.logger.info("\nGrade \t" + grade);
                this.logger.info("\nSAPMaterialCode\t" + sapMaterialCode);

                tblCommoditySubgroupAndGradeIds.clearRows();
                tblSapMaterialCode.clearRows();

            }

        } finally {
            GarSafe.destroy(tblCommoditySubgroupAndGradeIds, tblSapMaterialCode);
        }

    }

    /**
     * Get SAP Material Code
     *
     * @param tblSapMaterialCode
     * @param commoditySubgroupId
     * @param gradeId
     * @throws OException
     */
    private void populateSapMaterialcode(Table tblSapMaterialCode, int commoditySubgroupId, int gradeId) throws OException {
        String sqlSAPMaterialCode = "\n SELECT usmm.name AS sap_material_code" //
            + "\n FROM     " + GAR_USER_TABLE_ENUM.USER_SAP_MD_MATERIAL_MAPPING.toString() + " usmm " //
            + "\n JOIN     " + GAR_USER_TABLE_ENUM.USER_SAP_MATERIAL_CODE.toString() + " usmc ON usmm.id = usmc.SAPMaterialCode " //
            + "\n  AND     usmc.CommoditySubgroup = " + commoditySubgroupId //
            + "\n  AND     usmc.Grade = " + gradeId //
            + "\n ORDER BY usmc.last_modified_on DESC";

        this.logger.info("\n sqlSAPMaterialCode = \t" + sqlSAPMaterialCode);

        String errorMsg = "Unable to find an CommoditySubgroup value with supplied selected CommoditySubgroupId and gradeId: ";

        GarStrict.query(tblSapMaterialCode, sqlSAPMaterialCode, errorMsg, this.logger);
    }

    /**
     * Populate Commodity sub group and Grade id in the table
     *
     * @param tblCommoditySubgroupAndGradeIds
     * @param commodity
     * @param grade
     * @throws OException
     */
    private void populateCommoditySubGroupAndGrade(Table tblCommoditySubgroupAndGradeIds, String commodity, String grade)
        throws OException {
        String sql = "\n SELECT idxs.id_number commoditysubgroup, mg.id_number grade  " //
            + "\n FROM   idx_subgroup idxs , measure_group mg " //
            + "\n WHERE  idxs.id_number = mg.idx_subgroup " //
            + "\n   AND  idxs.name = '" + commodity + "'" //
            + "\n   AND  mg.name = '" + grade + "'";

        String errorMsg = "Unable to find commoditysubgroup and grade for selected commodity and grade " + commodity + " " + grade;

        GarStrict.query(tblCommoditySubgroupAndGradeIds, sql, errorMsg, this.logger);
    }
}
