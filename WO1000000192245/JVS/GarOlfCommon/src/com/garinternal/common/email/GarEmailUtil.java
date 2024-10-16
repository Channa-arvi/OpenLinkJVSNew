package com.garinternal.common.email;

/*
File Name:                      GarEmailUtil.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
This script contains utility methods for sending emails

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import java.util.UUID;
import com.garinternal.common.constants.GarConstants;
import com.garinternal.common.enums.GAR_SERVICE_NAME_ENUM;
import com.garinternal.common.enums.GAR_USER_TABLE_ENUM;
import com.garinternal.common.util.GarHasAValue;
import com.garinternal.common.util.GarLogger;
import com.garinternal.common.util.GarSafe;
import com.garinternal.common.util.GarStrict;
import com.garinternal.common.util.GarTableUtil;
import com.olf.openjvs.DBUserTable;
import com.olf.openjvs.EmailMessage;
import com.olf.openjvs.ODateTime;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.EMAIL_MESSAGE_TYPE;
import com.olf.openjvs.enums.OLF_RETURN_CODE;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public final class GarEmailUtil {

    /**
     * Constructor
     */
    private GarEmailUtil() {
        // do nothing
    }

    /**
     * Get Email data from configuration table USER_email_config
     * 
     * @param context    Context to match data in User table USER_email_config
     * @param subContext Sub Context to match data in User table USER_email_config
     * @param logger     GarLogger instance
     * @return Email data from USER_email_config table
     * @throws OException {@link OException}
     */
    public static Table getEmailDataFromConfigTable(String context, String subContext, GarLogger logger) throws OException {
        Table tblEmailConfig = Table.tableNew(GAR_USER_TABLE_ENUM.USER_EMAIL_CONFIG.toString());

        StringBuilder sql = new StringBuilder().append("\n SELECT *") //
            .append("\n FROM   " + GAR_USER_TABLE_ENUM.USER_EMAIL_CONFIG.toString()) //
            .append("\n WHERE  mail_context = '" + context + "'") //
            .append("\n   AND  mail_sub_context = '" + subContext + "'") //
            .append("\n   AND  active_flag = 1"); //

        GarStrict.query(tblEmailConfig, sql.toString(), logger);

        return tblEmailConfig;
    }

    /**
     * Send email
     * 
     * @param subject     Subject
     * @param body        Body
     * @param to          To addresses
     * @param cc          CC addresses
     * @param bcc         BCC addresses
     * @param attachments Attachments file path separated by ;
     * @param tblBody     This table will be attached at the end of the email body.
     * @param logger      GarLogger instance
     * @throws OException {@link OException}
     */
    public static void sendEmail(String subject, String body, String to, String cc, String bcc, String attachments, Table tblBody,
        GarLogger logger) throws OException {

        String  errorMsg       = "";
        boolean isError        = false;
        String  uniqueId       = UUID.randomUUID().toString();
        long    startTimeMills = System.currentTimeMillis();

        try {
            logToUserEmailStatusTable(uniqueId, subject, body, to, cc, bcc, attachments, logger);
            String tableHtml = GarTableUtil.convertTableToHtml(tblBody);
            body = body + "<br><br>" + tableHtml;

            sendEmailImpl(subject, body, to, cc, bcc, attachments);
        } catch (Exception e) {
            isError  = true;
            errorMsg = e.getMessage();

            logger.error(e);
        } finally {

            if (isError) {
                updateToUserEmailStatusTable(uniqueId, false, errorMsg, startTimeMills, logger);
            } else {
                updateToUserEmailStatusTable(uniqueId, true, null, startTimeMills, logger);
            }

        }

    }

    /**
     * Send email
     * 
     * @param subject     Subject
     * @param body        Body
     * @param to          To addresses
     * @param cc          CC addresses
     * @param bcc         BCC addresses
     * @param attachments Attachments file path separated by ;
     * @param logger      GarLogger instance
     * @throws OException {@link OException}
     */
    public static void sendEmail(String subject, String body, String to, String cc, String bcc, String attachments, GarLogger logger)
        throws OException {
        String  errorMsg       = "";
        boolean isError        = false;
        String  uniqueId       = UUID.randomUUID().toString();
        long    startTimeMills = System.currentTimeMillis();

        try {
            logToUserEmailStatusTable(uniqueId, subject, body, to, cc, bcc, attachments, logger);
            sendEmailImpl(subject, body, to, cc, bcc, attachments);
        } catch (Exception e) {
            isError  = true;
            errorMsg = e.getMessage();

            logger.error(e);
        } finally {

            if (isError) {
                updateToUserEmailStatusTable(uniqueId, false, errorMsg, startTimeMills, logger);
            } else {
                updateToUserEmailStatusTable(uniqueId, true, null, startTimeMills, logger);
            }

        }

    }

    /**
     * Send email
     * 
     * @param subject     Subject
     * @param body        Body
     * @param to          To addresses
     * @param cc          CC addresses
     * @param bcc         BCC addresses
     * @param attachments Attachments file path separated by ;
     * @throws OException {@link OException}
     */
    private static void sendEmailImpl(String subject, String body, String to, String cc, String bcc, String attachments) throws OException {
        EmailMessage email = null;

        try {
            email = EmailMessage.create();

            if (GarHasAValue.hasAValue(subject, true)) {
                email.addSubject(subject);
            }

            if (GarHasAValue.hasAValue(body, true)) {
                email.addBodyText(body, EMAIL_MESSAGE_TYPE.EMAIL_MESSAGE_TYPE_HTML);
            }

            if (GarHasAValue.hasAValue(to, true)) {
                email.addRecipients(to);
            } else {
                throw new OException("No to address specified");
            }

            if (GarHasAValue.hasAValue(cc, true)) {
                email.addCC(cc);
            }

            if (GarHasAValue.hasAValue(bcc, true)) {
                email.addBCC(bcc);
            }

            if (GarHasAValue.hasAValue(attachments, true)) {
                email.addAttachments(attachments, 0, "");
            }

            email.sendAs(GarConstants.FROM_EMAIL_ADDRESS, GAR_SERVICE_NAME_ENUM.MAIL.toString());
        } finally {
            GarSafe.dispose(email);
        }

    }

    /**
     * Send email
     * 
     * @param context       Context to match data in User table USER_email_config
     * @param subContext    Sub Context to match data in User table USER_email_config
     * @param appendSubject String to suffix to the subject from User table USER_email_config
     * @param appendBody    String to suffix to the body from User table USER_email_config
     * @param attachments   Attachments file path separated by ;
     * @param logger        GarLogger instance
     * @throws OException {@link OException}
     */
    public static void sendEmail(String context, String subContext, String appendSubject, String appendBody, String attachments,
        GarLogger logger) throws OException {
        sendEmail(context, subContext, appendSubject, appendBody, attachments, Util.NULL_TABLE, logger);
    }

    /**
     * Send email
     * 
     * @param context       Context to match data in User table USER_email_config
     * @param subContext    Sub Context to match data in User table USER_email_config
     * @param appendSubject String to suffix to the subject from User table USER_email_config
     * @param appendBody    String to suffix to the body from User table USER_email_config
     * @param attachments   Attachments file path separated by ;
     * @param tblBody       Extra Table body
     * @param logger        GarLogger instance
     * @throws OException {@link OException}
     */
    public static void sendEmail(String context, String subContext, String appendSubject, String appendBody, String attachments,
        Table tblBody, GarLogger logger) throws OException {
        Table   tblConfig      = Util.NULL_TABLE;
        String  errorMsg       = "";
        boolean isError        = false;
        String  uniqueId       = UUID.randomUUID().toString();
        long    startTimeMills = System.currentTimeMillis();

        try {
            logToUserEmailStatusTable(uniqueId, "", "", "", "", "", attachments, logger);
            tblConfig = GarEmailUtil.getEmailDataFromConfigTable(context, subContext, logger);

            if (!GarHasAValue.isTableEmpty(tblConfig)) {
                String subject      = tblConfig.getString("mail_subject", 1);
                String body         = tblConfig.getString("mail_body", 1);
                String toAddresses  = tblConfig.getString("mail_to", 1);
                String ccAddresses  = tblConfig.getString("mail_cc", 1);
                String bccAddresses = tblConfig.getString("mail_bcc", 1);

                // append extra
                subject = subject + " " + appendSubject;
                body    = body + "\n" + appendBody;

                if (!GarHasAValue.isTableEmpty(tblBody)) {
                    String tableHtml = GarTableUtil.convertTableToHtml(tblBody);
                    body = body + "<br><br>" + tableHtml;
                }

                updateToUserEmailStatusTable(uniqueId, subject, body, toAddresses, ccAddresses, bccAddresses, logger);

                GarEmailUtil.sendEmailImpl(subject, body, toAddresses, ccAddresses, bccAddresses, attachments);
            } else {
                throw new OException("No data found in user table " + GAR_USER_TABLE_ENUM.USER_EMAIL_CONFIG.toString()
                    + " for mail_context = " + context + " and mail_sub_context = " + subContext);
            }

        } catch (Exception e) {
            isError  = true;
            errorMsg = e.getMessage();

            logger.error(e);
        } finally {
            GarSafe.destroy(tblConfig);

            if (isError) {
                updateToUserEmailStatusTable(uniqueId, false, errorMsg, startTimeMills, logger);
            } else {
                updateToUserEmailStatusTable(uniqueId, true, null, startTimeMills, logger);
            }

        }

    }

    /**
     * Log status to user table
     * 
     * @param uniqueId
     * @param subject
     * @param body
     * @param to
     * @param cc
     * @param bcc
     * @param attachments
     * @throws OException
     */
    private static void logToUserEmailStatusTable(String uniqueId, String subject, String body, String to, String cc, String bcc,
        String attachments, GarLogger logger) throws OException {
        Table tblEmailStatus = Table.tableNew(GAR_USER_TABLE_ENUM.USER_EMAIL_STATUS.toString());

        try {
            DBUserTable.structure(tblEmailStatus);
            tblEmailStatus.addRow();
            tblEmailStatus.setString("id_number", 1, uniqueId);
            tblEmailStatus.setString("to_address", 1, to);
            tblEmailStatus.setString("cc_address", 1, cc);
            tblEmailStatus.setString("bcc_address", 1, bcc);
            tblEmailStatus.setString("subject", 1, subject);
            tblEmailStatus.setString("body", 1, body);
            tblEmailStatus.setInt("has_attachments", 1, (GarHasAValue.hasAValue(attachments, true) ? 1 : 0));
            tblEmailStatus.setDateTime("last_updated", 1, ODateTime.getServerCurrentDateTime());

            DBUserTable.insert(tblEmailStatus);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            GarSafe.destroy(tblEmailStatus);
        }

    }

    /**
     * Update status in user table
     * 
     * @param uniqueId
     * @param isSuccess
     * @param errorMsg
     * @param startTimeMills
     * @param logger
     * @throws OException
     */
    private static void updateToUserEmailStatusTable(String uniqueId, boolean isSuccess, String errorMsg, long startTimeMills,
        GarLogger logger) throws OException {
        Table tblEmailStatus = Util.NULL_TABLE;

        try {
            tblEmailStatus = Table.tableNew(GAR_USER_TABLE_ENUM.USER_EMAIL_STATUS.toString());

            tblEmailStatus.addCols("S(id_number) I(send_success) S(failure_reason) W(time_taken_in_millis) T(last_updated)");
            tblEmailStatus.addRow();
            tblEmailStatus.setString("id_number", 1, uniqueId);
            tblEmailStatus.setInt("send_success", 1, (isSuccess ? 1 : 0));
            tblEmailStatus.setString("failure_reason", 1, errorMsg);
            tblEmailStatus.setInt64("time_taken_in_millis", 1, (System.currentTimeMillis() - startTimeMills));
            tblEmailStatus.setDateTime("last_updated", 1, ODateTime.getServerCurrentDateTime());

            tblEmailStatus.group("id_number");

            int retVal = DBUserTable.update(tblEmailStatus);

            if (retVal != OLF_RETURN_CODE.OLF_RETURN_SUCCEED.toInt()) {
                logger.error(DBUserTable.dbRetrieveErrorInfo(retVal,
                    "Error occured while updating " + GAR_USER_TABLE_ENUM.USER_EMAIL_STATUS.toString() + " table : "));
            }

        } catch (Exception e) {
            logger.error(e);
        } finally {
            GarSafe.destroy(tblEmailStatus);
        }

    }

    /**
     * Update status in user table
     * 
     * @param uniqueId
     * @param subject
     * @param body
     * @param toAddresses
     * @param ccAddresses
     * @param bccAddresses
     * @throws OException
     */
    private static void updateToUserEmailStatusTable(String uniqueId, String subject, String body, String toAddresses, String ccAddresses,
        String bccAddresses, GarLogger logger) throws OException {
        Table tblEmailStatus = Util.NULL_TABLE;

        try {
            tblEmailStatus = Table.tableNew(GAR_USER_TABLE_ENUM.USER_EMAIL_STATUS.toString());

            tblEmailStatus.addCols("S(id_number) S(to_address) S(cc_address) S(bcc_address) S(subject) S(body)");
            tblEmailStatus.addRow();
            tblEmailStatus.setString("id_number", 1, uniqueId);
            tblEmailStatus.setString("to_address", 1, toAddresses);
            tblEmailStatus.setString("cc_address", 1, ccAddresses);
            tblEmailStatus.setString("bcc_address", 1, bccAddresses);
            tblEmailStatus.setString("subject", 1, subject);
            tblEmailStatus.setString("body", 1, body);
            tblEmailStatus.setDateTime("last_updated", 1, ODateTime.getServerCurrentDateTime());

            tblEmailStatus.group("id_number");

            DBUserTable.update(tblEmailStatus);
        } catch (OException e) {
            logger.error(e);
        } finally {
            GarSafe.destroy(tblEmailStatus);
        }

    }
}
