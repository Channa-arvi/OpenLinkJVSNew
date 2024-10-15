package com.garinternal.common.util;

/*
File Name:                      GarDmsUtil.java

Script Type:                    Util
Parameter Script:               None
Display Script:                 None

Description:
This script contains functionality to deal with DMS

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000206799 |             | Khalique   | Initial Version
---------------------------------------------------------------------------
 */

import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

import com.garinternal.common.enums.GAR_OPENLINK_DOC_EXPORT_TYPE;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.SystemUtil;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_EVENT_DOC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)

public final class GarDmsUtil {
    public static final String DMS_HOST_EXE_FILE_NAME = "Olf.DG.DocumentManager.Host.exe";

    private GarDmsUtil() {
        // do nothing
    }

    /**
     * Export DMS file to PDF
     *
     * @param dmsDocumentFullPath Path of DMS document
     * @param pdfOutputFullPath   Path to export PDF
     * @throws OException {@link OException}
     */
    public static void exportDMSToPDF(String dmsDocumentFullPath, String pdfOutputFullPath) throws OException {
        exportDMSFile(dmsDocumentFullPath, pdfOutputFullPath, GAR_OPENLINK_DOC_EXPORT_TYPE.PDF);
    }

    /**
     * Export DMS file to Word
     *
     * @param dmsDocumentFullPath Path of DMS document
     * @param wordOutputFullPath  Path to export Word file
     * @throws OException {@link OException}
     */
    public static void exportDMSToWord(String dmsDocumentFullPath, String wordOutputFullPath) throws OException {
        exportDMSFile(dmsDocumentFullPath, wordOutputFullPath, GAR_OPENLINK_DOC_EXPORT_TYPE.DOCX);
    }

    /**
     * Export DMS file to Excel
     *
     * @param dmsDocumentFullPath Path of DMS document
     * @param excelOutputFullPath Path to export Excel
     * @throws OException {@link OException}
     */
    public static void exportDMSToCSV(String dmsDocumentFullPath, String excelOutputFullPath) throws OException {
        exportDMSFile(dmsDocumentFullPath, excelOutputFullPath, GAR_OPENLINK_DOC_EXPORT_TYPE.CSV);
    }

    /**
     * Export DMS File
     *
     * @param dmsDocumentFullPath Path of DMS document
     * @param outputFullPath      Path to export file
     * @param docExportType       {@link GAR_OPENLINK_DOC_EXPORT_TYPE} enum
     * @throws OException {@link OException}
     */
    public static void exportDMSFile(String dmsDocumentFullPath, String outputFullPath, GAR_OPENLINK_DOC_EXPORT_TYPE docExportType)
        throws OException {

        String command = "C:\\OpenLinkApp\\Endur_V17_0_12122019MR_12262019_1185SG\\bin\\DMS\\" + DMS_HOST_EXE_FILE_NAME + " -d " + "\""
            + dmsDocumentFullPath + "\"" + " -exportFormat " + docExportType.getFormatCode() + " -exportPath " + "\"" + outputFullPath
            + "\"";

        int retVal = SystemUtil.createProcess(command, -1);

        if (retVal != 1) {
            throw new OException("ERROR:Failed to export DMS to word document.");
        }

    }
}
