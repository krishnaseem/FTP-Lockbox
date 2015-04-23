/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ftp.inbound.impl;

/**
 *
 * @author seemanapallik
 */
import com.washpost.integration.util.PASLevel;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;
import washpost.inbound.IFileAdapter;

public class FileClientImpl implements IFileAdapter
{ 
    private File mFile;
    private static Logger LOGGER = Logger.getLogger(FileClientImpl.class.getName());

    public void changeWorkingDirectory(String directoryName) throws Exception
    {
        mFile = new File( directoryName );
        LOGGER.log(PASLevel.FINER, directoryName + " is a directory : " + mFile.isDirectory());
 
    }

     public void setLocalDir(String localDir) throws Exception {   

        // set local directory 
 
    }
    

    public boolean connect(String hostName, int port, String userName, String password,HashMap dataMap) throws Exception
    {
        return true;
    }

    public void disconnect() throws Exception
    {
    }

    public ByteArrayOutputStream getFile(String pRemoteFileName) throws Exception
    {
        File lFile = new File( mFile.getAbsolutePath() + "/" + pRemoteFileName );
        if ( !lFile.exists() ) {
            return null;
        }
        FileInputStream iFileInputStream = new FileInputStream( lFile );
        ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
        int i;
        
        while ( (i = iFileInputStream.read()) != -1 ) {
            lByteArrayOutputStream.write(i);
        }
        
       iFileInputStream.close();
       lByteArrayOutputStream.close();
       return lByteArrayOutputStream;
    }

    public Date getFileTimestamp(String file) throws Exception
    {
      return new Date( this.mFile.lastModified());
    }

    public boolean isConnected()
    {
        if( this.mFile != null)
        return true;
        else
        return false;
        
    }

    public ArrayList listFiles(String filter) throws Exception
    {
        String[] lFileList = null;
        ArrayList list = new ArrayList();
        if ( mFile == null )
            throw new Exception("Please check configured directory. Looks like it does not exist");
        File[] lFTPFiles = mFile.listFiles();
        //System.out.println("lFTPFiles is:" + lFTPFiles);
        if ( lFTPFiles == null )
            throw new Exception("Please check configured directory. Looks like it does not exist");
        LOGGER.log(PASLevel.FINER,  "# of Files " + lFTPFiles.length + " in directory "  + mFile.getName());

        
        if ( lFTPFiles.length > 0 )
        {
            lFileList = new String[ lFTPFiles.length ];
            for ( int count=0; count < lFTPFiles.length; count++ )
            {
                LOGGER.log(PASLevel.FINER,  "File count: " + count  );
                list.add(lFTPFiles[ count ].getName());
            }
            return list;
        }
        
        return null;
    }

    public void putFile(String pRemoteFileName, InputStream pInputStream) throws Exception
    {
        FileOutputStream lFileOutputStream = new FileOutputStream( mFile.getAbsolutePath() + "/" + pRemoteFileName );
        InputStreamReader lInputStreamReader = null;
        BufferedReader lBufferedReader = null;
        try {
            lInputStreamReader = new InputStreamReader( pInputStream );
            lBufferedReader = new BufferedReader( lInputStreamReader );
            int i;
            while ( (i = lBufferedReader.read()) != -1 ) {
                lFileOutputStream.write( i );
            }
        } finally {
            lFileOutputStream.close();
            lBufferedReader.close();
            lInputStreamReader.close();
        }
    }

    public void renameFile(String pFromFile, String pToFile) throws Exception
    {
        File lFile = new File( mFile.getAbsolutePath() + "/" + pFromFile );
        
        //SJ 2009.01.22 added code to check status of command
        //lFile.renameTo( new File( mFile.getAbsolutePath() + "/" + pToFile ) );
         boolean renamed = lFile.renameTo( new File( mFile.getAbsolutePath() + "/" + pToFile ) );
         if ( !renamed )
            throw new Exception("File could not be renamed");
        
    }

    public void setFileType(String fileType) throws Exception
    {
    }

    public void archive(String remoteFileName , String ftpArchDir) throws Exception
    {
         //SJ 2009.01.16, fixed archiving issue
         //mFile.renameTo(new File(ftpArchDir+"/"+remoteFileName));
         File sourceFile  = new File(mFile.getAbsolutePath(), remoteFileName );
        //SJ 2009.01.22 added code to check status of command
        //sourceFile.renameTo(new File(ftpArchDir+"/"+remoteFileName));
         boolean renamed = sourceFile.renameTo(new File(ftpArchDir+"/"+remoteFileName));
         if ( !renamed )
            throw new Exception("File could not be archived");
    }

    public void delete(String remoteFileName) throws Exception
    {
        File lFile = new File( mFile.getAbsolutePath() + "/" + remoteFileName );
        boolean deleted = lFile.delete();
        
        //boolean deleted = mFile.delete();
        //SJ 2009.01.22 added code to check status of command
        if ( !deleted ) 
            throw new Exception("File could not be deleted");
    }

    public void rename(String remoteFileName) throws Exception
    {
    }
} 
