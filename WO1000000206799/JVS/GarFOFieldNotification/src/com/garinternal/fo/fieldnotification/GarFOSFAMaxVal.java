package com.garinternal.fo.fieldnotification;

/*
File Name:                      GarFOSFAMaxVal.java

Script Type:                    MAIN
Parameter Script:               None
Display Script:                 None

Description:
This script is an Instrument Builder's Notification Fields script.

It is used to copy the value of  fofsa max tolerance to fofsa min tolerance .

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000118967 |             |Alex Seow   | Initial Version
                 |             |            |
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

public class GarFOSFAMaxVal extends GarBasicScript {
    private GarLogger logger;

    /**
     *
     * Constructor
     *
     * @throws OException {@link OException}
     */
    public GarFOSFAMaxVal() throws OException {

        super();
        this.logger = super.getLoggerInstance();
    }

    @Override
    public void execute(Table argt, Table returnt) throws OException {

        Transaction tranPointer = argt.getTran("tran", 1);
        this.setTranInfoValues(tranPointer);
    }

    /**
     * Set TranInfo Values
     *
     *
     * @param tranPointer
     * @throws OException
     */
    private void setTranInfoValues(Transaction tranPointer) throws OException {
        // get current transaction number
        int tranNum = tranPointer.getTranNum();

        this.logger.info("\nIN TRIGGER SCRIPT TRAN_NUM = :" + tranNum);

        // get current value entered from fosfa max settlement field
        String colVal = tranPointer.getField(TRANF_FIELD.TRANF_TRAN_INFO, 0, GAR_TRAN_INFO_ENUM.T_FOSFA_MAX_TOLERANCE.toString(), -1, -1);

        this.logger.info("\nIN TRIGGER SCRIPT COLVAL = " + colVal);

        // The fofsa max settlement is 2, the fofsa min settlement value will set to the same value from fosfa max settlement
        int iRetVal = tranPointer.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, GAR_TRAN_INFO_ENUM.T_FOSFA_MIN_TOLERANCE.toString(),
            colVal, 0, 0, -1, -1);

        this.logger.info("\nIN TRIGGER SCRIPT RETVAL = " + iRetVal);

    }

}
