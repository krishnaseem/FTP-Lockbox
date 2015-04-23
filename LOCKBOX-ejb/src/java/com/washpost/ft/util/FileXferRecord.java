/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.washpost.ft.util;

/**
 *
 * @author seemanapallik
 */
import java.sql.Date;

public class FileXferRecord implements Cloneable
{ 
    public String fileName;
    public String remoteDir;
    public String userId;
    public String fileDirection;
    public String host;
    public String status;
    public String size;
    public String postAction;
    public String interfaceId = "LOCKBOX_IN";
    public Date endTime;
    public Date startTime;
    public Date updateTime;
    public long transactionId;
    public long fileId;
    public String transferMode;
    public String encrypted;
    public long fileSize;
    public String errorText;
    public static final String XFER_DIRECTION_IN="IN";
    public static final String XFER_DIRECTION_OUT="OUT";
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public String toString()
    {
        return "filename:"+ fileName+ ",remoteDir:"+ remoteDir+  ",:userId"+ userId+  ",fileDirection:"+ fileDirection 
            +  ",host:"+host +  ",:status"+ status+  ",size:"+ size+  ",postAction:"+postAction 
            +  ",interfaceId:"+ interfaceId +  ",endTime:"+ endTime+  ",startTime:"+ startTime 
            +  ",updateTime:"+ updateTime+  ",transactionId:"+ transactionId+  ",fileId:"+ fileId
            +  ",transferMode:"+ transferMode+  ",encrypted:"+ encrypted +  ",fileSize:"+ fileSize+  ",errorText:"+ errorText;
    }
} 
