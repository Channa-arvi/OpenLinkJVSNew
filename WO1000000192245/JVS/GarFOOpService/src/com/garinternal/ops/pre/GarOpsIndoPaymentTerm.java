package com.garinternal.ops.pre;

/*
File Name:                      GarOpsIndoPaymentTerm.java

Script Type:                    MAIN
Parameter Script:               None
Display Script:                 None

Description:
This script is an Operational Services Trading Pre-Processing script.

It checks to make sure each deal that is about to be processed has
the valid payment term selected based on their external counterparty. This script will check on user table: USER_counterparty_payment_term.

If the Payment Term is not valid, process will be blocked.
.

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
                 |             |Channa Arvi | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */
import com.garinternal.basic.GarBasicScript;
import com.garinternal.common.util.GarLogger;
import com.garinternal.common.util.GarSafe;
import com.garinternal.common.util.GarStrict;
import com.olf.openjvs.OException;
import com.olf.openjvs.OpService;
import com.olf.openjvs.Table;
import com.olf.openjvs.Transaction;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.TRANF_FIELD;

public class GarOpsIndoPaymentTerm extends GarBasicScript {
    private GarLogger logger;

    /**
     * Constructor
     *
     * @throws OException {@link OException}
     */
    public GarOpsIndoPaymentTerm() throws OException {
        super();
        this.logger = super.getLoggerInstance();
    }

    @Override
    public void execute(Table argt, Table returnt) throws OException {

        int totalTrans = OpService.retrieveNumTrans();

        /* Loop one time for each deal*/
        for (int tranCount = 1; tranCount <= totalTrans; tranCount++) {
            /* Get a pointer to the Instrument*/
            Transaction tranPointer = OpService.retrieveTran(tranCount);

            this.validatePaymentTerm(tranPointer);

        }

    }

    /**
     * Get Index Sub Group Id
     *
     * @param indexId
     * @return
     * @throws OException
     */
    private void validatePaymentTerm(Transaction tranPointer) throws OException {

        Table tblPaymentTerm = Util.NULL_TABLE;
        tblPaymentTerm = Table.tableNew();
        boolean shouldFail = false;

        try {
            /*Check if current trade's external counterparty and payment term is listed in USER_counterparty_payment_term*/
            String sqlPaymentTermquery = "\nSELECT cpty_ext_le, payment_term_short_code, status "
                + "\n FROM USER_counterparty_payment_term " + "\n WHERE status = ' ' and cpty_ext_le = "
                + tranPointer.getFieldInt(TRANF_FIELD.TRANF_EXTERNAL_LENTITY, 0, null) + "\n AND payment_term_short_code = '"
                + tranPointer.getField(TRANF_FIELD.TRANF_TRAN_INFO, 0, "t_payment_term", -1, -1) + "'";

            String errorMsg = "Please Change: Payment Term is not valid for selected Counterparty ";

            GarStrict.query(tblPaymentTerm, sqlPaymentTermquery, errorMsg, this.logger);

            int numDeals = tblPaymentTerm.getNumRows();

            /*If there is no active payment term under the external counterparty, the set should_fail to 1*/
            if (numDeals < 1) {
                shouldFail = true;
            }

            /* Set the failure status if appropriate. */
            if (shouldFail) {
                /*  Set allow_override to 0 - means totally block */
                OpService.serviceFail(errorMsg, 0);
            }

        } finally {
            GarSafe.destroy(tblPaymentTerm);
        }

    }
}
