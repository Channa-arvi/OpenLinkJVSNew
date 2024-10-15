package com.garinternal.ops.pre;

/*
File Name:                      GarOpsIndoSalesDataPrePopulate.java

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
import com.garinternal.common.enums.GAR_LOCAL_INTERNATIONAL;
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
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.BUY_SELL_ENUM;
import com.olf.openjvs.enums.TRANF_FIELD;

public class GarOpsIndoSalesDataPrePopulate extends GarBasicScript {
    private GarLogger logger;

    /**
     * Constructor
     *
     * @throws OException {@link OException}
     */
    public GarOpsIndoSalesDataPrePopulate() throws OException {
        super();
        this.logger = super.getLoggerInstance();

    }

    @Override
    public void execute(Table argt, Table returnt) throws OException {
        Table tblSap = Util.NULL_TABLE;

        try {
            // Loop one time for each deal
            int totalTrans = OpService.retrieveNumTrans();

            for (int tranCount = 1; tranCount <= totalTrans; tranCount++) {
                Transaction tranPointer = OpService.retrieveOriginalTran(tranCount);
                this.logger.info("Transaction number " + tranPointer.getTranNum());

                int    ctpLntity           = tranPointer.getFieldInt(TRANF_FIELD.TRANF_INTERNAL_LENTITY.toInt(), 0, "int_lentity", 0, 0);
                String isLocal             =
                    tranPointer.getField(TRANF_FIELD.TRANF_TRAN_INFO, 0, GAR_TRAN_INFO_ENUM.T_IS_LOCAL.toString(), -1, -1);
                String riskLocation        = tranPointer.getField(TRANF_FIELD.TRANF_LOCATION, 1, "location", -1, -1);
                int    indexId             = tranPointer.getFieldInt(TRANF_FIELD.TRANF_PROJ_INDEX, 1, "Proj Index", -1, -1);
                int    intCommoditySugroup = tranPointer.getFieldInt(TRANF_FIELD.TRANF_IDX_SUBGROUP, 1, "Commodity Sub-Group", -1, -1);
                this.logger.info("intCommoditySugroup" + intCommoditySugroup);
                String buySellFlag = tranPointer.getField(TRANF_FIELD.TRANF_BUY_SELL, 0, "buy/sell", -1, -1);
                int    extPortolio = tranPointer.getFieldInt(TRANF_FIELD.TRANF_EXTERNAL_PORTFOLIO.toInt(), 0, "ext_portfolio", 0, 0);

                if (extPortolio == 0 && BUY_SELL_ENUM.SELL.toString().equalsIgnoreCase(buySellFlag)) {

                    int idxSubgroupId = 0;

                    try {
                        // getting idx_subgroup id based on projection index
                        idxSubgroupId = this.getIdxSubGroupId(indexId);
                        this.logger.info("idxSubgroupId" + idxSubgroupId);
                    } catch (OException oex) {
                        this.logger.info("OException, unsuccessful database query, " + oex.getMessage());
                    }

                    String code = this.getCompanyCode(ctpLntity);
                    this.logger.info("Code = " + code);

                    int isLocalId = GAR_LOCAL_INTERNATIONAL.fromString(isLocal).toInt();
                    this.logger.info("Is Local Id " + isLocalId);

                    int locationId = this.getLocationId(riskLocation, indexId, intCommoditySugroup);
                    this.logger.info("Location Id " + locationId);

                    tblSap = this.getSapData(code, isLocalId, locationId);

                    this.setTranInfoValues(tblSap, tranPointer);

                }

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
        String salesOrg       = "";
        String distribChannel = "";
        String division       = "";

        if (!GarHasAValue.isTableEmpty(tblSap)) {
            salesOrg       = tblSap.getString("salesorg", 1);
            distribChannel = tblSap.getString("distributionchannel", 1);
            division       = tblSap.getString("division", 1);

            if (!GarHasAValue.hasAValue(salesOrg, true) || !GarHasAValue.hasAValue(distribChannel, true)
                || !GarHasAValue.hasAValue(division, true)) {
                throw new OException("Invalid Sap Data");
            }

        } else {
            throw new OException("No Sap Data found");
        }

        // setting sales org, distribution channel and division tran info fields
        tranPointer.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, GAR_TRAN_INFO_ENUM.T_SAP_SALES_ORG.toString(), salesOrg, 0, 0, -1, -1);
        tranPointer.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, GAR_TRAN_INFO_ENUM.T_SAP_DIST_CHANNEL.toString(), distribChannel, 0, 0,
            -1, -1);
        tranPointer.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, GAR_TRAN_INFO_ENUM.T_SAP_DIVISION.toString(), division, 0, 0 - 1, -1);

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
                + "\n SELECT code" //
                + "\n FROM  " + GAR_USER_TABLE_ENUM.USER_SAP_MD_COMPANY_CODE.toString() //
                + "\n WHERE  internal_lentity = " + ctpLntity;

            // getting company code based on Int LEntity
            String errorMsg = "Unable to find company code for Internal LE = " + ctpLntity;
            GarStrict.query(tblCompanyCode, sqlQueryCompanyCode, errorMsg, this.logger);
            this.logger.info("Company Code " + tblCompanyCode.getInt("Code", 1));

            if (!GarHasAValue.isTableEmpty(tblCompanyCode)) {
                companyCode = tblCompanyCode.getString("code", 1);
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
                + "\n SELECT idx_subgroup" //
                + "\n FROM   idx_def" //
                + "\n WHERE index_id = " + indexId;

            String errorMsg = "Unable to find idx_subgroup for selected index id";

            GarStrict.query(tblIdxSubGroup, sqlQueryIdxSubgroup, errorMsg, this.logger);

            if (!GarHasAValue.isTableEmpty(tblIdxSubGroup)) {
                idxSubGroupID = tblIdxSubGroup.getInt("idx_subgroup", 1);
            }

        } finally {
            GarSafe.destroy(tblIdxSubGroup);
        }

        return idxSubGroupID;
    }

    /**
     * Get Location Id
     *
     * @param riskLocation
     * @param indexId
     * @param idxSubgroupId
     * @return
     * @throws OException
     */
    private int getLocationId(String riskLocation, int indexId, int idxSubgroupId) throws OException {
        int   locationId     = 0;
        Table tblDistChannel = Table.tableNew();

        try {
            String sqlQueryRiskLocation = "" //
                + "\n SELECT location_id" //
                + "\n FROM   gas_phys_location" //
                + "\n WHERE  location_name = '" + riskLocation + "'" //
                + "\n AND active = 1" //
                + "\n   AND index_id = " + indexId //
                + "\n   AND idx_subgroup = " + idxSubgroupId;

            String errorMsg = "Unable to find Location Id for Location Name = " + riskLocation + ", Index Id = " + indexId
                + ", Index Subgroup Id = " + idxSubgroupId;

            GarStrict.query(tblDistChannel, sqlQueryRiskLocation, errorMsg, this.logger);

            if (!GarHasAValue.isTableEmpty(tblDistChannel)) {
                locationId = tblDistChannel.getInt("location_id", 1);
            }

        } finally {
            GarSafe.destroy(tblDistChannel);
        }

        return locationId;
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
    private Table getSapData(String code, int isLocalId, int locationId) throws OException {

        String sqlQuerySapTranInfo = "" //
            + "\n SELECT DISTINCT usmso.code AS salesorg, usmdl.code AS distributionchannel" //
            + "\n      , usmd.code AS division " //
            + "\n FROM   " + GAR_USER_TABLE_ENUM.USER_SAP_SALES_AREA.toString() + " ussa " //
            + "\n JOIN   " + GAR_USER_TABLE_ENUM.USER_SAP_MD_SALES_ORG.toString() + " usmso ON usmso.id = ussa.sales_org " //
            + "\n JOIN   " + GAR_USER_TABLE_ENUM.USER_SAP_MD_DISTR_CHL.toString() + " usmdl " //
            + "     ON   usmdl.id = ussa.distribution_channel " //
            + "\n JOIN   " + GAR_USER_TABLE_ENUM.USER_SAP_MD_DIVISION.toString() + " usmd ON usmd.id = ussa.division " //
            + "\n WHERE  ussa.company_code= '" + code + "'" //
            + "\n   AND  ussa.local_international= " + isLocalId //
            + "\n   AND  ussa.delivery_location= " + locationId;

        String errorMsg = "Unable to find sales org, distribution channel, division value with supplied selected  code"
            + " , local-internationala dn  location id ";

        return GarStrict.query(sqlQuerySapTranInfo, errorMsg, this.logger);
    }
}
