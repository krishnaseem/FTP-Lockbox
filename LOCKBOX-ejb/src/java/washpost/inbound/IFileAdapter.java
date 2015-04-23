/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package washpost.inbound;

/**
 *
 * @author seemanapallik
 */
import com.jscape.inet.sftp.SftpException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public interface IFileAdapter 
{ 
    /** DOCUMENT ME! */            
    public final static String ASCII_FILE_TYPE = "ASCII";
    
    /** DOCUMENT ME! */
    public final static String BINARY_FILE_TYPE = "BINARY";
    
    /** DOCUMENT ME! */
    public final static String DEFAULT_FILE_TYPE = "BINARY";
    
    /** DOCUMENT ME! */
    public final static String FTP_CLIENT_FILE_TYPE = "BINARY";
     /**
     * DOCUMENT ME!
     */        
     public boolean connect(
         String hostName, int port, String userName, String password,HashMap dataMap
     ) throws Exception;
     
     public void setFileType( String fileType ) throws Exception;

 public Date getFileTimestamp(String file) throws Exception;
    
     /**
      * DOCUMENT ME!
      */  
     public boolean isConnected();

     /**
     * DOCUMENT ME!
     */  
     public void disconnect() throws Exception;
     
    /**
     * DOCUMENT ME!
     */  
     public void changeWorkingDirectory(String directoryName)
         throws Exception;


    /**
     * DOCUMENT ME!
     */  
     public void setLocalDir(String localDir) 
         throws Exception;


     /**
     * DOCUMENT ME!
     */  
    public ArrayList listFiles(String filter) 
        throws Exception;

     /**
     * DOCUMENT ME!
     */  
    public void renameFile(String fromFile, String toFile) 
        throws Exception;
        
    
     /**
     * DOCUMENT ME!
     */          
     public void putFile(String remoteFileName, InputStream in)
         throws Exception;

     /**
     * DOCUMENT ME!
     */  
     public ByteArrayOutputStream getFile(String remoteFileName)
         throws Exception;
         
       
              /**
     * DOCUMENT ME!
     */  
     public void delete(String remoteFileName)
         throws Exception;
         
                  /**
     * DOCUMENT ME!
     */  
     public void archive(String remoteFileName,String ftpArchDir)
         throws Exception;
} 

