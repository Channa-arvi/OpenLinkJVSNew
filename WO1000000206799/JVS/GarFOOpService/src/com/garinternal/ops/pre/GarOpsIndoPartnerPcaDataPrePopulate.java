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
import com.garinternal.common.enums.GAR_PARTY_INFO_ENUM;
import com.garinternal.common.enums.GAR_TRAN_INFO_ENUM;
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

    /**
     * Constructor
     *
     * @throws OException {@link OException}
     */
    public GarOpsIndoPartnerPcaDataPrePopulate() throws OException {
        super();
        this.logger = super.getLoggerInstance();
    }

    @Override
    public void execute(Table argt, Table returnt) throws OException {
        Table tblSap = Util.NULL_TABLE;

        try {
            int totalTrans = OpService.retrieveNumTrans();

            for (int tranCount = 1; tranCount <= totalTrans; tranCount++) {
                Transaction tranPointer = OpService.retrieveOriginalTran(tranCount);
                OConsole.oprint("\nTransaction number " + tranPointer.getTranNum());

                int    intLntity = tranPointer.getFieldInt(TRANF_FIELD.TRANF_INTERNAL_LENTITY.toInt(), 0, "int_lentity", 0, 0);
                int    extLntity = tranPointer.getFieldInt(TRANF_FIELD.TRANF_EXTERNAL_LENTITY.toInt(), 0, "ext_lentity", 0, 0);
                int    indexId   = tranPointer.getFieldInt(TRANF_FIELD.TRANF_PROJ_INDEX, 1, "Proj Index", -1, -1);
                String zone      = tranPointer.getField(TRANF_FIELD.TRANF_ZONE, 1, "Zone", -1, -1);

                // getting idx_subgroup id based on projection index
                int idxSubgroupId = this.getIdxSubGroupId(indexId);
                this.logger.info("idxSubgroupId" + idxSubgroupId);

                String intLentitycode = this.getCompanyCode(intLntity);
                this.logger.info("Internal LE Code = " + intLentitycode);

                String cptyCode = this.getCompanyCode(extLntity);
                this.logger.info("External LE Code = " + cptyCode);

                tblSap = this.getSapData(intLentitycode, cptyCode, idxSubgroupId, zone);

                this.setTranInfoValues(tblSap, tranPointer);

            }

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

        String partnerPcaCode = "";

        if (!GarHasAValue.isTableEmpty(tblSap)) {
            partnerPcaCode = tblSap.getString("partner_pca_Code", 1);

            if (!GarHasAValue.hasAValue(partnerPcaCode, true)) {
                throw new OException("Invalid Sap Data");
            }

        } else {
            throw new OException("No Sap Data found");
        }

        String strPartnerPCA =
            tranPointer.getField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, GAR_TRAN_INFO_ENUM.T_SAP_PARTNER_PCA.toString(), 0, 0, -1, 1);

        this.logger.info("strPartnerPCA--->" + strPartnerPCA);

        // setting partner pca
        if (!GarHasAValue.hasAValue(strPartnerPCA, true)) {
            tranPointer.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, GAR_TRAN_INFO_ENUM.T_SAP_PARTNER_PCA.toString(), partnerPcaCode, 0,
                0, -1, -1);
        }

    }

    /**
     * Get Company code
     *
     * @param legalEntityId
     * @return
     * @throws OException
     */
    private String getCompanyCode(int legalEntityId) throws OException {
        String companyCode    = "";
        Table  tblCompanyCode = Table.tableNew();

        try {
            String sqlQueryCompanyCode = "" //
                + "\n SELECT value "//
                + "\n FROM party_info " //
                + "\n WHERE  type_id = " + GAR_PARTY_INFO_ENUM.PTY_SAP_COMPANY_CODE_INT.toInt() //
                + "\n   AND  party_id = " + legalEntityId;

            // getting company code based on Int LEntity
            String errorMsg = "Unable to find company code for Internal LE = " + legalEntityId;
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
     * @param intLecode
     * @param cptyCode
     * @param idxgroupId
     * @param zone
     * @return
     * @throws OException
     */
    private Table getSapData(String intLecode, String cptyCode, int idxgroupId, String zone) throws OException {

        String sqlQuerySapTranInfo = "" + "\n SELECT  pca.partner_pca_code as partner_pca_Code "//
            + "\n FROM    " + GAR_USER_TABLE_ENUM.USER_SAP_MD_PARTNER_PCA.toString() + " pca" //
            + "\n WHERE   pca.company_code = " + intLecode //
            + "\n   AND   pca.cpty_company_code = " + cptyCode //
            + "\n   AND   pca.idx_group = " + idxgroupId //
            + "\n   AND  (pca.zone= '" + zone + "' " //
            + "\n         OR pca.zone = 'ALL')";//

        return GarStrict.query(sqlQuerySapTranInfo, "", this.logger);
    }
}
