/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package processes;

/**
 *
 * @author seemanapallik
 */
//import com.bea.jpd.JpdContext;
//import com.bea.data.RawData;
//import com.bea.xml.XmlObject;
import org.apache.derby.impl.store.raw.data.*; // related to derby-jar; Need to check this again, change was made to substitute import com.bea.data.RawData; stmt.
//import com.bea.xml.XmlObject;
import com.lockbox.db.JDBCPreparedStatementSelectExample;
import com.lockbox.db.ParamEnum;
import com.lockbox.db.SFTPConfiguration;
//import org.apache.xmlbeans.XmlObject;
import com.washpost.ft.util.FileTransferOperation;
import com.washpost.ft.util.FileXferRecord;
import com.washpost.ft.util.SFTPConstants;
import com.washpost.integration.util.Constants;
import com.washpost.integration.util.EnvProperties;
import com.washpost.ft.util.KeyGenerator;
import com.washpost.ft.util.Util;
import com.washpost.integration.util.LogParameters;
import com.washpost.integration.util.PASLevel;
import com.washpost.integration.util.PASLogger;
import com.washpost.integration.util.Utility;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import washpost.inbound.IFileAdapter;
import ftp.inbound.impl.FTPClientImpl;
import ftp.inbound.impl.FileClientImpl;
import ftp.inbound.impl.SFTPClientImpl;
import java.io.FileOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import washpost.pas.outbound.PostProcess;


public class FTPInbound 
{
      
    //Logger pasLogger;
    int inProcessTimeoutMins ;

    static final int DEFAULT_INPROCESS_TIMEOUTMINS = 5 ;
    String asciiTransferMode;

    FileXferRecord batchFileXferRec;
    long batchTransactionId;
//    public com.bea.xml.XmlObject eventXml;
 //public org.apache.xmlbeans.XmlObject eventXml; // i changed this
//    washpost.pas.ftp.InterfacesDocument eventInDoc ;
    String interfaceId = "LOCKBOX_IN";
    Logger logger;
    String postProcessClass;
    boolean duplicatesAllowed;
    String processLabel;

    public static String PROCESS_NAME = "FileXFRI";    
    
    public IFileAdapter ftp;
    
    String[] listOfFTPFiles;
    
    HashMap dataMap;

    String protocol;
    String ftpHostName;
    int ftpPort;
    String sftpkey;
    String ftpUserName;
    String ftpPassword;
    String filePattern;
    String ftpArchDir;
    String ftpDir;
    String ftpLocalDir;
    String fileArchiveDir;
    String isFtpPostActionDeleteRequired;
    String isEncryptRequired;
    String isFtpPostActionArchiveRequired;
    String dupsAllowed;
    
    
    String currentFileName ;
    static final long serialVersionUID = 1L;
     Map<String,String> configuration;
    
    public static Logger Log=LoggerFactory.getLogger(FTPInbound.class);
  
    
    
    public void initProcessVariables() throws Exception
    {
        this.batchFileXferRec = null;
        this.batchTransactionId = 0;
        this.currentFileName = null;
        this.dataMap = null;
        this.duplicatesAllowed = false;
//        this.eventInDoc = null;
        this.fileArchiveDir = null;
        this.filePattern = null;
        this.ftpArchDir = null;
        this.ftpDir = null;
        this.ftpHostName = null;
        this.ftpLocalDir = null;
        this.ftpPassword  = null;
        this.ftpPort = 22;
        this.ftpUserName = null;
        this.inProcessTimeoutMins = 0;
        this.interfaceId = null;
        this.isEncryptRequired = null;
        this.isFtpPostActionArchiveRequired = null;
        this.isFtpPostActionDeleteRequired = null;
        this.listOfFTPFiles = null;
        this.postProcessClass = null;
        this.processLabel = null;
        this.protocol = null;
        this.sftpkey = null;
        
    }

    public void initialize() throws Exception
    {
        initProcessVariables();
        this.dataMap = new HashMap();
        this.processLabel = "InterfaceId:" + this.interfaceId;
        JDBCPreparedStatementSelectExample dao=new JDBCPreparedStatementSelectExample();
       configuration=dao.selectRecordsFromTable();
        

    }

    @SuppressWarnings("static-access")
    public void readInterfaceProps() throws Exception
    {
        
            JDBCPreparedStatementSelectExample dao=new JDBCPreparedStatementSelectExample();
            configuration=dao.selectRecordsFromTable();
            dataMap  = new HashMap();
            Properties prop = new Properties();      
            prop.load(new FileInputStream(SFTPConstants.SFTP_PATH_PROP_FILE)); 

            this.protocol=configuration.get(ParamEnum.PROTOCOL.name);
            Log.debug("PROTOCOL is: " + this.protocol);


            this.ftpHostName =configuration.get(ParamEnum.FTPHOST.name);
            Log.debug("FTPHOST is: " + this.ftpHostName);

                


            this.sftpkey = configuration.get(ParamEnum.SFTPKEY.name);

            dataMap.put("SFTPKEY",this.sftpkey);
            
              this.ftpUserName = configuration.get(ParamEnum.FTPUSER.name);
              Log.debug("FTPUSER is: " + this.ftpUserName);

              this.ftpPassword = configuration.get(ParamEnum.FTPPASSWORD.name);

              this.filePattern = configuration.get(ParamEnum.FILEPATTERN.name);

              this.ftpArchDir = configuration.get(ParamEnum.FTPARCH.name);
              

              this.ftpDir = configuration.get(ParamEnum.FTPDIR.name);
              Log.debug("FTPDIR is: " + this.ftpDir);
            
              this.ftpLocalDir = configuration.get(ParamEnum.FTPLOCALDIR.name);
              Log.debug("FTPLOCALDIR is: " + this.ftpLocalDir);
            
              this.fileArchiveDir = configuration.get(ParamEnum.FILEARCHIVEDIR.name);
              Log.debug("FILEARCHIVEDIR is: " + this.fileArchiveDir);

              this.isFtpPostActionDeleteRequired = configuration.get(ParamEnum.FTPPOSTDELETE.name); 
            
              this.isEncryptRequired = configuration.get(ParamEnum.ENCRYPT.name); 
              Log.debug("ENCRYPT is: " + this.isEncryptRequired);

              this.isFtpPostActionArchiveRequired = configuration.get(ParamEnum.FTPPOSTARCHIVE.name);// TODO on uncommenting this, getting an error. CHECK THIS !!!
              this.postProcessClass = configuration.get(ParamEnum.POSTPROCESS.name);         
            
            String dupsAllowed = configuration.get(ParamEnum.DUPLICATESALLOWED.name);
            Log.debug("DUPLICATESALLOWED : " + dupsAllowed);
            if ( dupsAllowed != null && dupsAllowed.equalsIgnoreCase("Y") )
                this.duplicatesAllowed = true;
            else
                this.duplicatesAllowed = false;
                
         
            this.asciiTransferMode = configuration.get(ParamEnum.ASCII_YN.name);
             try { 
              this.ftpPort = Integer.parseInt(prop.getProperty(this.PROCESS_NAME + "." + this.interfaceId + ".PORT"));         
            }
            catch(Exception e) {
               
               Log.error(e.getMessage());
             //   Log.debug("PORT is: " + this.ftpPort);
            }finally{
                this.ftpPort=22;
            }  
            
//            
//            int inProcessTimeoutMins = EnvProperties.getIntProperty(this.PROCESS_NAME + "." + interfaceId + ".INPROCESSTIMEOUTMINS");

    }

    public void connectAndGetListOfFiles() throws Exception
    {

        readInterfaceProps();
        if((protocol.equals("FTP") || protocol.equals("SFTP") ) && 
          (ftpUserName == null || this.ftpPassword == null || ftpHostName == null ))
        {
            Exception e = new Exception("ftpUserName = null || this.ftpPassword = null || ftpHostName = null for interfacrId:" + this.interfaceId);
            Log.debug("LockBox Error in FTP connectivity is: " + e);
            throw e;
        }

        //Create protocol dependent client
        if( this.protocol.equalsIgnoreCase("SFTP"))
        {
            ftp = new SFTPClientImpl();
        }
        else if( this.protocol.equalsIgnoreCase("FTP"))
        {
            ftp = new FTPClientImpl();
        }
        else
            ftp = new FileClientImpl();        

        //connect
        Log.debug("Connecting to the server ....Host:" + this.ftpHostName + ":User:" + this.ftpUserName );
        
        
        batchTransactionId = new KeyGenerator().generateKey("FTP_SEQ");  
        this.batchFileXferRec = new FileXferRecord();
        this.batchFileXferRec.transactionId =  batchTransactionId;
        this.batchFileXferRec.interfaceId = this.interfaceId;
        this.batchFileXferRec.fileDirection = FileXferRecord.XFER_DIRECTION_IN;
        this.batchFileXferRec.fileName = this.filePattern;
        this.batchFileXferRec.host = this.ftpHostName;
        this.batchFileXferRec.userId = this.ftpUserName;
        this.batchFileXferRec.remoteDir = this.ftpDir;
        this.batchFileXferRec.postAction = this.postProcessClass;
        this.batchFileXferRec.status = FileTransferOperation.IN_PROCESS_STATUS;
        if ( this.asciiTransferMode != null && this.asciiTransferMode.equals(Constants.YES_Y) )
        {
            this.batchFileXferRec.transferMode = FileTransferOperation.TRANSFER_MODE_ASCII;
        }
        else
        {
            this.batchFileXferRec.transferMode = FileTransferOperation.TRANSFER_MODE_BINARY;
        }
        Log.debug("batchFileXferRec is: " + this.batchFileXferRec.toString());
       new  FileTransferOperation().insertHeaderFileTransferRecord(this.batchFileXferRec);
        
        this.processLabel = this.processLabel + ", TransactionId:" + this.batchTransactionId ;
        
        ArrayList list = null;
        try{
            Log.debug("Connected =" +ftp.connect(this.ftpHostName, this.ftpPort, this.ftpUserName, this.ftpPassword,dataMap));  
            
                    
            //set FTP parameters
            if(this.ftpLocalDir!=null && !this.ftpLocalDir.equals(""))
                this.ftp.setLocalDir(this.ftpLocalDir);
            
            if(this.ftpDir!=null && !this.ftpDir.equals(""))
                this.ftp.changeWorkingDirectory(this.ftpDir);
                
            //Set transfer typre
            if ( this.asciiTransferMode != null && this.asciiTransferMode.equals(Constants.YES_Y) )
            {
                this.ftp.setFileType(IFileAdapter.ASCII_FILE_TYPE);
            }
            else
            {
                this.ftp.setFileType(IFileAdapter.BINARY_FILE_TYPE);
            }
    
            //getting list of files
            list = this.ftp.listFiles(filePattern);
            Log.debug("getting list of files: " + list.size());
            

        }catch(Exception ex)
        {
            this.batchFileXferRec.errorText = "Error while connect/CWD/listFiles operation. Exception Message : " + ex.getMessage();
            Log.error("LockBox Error while connect/CWD/listFiles operation. Exception Message : " + ex.getMessage());
            this.batchFileXferRec.status = FileTransferOperation.RESOURCE_ERROR_STATUS;
          new  FileTransferOperation().updateHeaderFileTransferRecord(this.batchFileXferRec, true);
          ex.printStackTrace();
            throw ex;
        }
                
        if ( list != null )
        {
            listOfFTPFiles = new String[list.size()];
        
            System.arraycopy(list.toArray(),0,listOfFTPFiles,0,list.size());
             Log.info("List of Files: ");
            for(int ind=0;ind < listOfFTPFiles.length;ind++)
                Log.info(this.listOfFTPFiles[ind]);                
        }    
    }

    public void getFileAndProcessIt() throws Exception
    {
                Properties prop = new Properties();      
                prop.load(new FileInputStream(SFTPConstants.SFTP_PATH_PROP_FILE)); 
        if ( this.listOfFTPFiles == null || this.listOfFTPFiles.length ==0 )
        {
            Log.debug("Files not found.");
            this.batchFileXferRec.status = FileTransferOperation.TRANSFER_SUCCESS_STATUS;    
            new FileTransferOperation().updateHeaderFileTransferRecord(this.batchFileXferRec, true);
            return;
        }
        
        FileXferRecord currentFileXferRecord = null;

        
        for ( int fileCounter=0;  fileCounter <this.listOfFTPFiles.length; fileCounter++ )
        {
            Log.info("processing file :" + this.listOfFTPFiles[fileCounter]);
         
            //Insert file transfer traking record
            Log.info(" CLONING FILE TRANSFER RECORD");
        
            if ( currentFileXferRecord == null )
            currentFileXferRecord = (FileXferRecord) this.batchFileXferRec.clone();
                        
            currentFileXferRecord.status = FileTransferOperation.IN_PROCESS_STATUS;
            currentFileXferRecord.fileId = new KeyGenerator().generateKey("FTP_SEQ");
            //currentFileXferRecord.fileId = 2;
            currentFileXferRecord.transactionId = this.batchFileXferRec.transactionId;
            currentFileXferRecord.fileName = this.listOfFTPFiles[fileCounter];
            Log.debug(" Inserting filetransfer record for file:" + this.listOfFTPFiles[fileCounter] );
        
           new FileTransferOperation().insertDetailFileTransferRecord(currentFileXferRecord);
             Log.debug("currentFileXferRecord is: " + currentFileXferRecord.toString());
       

            currentFileName = this.listOfFTPFiles[fileCounter];
            if( this.currentFileName == null) 
               throw new Exception("LockBox Error, FTP fileName is null"); 
            //   Log.debug("LockBox Error, FTP fileName is null");
            
                                   
            //Check if duplicates are allowed
            
            
            if ( !this.duplicatesAllowed )
            {
                Log.info(" DUPLICATES NOT ALLOWED CHECK ");
       
                boolean dupsPresent = new FileTransferOperation().areDuplicatesPresentForRecord(currentFileXferRecord);
                if ( dupsPresent  )
                {
                    Log.debug("Duplicate transfers are not allowed for interface:" + this.interfaceId + ". File being transferred was : " + currentFileName);
                    currentFileXferRecord.status = FileTransferOperation.DUPLICATE_ERROR_STATUS;
                    currentFileXferRecord.errorText = "Duplicate";
                    
                   new FileTransferOperation().updateDetailFileTransferRecord(currentFileXferRecord, true);
                    continue;
                }
            }
            
 
//            //Check if current file is being processed
            

            if ( inProcessTimeoutMins == 0 )
                inProcessTimeoutMins = DEFAULT_INPROCESS_TIMEOUTMINS;
                
            boolean currentFileBeingProcessed = new FileTransferOperation().isCurrentRecordBeingProcessed(currentFileXferRecord, this.inProcessTimeoutMins );
            if ( currentFileBeingProcessed  )
            {
                Log.debug("Current file is being processed by another instance for interface:" + this.interfaceId + ". File being processed was : " + currentFileName);
                currentFileXferRecord.status = FileTransferOperation.DUPLICATE_ERROR_STATUS;
                
               new FileTransferOperation().updateDetailFileTransferRecord(currentFileXferRecord, true);
                continue;
            }
            //Check if current file is FAILED  while running post action 
            //Only if duplicates are not allowed
            if ( !duplicatesAllowed )
            {
                Log.debug("Checking file failed in post action previously");
                boolean currentFileFailedInPostAction = new FileTransferOperation().isCurrentFileFailedInPostAction(currentFileXferRecord, this.inProcessTimeoutMins);
                if ( currentFileFailedInPostAction )
                {
                  Log.debug("LockBox Error, this file will not be transferred. Previous transfer failed in post process for interface :" + this.interfaceId + ". File being processed was : " + currentFileName);
                    currentFileXferRecord.status = FileTransferOperation.DUPLICATE_ERROR_STATUS;
                    
                   new FileTransferOperation().updateDetailFileTransferRecord(currentFileXferRecord, true);
                    continue;
                }
            }
            
            
            //get fileData
            Log.debug(" READING FILE ");
             
            
            java.io.ByteArrayOutputStream getFileByteArrayOutputStream = null;   
             Log.debug("Current filename is: "+ currentFileName);
            Log.debug("FileByteArrayOutputStream is: "+ getFileByteArrayOutputStream);
            
            try{
                getFileByteArrayOutputStream = ftp.getFile(currentFileName); 
                Log.debug("Current filename is: "+ currentFileName);
            }
            catch(Exception ex)
            {
                Log.debug(" LockBox Error, EXCEPION WHILE READING FILE. ");
                currentFileXferRecord.errorText = "Error while transferring file. Exception Message : " + ex.getMessage();
                Log.debug("Error while transferring file. Exception Message : " + ex.getMessage());
                currentFileXferRecord.status = FileTransferOperation.TRANSFER_ERROR_STATUS;
                new FileTransferOperation().updateDetailFileTransferRecord(currentFileXferRecord, true);
                throw ex;
            }
            
                  
            if ( getFileByteArrayOutputStream == null )
            {
                Log.debug(" LockBox Error, file stream is null for file: " + currentFileName);
                currentFileXferRecord.status = FileTransferOperation.TRANSFER_ERROR_STATUS;
                currentFileXferRecord.errorText= " File Stream is null for file:" + currentFileName;
                new FileTransferOperation().updateDetailFileTransferRecord(currentFileXferRecord, true); 
                continue;
                
            }
            byte[] ftpData = getFileByteArrayOutputStream.toByteArray();
            currentFileXferRecord.fileSize = ftpData.length;
            
            //if required, write file to local archive folder
            String archiveYN = configuration.get(ParamEnum.LOCALARCHIVE.name);
            Log.debug("LOCALARCHIVE is: " + archiveYN);
            
            Log.debug(" RUNNING ARCHIVE LOGIC");
            if(archiveYN.equals("Y")) {
                 Log.debug(" in ARCHIVE LOGIC");
                byte [] fileArchData;
                if(isEncryptRequired.equals("Y")) {
                    Log.debug("ENCRYPTING");
                     ByteEncryptDecrypt encrypter = new ByteEncryptDecrypt();
                     fileArchData = encrypter.encrypt(ftpData);
                }    
                else {
                      fileArchData = ftpData;
                }   
                   
                Log.debug(" CREATING FILE");
               Util.createFile(this.fileArchiveDir + "/" + this.interfaceId + "_" + currentFileXferRecord.fileId+ "_" +  this.currentFileName,fileArchData);   
                Log.debug("File Archieved ...::" + archiveYN + "::EncryptionYN::" + isEncryptRequired);

            }    

            // Run POST process, this transmits the file to a onfigured destination, Queue/SAP/Local folder
            //String sapPostProcessClass = "washpost.outbound" + this.postProcessClass ; //TODO this also needs to be changed. This.postprocessClass hardcoded to .PostMessageToQueue.
            String sapPostProcessClass = "washpost.outbound" + ".PostMessageToQueue";
            
            
            // Put Interface Id
            // File Name
            Map fileMap = new HashMap();
               Log.debug("PostProcess ::" + sapPostProcessClass);
          
        
            try{
                if(!sapPostProcessClass.equals("")) {
                    Log.debug("Creating PostProcess class::" + sapPostProcessClass);
           
                   Class c = Class.forName(sapPostProcessClass);
                   Log.debug("instantiating PostProcess class::" + sapPostProcessClass);
               
                   PostProcess postProcess = (PostProcess) c.newInstance();
                    String passByRefFlag = prop.getProperty("REFERENCE_YN"); 
                    Log.debug("REFERENCE_YN value is: " + passByRefFlag);
                    byte[] tempFtpData = ftpData;
                    if ( passByRefFlag != null && passByRefFlag.equalsIgnoreCase(Constants.YES_Y) )
                    {
                        Log.debug("PASSED BY REFERENCE");

                        String localDir = configuration.get(ParamEnum.FTPLOCALDIR.name);
                        Log.debug("FTPLOCALDIR is: " + localDir );
                        String localFileName = localDir + System.getProperty("file.separator") + this.currentFileName; 
                        Log.debug("localFileName is: " + localFileName);
                        Util.createFile(localFileName, ftpData);
                        tempFtpData = new byte[0];
                        Log.debug("PASSED BY REFERENCE FILE SAVED");
                   
                    }
                    
                   Log.debug("RUNNING POST PROCESS");
                 

                   int postProcessReturnvalue = postProcess.doUpload(ftpData, this.currentFileName,this.interfaceId,0 ,fileMap );
                   Log.debug("InterfaceId in postProcessReturnvalue is: " + this.interfaceId);
                   Log.debug("***** PostProcess Return::" + postProcessReturnvalue);
                   //There is no need to check for postProcessReturnValue as it always returns 0 or throws exception.
                }  
            }
            catch(Exception ex)
            {
                  
                currentFileXferRecord.errorText = "LockBox Error while executing post process. Exception Message : " + ex.getMessage();
                currentFileXferRecord.status = FileTransferOperation.POST_PROCESS_ERROR_STATUS;
                ex.printStackTrace();
                
               new FileTransferOperation().updateDetailFileTransferRecord(currentFileXferRecord, true); 
                throw ex;
            }
            catch(Throwable ex)
            {
                ex.printStackTrace();
                 Log.debug("Throwable catched");
                
                throw new Exception (ex);
            }
               //Run Post process action
            Log.debug("******   perform Post Action ******");
            
            try{
                if( isFtpPostActionArchiveRequired != null && isFtpPostActionArchiveRequired.equals("Y") ) {
                    Log.debug("Archiving");
                    this.ftp.archive(this.currentFileName, this.ftpArchDir);
                }else
                {
                   Log.debug("Deleting.."); 
                    this.ftp.delete(this.currentFileName);
                }
            }catch(Exception ex)
            {
                Log.debug("Exception while Archiving");
              
                currentFileXferRecord.errorText = "POST action : " + ex.getMessage();
                currentFileXferRecord.status = FileTransferOperation.POST_ACTION_ERROR_STATUS;
                new FileTransferOperation().updateDetailFileTransferRecord(currentFileXferRecord, true);
            
                
                Log.debug("Archive/Delete operation failed, though File was transferred successfully." + ex.getMessage());
              //  System.out.println("Archive/Delete operation failed, though File was transferred successfully." + ex.getMessage());
                throw ex;
            }
          
            currentFileXferRecord.status = FileTransferOperation.TRANSFER_SUCCESS_STATUS;
            new FileTransferOperation().updateDetailFileTransferRecord(currentFileXferRecord, true); 
            this.sendEmail(currentFileXferRecord.fileId);   
           
        }
               
        
        this.batchFileXferRec.status = FileTransferOperation.TRANSFER_SUCCESS_STATUS; 
        new FileTransferOperation().updateHeaderFileTransferRecord(this.batchFileXferRec, true); 
               
    }

    public void disconnect() throws Exception
    {
        this.ftp.disconnect();
    
    }
    
     public void sendEmail(long fileId) throws Exception
    {
        String subject="";
        String to="";
        String cc="";
        String bcc="";
        String body="";
            
         
        HashMap map = new HashMap();
                        
        try {
            
            String emailYN = this.PROCESS_NAME + "." + interfaceId + ParamEnum.EMAIL_YN.name;         
            
            if(emailYN != null && !emailYN.equals("Y"))
                return;

            String emailUser = this.PROCESS_NAME + "." + interfaceId + ParamEnum.EMAIL_USER.name;   
            String environment = ParamEnum.ENV.name;   
            
            body = "Tracking Info is below: \n\nInterface ID: " + this.interfaceId + "\nFile ID: " + fileId ;
                        
            to = emailUser;

            subject = environment + " - " + this.PROCESS_NAME + " has processed '"+currentFileName+"' successfully";
            
            if(to == null || to.equals("")) {
                // Email Receipent no defined
                logger.info(
                           ":Error:" + "Email Receipent not specified."
                           ,new LogParameters(0,this.PROCESS_NAME,null,null));            
                to = Utility.getErrorEmail();
            }                

            logger.info("Sending an Email");
            
            // Send Email Message.                                                                 
            Utility.sendEmail(this.PROCESS_NAME,0,
                              Constants.BUSINESS_MESSAGE, 
                              body,subject,
                              to,"",cc,bcc,
                              "");
                                                     
        }
        catch(Exception e) {
           //Error while sending email
                  logger.error(":Error:" + e.getMessage(),new LogParameters(0,this.PROCESS_NAME,null,e));            
                  throw e;                                                                    
        }            
    }
    

}
