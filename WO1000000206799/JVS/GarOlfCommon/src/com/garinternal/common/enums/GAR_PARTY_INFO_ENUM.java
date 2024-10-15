package com.garinternal.common.enums;

/*
File Name:                      GAR_PARTY_INFO_ENUM.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum for Parcel Info types

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import com.garinternal.common.enums.validator.GarEnumInfo;
import com.garinternal.common.util.GarHasAValue;
import com.garinternal.common.util.GarSafe;
import com.garinternal.common.util.GarStrict;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.TRAN_INFO_DATA_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)
@GarEnumInfo(tableName = "tran_info_types", stringColumn = "type_name", dBColumns = { "type_id", "data_type" },
    methods = { "toInt", "getTranInfoDataTypeId" })

public enum GAR_PARTY_INFO_ENUM {
    //@formatter:off
	//                                 Name                                    Id    Data Type
    PTY_SAP_CUSTOMER_CODE_INT      ("pty_sap_customer_code_int",       -1,  TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    PTY_SAP_CUSTOMER_CODE_EXT      ("pty_sap_customer_code_ext",       -1,  TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    PTY_SAP_VENDOR_CODE_INT        ("pty_sap_vendor_code_int",         -1,  TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    PTY_SAP_VENDOR_CODE_EXT        ("pty_sap_vendor_code_ext",         -1,  TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    PTY_SAP_COMPANY_CODE_EXT       ("pty_sap_company_code_ext",        -1,  TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    PTY_SAP_COMPANY_CODE_INT       ("pty_sap_company_code_int",        -1,  TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    PTY_REPORTING_CURRENCY         ("pty_reporting_currency",          -1,  TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    PTY_LE_SHORT_CODE              ("pty_le_short_code",               -1,  TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    PTY_ACCOUNTING_SCOPE           ("pty_accounting_scope",            -1,  TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    PTY_OKAY_TO_PAY                ("pty_okay_to_pay",                 -1,  TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    PTY_SAP_INSTANCE               ("pty_sap_instance",                -1,  TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    PTY_BACKSTOP_ENTITY_TYPE       ("pty_backstop_entity_type",        -1,  TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    PTY_SAP_SALES_CONTRACT_EXIST   ("pty_sap_sales_contract_exist",    -1,  TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    PTY_SAP_PUR_CONTRACT_EXIST     ("pty_sap_pur_contract_exist",      -1,  TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    PTY_SAP_COMPANY_CODE_EDEALSLIP ("pty_sap_company_code_edealslip",  -1,  TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    PTY_IS_UPSTREAM_ENTITY         ("pty_is_upstream_entity",          -1,  TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    ;
    //@formatter:on

    private String                   name;
    private int                      id;
    private TRAN_INFO_DATA_TYPE_ENUM parcelInfoDataType;

    GAR_PARTY_INFO_ENUM(String name, int id, TRAN_INFO_DATA_TYPE_ENUM parcelInfoDataType) {
        this.name               = name;
        this.id                 = id;
        this.parcelInfoDataType = parcelInfoDataType;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Returns Tran Info Type Id
     *
     * @return Tran Info Type Id
     * @throws OException {@link OException}
     */
    public int toInt() throws OException {
        int   idLocal = this.id;
        Table tblId   = Util.NULL_TABLE;

        try {

            if (idLocal == -1) {
                StringBuilder sql = new StringBuilder() //
                    .append("\n SELECT type_id") //
                    .append("\n FROM   parcel_info_types") //
                    .append("\n WHERE  type_name = '" + this.name + "'");

                tblId = GarStrict.query(sql, null);

                if (!GarHasAValue.isTableEmpty(tblId)) {
                    idLocal = tblId.getInt("type_id", 1);
                }

            }

        } finally {
            GarSafe.destroy(tblId);
        }

        return idLocal;
    }

    /**
     * Returns Data Type for the Info field
     *
     * @return
     */
    public TRAN_INFO_DATA_TYPE_ENUM getParcelInfoDataType() {
        return this.parcelInfoDataType;
    }

    /**
     * Returns Data Type Id for the Info field
     *
     * @return
     */
    public int getParcelInfoDataTypeId() {
        return this.parcelInfoDataType.toInt();
    }
}
