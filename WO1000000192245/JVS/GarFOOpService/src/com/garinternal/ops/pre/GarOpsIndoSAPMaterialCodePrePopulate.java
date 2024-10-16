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
import com.garinternal.common.util.GarHasAValue;
import com.garinternal.common.util.GarLogger;
import com.garinternal.common.util.GarSafe;
import com.garinternal.common.util.GarStrict;
import com.olf.openjvs.OConsole;
import com.olf.openjvs.OException;
import com.olf.openjvs.OpService;
import com.olf.openjvs.Table;
import com.olf.openjvs.Transaction;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.TRANF_FIELD;

public class GarOpsIndoSAPMaterialCodePrePopulate extends GarBasicScript {

    private GarLogger logger;

    public GarOpsIndoSAPMaterialCodePrePopulate() throws OException {
        super();
        this.logger = super.getLoggerInstance();
    }

    @Override
    public void execute(Table argt, Table returnt) throws OException {

        Transaction transaction_pointer;
        String      commodity, grade, sqlSAPMaterialCode = "", sqCommoditySubgroupAndGradeIds = "";
        String      SAPMaterialCode;
        String      errorMsg                                = "";
        Table       temp_SapMaterialCode_table              = Util.NULL_TABLE;
        Table       temp_CommoditySubgroupAndGradeIds_table = Util.NULL_TABLE;

        /* Loop one time for each deal */
        for (int i = 1; i <= OpService.retrieveNumTrans(); i++) {
            OConsole.oprint("\nTransaction " + OpService.retrieveTran(i));
            transaction_pointer = OpService.retrieveOriginalTran(i);
            OConsole.oprint("\nTransaction number " + transaction_pointer.getTranNum());
            temp_SapMaterialCode_table              = Table.tableNew();
            temp_CommoditySubgroupAndGradeIds_table = Table.tableNew();

            commodity = transaction_pointer.getField(TRANF_FIELD.TRANF_IDX_SUBGROUP.toInt(), 1, "", -1, -1);
            grade     = transaction_pointer.getField(TRANF_FIELD.TRANF_MEASURE_GROUP.toInt(), 1, "", -1, -1);
            this.logger.info("commodity ---->" + commodity);
            OConsole.oprint("grade ---->" + grade);
            /*
             * sqlSAPMaterialCode = "SELECT usmm.name name " +
             * " FROM USER_SAP_md_material_mapping usmm " +
             * " JOIN USER_SAP_MATERIAL_CODE usmc ON usmc.commoditySubgroup = usmm.idx_subgroup_id AND usmc.SAPMaterialCode = usmm.id "
             * +
             * " JOIN idx_subgroup idxs ON idxs.id_number = usmm.idx_subgroup_id AND idxs.name = '"
             * +commodity+"'" +
             * " JOIN measure_group mg ON mg.id_number = usmc.Grade AND mg.name = '"+ grade
             * +"'";
             */

            /*
             * sqlSAPMaterialCode = "SELECT usmm.idx_subgroup_name name1 , usmm.name name2 "
             * + "  FROM USER_SAP_md_material_mapping usmm" +
             * "  JOIN USER_SAP_MATERIAL_CODE usmc ON  usmc.sapmaterialcode = usmm.id  " +
             * "  JOIN idx_subgroup idxs ON idxs.id_number = usmm.idx_subgroup_id and idxs.name = 'CPO'"
             * ;
             */

            sqCommoditySubgroupAndGradeIds = "SELECT idxs.id_number commoditysubgroup, mg.id_number grade  "
                + " FROM idx_subgroup idxs , measure_group mg " + " WHERE idxs.id_number = mg.idx_subgroup " + " AND  idxs.name = '"
                + commodity + "'" + " AND  mg.name = '" + grade + "'";

            errorMsg = "Unable to find commoditysubgroup and grade for selected commodity and grade" + commodity + " " + grade;

            GarStrict.query(temp_CommoditySubgroupAndGradeIds_table, sqCommoditySubgroupAndGradeIds, errorMsg, this.logger);

            int CommoditySubgroupId = temp_CommoditySubgroupAndGradeIds_table.getInt("commoditysubgroup", 1);
            OConsole.oprint(" CommoditySubgroupId" + CommoditySubgroupId);
            int GradeId = temp_CommoditySubgroupAndGradeIds_table.getInt("grade", 1);
            OConsole.oprint("GradeId" + GradeId);

            sqlSAPMaterialCode = "SELECT * from user_sap_md_material_mapping usmm "
                + " JOIN USER_SAP_MATERIAL_CODE usmc on usmm.id = usmc.SAPMaterialCode " + " AND usmc.CommoditySubgroup = "
                + CommoditySubgroupId + " AND usmc.Grade = " + GradeId + " ORDER BY usmc.last_modified_on DESC";

            OConsole.oprint("\nsqlSAPMaterialCode = \t" + sqlSAPMaterialCode);

            errorMsg = "Unable to find an CommoditySubgroup value with supplied selected CommoditySubgroupId and gradeId: ";

            GarStrict.query(temp_SapMaterialCode_table, sqlSAPMaterialCode, errorMsg, this.logger);

            if (!GarHasAValue.isTableEmpty(temp_SapMaterialCode_table)) {
                SAPMaterialCode = temp_SapMaterialCode_table.getString("name", 1);

                if (SAPMaterialCode.equalsIgnoreCase("")) {
                    throw new OException(errorMsg);
                }

            } else {
                throw new OException(errorMsg);
            }

            transaction_pointer.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, "t_sap_material_code", SAPMaterialCode, 0, 0, -1, -1);

            OConsole.oprint("\nCommodity= \t" + commodity);
            OConsole.oprint("\nGrade \t" + grade);
            OConsole.oprint("\nSAPMaterialCode\t" + SAPMaterialCode);

            GarSafe.destroy(temp_CommoditySubgroupAndGradeIds_table, temp_SapMaterialCode_table);

        }

    }
}
