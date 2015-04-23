/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package washpost.outbound;

/**
 *
 * @author seemanapallik
 */
import com.lockbox.db.JDBCPreparedStatementSelectExample;
import com.lockbox.db.ParamEnum;
import com.washpost.ft.util.SFTPConstants;
import com.washpost.integration.util.EnvProperties;
//import com.washpost.ft.util.Queue_Send;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Map;
import com.washpost.integration.util.*;
import processes.Sapclient;
import java.io.FileInputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PostMessageToQueue implements washpost.pas.outbound.PostProcess
{ 
    public static Logger logger=LoggerFactory.getLogger(PostMessageToQueue.class);
    Map<String,String> configuration;
    public int doUpload(byte [] data,String filename,String interfaceId,long processId,  Map fileDetailsMap) throws Exception
    {

        String INPUT_QUEUE_ENUM_VALUE = ParamEnum.INPUT_QUEUE.name;
        JDBCPreparedStatementSelectExample dao=new JDBCPreparedStatementSelectExample();
       configuration=dao.selectRecordsFromTable();
        String inputQueue = configuration.get(INPUT_QUEUE_ENUM_VALUE);
        logger.debug("INPUT_QUEUE is: " + inputQueue);
        
        // Call SapClient.
		Sapclient sapclient = new Sapclient();
		sapclient.transferFileToSAP(data,filename,interfaceId);
                sapclient.sendEmail();
        return 0;
    }
} 
