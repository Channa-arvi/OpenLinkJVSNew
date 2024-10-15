package com.garinternal.common.util.runnable;

/*
File Name:                      GarHealthChecksMain.java

Script Type:                    MAIN
Parameter Script:               None
Display Script:                 None

Description:
Script to perform health checks as defined in user table USER_health_checks

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import com.garinternal.basic.GarBasicScript;
import com.garinternal.common.email.GarEmailUtil;
import com.garinternal.common.enums.GAR_USER_TABLE_ENUM;
import com.garinternal.common.util.GarHasAValue;
import com.garinternal.common.util.GarLogger;
import com.garinternal.common.util.GarSafe;
import com.garinternal.common.util.GarStrict;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)

public class GarHealthChecksMain extends GarBasicScript {

    private static final String MAIL_CONTEXT = "GarHealthChecksMain";
    private GarLogger           logger;

    /**
     * Constructor
     * 
     * @throws OException {@link OException}
     */
    public GarHealthChecksMain() throws OException {
        super();
        this.logger = new GarLogger(this.getClass().getSimpleName());
    }

    @Override
    public void execute(Table argt, Table returnt) throws Exception {
        Table tblResults = Util.NULL_TABLE;

        try {
            tblResults = this.runHealthChecksAndGetResults();

            if (!GarHasAValue.isTableEmpty(tblResults)) {
                GarEmailUtil.sendEmail(MAIL_CONTEXT, "Status Error", "", "", "", tblResults, this.logger);
            } else {
                GarEmailUtil.sendEmail(MAIL_CONTEXT, "Status No Error", "", "", "", this.logger);
            }

        } finally {
            GarSafe.destroy(tblResults);
        }

    }

    /**
     * Run health check SQL's from USER_health_checks table and return the results
     * 
     * @return
     * @throws OException
     */
    private Table runHealthChecksAndGetResults() throws OException {
        Table tblHealthCheckConfig = Util.NULL_TABLE;
        Table tblHealthCheckResult = Util.NULL_TABLE;
        Table tblResults           = Table.tableNew();

        try {
            tblResults.addCols("I(id_number) S(name) S(message)");

            StringBuilder sql = new StringBuilder().append("\n SELECT *")
                .append("\n FROM   " + GAR_USER_TABLE_ENUM.USER_HEALTH_CHECKS.toString()).append("\n WHERE  active_flag = 1");

            tblHealthCheckConfig = GarStrict.query(sql, this.logger);

            if (!GarHasAValue.isTableEmpty(tblHealthCheckConfig)) {
                int numRows           = tblHealthCheckConfig.getNumRows();
                int colNumHCSql       = tblHealthCheckConfig.getColNum("sql");
                int colNumHCName      = tblHealthCheckConfig.getColNum("name");
                int colNumHCMessage   = tblHealthCheckConfig.getColNum("message");
                int colNumHCIdNumber  = tblHealthCheckConfig.getColNum("id_number");
                int colNumResName     = tblResults.getColNum("name");
                int colNumResMessage  = tblResults.getColNum("message");
                int colNumResIdNumber = tblResults.getColNum("id_number");

                for (int rowNum = 1; rowNum <= numRows; rowNum++) {
                    int rowNumTblRes = tblResults.addRow();

                    try {
                        String sqlHealthCheck = tblHealthCheckConfig.getString(colNumHCSql, rowNum);
                        String name           = tblHealthCheckConfig.getString(colNumHCName, rowNum);
                        String message        = tblHealthCheckConfig.getString(colNumHCMessage, rowNum);
                        int    idNumber       = tblHealthCheckConfig.getInt(colNumHCIdNumber, rowNum);

                        tblResults.setString(colNumResName, rowNumTblRes, name);
                        tblResults.setInt(colNumResIdNumber, rowNumTblRes, idNumber);

                        tblHealthCheckResult = GarStrict.query(sqlHealthCheck, this.logger);

                        if (!GarHasAValue.isTableEmpty(tblHealthCheckResult)) {
                            int count = tblHealthCheckResult.getInt(1, 1);

                            if (count != 0) {
                                tblResults.setString(colNumResMessage, rowNumTblRes, message.replace("%d", String.valueOf(count)));
                            } else {
                                // no need to report if number of issues reported are zero.
                                tblResults.delRow(rowNumTblRes);
                            }

                        } else {
                            // no need to report if number of issues reported are zero.
                            tblResults.delRow(rowNumTblRes);
                        }

                    } catch (OException e) {
                        tblResults.setString(colNumResMessage, rowNumTblRes, "Failure while running SQL: " + e.getMessage());
                    }

                }

            }

        } finally {
            GarSafe.destroy(tblHealthCheckConfig, tblHealthCheckResult);
        }

        return tblResults;
    }
}
