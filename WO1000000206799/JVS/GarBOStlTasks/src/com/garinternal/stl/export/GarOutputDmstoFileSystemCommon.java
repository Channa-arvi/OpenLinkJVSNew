package com.garinternal.stl.export;

/*
File Name:                      GarOutputDmstoFileSystemCommon.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Common Script for the DMS Output scripts

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000206799 |             | Khalique   | Initial Version
---------------------------------------------------------------------------
 */

import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

import java.util.ArrayList;
import java.util.List;

import com.garinternal.common.enums.GAR_STLDOC_DOCUMENT_TYPE_ENUM;
import com.garinternal.common.enums.GAR_STLDOC_TEMPLATE_ENUM;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public class GarOutputDmstoFileSystemCommon {
    private List<Integer> opsTemplates     = new ArrayList<>();
    private List<Integer> contractDocTypes = new ArrayList<>();
    private List<Integer> invoiceDocTypes  = new ArrayList<>();

    /**
     * Constructor
     *
     * @throws OException {@link OException}
     */
    public GarOutputDmstoFileSystemCommon() throws OException {
        this.addOpsTemplates();
        this.addContractDocTypes();
        this.addInvoiceDocTypes();
    }

    /**
     * Get OPS Templates
     *
     * @return Ops Templates
     */
    public List<Integer> getOpsTemplates() {
        return new ArrayList<>(this.opsTemplates);
    }

    /**
     * Get Contract Document Types
     *
     * @return Contract Document Types
     */
    public List<Integer> getContractDocTypes() {
        return new ArrayList<>(this.contractDocTypes);
    }

    /**
     * Get Invoice Document Types
     *
     * @return Invoice Document Types
     */
    public List<Integer> getInvoiceDocTypes() {
        return new ArrayList<>(this.invoiceDocTypes);
    }

    /**
     * Is Invoice Document
     *
     * @param docType Doc Type
     * @return True/False
     */
    public boolean isInvoiceDoc(int docType) {
        return this.invoiceDocTypes.contains(docType);
    }

    /**
     * Is Contract Document
     *
     * @param docType Doc Type
     * @return True/False
     */
    public boolean isContractDoc(int docType) {
        return this.contractDocTypes.contains(docType);
    }

    /**
     * Is Operations Document
     *
     * @param templateId Template Id
     * @return True/False
     */
    public boolean isOperationsDoc(int templateId) {
        return this.opsTemplates.contains(templateId);
    }

    /**
     * Add Invoice Document Types
     *
     * @throws OException {@link OException}
     */
    private void addInvoiceDocTypes() throws OException {
        this.invoiceDocTypes.add(GAR_STLDOC_DOCUMENT_TYPE_ENUM.ADVANCE.toInt());
        this.invoiceDocTypes.add(GAR_STLDOC_DOCUMENT_TYPE_ENUM.CREDIT_DEBIT_NOTE.toInt());
        this.invoiceDocTypes.add(GAR_STLDOC_DOCUMENT_TYPE_ENUM.INVOICE.toInt());
        this.invoiceDocTypes.add(GAR_STLDOC_DOCUMENT_TYPE_ENUM.PROFORMA_INVOICE.toInt());
    }

    /**
     * Add Contract Document Types
     *
     * @throws OException {@link OException}
     */
    private void addContractDocTypes() throws OException {
        this.contractDocTypes.add(GAR_STLDOC_DOCUMENT_TYPE_ENUM.CONFIRM.toInt());
    }

    /**
     * Add Contract Templates
     *
     * @throws OException {@link OException}
     */
    private void addOpsTemplates() throws OException {
        this.opsTemplates.add(GAR_STLDOC_TEMPLATE_ENUM.OPS_APPOINTMENT_LETTER.toInt());
        this.opsTemplates.add(GAR_STLDOC_TEMPLATE_ENUM.OPS_PACKING_LIST.toInt());
        this.opsTemplates.add(GAR_STLDOC_TEMPLATE_ENUM.OPS_SHIPPING_INSTRUCTIONS.toInt());
    }

}
