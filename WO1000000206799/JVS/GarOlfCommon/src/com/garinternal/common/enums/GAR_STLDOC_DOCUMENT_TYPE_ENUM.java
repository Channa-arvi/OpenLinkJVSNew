package com.garinternal.common.enums;

/*
File Name:                      GAR_STLDOC_DOCUMENT_TYPE_ENUM.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum for Stldoc document types

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import com.garinternal.common.enums.validator.GarEnumInfo;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.SHM_USR_TABLES_ENUM;
import com.olf.openjvs.fnd.RefBase;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)
@GarEnumInfo(tableName = "stldoc_document_type", stringColumn = "doc_type_desc", dBColumns = { "doc_type" }, methods = { "toInt" })

public enum GAR_STLDOC_DOCUMENT_TYPE_ENUM {
    //@formatter:off
    //                   Name                   Id           Is System Defined        Is single Event Mode
    INVOICE             ("Invoice",             -1,          1,                       0),
    CONFIRM             ("Confirm",             -1,          1,                       1),
    RATE_RESET_NOTICE   ("Rate Reset Notice",   -1,          1,                       1),
    CREDIT_DEBIT_NOTE   ("Credit/Debit Note",   -1,          0,                       0),
    ADVANCE             ("Advance",             -1,          0,                       0),
    OPERATIONS_DOC      ("OperationsDoc",       -1,          0,                       0),
    OPERATIONS_DOC_SI   ("OperationsDocSI",     -1,          0,                       0),
    OPERATIONS_DOC_PL   ("OperationsDocPL",     -1,          0,                       0),
    PROFORMA_INVOICE    ("ProformaInvoice",     -1,          0,                       0),
    ;
    //@formatter:on

    private String  name;
    private int     id;
    private boolean isSystemDefined;
    private boolean isSingleEventMode;

    GAR_STLDOC_DOCUMENT_TYPE_ENUM(String name, int id, int isSystemDefined, int isSingleEventMode) {
        this.name              = name;
        this.id                = id;
        this.isSystemDefined   = (isSystemDefined == 1);
        this.isSingleEventMode = (isSingleEventMode == 1);
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Returns Settlement Doc Type Id
     *
     * @return Doc Type ID
     * @throws OException {@link OException}
     */
    public int toInt() throws OException {
        int idLocal = this.id;

        if (idLocal == -1) {
            idLocal = RefBase.getValue(SHM_USR_TABLES_ENUM.STLDOC_DOCUMENT_TYPE_TABLE, this.name);
        }

        return idLocal;
    }

    /**
     * Returns if the Settlement Doc Type is system defined
     *
     * @return
     */
    public boolean isSystemDefined() {
        return this.isSystemDefined;
    }

    /**
     * Returns if the Settlement Doc Type is system defined
     *
     * @return
     */
    public boolean isSingleEventMode() {
        return this.isSingleEventMode;
    }
}
