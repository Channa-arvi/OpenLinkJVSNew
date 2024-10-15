package com.garinternal.common.enums;

/*
File Name:                      GAR_TRAN_INFO_ENUM.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum for Tran Info types

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

public enum GAR_TRAN_INFO_ENUM {
    //@formatter:off
	//                                 Name                                    Id    Data Type
    ACTUAL_INPUT_DATE                 ("Actual Input Date",                    -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    EXERCISE_ACTUAL_QTY               ("Exercise Actual Qty",                  -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_DOUBLE),
    EXERCISE_BY_SETTLE_PRICE          ("Exercise By Settle Price",             -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    NDF_FAR_CCY                       ("NDF Far CCY",                          -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    NDF_FAR_LEG_AMOUNT                ("NDF Far Leg Amount",                   -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_DOUBLE),
    NDF_FAR_LEG_FIXING_DATE           ("NDF Far Leg Fixing Date",              -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_DATE),
    NDF_FAR_LEG_FWD_POINTS            ("NDF Far Leg Fwd Points",               -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_DOUBLE),
    NDF_FAR_LEG_FWD_RATE              ("NDF Far Leg Fwd Rate",                 -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_DOUBLE),
    NDF_FAR_LEG_SETTLE_DATE           ("NDF Far Leg Settle Date",              -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_DATE),
    T_ALLOC_NON_ALLOCATION_BIO        ("t_alloc_non_allocation_bio",           -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_AREA_CODE                       ("t_area_code",                          -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_BACKSTOP_INTENT                 ("t_backstop_intent",                    -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_BANK_ID                         ("t_Bank ID",                            -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_BB_REVISE_VER                   ("t_BB Revise Ver",                      -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_BB_TRANS_TYPE                   ("t_BB Trans Type",                      -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_BILL_TO_COUNTERPARTY            ("t_bill_to_counterparty",               -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_BONDED_N_NONBONDED_INDICATOR    ("t_bonded_n_nonbonded_indicator",       -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_BROKER_REF                      ("t_broker_ref",                         -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_CERTIFICATE_NUMBER              ("t_certificate_number",                 -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_CLEARING_BROKER_ACCT            ("t_Clearing Broker Acct",               -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_CN_DEAL_RPRICE                  ("t_cn_deal_rprice",                     -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_DOUBLE),
    T_CN_PAYTERM_CONTRACT             ("t_cn_payterm_contract",                -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_INT),
    T_CN_PAYTERM_FIXING               ("t_cn_payterm_fixing",                  -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_INT),
    T_CN_PAYTERM_MARGIN               ("t_cn_payterm_margin",                  -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_INT),
    T_CN_PAYTERM_VARIATION            ("t_cn_payterm_variation",               -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_INT),
    T_COMMODITY                       ("t_commodity",                          -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_COMMODITY_CERTIFICATE           ("t_commodity_certificate",              -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_COMMODITY_DESC                  ("t_commodity_desc",                     -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_CONTRACT_CHECK                  ("t_Contract Check?",                    -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_CONTRACT_REFERENCE              ("t_contract_reference",                 -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_CONTRACT_REMARKS                ("t_contract_remarks",                   -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_CONTRACT_TERM                   ("t_contract_term",                      -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_CONTRACT_TERMS_REMARKS          ("t_contract_terms_remarks",             -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_CONTRACT_TYPE                   ("t_contract_type",                      -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_COUNTERPARTY_INV_REF            ("t_counterparty_inv_ref",               -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_CSA_LINE                        ("t_CSA Line",                           -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_DEAL_LENTITY_NUMBER             ("t_deal_lentity_number",                -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_DEAL_SLIP_NO                    ("t_deal_slip_no",                       -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_DISCHARGE_PORT                  ("t_discharge_port",                     -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_EXECUTION_BROKER_ACCT           ("t_Execution Broker Acct",              -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_EXTERNAL_DEAL_REF               ("t_external_deal_ref",                  -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_EXTERNAL_OFFSET_DEAL_REF        ("t_external_offset_deal_ref",           -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_FOB_DATE                        ("t_fob_date",                           -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_DATE),
    T_FOB_PRICE                       ("t_fob_price",                          -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_DOUBLE),
    T_FOSFA_MAX_TOLERANCE             ("t_fosfa_max_tolerance",                -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_DOUBLE),
    T_FOSFA_MIN_TOLERANCE             ("t_fosfa_min_tolerance",                -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_DOUBLE),
    T_FOSFA_SETTLEMENT_TYPE           ("t_fosfa_settlement_type",              -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_FREIGHT_TERM                    ("t_freight_term",                       -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_FUTURES_MONTH                   ("t_futures_month",                      -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_FUTURES_TERM                    ("t_futures_term",                       -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_FX_RATE_DEAL_RPT_CURR           ("t_fx_rate_deal_rpt_curr",              -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_DOUBLE),
    T_GMO_CERTIFICATION               ("t_gmo_certification",                  -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_IS_LOCAL                        ("t_is_local",                           -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_IS_RETURN_CONTRACT              ("t_is_return_contract",                 -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_LAST_PRICE_FIX_DATE             ("t_last_price_fix_date",                -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_DATE),
    T_LATE_PENALTY_CLAUSE_DELIVERY    ("t_late_penalty_clause_delivery",       -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_LATE_PENALTY_CLAUSE_PAYMENT     ("t_late_penalty_clause_payment",        -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_LATE_PENALTY_PERCENT            ("t_late_penalty_percent",               -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_DOUBLE),
    T_LIT__MIN_TOLERANCE              ("t_lit__min_tolerance",                 -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_DOUBLE),
    T_LIT_MAX_TOLERANCE               ("t_lit_max_tolerance",                  -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_DOUBLE),
    T_LIT_MIN_TOLERANCE               ("t_lit_min_tolerance",                  -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_DOUBLE),
    T_LIT_SETTLEMENT_TYPE             ("t_lit_settlement_type",                -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_LOAD_PORT                       ("t_load_port",                          -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_NDF_SWAP_ID                     ("t_NDF Swap ID",                        -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_NDF_SWAP_LEG                    ("t_NDF Swap Leg",                       -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_OFFSET_BILL_TO_COUNTERPARTY     ("t_offset_bill_to_counterparty",        -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_OFFSET_CONTRACT_REF             ("t_offset_contract_ref",                -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_OFFSET_DEAL_LENTITY_NUM         ("t_offset_deal_lentity_num",            -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_OFFSET_SHIP_TO_COUNTERPARTY     ("t_offset_ship_to_counterparty",        -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_OPERATION_COST_TEMPLATE         ("t_operation_cost_template",            -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_OPS_COST_DEAL_FEEDEF            ("t_ops_cost_deal_feedef",               -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_TABLE),
    T_OPTIONAL_GRADE                  ("t_optional_grade",                     -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_TABLE),
    T_OPTIONAL_PAY_INDICATOR          ("t_optional_pay_indicator",             -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_OPTIONAL_PORT                   ("t_optional_port",                      -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_TABLE),
    T_ORIGINAL_CONTRACT_REFERENCE     ("t_original_contract_reference",        -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_OTHER_PENALTY_CLAUSE            ("t_other_penalty_clause",               -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_PACKING_DESCRIPTION             ("t_packing_description",                -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_PACKING_TYPE                    ("t_packing_type",                       -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_PAPERCRUSH_LINK_DEAL_NUM        ("t_papercrush_link_deal_num",           -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_INT),
    T_PAPERCRUSH_PRICE_DETERMINATION  ("t_papercrush_price_determination",     -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_PARENT_CONTRACT_REF             ("t_parent_contract_ref",                -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_PARTIAL_SHIPMENT                ("t_partial_shipment",                   -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_PAYMENT_TERM                    ("t_payment_term",                       -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_PAYMENT_TERM_CATEGORY           ("t_payment_term_category",              -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_PAYMENT_TERM_EVENT              ("t_payment_term_event",                 -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_TABLE),
    T_PHYSICAL_BROKER                 ("t_physical_broker",                    -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_PHYSICAL_SIT_FLAG               ("t_physical_sit_flag",                  -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_PLACE_OF_ISSUE                  ("t_place_of_Issue",                     -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_PLACE_OF_RECEIPT                ("t_place_of_receipt",                   -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_PLANT_CODE                      ("t_plant_code",                         -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_PRICE_TERM                      ("t_price_term",                         -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_PRODUCT_USAGE                   ("t_product_usage",                      -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_PURCHASE_ORG                    ("t_purchase_org",                       -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_PURPOSE                         ("t_Purpose",                            -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_QUALITY_BASIS                   ("t_quality_basis",                      -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_QUALITY_PENALTY_CLAUSE          ("t_quality_penalty_clause",             -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_QUANTITY_BASIS                  ("t_quantity_basis",                     -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_SAP_CONTRACT_DOC_TYPE           ("t_sap_contract_doc_type",              -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_SAP_CONTRACT_INTERFACE_STATUS   ("t_sap_contract_interface_status",      -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_SAP_CONTRACT_ITEM_CATEGORY      ("t_sap_contract_item_category",         -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_SAP_CONTRACT_ITEM_NUM           ("t_sap_contract_item_num",              -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_SAP_CONTRACT_NUM                ("t_sap_contract_num",                   -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_SAP_DIST_CHANNEL                ("t_sap_dist_channel",                   -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_SAP_DIVISION                    ("t_sap_division",                       -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_SAP_MATERIAL_CODE               ("t_sap_material_code",                  -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_SAP_PARTNER_PCA                 ("t_sap_partner_pca",                    -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_SAP_PAYMENT_TERM_CODE           ("t_sap_payment_term_code",              -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_SAP_PURCH_GRP                   ("t_sap_purch_grp",                      -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_SAP_SALES_ORG                   ("t_sap_sales_org",                      -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_SAP_SEND_TO_SAP                 ("t_sap_send_to_sap",                    -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_SG_EXT_BU                       ("t_sg_ext_bu",                          -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_SHIP_TO_COUNTERPARTY            ("t_ship_to_counterparty",               -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_SHIPPING_POINT                  ("t_shipping_point",                     -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_SHIPPING_TYPE                   ("t_shipping_type",                      -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_SIGNED_STATUS                   ("t_signed_status",                      -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_STF_INDICATOR                   ("t_stf_indicator",                      -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_TRADE_CONTROL                   ("t_Trade Control",                      -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),
    T_TRADE_CONTROL_REMARK            ("t_Trade Control Remark",               -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_TRADE_IMPORT_ID                 ("t_Trade Import ID",                    -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_TRADE_SOURCE                    ("t_Trade Source",                       -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
    T_VESSEL_SIZE                     ("t_vessel_size",                        -1,   TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_USER_PICK_LIST),

	;
    //@formatter:on

    private String                   name;
    private int                      id;
    private TRAN_INFO_DATA_TYPE_ENUM tranInfoDataType;

    GAR_TRAN_INFO_ENUM(String name, int id, TRAN_INFO_DATA_TYPE_ENUM tranInfoDataType) {
        this.name             = name;
        this.id               = id;
        this.tranInfoDataType = tranInfoDataType;
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
                StringBuilder sql = new StringBuilder().append("\n SELECT type_id").append("\n FROM   tran_info_types")
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
    public TRAN_INFO_DATA_TYPE_ENUM getTranInfoDataType() {
        return this.tranInfoDataType;
    }

    /**
     * Returns Data Type Id for the Info field
     *
     * @return
     */
    public int getTranInfoDataTypeId() {
        return this.tranInfoDataType.toInt();
    }
}
