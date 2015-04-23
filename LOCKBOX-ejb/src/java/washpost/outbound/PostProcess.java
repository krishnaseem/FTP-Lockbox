/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package washpost.outbound;

/**
 *
 * @author seemanapallik
 */
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Map;

public interface PostProcess 
{ 
    public int doUpload(byte [] data,String filename,String interfaceId,long processId,  Map fileDetailsMap) throws Exception;
} 