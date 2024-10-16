package com.garinternal.common.enums;

/*
File Name:                      GAR_USER_TABLE_ENUM.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum for USER tables in the system

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import com.garinternal.common.enums.validator.GarEnumInfo;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)
@GarEnumInfo(tableName = "sys.tables", stringColumn = "name")

public enum GAR_USER_TABLE_ENUM {
    //@formatter:off
	USER_BBG_INDEX                   ("USER_bbg_index"),
	USER_EMAIL_CONFIG                ("USER_email_config"),
	USER_CONST_REPOSITORY            ("USER_const_repository"),
	USER_SCRIPT_LOGS                 ("USER_script_logs"),
	USER_EMAIL_STATUS                ("USER_email_status"),
	USER_HEALTH_CHECKS               ("USER_health_checks"),
	USER_SAP_MD_COMPANY_CODE         ("USER_sap_md_company_code"),
	USER_LOCAL_INTERNATIONAL         ("USER_local_international"),
    USER_SAP_SALES_AREA              ("user_sap_sales_area"),
    USER_SAP_MD_SALES_ORG            ("USER_sap_md_sales_org"),
    USER_SAP_MD_DISTR_CHL            ("USER_sap_md_distr_chl"),
    USER_SAP_MD_DIVISION             ("USER_sap_md_division"),
    USER_SAP_MD_PURCH_ORG            ("USER_sap_md_purch_org"),
    USER_SAP_MD_PURCH_GRP            ("USER_sap_md_purch_grp"),
    USER_SAP_MD_PARTNER_PCA          ("USER_sap_md_partner_pca"),
    USER_FILTER_PHYS_EXTBU           ("USER_filter_phys_extbu"),
    USER_SAP_MD_MATERIAL_MAPPING     ("USER_sap_md_material_mapping"),
    USER_SAP_MATERIAL_CODE           ("USER_sap_material_code"),

	;
    //@formatter:on

    private final String name;

    GAR_USER_TABLE_ENUM(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
