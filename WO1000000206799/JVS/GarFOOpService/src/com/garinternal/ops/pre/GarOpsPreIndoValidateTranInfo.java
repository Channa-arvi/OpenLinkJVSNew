package com.garinternal.ops.pre;

/*
File Name:                      GarOpsPreIndoValidateTranInfo.java

Script Type:                    MAIN
Parameter Script:               None
Display Script:                 None

Description:
This script is an Operational Services Trading Pre-Processing script.

It checks to make sure each deal that is about to be processed has
    1. The valid payment term selected based on their external counterparty based on user table: USER_counterparty_payment_term.

If the above fields are not valid, process will be blocked.

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
                 |             |Channa Arvi | Initial Version
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
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.Transaction;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.TRANF_FIELD;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_OPS_SERVICE)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)

public class GarOpsPreIndoValidateTranInfo extends GarBasicScript {
    private GarLogger logger;

    /**
     * Constructor
     *
     * @throws OException {@link OException}
     */
    public GarOpsPreIndoValidateTranInfo() throws OException {
        super();
        this.logger = super.getLoggerInstance();
    }

    @Override
    public void execute(Table argt, Table returnt) throws OException {

        int totalTrans = OpService.retrieveNumTrans();

        // Loop one time for each deal
        for (int tranCount = 1; tranCount <= totalTrans; tranCount++) {
            // Get a pointer to the Instrument
            Transaction tranPointer = OpService.retrieveTran(tranCount);

            boolean isPaymentTermValid = this.isPaymentTermValid(tranPointer);

            StringBuilder errorMsg = new StringBuilder();
            int           count    = 1;

            if (!isPaymentTermValid) {
                errorMsg.append("\n" + count + ". Payment Term is not valid for selected Counterparty");
            }

            if (GarHasAValue.hasAValue(errorMsg, true)) {
                // Set allow_override to 0 - means totally block
                OpService.serviceFail(errorMsg.toString(), 0);
            }

        }

    }

    /**
     * Validate Payment term
     *
     * @param tranPointer
     * @return
     * @throws OException
     */
    private boolean isPaymentTermValid(Transaction tranPointer) throws OException {

        Table   tblPaymentTerm     = Table.tableNew();
        boolean isPaymentTermValid = true;

        try {
            /*Check if current trade's external counterparty and payment term is listed in USER_counterparty_payment_term*/
            String sqlPaymentTermquery = "\n SELECT cpty_ext_le, payment_term_short_code, status " //
                + "\n FROM   " + GAR_USER_TABLE_ENUM.USER_COUNTERPARTY_PAYMENT_TERM.toString() //
                + "\n WHERE  status = ' '" //
                + "\n   AND  cpty_ext_le = " + tranPointer.getFieldInt(TRANF_FIELD.TRANF_EXTERNAL_LENTITY, 0, null) //
                + "\n   AND  payment_term_short_code = '"
                + tranPointer.getField(TRANF_FIELD.TRANF_TRAN_INFO, 0, GAR_TRAN_INFO_ENUM.T_PAYMENT_TERM.toString(), -1, -1) + "'";

            GarStrict.query(tblPaymentTerm, sqlPaymentTermquery, "", this.logger);

            int numRows = tblPaymentTerm.getNumRows();

            // If there is no active payment term under the external counterparty, then ops service should be failed
            if (numRows < 1) {
                isPaymentTermValid = false;
            }

        } finally {
            GarSafe.destroy(tblPaymentTerm);
        }

        return isPaymentTermValid;
    }
}
