package com.garinternal.ops.pre;
/*
File Name:                      GarOpsIndoPartnerPcaDataPrePopulate.java

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
import com.garinternal.common.enums.GAR_USER_TABLE_ENUM;
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

public class GarOpsIndoPartnerPcaDataPrePopulate extends GarBasicScript {

    private GarLogger logger;

    public GarOpsIndoPartnerPcaDataPrePopulate() throws OException {
        super();
        this.logger = super.getLoggerInstance();
    }

    @Override
    public void execute(Table argt, Table returnt) throws OException {
        Transaction tranPointer;
        Table       tblSap     = Util.NULL_TABLE;
        int         totalTrans = OpService.retrieveNumTrans();

        for (int tranCount = 1; tranCount <= totalTrans; tranCount++) {
            tranPointer = OpService.retrieveOriginalTran(tranCount);
            OConsole.oprint("\nTransaction number " + tranPointer.getTranNum());

            int    intLntity = tranPointer.getFieldInt(TRANF_FIELD.TRANF_INTERNAL_LENTITY.toInt(), 0, "int_lentity", 0, 0);
            int    extLntity = tranPointer.getFieldInt(TRANF_FIELD.TRANF_EXTERNAL_LENTITY.toInt(), 0, "ext_lentity", 0, 0);
            int    indexId   = tranPointer.getFieldInt(TRANF_FIELD.TRANF_PROJ_INDEX, 1, "Proj Index", -1, -1);
            String zone      = tranPointer.getField(TRANF_FIELD.TRANF_ZONE, 1, "Zone", -1, -1);

            int idxSubgroupId = 0;

            try {
                // getting idx_subgroup id based on projection index
                idxSubgroupId = this.getIdxSubGroupId(indexId);
                this.logger.info("idxSubgroupId" + idxSubgroupId);
            } catch (OException oex) {
                this.logger.info("OException, unsuccessful database query, " + oex.getMessage());
            }

            // OConsole.oprint("idxgroupId" + idxgroupId);

            String code = this.getCompanyCode(intLntity);
            this.logger.info("Code = " + code);

            String cpty_code = this.getCompanyCode(extLntity);
            this.logger.info("cpty_code = " + cpty_code);

            tblSap = this.getSapData(code, cpty_code, idxSubgroupId, zone);

            this.setTranInfoValues(tblSap, tranPointer);

        }

    }

    /**
     * Set TranInfo Values
     *
     * @param tblSap
     * @param tranPointer
     * @param salesOrg
     * @param distribChannel
     * @param division
     * @throws OException
     */
    private void setTranInfoValues(Table tblSap, Transaction tranPointer) throws OException {

        String partner_pca_code = "";

        String strPartnerPCA = tranPointer.getField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, "t_sap_partner_pca", 0, 0, -1, 1);

        if (!GarHasAValue.isTableEmpty(tblSap)) {
            partner_pca_code = tblSap.getString("partner_pca_Code", 1);

            if (!GarHasAValue.hasAValue(partner_pca_code, true)) {
                throw new OException("Invalid Sap Data");
            }

        } else {
            throw new OException("No Sap Data found");
        }

        strPartnerPCA = tranPointer.getField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, "t_sap_partner_pca", 0, 0, -1, 1);

        this.logger.info("strPartnerPCA--->" + strPartnerPCA);

        // setting partner pca
        if (strPartnerPCA.equalsIgnoreCase("")) {
            tranPointer.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, "t_sap_partner_pca", partner_pca_code.toString(), 0, 0, -1, -1);
        }

    }

    /**
     * Get Company code
     *
     * @param ctpLntity
     * @return
     * @throws OException
     */
    private String getCompanyCode(int ctpLntity) throws OException {
        String companyCode    = "";
        Table  tblCompanyCode = Util.NULL_TABLE;

        tblCompanyCode = Table.tableNew();

        try {
            String sqlQueryCompanyCode = "" //
                + "\n SELECT value "//
                + "\n FROM party_info " + "\n WHERE  type_id = 20006 AND  party_id = " + ctpLntity;

            // getting company code based on Int LEntity
            String errorMsg = "Unable to find company code for Internal LE = " + ctpLntity;
            GarStrict.query(tblCompanyCode, sqlQueryCompanyCode, errorMsg, this.logger);
            this.logger.info("Company Code " + tblCompanyCode.getString("value", 1));

            if (!GarHasAValue.isTableEmpty(tblCompanyCode)) {
                companyCode = tblCompanyCode.getString("value", 1);
            }

        } finally {
            GarSafe.destroy(tblCompanyCode);
        }

        return companyCode;
    }

    /**
     * Get Index Sub Group Id
     *
     * @param indexId
     * @return
     * @throws OException
     */
    private int getIdxSubGroupId(int indexId) throws OException {
        int   idxSubGroupID  = 0;
        Table tblIdxSubGroup = Util.NULL_TABLE;

        tblIdxSubGroup = Table.tableNew();

        try {
            String sqlQueryIdxSubgroup = "" //
                + "\n SELECT idx_group" //
                + "\n FROM   idx_def" //
                + "\n WHERE index_id = " + indexId;

            String errorMsg = "Unable to find idx_subgroup for selected index id";

            GarStrict.query(tblIdxSubGroup, sqlQueryIdxSubgroup, errorMsg, this.logger);

            if (!GarHasAValue.isTableEmpty(tblIdxSubGroup)) {
                idxSubGroupID = tblIdxSubGroup.getInt("idx_group", 1);
            }

        } finally {
            GarSafe.destroy(tblIdxSubGroup);
        }

        return idxSubGroupID;
    }

    /**
     * Get SAP Data
     *
     * @param code
     * @param isLocalId
     * @param locationId
     * @return
     * @throws OException
     */
    private Table getSapData(String code, String cpty_code, int idxgroupId, String zone) throws OException {

        String sqlQuerySapTranInfo = "SELECT  pca.partner_pca_code as partner_pca_Code "//
            + " FROM  " + GAR_USER_TABLE_ENUM.USER_SAP_MD_PARTNER_PCA.toString() //
            + " pca  " + " WHERE pca.company_code = " + code //
            + " AND pca.cpty_company_code = " + cpty_code + " AND pca.idx_group =" //
            + idxgroupId + " AND  (pca.zone= '" + zone + "'  OR pca.zone = 'ALL')";//

        String errorMsg = "Unable to find sales org, distribution channel, division value with supplied selected  code"
            + " , local-internationala dn  location id ";

        return GarStrict.query(sqlQuerySapTranInfo, errorMsg, this.logger);
    }
}
