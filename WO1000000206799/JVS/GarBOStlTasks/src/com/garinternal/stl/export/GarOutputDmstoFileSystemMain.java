package com.garinternal.stl.export;

/*
File Name:                      GarOutputDmstoFileSystemMain.java

Script Type:                    MAIN
Parameter Script:               None
Display Script:                 None

Description:
This script exports the selected documents on the Settlement Desktop screen to the File System

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000206799 |             | Khalique   | Initial Version
---------------------------------------------------------------------------
 */

import com.garinternal.basic.GarBasicScript;
import com.garinternal.common.util.GarHasAValue;
import com.garinternal.common.util.GarLogger;
import com.garinternal.common.util.GarSafe;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Query;
import com.olf.openjvs.Table;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_EVENT_DOC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)

public class GarOutputDmstoFileSystemMain extends GarBasicScript {
    private GarLogger logger;

    /**
     * Constructor
     *
     * @throws OException {@link OException}
     */
    public GarOutputDmstoFileSystemMain() throws OException {
        super();
        this.logger = super.getLoggerInstance();
    }

    @Override
    public void execute(Table argt, Table returnt) throws Exception {
        boolean isError       = false;
        String  errorMsg      = "";
        int     queryId       = -1;
        Table   tblLatestDocs = Util.NULL_TABLE;
        Table   tblResults    = Util.NULL_TABLE;

        try {
            Table tblStlEvents = argt.getTable("settlement_desktop_events", 1);
            tblStlEvents.deleteWhereValue("select_flag", 0);

            if (GarHasAValue.isTableEmpty(tblStlEvents)) {
                throw new OException("No rows selected for processing");
            }

            tblStlEvents.deleteWhereValue("document_num", 0);

            if (GarHasAValue.isTableEmpty(tblStlEvents)) {
                throw new OException("All the selected rows are in Undesignated status");
            }

            int               templateId = tblStlEvents.getInt("stldoc_template_id", 1);
            int               docTypeId  = tblStlEvents.getInt("doc_type", 1);
            GarDmsBasicOutput output     = GarDmsOutputFactory.getOutputInstance(templateId, docTypeId, this.logger);

            Table  tblUserParams = argt.getTable("user_input_params", 1);
            String filePrefix    = tblUserParams.getString("file_name_prefix", 1);
            queryId       = Query.tableQueryInsert(tblStlEvents, "document_num");
            tblLatestDocs = output.getOutputDocFileData(queryId, filePrefix);

            if (!GarHasAValue.isTableEmpty(tblLatestDocs)) {
                tblResults = output.outputDocs(tblLatestDocs, tblUserParams);
            } else {
                throw new OException("None of the selected documents have a document generated to be exported");
            }

        } catch (OException e) {
            isError  = true;
            errorMsg = e.getMessage();
            this.logger.error(errorMsg);
        } finally {
            returnt.addCol("status_table", COL_TYPE_ENUM.COL_TABLE);
            returnt.addCol("status_message", COL_TYPE_ENUM.COL_STRING);
            returnt.addRow();

            returnt.setTable("status_table", 1, tblResults.copyTable());

            if (isError) {
                returnt.setString("status_message", 1, errorMsg);
            } else {
                returnt.setString("status_message", 1, "");
            }

            GarSafe.clear(queryId);
            GarSafe.destroy(tblLatestDocs, tblResults);
        }

    }
}
