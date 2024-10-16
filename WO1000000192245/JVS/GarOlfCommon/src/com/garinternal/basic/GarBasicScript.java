package com.garinternal.basic;

/*
File Name:                      GarBasicScript.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
This script will be used as a wrapper class to be extended by every runnable script to be implemented. This script provides below
functionality:
	1. Automated logging for start and end of script
	2. Automated emails if configured in USER_email_config table

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import com.garinternal.common.email.GarEmailUtil;
import com.garinternal.common.enums.GAR_EMAIL_SUB_CONTEXT_ENUM;
import com.garinternal.common.util.GarHasAValue;
import com.garinternal.common.util.GarLogger;
import com.garinternal.common.util.GarSafe;
import com.garinternal.common.util.GarStrict;
import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.IScript;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.fnd.RefBase;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public abstract class GarBasicScript implements IScript {
    private String    className;
    private GarLogger logger;
    private String    successAttachments;
    private String    failureAttachments;
    private String    taskName;
    private String    scriptType;
    private String    successMsg = "";

    protected GarBasicScript() throws OException {
        Table tblInfo = Util.NULL_TABLE;

        try {
            this.className = this.getClass().getSimpleName();
            this.logger    = new GarLogger(this.className);
            tblInfo        = RefBase.getInfo();

            this.taskName   = tblInfo.getString("task_name", 1);
            this.scriptType = this.getScriptType();
        } finally {
            GarSafe.destroy(tblInfo);
        }

    }

    @Override
    public final void execute(IContainerContext context) throws OException {
        this.logger.start();

        boolean       isError  = false;
        StringBuilder errorMsg = new StringBuilder();

        try {
            this.execute(context.getArgumentsTable(), context.getReturnTable());
        } catch (Exception e) {
            isError = true;
            errorMsg.append(e.getMessage());

            for (StackTraceElement ste : e.getStackTrace()) {
                errorMsg.append("\n\t\t" + ste.toString());
            }

            this.logger.error(errorMsg.toString());
            throw new OException(e);
        } finally {

            try {

                if (isError) {
                    this.sendFailureEmail(errorMsg.toString());
                    this.logger.exitFail(errorMsg.toString());
                } else {
                    this.sendSuccessEmail();
                    this.logger.exitSucceed();
                }

            } catch (Exception e) {
                // do nothing
            }

        }

    }

    /**
     * Method to start execution at
     *
     * @param argt    argt
     * @param returnt returnt
     * @throws Exception {@link OException}
     */
    public abstract void execute(Table argt, Table returnt) throws Exception; /*NOSONAR - This is a common class
    which needs to handle all the known exception types for error logging*/

    /**
     * Get Logger instance
     *
     * @return
     */
    public GarLogger getLoggerInstance() {
        return this.logger;
    }

    /**
     * Set Success email message
     *
     * @param successMsg
     */
    public void setSuccessMessageEmail(String successMsg) {
        this.successMsg = successMsg;
    }

    /**
     * Set attachments for success email
     *
     * @param successAttachments
     */
    public void setSuccessAttachments(String successAttachments) {
        this.successAttachments = successAttachments;
    }

    /**
     * Get attachments for success email
     *
     * @return
     */
    public String getSuccessAttachments() {
        return this.successAttachments;
    }

    /**
     * Set attachments for failure email
     *
     * @param failureAttachments
     */
    public void setFailureAttachments(String failureAttachments) {
        this.failureAttachments = failureAttachments;
    }

    /**
     * Get attachments for failure email
     *
     * @return
     */
    public String getFailureAttachments() {
        return this.failureAttachments;
    }

    /**
     * Send success email
     *
     * @throws OException
     */
    private void sendSuccessEmail() throws OException {

        try {
            GarEmailUtil.sendEmail(this.taskName, this.scriptType + "_" + GAR_EMAIL_SUB_CONTEXT_ENUM.SUCCESS.toString(), "",
                this.successMsg, this.successAttachments, this.logger);
        } catch (Exception e) {
            this.logger.error("Failed to send email due to following reason:\n" + e.getMessage());
        }

    }

    /**
     * Send failure email
     *
     * @throws OException
     */
    private void sendFailureEmail(String errorMsg) throws OException {

        try {
            GarEmailUtil.sendEmail(this.taskName, this.scriptType + "_" + GAR_EMAIL_SUB_CONTEXT_ENUM.FAILURE.toString(), "", errorMsg,
                this.failureAttachments, this.logger);
        } catch (Exception e) {
            this.logger.error("Failed to send email due to following reason:\n" + e.getMessage());
        }

    }

    /**
     * Get Script type
     *
     * @return
     * @throws OException
     */
    private String getScriptType() throws OException {
        Table  tblScriptType = Table.tableNew();
        String scriptTypeStr = "";

        try {
            StringBuilder sql = new StringBuilder().append("\n SELECT     ats.script_type") //
                .append("\n FROM       avs_task_def_view atdv") //
                .append("\n INNER JOIN avs_task_scripts ats on ats.task_id = atdv.task_id") //
                .append("\n INNER JOIN plugin_view pv ON pv.plugin_id = ats.script_id") //
                .append("\n WHERE      atdv.name = '" + this.taskName + "'") //
                .append("\n   AND      pv.plugin_name = '" + this.className + "'"); //

            GarStrict.query(tblScriptType, sql.toString(), this.logger);

            if (!GarHasAValue.isTableEmpty(tblScriptType)) {
                scriptTypeStr = SCRIPT_TYPE_ENUM.fromInt(tblScriptType.getInt("script_type", 1)).toString().replace("_SCRIPT", "");
            }

        } finally {
            GarSafe.destroy(tblScriptType);
        }

        return scriptTypeStr;
    }
}
