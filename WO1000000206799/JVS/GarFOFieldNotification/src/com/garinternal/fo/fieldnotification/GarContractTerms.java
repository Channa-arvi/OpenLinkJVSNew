package com.garinternal.fo.fieldnotification;
/*
File Name:                      GarContractTerms.java

Script Type:                    MAIN
Parameter Script:               None
Display Script:                 None

Description:
This script is an Instrument Builder's Notification Fields script.

It is invoked when a change to tran info field: t_contract_terms is made. It will take the selected value of this field and
put it under tran info field: t_contract_terms_remarks in this format
"AS PER <value> CONTRACT TERMS".

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
                 |             |Willyam Anton| Initial Version
---------------------------------------------------------------------------
**/

import com.garinternal.basic.GarBasicScript;
import com.garinternal.common.enums.GAR_TRAN_INFO_ENUM;
import com.garinternal.common.util.GarLogger;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.Transaction;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.TRANF_FIELD;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_FIELD_NOTIFICATION)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)

public class GarContractTerms extends GarBasicScript {

    private GarLogger logger;

    /**
     * Constructor
     *
     * @throws OException {@link OException}
     */
    public GarContractTerms() throws OException {
        super();
        this.logger = super.getLoggerInstance();
    }

    @Override
    public void execute(Table argt, Table returnt) throws OException {
        // get current transaction number
        Transaction tranPointer = argt.getTran("tran", 1);
        this.setTranInfoValues(tranPointer);

    }

    /**
     * Set TranInfo Values
     *
     * @param tranPointer
     * @throws OException
     */
    private void setTranInfoValues(Transaction tranPointer) throws OException {
        int tranNum = tranPointer.getTranNum();
        this.logger.info("\nIN TRIGGER SCRIPT TRAN_NUM = :" + tranNum);

        // get current selected contract term
        String colVal = tranPointer.getField(TRANF_FIELD.TRANF_TRAN_INFO, 0, GAR_TRAN_INFO_ENUM.T_CONTRACT_TERM.toString(), -1, -1);

        // set selected contract term value to contract description field
        int iRetVal = tranPointer.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, GAR_TRAN_INFO_ENUM.T_CONTRACT_TERMS_REMARKS.toString(),
            "AS PER " + colVal + " CONTRACT TERMS", 0, 0, -1, -1);

        this.logger.info("\nIN TRIGGER SCRIPT RETVAL = " + iRetVal);

    }

}
