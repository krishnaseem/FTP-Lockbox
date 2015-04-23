/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package processes;

import com.google.common.io.Files;
import com.lockbox.db.ParamEnum;
import com.sap.conn.jco.*;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRepository;
import com.washpost.ft.util.KeyGenerator;
import com.sap.conn.jco.JCoTable;
import com.twp.it.pubsys.sap.ResourceAdapter.PubSysSapDataSource;
import com.washpost.ft.util.Constants;
import com.washpost.ft.util.FileTransferOperation;
import com.washpost.ft.util.Lockbox_Constants;
import com.washpost.integration.exception.AppException;
import com.washpost.integration.util.EnvProperties;
import com.washpost.integration.util.Utility;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.log4j.Logger;


/**
 *
 * @author seemanapallik
 */
public class Sapclient {
    
    Logger logger = Logger.getLogger(this.getClass());
    Connection connection = null;
    JCoDestination destination = null;
    public String filename;
    public String interfaceId;
    public File inputFile;
    public long processId;
    public String processLabel;
    public String sapErrorCode;               
    public String sapErrorMessage;               
    public String sapFilename;  
    public static String PROCESS_NAME = "FileXFRI";  
    
    
      public void transferFileToSAP(byte [] byteData,String filename,String interfaceId) throws Exception
    {
        
     try {
            
            // get an initial context object
            Context ctx = new InitialContext();
    
            // Use the context to lookup the datasource
             PubSysSapDataSource sapDS = (PubSysSapDataSource)ctx.lookup(Lockbox_Constants.LOCKBOX_JCO_ENV_DATASOURCE_NAME);
            
            // get SAP instance name
            String sapInstance = (String)ctx.lookup(Lockbox_Constants.LOCKBOX_JCO_ENV_INSTANCE_JNDI_NAME);

            this.connection = sapDS.getConnection(sapInstance);
            this.destination = connection.unwrap(JCoDestination.class);
            JCoRepository repository = destination.getRepository();
            
            JCoFunctionTemplate zmamTemplate =                    
                    repository.getFunctionTemplate("ZMAM_FILE_RECEIVER_SERVICE");
            JCoFunction zmamFunction = zmamTemplate.getFunction();
            
//            zmamFunction.execute(destination);
            
            //JCoParameterList exportParams = zmamFunction.getExportParameterList();
            JCoParameterList importParams = zmamFunction.getImportParameterList();
            
            String inputContent = new String(byteData);
            
           String convertedInputFileContent = inputContent.replaceAll("\r\n","\n");
           logger.debug(convertedInputFileContent);
           
                      
           importParams.setValue("BIN_CONTENT",convertedInputFileContent.getBytes());
           logger.debug("BIN_CONTENT is: " + convertedInputFileContent.getBytes());
           System.out.println("BIN_CONTENT is: " + convertedInputFileContent.getBytes());
           importParams.setValue("BIN_FILESIZE", convertedInputFileContent.length());
           logger.debug("BIN_FILESIZE is: " +  convertedInputFileContent.length());
           System.out.println("BIN_FILESIZE is: " +  convertedInputFileContent.length());
           importParams.setValue("FILENAME",filename);
           logger.debug("FILENAME is: " + filename);
           System.out.println("FILENAME is: " + filename);
           importParams.setValue("INTERFACE_ID",interfaceId);
           System.out.println("INTERFACE_ID is: " + interfaceId);
           logger.debug("INTERFACE_ID is: " + interfaceId);
           // get new process Id  ..... New Message         
           processId = KeyGenerator.generateKey(Constants.PROCESS_ID_SEQ); 
           importParams.setValue("PROCESS_KEY",processId); 
           logger.debug("PROCESS_KEY is: " + processId);
           System.out.println("PROCESS_KEY is: " + processId);
           importParams.setValue("SERIAL" , System.currentTimeMillis());
           logger.debug("SERIAL is: " + System.currentTimeMillis());
           System.out.println("SERIAL is: " + System.currentTimeMillis());
           
           /* The "BAPI_TRANSACTION_COMMIT" function call is needed to commit changes */
            JCoFunction comFunc = repository.getFunction("BAPI_TRANSACTION_COMMIT");
           /* tell SAP that a transaction is beginning */
            JCoContext.begin(destination);

          zmamFunction.execute(destination);
          
           /* The execute "BAPI_TRANSACTION_COMMIT" to commit changes */
            comFunc.execute(destination);
           /* tell SAP that the transaction has ended */
            JCoContext.end(destination);
          
          logger.info("SAP Destination is: " + destination);
          System.out.println("SAP Destination is: " + destination);           
         // JCoResponse resp = (JCoResponse) zmamFunction.getExportParameterList();
              
          JCoParameterList resp = zmamFunction.getExportParameterList();
         // params.getValue("RET_CODE");
          
          
           logger.info("SAP Response:" + resp.toString());    
           System.out.println("SAP Response:" + resp.toString()); 
           // Check for the error
           if(resp != null && resp.getValue("RET_CODE") != null) {
    
           logger.info("SAP Response:" + resp.toXML());  
           System.out.println("SAP Response:" + resp.toXML());
           System.out.println("Today's file:" + filename + " has been processed successfully by the interface.");
           // logger.info("-----------------END PROCESSING------------------");     
              
              if(!resp.getValue("RET_CODE").equals("000")) {
                  processLabel = processLabel + ":Error:Code=" + resp.getValue("RET_CODE")
                                              + ",ErrorMessage=" +  resp.getValue("RET_MESSAGE");
                  sapErrorCode = resp.getString("RET_CODE");
                  sapErrorMessage = resp.getString("RET_MESSAGE");
                  logger.info(" LockBox Error, Error in SAP: " +sapErrorMessage.toString());
                  System.out.println(" LockBox Error, Error in SAP: " +sapErrorMessage.toString());
                  sapFilename =  resp.getString("SAP_FILENAME");                                                 
                  throw new Exception(sapErrorMessage);  
              }             
           }   
           // currentFileXferRecord.status = FileTransferOperation.TRANSFER_SUCCESS_STATUS;
            //FileTransferOperation.updateDetailFileTransferRecord(currentFileXferRecord, true);
           
                       
        } catch (SQLException ex) {
            logger.error("SQL Exception = " + ex);
            System.out.println("LockBox Error,SQL Exception = " + ex);
             throw ex;
        } catch (JCoException ex) {
            logger.error("JCo Exception = " + ex);
            System.out.println("LockBox Error,JCo Exception = " + ex);
            throw ex;
        } catch (Exception ex){
            logger.error("Exception = " + ex);
            System.out.println("LockBox Error, Exception = " + ex);
            throw ex;
        } finally {
            if (connection != null){   
                connection.close();
            }
            logger.info("-----------------END PROCESSING - Closed Connection------------------");  
        }
    }
      
          public void sendEmail() throws Exception
    {
        String subject="";
        String to="";
        String cc="";
        String bcc="";
        String body="";
            
         
        HashMap map = new HashMap();
                        
        String emailYN = this.PROCESS_NAME + "." + interfaceId + ParamEnum.EMAIL_YN.name;         
        
        if(emailYN != null && !emailYN.equals("Y"))
            return;
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        
        body = "The Following file has been sent to SAP:\n\nFilename: "+ filename +"\nTime: "+df.format(Calendar.getInstance().getTime());
                    
        to = this.PROCESS_NAME +  "." + interfaceId +  ParamEnum.EMAIL_USER.name;  
        subject = ParamEnum.ENV.name+ ": Interface "+this.PROCESS_NAME +" has sent the following file succesfully." + ":Filename:" + filename;
        
        if(to == null || to.equals("")) {
            // Email Receipent no defined
            throw new AppException(null,"Email Receipent not specified !");                
        }                
        
        // Send Email Message.                                                                 
        Utility.sendEmail(this.PROCESS_NAME,processId,
                          Constants.BUSINESS_MESSAGE, 
                          body,subject,
                          to,"",cc,bcc,
                          "");
                                                     
    }
    
}
