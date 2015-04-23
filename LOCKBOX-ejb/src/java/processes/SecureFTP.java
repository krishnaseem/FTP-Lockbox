/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package processes;

/**
 *
 * @author seemanapallik
 */
import com.jscape.inet.sftp.Sftp;
import com.jscape.inet.sftp.SftpException;
import com.jscape.inet.sftp.events.SftpAdapter;
import com.jscape.inet.sftp.events.SftpConnectedEvent;
import com.jscape.inet.sftp.events.SftpDisconnectedEvent;
import com.jscape.inet.sftp.events.SftpDownloadEvent;
import com.jscape.inet.sftp.events.SftpUploadEvent;
import com.jscape.inet.ssh.util.SshParameters;
import java.io.File;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecureFTP extends SftpAdapter
{ 
    
    public static Logger logger=LoggerFactory.getLogger(SecureFTP.class);
    private String ftpHostname;
    private String ftpUsername;
    private String ftpPassword;
    
    private SshParameters params ;
    
    public Sftp ftp;
 
    static final long serialVersionUID = 1L;
    

    public Date getFileTimestamp(String file) throws SftpException {
   
        
        // get last modified date of image.gif 
        Date date = ftp.getFileTimestamp(file); 
          
        
        return date;
    
    }
    

    public long getFileSize(String file) throws SftpException {
   
        
        // get size of file image.gif in bytes 
        long filesize = ftp.getFilesize(file); 
        
        return filesize;
    
    }
    

    public void setRemoteDir(String remoteDir) throws SftpException {
   
        ftp.setDir(remoteDir); 

    }
    

    public void setBinary() throws SftpException {
   
      // set transfer mode to binary 
        ftp.setAuto(false); 
        ftp.setBinary();       

    }
    

    public void setAscii() throws SftpException {
   
        // turn off auto transfer mode detection if enabled 
        ftp.setAuto(false); 
        
        // set transfer mode to ASCII 
        ftp.setAscii(); 
     

    }
    


    public void setLocalDir(String localDir) throws SftpException {
   
       // set local directory 
        ftp.setLocalDir(new File(localDir));  

    }

    public void rename(String fileFrom, String to) throws SftpException {
        
        
        //capture FTP related events
        ftp.addSftpListener(this);
   
    	ftp.renameFile(fileFrom , to);
        
   
    }
    

    public void getFileListing() throws SftpException {
    	
        //capture FTP related events
        ftp.addSftpListener(this);
        
        // get directory listing
        String results = ftp.getDirListingAsString();
        logger.debug(results);
        
     
    }
    

    public void createFtpClient(String ftpHostname, String ftpUsername, String ftpPassword) {
        this.ftpHostname = ftpHostname;
        this.ftpUsername = ftpUsername;
        this.ftpPassword = ftpPassword;
        
        // create new SshParameters instance
		params = new SshParameters(ftpHostname,ftpUsername,ftpPassword);    		
		    	
		// create new Ftp instance
		ftp = new Sftp(params);
    }   
    
    	/**
	 * Captures FtpConnectedEvent event
	 */
    public void connected(SftpConnectedEvent evt) {
        logger.debug("Connected to server: " + evt.getHostname());
    }
    
    /**
     * Captures FtpDisconnectedEvent event
     */
    public void disconnected(SftpDisconnectedEvent evt) {
        logger.debug("Disconnected from server: " + evt.getHostname());
    }
    
    // captures download event
    public void download(SftpDownloadEvent evt) {
        logger.debug("Downloaded file: " + evt.getFilename());
    }
 
    // captures upload event
    public void upload(SftpUploadEvent evt) {
        logger.debug("Uploaded file: " + evt.getFilename());
    }
    

    public void connect() throws SftpException {

    // establish secure connection
        ftp.connect();     
    }
    

    public void disconnect() throws SftpException {

      // disconnect
        ftp.disconnect();
    }
    

    public void doUploadFile(String fileNameWithAbsolutePath) throws SftpException {

		
                
        // register to capture FTP related events
        ftp.addSftpListener(this);
        
          
        
        // upload all files in local directory matching filter
        ftp.mupload(fileNameWithAbsolutePath);
        
   
    }    
   
   
    public void doDownloadDirRecursively( String directory ) throws SftpException {
    	
        
        // register to capture FTP related events
        ftp.addSftpListener(this);
        
        // download specified directory
        ftp.downloadDir(directory);
        
    }   
    
   // perform multiple file download
    public void doDownload ( String filter) throws SftpException {
    		
		   
        //capture FTP related events
        ftp.addSftpListener(this);
        
        // establish connection
      //  ftp.connect();  
        
      
        
        // download files matching filter
        ftp.mdownload(filter);
        
        // disconnect
       // ftp.disconnect();
    }    
  
    public void doDownloadFilesWithFilter( String filter) throws SftpException {
    		
       
   /*     //capture FTP related events
        ftp.addSftpListener(this);
       logger.debug( "length " + filter);
        
        // download files matching filter
       File f = ftp.download(filter); 
       
      logger.debug( "length " + f.length() ); */
   
   
     } 
    
     
  
    public void doUploadDirRecursively(File directory) throws SftpException {
    	
	    
        // register to capture FTP related events
        ftp.addSftpListener(this);
        
	     
        // upload directory
        ftp.uploadDir(directory);
    }        
}
