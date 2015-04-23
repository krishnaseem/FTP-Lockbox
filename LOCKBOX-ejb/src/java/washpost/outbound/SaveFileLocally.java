/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package washpost.outbound;

/**
 *
 * @author seemanapallik
 */
import com.washpost.integration.util.EnvProperties;
//import com.washpost.integration.util.QueueSend;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Map;
import com.washpost.integration.util.*;
import java.io.File;


public class SaveFileLocally implements washpost.pas.outbound.PostProcess
{ 
    
    public int doUpload(byte [] data,String filename,String interfaceId,long processId,  Map fileDetailsMap) throws Exception
    {
        
        String localFolder = EnvProperties.getProperty("FileXFRI" + "." + interfaceId + ".LOCALFOLDER");
        String filePattern = EnvProperties.getProperty("FileXFRI" + "." + interfaceId + ".FILEPATTERN");
        String renameLocalFile = EnvProperties.getProperty("FileXFRI" + "." + interfaceId + ".RENAMELOCALFILE");
        String fileName = localFolder + "/" + filename;
        
        //rename file if required
        if ( renameLocalFile != null && renameLocalFile.equalsIgnoreCase("Y"))
        {
            if ( filePattern != null && filePattern.equalsIgnoreCase("*.xml") )
            {
                String renamedFile = fileName.replaceAll("[.]xml", ".tmp");
            
                Utility.createFile( renamedFile, data);
                File file = new File( renamedFile );
                file.renameTo( new File(fileName ) );
            }
        }
        else
            Utility.createFile(fileName, data);
        
        return 0;
    }
} 
