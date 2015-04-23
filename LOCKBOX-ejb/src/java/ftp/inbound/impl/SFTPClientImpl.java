/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ftp.inbound.impl;

/**
 *
 * @author seemanapallik
 */
import com.jscape.inet.ftp.FtpFile;
import com.jscape.inet.sftp.Sftp;
import com.jscape.inet.sftp.SftpFile;
import com.jscape.inet.sftp.SftpException;
import com.jscape.inet.sftp.events.SftpAdapter;
import com.jscape.inet.ssh.util.SshHostKeys;
import com.jscape.inet.ssh.util.SshParameters;
import com.washpost.integration.util.PASLevel;
import com.washpost.integration.util.PASLogger;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import washpost.inbound.IFileAdapter;


public class SFTPClientImpl extends SftpAdapter implements IFileAdapter
{ 

    
    private static Logger Log = LoggerFactory.getLogger(SFTPClientImpl.class);

    private final static int SUCCESSFUL_FROM_RETURN_CODE = 350;

    private final static int SUCCESSFUL_TO_RETURN_CODE = 250;
    
    private String ftpHostname;
    private String ftpUsername;
    private String ftpPassword;
    
    private SshParameters params ;

    
    public Sftp mFTPClient;
 
    public SFTPClientImpl() {
        Log.debug(" Protocol used is SFTP");
     //  this.LOGGER = PASLogger.getLogger("SFTP");
        
    }
    
    public void changeWorkingDirectory(String directoryName) throws Exception
    {
        
         mFTPClient.setDir( directoryName );
         Log.debug("directoryName is: "+ directoryName);
        
    }

    public boolean connect(String ftpHostname, int port, String ftpUsername, String ftpPassword,HashMap dataMap) throws Exception
    {
        
        this.ftpHostname = ftpHostname;
        this.ftpUsername = ftpUsername;
        this.ftpPassword = ftpPassword;
        
        //new SshParameters(
        // create new SshParameters instance
//		SshParameters sshParams = new SshParameters(ftpHostname,port,ftpUsername,new File("C:/Users/seemanapallik/.ssh/pvtkey.ppk"));   // key file to test with wlqa2nw.
                SshParameters sshParams = new SshParameters(ftpHostname,port,ftpUsername,new File("/local/publish/lockbox/WFPRODKEY.ppk"));      // key file for safetrans(PROD).          
//            		SshParameters sshParams = new SshParameters(ftpHostname,port,ftpUsername,new File("/local/publish/lockbox/pvtkey.ppk")); // key file to test with wlqa2nw.
                
        // create new SshHostKeys instance 
        SshHostKeys keys = new SshHostKeys(); 

        // specify valid remote server address       
        InetAddress address = InetAddress.getByName(ftpHostname); 
        
        String sftpkey = (String)dataMap.get("SFTPKEY");
        Log.debug("********* KEY::" + sftpkey);
        
        // add valid fingerprint to SshHostKeys instance 
        //keys.addKey(address, "8d:75:f5:e6:0a:44:0e:d2:50:ee:0c:06:1c:27:72:86"); 
        keys.addKey(address, sftpkey); 

        // update SshParameters instance to validate against fingerprint in SshHostKeys instance 

        sshParams.setHostKeys(keys, true); 
                
        // create new Ftp instance
		mFTPClient = new Sftp(sshParams);
        
        mFTPClient.addSftpListener(this);
       
        
        mFTPClient.connect();
        Log.debug("Connected to: " + mFTPClient.toString());
        
        return    mFTPClient.isConnected();
       
        
    }

    public void disconnect() throws Exception
    {
         mFTPClient.disconnect();
    }

    public ByteArrayOutputStream getFile(String pRemoteFileName) throws Exception
    {
        ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
        mFTPClient.download( lByteArrayOutputStream, pRemoteFileName );
        return lByteArrayOutputStream;
    }

    public Date getFileTimestamp(String file) throws Exception
    {
         
        Date date = mFTPClient.getFileTimestamp(file); 
        
        return date;
    }

    public boolean isConnected()
    {
       return    mFTPClient.isConnected();
    }

    public ArrayList listFiles(String filter) throws Exception
    {
        
        String[] lFileList = null;
           
          Log.debug("Is Connection Active?: " + mFTPClient.isConnected());
       
        // get directory listing 
        Enumeration listing = null; 
        if(filter.equals(""))
               listing = mFTPClient.getDirListing(); 
        else 
               listing = mFTPClient.getDirListing(filter); 
        Log.debug(mFTPClient.getDirListingAsString());
        ArrayList list = new ArrayList();
        int i = 0;
        // enumerate thru listing printing filename for each entry 
        Log.debug("Listing is: " + list);
        while(listing.hasMoreElements()) { 
            SftpFile file = (SftpFile)listing.nextElement(); 
            list.add(new String(file.getFilename()));
            Log.debug("Filename: " + file.getFilename() + " Size: " + file.getFilesize());
        } 

      
        
        return list;
    }

    public void putFile(String remoteFileName, InputStream pInputStream) throws Exception
    {
         mFTPClient.upload(pInputStream, remoteFileName );
    }
    
     public void setLocalDir(String localDir) throws Exception {
   
       // set local directory 
        mFTPClient.setLocalDir(new File(localDir));  

    }

    public void renameFile(String fromFile, String toFile) throws Exception
    {
        mFTPClient.renameFile(fromFile, toFile);

    }

    public void setFileType(String fileType) throws Exception
    {
        mFTPClient.setAuto(false); 
        
        if ( fileType.equalsIgnoreCase( FTP_CLIENT_FILE_TYPE ) ) {
        mFTPClient.setBinary();
        } else {
        mFTPClient.setAscii();
        }
    }

    public void archive(String remoteFileName, String arcDir) throws Exception
    {
      try{
      
            SftpFile currentFile=null;
            if(mFTPClient.isConnected()){
                Log.debug("Uploaded File Name: "+ mFTPClient.getDir()+"/"+ remoteFileName);
                mFTPClient.renameFile(mFTPClient.getDir()+"/"+remoteFileName,arcDir+"/"+remoteFileName);
                Log.debug("Archived File Name: " + arcDir+ "/"+ remoteFileName);
            }

           
      }catch(SftpException ex){
          Log.error(ex.getMessage());
          throw ex;
      }
        
    }

    public void delete(String remoteFileName) throws Exception
    {
        try{
       Log.debug("Remote file is : " + mFTPClient.getDir()+"/"+remoteFileName );
       if(mFTPClient.isConnected()){
          mFTPClient.deleteFile(mFTPClient.getDir()+"/"+remoteFileName);

       }
        }catch(SftpException ex){
          Log.error(ex.getMessage());
          throw ex;
      }
    }

    public void rename(String remoteFileName) throws Exception
    {
    }
}
