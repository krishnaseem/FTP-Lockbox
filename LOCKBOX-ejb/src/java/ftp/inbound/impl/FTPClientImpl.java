/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ftp.inbound.impl;

/**
 *
 * @author seemanapallik
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import washpost.inbound.IFileAdapter;
import com.jscape.inet.ftp.Ftp;
import com.jscape.inet.ftp.FtpAdapter;
import com.jscape.inet.ftp.FtpConnectedEvent;
import com.jscape.inet.ftp.FtpDisconnectedEvent;
import com.jscape.inet.ftp.FtpFile;
import com.jscape.inet.sftp.SftpException;
import com.washpost.integration.util.PASLevel;
import com.washpost.integration.util.PASLogger;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;

public class FTPClientImpl extends FtpAdapter implements IFileAdapter
{ 
        /** DOCUMENT ME! */
    private static Logger LOGGER = Logger.getLogger(FTPClientImpl.class.getName());
    
    
    /** DOCUMENT ME! */
    private final static int SUCCESSFUL_FROM_RETURN_CODE = 350;
    
    /** DOCUMENT ME! */
    private final static int SUCCESSFUL_TO_RETURN_CODE = 250;
    
    /** DOCUMENT ME! */
    public Ftp mFTPClient;

    private String hostname;
    private String username;
    private String password;
    
      public FTPClientImpl() {
       this.LOGGER = PASLogger.getLogger("FTP");
        
    }
    

    /**
     * DOCUMENT ME!
     */
    public boolean connect(String hostname, int pPort, String username, String password,HashMap dataMap) 
        throws Exception {
        
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        
        mFTPClient = new Ftp(hostname,username,password,pPort);
             //capture Ftp related events
        mFTPClient.addFtpListener(this);
        mFTPClient.connect();
        
        return    mFTPClient.isConnected();
                       
    }

    /**
     * DOCUMENT ME!
     */
    private void login()
        throws Exception {
            
        mFTPClient.login();
        
    }
    
      // captures connect event
    public void connected(FtpConnectedEvent evt) {
        //System.out.println("Connected to server: " + evt.getHostname());
    }
    
    // captures disconnect event
    public void disconnected(FtpDisconnectedEvent evt) {
        //System.out.println("Disconnected from server: " + evt.getHostname());
    }

    /**
     * DOCUMENT ME!
     */
	public void renameFile( String pFromFile, String pToFile ) 
		throws Exception {
            
            mFTPClient.renameFile(pFromFile,pFromFile);
	
	}

    /**
     * DOCUMENT ME!
     */
    public void changeWorkingDirectory( String pDirectoryPath )
        throws Exception {
            
        mFTPClient.setDir( pDirectoryPath );

    }

    /**
     * DOCUMENT ME!
     */
	public ArrayList listFiles(String filter) throws 
    Exception
		{
            String[] lFileList = null;
           
            LOGGER.log(PASLevel.FINER,"Is Connection Active?: " + mFTPClient.isConnected());

            LOGGER.log(PASLevel.FINE,"HOOK 1 ::" + filter);

            // get directory listing
            Enumeration listing = null; 
            if(filter.equals(""))
               listing = mFTPClient.getDirListing(); 
            else 
               listing = mFTPClient.getDirListing(filter); 
            
            LOGGER.log(PASLevel.FINE,"HOOK 2 ");
            ArrayList list = new ArrayList();
            int i = 0;
            // enumerate thru listing printing filename for each entry 
            while(listing.hasMoreElements()) { 
                FtpFile file = (FtpFile)listing.nextElement(); 
                LOGGER.log(PASLevel.FINE,"Value of FILE IS :" + file + "::filename::" + file.getFilename());
                list.add(file.getFilename());
                LOGGER.log(PASLevel.FINE,"Filename: " + file.getFilename() + " Size " + file.getFilesize());
            } 
          
            
            return list;
        }

    /**
     * DOCUMENT ME!
     */
    
    public void setFileType( String fileType ) 
        throws Exception {
            
        mFTPClient.setAuto(false); 
        
        if ( fileType.equalsIgnoreCase( FTP_CLIENT_FILE_TYPE ) ) {
        mFTPClient.setBinary();
        } else {
        mFTPClient.setAscii();
        }
        
    }

    /**
     * DOCUMENT ME!
     */
	public boolean isConnected() {
	   
		return mFTPClient.isConnected();
		
	}

    /**
     * DOCUMENT ME!
     */
    public void putFile( String pRemoteFileName, InputStream pInputStream )
        throws Exception {
            
     
      mFTPClient.upload(pInputStream, pRemoteFileName );
        
    }

    /**
     * DOCUMENT ME!
     */
    public ByteArrayOutputStream getFile( String pRemoteFileName )
        throws Exception {
            
        ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
        mFTPClient.download( lByteArrayOutputStream, pRemoteFileName );
        return lByteArrayOutputStream;
        
    }

   
    /**
     * DOCUMENT ME!
     */
    public void disconnect() 
        throws Exception {
            
        
        mFTPClient.disconnect();
        
    }
    
    public Date getFileTimestamp(String file) throws Exception {
   
        
        Date date = mFTPClient.getFileTimestamp(file); 
        
        return date;
    
    }
    

    public void archive(String remoteFileName, String ftpArchDir) throws Exception
    {
        mFTPClient.renameFile(remoteFileName, ftpArchDir+ "/"+ remoteFileName);

    }

    public void delete(String remoteFileName) throws Exception
    {
        mFTPClient.deleteFile(remoteFileName);
    }

    public void rename(String remoteFileName) throws Exception
    {
    }
    
    
     public void setLocalDir(String localDir) throws Exception {   

        LOGGER.log(PASLevel.FINE,"Setting LOcal Dir ::" + localDir);

       // set local directory 
        mFTPClient.setLocalDir(new File(localDir));  

        LOGGER.log(PASLevel.FINE,"HOOK LOCAL");

    }
    
}
