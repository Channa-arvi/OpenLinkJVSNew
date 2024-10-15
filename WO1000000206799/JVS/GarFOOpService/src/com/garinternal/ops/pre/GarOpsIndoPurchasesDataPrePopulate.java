package com.garinternal.ops.pre;

/*
File Name:                      GarOpsIndoPurchasesDataPrePopulate.java

Script Type:                    MAIN
Parameter Script:               None
Display Script:                 None

Description:
This script is an Operational Services Trading Pre-Processing script.
It checks to make sure each deal with Internal Legal Entity and (is local or International) and Risk location and set the
values from Sales org , distribution List and Division Tran info fields . This script will using these user tables:
USER_sap_sales_area, user_sap_md_company_code, gas_phys_location, user_sap_md_dist_chl, user_sap_md_division.


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
import com.olf.openjvs.OConsole;
import com.olf.openjvs.OException;
import com.olf.openjvs.OpService;
import com.olf.openjvs.Table;
import com.olf.openjvs.Transaction;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.BUY_SELL_ENUM;
import com.olf.openjvs.enums.TRANF_FIELD;

public class GarOpsIndoPurchasesDataPrePopulate extends GarBasicScript {
    private GarLogger logger;

    /**
     * Constructor
     *
     * @throws OException {@link OException}
     */
    public GarOpsIndoPurchasesDataPrePopulate() throws OException {
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

                int    intLntity    = tranPointer.getFieldInt(TRANF_FIELD.TRANF_INTERNAL_LENTITY.toInt(), 0, "int_lentity", 0, 0);
                String isLocal      =
                    tranPointer.getField(TRANF_FIELD.TRANF_TRAN_INFO, 0, GAR_TRAN_INFO_ENUM.T_IS_LOCAL.toString(), -1, -1);
                String riskLocation = tranPointer.getField(TRANF_FIELD.TRANF_LOCATION, 1, "location", -1, -1);
                int    indexId      = tranPointer.getFieldInt(TRANF_FIELD.TRANF_PROJ_INDEX, 1, "Proj Index", -1, -1);
                String buySellFlag  = tranPointer.getField(TRANF_FIELD.TRANF_BUY_SELL, 0, "buy/sell", -1, -1);
                int    extPortolio  = tranPointer.getFieldInt(TRANF_FIELD.TRANF_EXTERNAL_PORTFOLIO.toInt(), 0, "ext_portfolio", 0, 0);

                if (extPortolio == 0 && BUY_SELL_ENUM.BUY.toString().equalsIgnoreCase(buySellFlag)) {

                    int idxSubgroupId = 0;

                    try {
                        // getting idx_subgroup id based on projection index
                        idxSubgroupId = this.getIdxSubGroupId(indexId);
                        this.logger.info("idxSubgroupId" + idxSubgroupId);
                    } catch (OException oex) {
                        OConsole.oprint("OException, unsuccessful database query, " + oex.getMessage());
                    }

                    String code = this.getCompanyCode(intLntity);
                    this.logger.info("Int LE Company Code = " + code);

                    int isLocalId = GAR_LOCAL_INTERNATIONAL.fromString(isLocal).toInt();
                    this.logger.info("Is Local Id " + isLocalId);

                    int locationId = this.getLocationId(riskLocation, indexId, idxSubgroupId);
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
        String purchaseOrg   = "";
        String purchaseGroup = "";

        if (!GarHasAValue.isTableEmpty(tblSap)) {
            purchaseOrg   = tblSap.getString("purchorg", 1);
            purchaseGroup = tblSap.getString("purchgrp", 1);

            if (!GarHasAValue.hasAValue(purchaseOrg, true) || !GarHasAValue.hasAValue(purchaseGroup, true)) {
                throw new OException("Invalid Sap Data");
            }

        } else {
            throw new OException("No Sap Data found");
        }

        // setting Purchase org, purchase group tran info fields
        tranPointer.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, GAR_TRAN_INFO_ENUM.T_PURCHASE_ORG.toString(), purchaseOrg, 0, 0, -1,
            -1);
        tranPointer.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, GAR_TRAN_INFO_ENUM.T_SAP_PURCH_GRP.toString(), purchaseGroup, 0, 0, -1,
            -1);
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
        Table tblIdxSubGroup = Table.tableNew();

        try {
            String sqlQueryIdxSubgroup = "" //
                + "\n SELECT idx_subgroup" //
                + "\n FROM   idx_def" //
                + "\n WHERE  index_id = " + indexId;

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
     * Get Company code
     *
     * @param lentityId
     * @return
     * @throws OException
     */
    private String getCompanyCode(int lentityId) throws OException {
        String companyCode    = "";
        Table  tblCompanyCode = Table.tableNew();

        try {
            String sqlQueryCompanyCode = "" //
                + "\n SELECT code" //
                + "\n FROM  " + GAR_USER_TABLE_ENUM.USER_SAP_MD_COMPANY_CODE.toString() //
                + "\n WHERE  internal_lentity = " + lentityId;

            // getting company code based on Int LEntity
            String errorMsg = "Unable to find company code for Internal LE = " + lentityId;
            GarStrict.query(tblCompanyCode, sqlQueryCompanyCode, errorMsg, this.logger);

            if (!GarHasAValue.isTableEmpty(tblCompanyCode)) {
                companyCode = tblCompanyCode.getString("code", 1);
            }

        } finally {
            GarSafe.destroy(tblCompanyCode);
        }

        return companyCode;
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
        Table tblDistChannel = Util.NULL_TABLE;

        tblDistChannel = Table.tableNew();

        try {
            String sqlQueryRiskLocation = "" //
                + "\n SELECT location_id" //
                + "\n FROM   gas_phys_location" //
                + "\n WHERE  location_name = '" + riskLocation + "'" //
                + "\n   AND  index_id = " + indexId //
                + "\n   AND  idx_subgroup = " + idxSubgroupId;

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
            + "\n SELECT DISTINCT usmpo.code AS purchorg, usmpg.code AS purchgrp " //
            + "\n FROM   " + GAR_USER_TABLE_ENUM.USER_SAP_PURCHASING_DATA.toString() + " ussa "//
            + "\n JOIN   " + GAR_USER_TABLE_ENUM.USER_SAP_MD_PURCH_ORG.toString() + " usmpo ON usmpo.id = ussa.purch_org "//
            + "\n JOIN   " + GAR_USER_TABLE_ENUM.USER_SAP_MD_PURCH_GRP.toString() + " usmpg ON usmpg.id = ussa.purch_group "//
            + "\n WHERE  ussa.company_code= '" + code + "'" //
            + "\n   AND  ussa.local_international= " + isLocalId //
            + "\n   AND  ussa.delivery_location= " + locationId;//

        String errorMsg =
            "Unable to find purchae org, purchase group with supplied selected  code" + " , local-internationala dn  location id ";

        return GarStrict.query(sqlQuerySapTranInfo, errorMsg, this.logger);
    }
}
