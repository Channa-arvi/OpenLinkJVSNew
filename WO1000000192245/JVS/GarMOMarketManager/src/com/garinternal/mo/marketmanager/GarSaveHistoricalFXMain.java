package com.garinternal.mo.marketmanager;

/*
File Name:                      GarSaveHistoricalFXMain.java

Script Type:                    MAIN
Parameter Script:               None
Display Script:                 None

Description:
This script will automatically save all current business day FX rates into idx_historical_fx_rates table.
Only applicable to Indexes with Group ID = 32 (FX).
Assumption for GAR:
1. Data set type 3PM EOD (closing dataset id = 20001) and Closing (closing dataset id = 1) will have the same fx rates daily.

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000119461 |             | Willyam    | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import com.garinternal.basic.GarBasicScript;
import com.garinternal.common.util.GarHasAValue;
import com.garinternal.common.util.GarLogger;
import com.garinternal.common.util.GarSafe;
import com.garinternal.common.util.GarStrict;
import com.garinternal.common.enums.GAR_IDX_GROUP;
import com.garinternal.common.enums.GAR_IDX_MARKET_DATA_TYPE;
import com.olf.openjvs.Index;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Sim;
import com.olf.openjvs.Table;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.fnd.UtilBase;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)

public class GarSaveHistoricalFXMain extends GarBasicScript {
    private GarLogger logger;

    /**
     *
     * Constructor
     *
     * @throws OException {@link OException}
     */
    public GarSaveHistoricalFXMain() throws OException {
        super();
        this.logger = super.getLoggerInstance();
    }

    @Override
    public void execute(Table argt, Table returnt) throws OException {

        Table tblHistoricalFX = Util.NULL_TABLE;
        Table tblIndexList    = Util.NULL_TABLE;
        Table tblIndexRate    = Util.NULL_TABLE;

        try {
            // table to store list on currency indexes
            tblIndexList = this.getFXIdxList();

            // table to store each index rates into historical fx rates
            tblHistoricalFX = Table.tableNew("Hist Fx Rates");

            // construct relevant columns to save into for historical fx rates
            tblHistoricalFX.addCols(
                "I(fx_rate_date) I(currency_id) I(reference_currency_id) F(fx_rate_bid) F(fx_rate_mid) F(fx_rate_offer) I(data_set_type) I(currency_convention)");

            // loop through each index
            int rowNum          = 0;
            int numRows         = tblIndexList.getNumRows();
            int colNumIdxID     = tblIndexList.getColNum("index_id");
            int colNumCurrID    = tblIndexList.getColNum("currency");
            int colNumCurrIDInv = tblIndexList.getColNum("currency2"); // non-usd currency2 indicates it is an inverse currency
            int colNumPriceMid  = -1;

            for (rowNum = 1; rowNum <= numRows; rowNum++) {
                int idxName  = tblIndexList.getInt(colNumIdxID, rowNum);
                int currency = tblIndexList.getInt(colNumCurrID, rowNum);

                // verify if currency convention is reverse
                if (currency == 0) {
                    currency = tblIndexList.getInt(colNumCurrIDInv, rowNum);
                }

                // get currency convention id
                int convention = this.getCurrConvention(currency);
                // load FX close rates
                int gbd = UtilBase.getBusinessDate();
                Sim.loadCloseIndexList(tblIndexList, 1, gbd);
                // load output name SPOT and get the effective.mid price (input)
                tblIndexRate = Index.getOutput(idxName);

                if (colNumPriceMid == -1) {
                    colNumPriceMid = tblIndexRate.getColNum("Price (Mid)");
                }

                double rate = tblIndexRate.getDouble(colNumPriceMid, 2);

                // add row into tblHistoricalFX
                // CLOSING Data Set - closing Dataset ID = 1
                tblHistoricalFX.addRowsWithValues(gbd + "," + currency + ", 0," + rate + "," + rate + "," + rate + ","
                    + GAR_IDX_MARKET_DATA_TYPE.CLOSING.toInt() + ", " + convention);
                // 3PM Data Set - closing Dataset ID = 20001
                tblHistoricalFX.addRowsWithValues(gbd + "," + currency + ", 0," + rate + "," + rate + "," + rate + ","
                    + GAR_IDX_MARKET_DATA_TYPE.EOD_3PM.toInt() + ", " + convention);

                // save data to database
                Index.saveHistoricalFxRates(tblHistoricalFX);

                // initialize currency
                currency = 0;
                // destroy unused table
                GarSafe.destroy(tblIndexRate);
            }

        } finally {
            // destroy unused tables
            GarSafe.destroy(tblIndexList, tblHistoricalFX, tblIndexRate);
        }

    }

    /**
     * Returns the List of FX Indexes
     *
     * @return tblIndexName
     * @throws OException
     */
    private Table getFXIdxList() throws OException {
        String idxGroupId = Double.toString(GAR_IDX_GROUP.FX.toInt());

        String sql = "\n SELECT * " //
            + "\n FROM idx_def " //
            + "\n WHERE idx_group =" + idxGroupId //
            + "\n   AND db_status = 1 " //
            + "\n ORDER BY index_id ASC"; //

        String errorMsg = "Unable to retrieve index group = 32 and db status = 1";

        Table tblIndexName = GarStrict.query(sql, errorMsg, this.logger);

        if (GarHasAValue.isTableEmpty(tblIndexName)) {

            throw new OException(errorMsg);
        }

        return tblIndexName;
    }

    /**
     * Returns the convention id (standard/reverse) from currency table
     *
     * @param currId
     * @return currConvention
     * @throws OException
     */
    private int getCurrConvention(int currId) throws OException {
        Table tblIndexName   = Util.NULL_TABLE;
        int   currConvention = -1;

        try {
            String sql = "\n SELECT convention " //
                + "\n FROM currency " //
                + "\n WHERE id_number = " + currId; //

            String errorMsg = "Unable to retrieve currency id number: " + currId;

            tblIndexName = GarStrict.query(sql, errorMsg, this.logger);

            if (!GarHasAValue.isTableEmpty(tblIndexName)) {
                currConvention = tblIndexName.getInt("convention", 1);

            } else {

                throw new OException(errorMsg);
            }

        } finally {

            GarSafe.destroy(tblIndexName);
        }

        return currConvention;
    }

}
