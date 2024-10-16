package com.garinternal.ops.pre;

/*
File Name:                     GarOPSIndoSalesAndPurchaseOffsetDataPrePopulate.java

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

public class GarOPSIndoSalesAndPurchaseOffsetDataPrePopulate extends GarBasicScript {

    private GarLogger logger;

    public GarOPSIndoSalesAndPurchaseOffsetDataPrePopulate() throws OException {
        super();
        this.logger = super.getLoggerInstance();

    }

    @Override
    public void execute(Table argt, Table returnt) throws OException {

        Table tblSap = Util.NULL_TABLE;

        /* Loop one time for each deal*/
        OConsole.oprint( ("\ntrans" + OpService.retrieveNumTrans()));

        int totalTrans = OpService.retrieveNumTrans();

        for (int tranCount = 1; tranCount <= totalTrans; tranCount++) {

            // transaction_pointer = OpService.retrieveTran(i);
            OConsole.oprint("\n no of transaction " + OpService.retrieveNumTrans());
            OConsole.oprint("\nTransaction " + OpService.retrieveTran(tranCount));
            Transaction tranPointer = OpService.retrieveOriginalTran(tranCount);
            OConsole.oprint("\nTransaction number " + tranPointer.getTranNum());

            int    intLntity    = tranPointer.getFieldInt(TRANF_FIELD.TRANF_INTERNAL_LENTITY.toInt(), 0, "int_lentity", 0, 0);
            int    extLntity    = tranPointer.getFieldInt(TRANF_FIELD.TRANF_EXTERNAL_LENTITY.toInt(), 0, "int_lentity", 0, 0);
            String isLocal      = tranPointer.getField(TRANF_FIELD.TRANF_TRAN_INFO, 0, "t_is_local", -1, -1);
            String riskLocation = tranPointer.getField(TRANF_FIELD.TRANF_LOCATION, 1, "location", -1, -1);
            int    vpool        = tranPointer.getFieldInt(TRANF_FIELD.TRANF_GAS_VPOOL, 1, "Valuation Pool", -1, -1);
            int    indexId      = tranPointer.getFieldInt(TRANF_FIELD.TRANF_PROJ_INDEX, 1, "Proj Index", -1, -1);
            // tranPointer.getField(TRANF_FIELD.TRANF_INTERNAL_LENTITY.toInt(), 0, "int_lentity", 0, 0);
            String buySellFlag = tranPointer.getField(TRANF_FIELD.TRANF_BUY_SELL, 0, "buy/sell", -1, -1);

            int extPortolio = tranPointer.getFieldInt(TRANF_FIELD.TRANF_EXTERNAL_PORTFOLIO.toInt(), 0, "ext_portfolio", 0, 0);

            OConsole.oprint("\nextPortolio" + extPortolio);

            if (extPortolio != 0) {

                int idxSubgroupId = 0;

                try {
                    // getting idx_subgroup id based on projection index
                    idxSubgroupId = this.getIdxSubGroupId(indexId);
                    this.logger.info("idxSubgroupId" + idxSubgroupId);
                } catch (OException oex) {
                    OConsole.oprint("OException, unsuccessful database query, " + oex.getMessage());
                }

                String code = this.getCompanyCode(intLntity);
                this.logger.info("Code = " + code);

                String cpty_code = this.getCompanyCode(extLntity);
                this.logger.info("cpty_code = " + cpty_code);

                int isLocalId = GAR_LOCAL_INTERNATIONAL.fromString(isLocal).toInt();
                this.logger.info("Is Local Id " + isLocalId);

                int locationId = this.getLocationId(riskLocation, indexId, idxSubgroupId);
                this.logger.info("Location Id " + locationId);

                if (buySellFlag.equalsIgnoreCase("Buy")) {

                    tblSap = this.getSapDataForBuy(code, isLocalId, locationId);

                    this.setTranInfoValuesForBuy(tblSap, tranPointer);

                }

                if (buySellFlag.equalsIgnoreCase("Sell")) {

                    tblSap = this.getSapDataForSell(code, isLocalId, locationId);

                    this.setTranInfoValuesForSell(tblSap, tranPointer);

                }

            }

        }

    }

    /**
     * Set TranInfo Values
     *
     * @param tblSap
     * @param tranPointer
     * @param purchaseorg
     * @param purchasegroup
     * @throws OException
     */
    private void setTranInfoValuesForBuy(Table tblSap, Transaction tranPointer) throws OException {
        String purchase_org   = "";
        String purchase_group = "";

        if (!GarHasAValue.isTableEmpty(tblSap)) {
            purchase_org   = tblSap.getString("purchorg", 1);
            purchase_group = tblSap.getString("purchgrp", 1);

            if (!GarHasAValue.hasAValue(purchase_org, true) || !GarHasAValue.hasAValue(purchase_group, true)) {
                throw new OException("Invalid Sap Data");
            }

        } else {
            throw new OException("No Sap Data found");
        }

        // setting Purchase org, purchase group tran info fields
        tranPointer.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, "t_purchase_org", purchase_org, 0, 0, -1, -1);
        tranPointer.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, "t_sap_purch_grp", purchase_group, 0, 0, -1, -1);
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
    private void setTranInfoValuesForSell(Table tblSap, Transaction tranPointer) throws OException {
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
        tranPointer.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, "t_sap_sales_org", salesOrg, 0, 0, -1, -1);
        tranPointer.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, "t_sap_dist_channel", distribChannel, 0, 0, -1, -1);
        tranPointer.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, "t_sap_division", division, 0, 0 - 1, -1);

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
    private Table getSapDataForBuy(String code, int isLocalId, int locationId) throws OException {
        String sqlQuerySapTranInfo =
            "SELECT distinct usmpo.code as purchorg, usmpg.code as purchgrp " + " FROM USER_sap_purchasing_data ussa " + " JOIN "
                + GAR_USER_TABLE_ENUM.USER_SAP_MD_PURCH_ORG.toString() + " usmpo ON usmpo.id = ussa.purch_org "//
                + " JOIN " + GAR_USER_TABLE_ENUM.USER_SAP_MD_PURCH_GRP.toString() + " usmpg ON usmpg.id = ussa.purch_group "
                + " WHERE ussa.company_code= '" + code + "'" + " AND ussa.local_international= " + isLocalId
                + " AND ussa.delivery_location= " + locationId;

        String errorMsg =
            "Unable to find purchae org, purchase group with supplied selected  code" + " , local-internationala dn  location id ";

        return GarStrict.query(sqlQuerySapTranInfo, errorMsg, this.logger);
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
    private Table getSapDataForSell(String code, int isLocalId, int locationId) throws OException {

        for (GAR_USER_TABLE_ENUM enums : GAR_USER_TABLE_ENUM.values()) {
            this.logger.info("Name = " + enums.name() + " Value = " + enums.toString());
        }

        String sqlQuerySapTranInfo = "" //
            + "\n SELECT DISTINCT usmso.code AS salesorg, usmdl.code AS distributionchannel" //
            + "\n     , usmd.code AS division " //
            + "\n FROM  user_sap_sales_area ussa " //
            + "\n JOIN  " + GAR_USER_TABLE_ENUM.USER_SAP_MD_SALES_ORG.toString() + " usmso ON usmso.id = ussa.sales_org " //
            + "\n JOIN  " + GAR_USER_TABLE_ENUM.USER_SAP_MD_DISTR_CHL.toString() + " usmdl " //
            + "     ON  usmdl.id = ussa.distribution_channel " //
            + "\n JOIN  " + GAR_USER_TABLE_ENUM.USER_SAP_MD_DIVISION.toString() + " usmd ON usmd.id = ussa.division " //
            + "\n WHERE ussa.company_code= '" + code + "'" //
            + "\n   AND ussa.local_international= " + isLocalId //
            + "\n   AND ussa.delivery_location= " + locationId;

        String errorMsg = "Unable to find sales org, distribution channel, division value with supplied selected  code"
            + " , local-internationala dn  location id ";

        return GarStrict.query(sqlQuerySapTranInfo, errorMsg, this.logger);
    }

}
