package com.garinternal.fo.fieldnotification;

/*
File Name:                      GarCommDescMain.java

Script Type:                    MAIN
Parameter Script:               None
Display Script:                 None

Description:
This script is an Instrument Builder's Notification Fields script.
This script will concatenate values of <Commodity Certificate> + <Commodity Sub Group> = Commodity Description
If <Commodity Certificate> is none, value will be set to blank.
<Commodity Sub Group > is to derive from the full name of the commodity sub group, example: CPO -> CRUDE PALM OIL.

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000119461 |             | Willyam    | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */
import com.garinternal.basic.GarBasicScript;
import com.garinternal.common.enums.GAR_TRAN_INFO_ENUM;
import com.garinternal.common.util.GarHasAValue;
import com.garinternal.common.util.GarLogger;
import com.garinternal.common.util.GarSafe;
import com.garinternal.common.util.GarStrict;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.Transaction;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.TRANF_FIELD;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_FIELD_NOTIFICATION)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)

public class GarCommDescMain extends GarBasicScript {
    private GarLogger logger;

    /**
     *
     * Constructor
     *
     * @throws OException {@link OException}
     */
    public GarCommDescMain() throws OException {
        super();
        this.logger = super.getLoggerInstance();
    }

    @Override
    public void execute(Table argt, Table returnt) throws OException {

        if (GarHasAValue.isTableEmpty(argt)) {
            this.logger.info("Either the param script failed or was not set in the task");
            return;
        }

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
        // get current selected commodity certificate
        String comcert =
            tranPointer.getField(TRANF_FIELD.TRANF_TRAN_INFO, 0, GAR_TRAN_INFO_ENUM.T_COMMODITY_CERTIFICATE.toString(), -1, -1);

        // get current selected index sub group full name
        String idxsubgroup     = tranPointer.getField(TRANF_FIELD.TRANF_IDX_SUBGROUP, 1, "Commodity Sub-Group", -1, -1);
        String idxsubgroupfull = this.getIdxSubGroupFullName(idxsubgroup);

        // set commodity description field if certificate is selected
        if ("None".equals(comcert)) {
            tranPointer.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, GAR_TRAN_INFO_ENUM.T_COMMODITY_DESC.toString(), idxsubgroupfull, 0,
                0, -1, -1);
        } else {
            tranPointer.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, GAR_TRAN_INFO_ENUM.T_COMMODITY_DESC.toString(),
                comcert + " " + idxsubgroupfull, 0, 0, -1, -1);
        }

    }

    /**
     * Returns the index sub group full name
     *
     * @param idxSubGroup Index Sub Group
     * @return idxSubGroupFull Index Sub Group Full Name
     * @throws OException
     */
    private String getIdxSubGroupFullName(String idxSubGroup) throws OException {
        Table  tblIndexName    = Util.NULL_TABLE;
        String idxSubGroupFull = "";

        try {
            String sql = "\n SELECT description " //
                + "\n FROM idx_subgroup " //
                + "\n WHERE name = " + "'" + idxSubGroup + "'"; //

            String errorMsg = "Unable to find a idxSubGroup Full Name with supplied idxsubgroup: " + idxSubGroup + "";

            tblIndexName = GarStrict.query(sql, errorMsg, this.logger);

            if (!GarHasAValue.isTableEmpty(tblIndexName)) {
                idxSubGroupFull = tblIndexName.getString("description", 1);

                if (GarHasAValue.hasAValue(idxSubGroupFull, true)) {
                    throw new OException(errorMsg);
                }

            } else {
                throw new OException(errorMsg);
            }

        } finally {
            GarSafe.destroy(tblIndexName);
        }

        return idxSubGroupFull;
    }

}
