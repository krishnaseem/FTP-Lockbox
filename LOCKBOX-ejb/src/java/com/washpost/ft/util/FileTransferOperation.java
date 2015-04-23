/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.washpost.ft.util;

/**
 *
 * @author seemanapallik
 */
import com.washpost.integration.util.PASLevel;
import com.washpost.integration.util.PASLogger;
import com.washpost.integration.util.Utility;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTransferOperation 
{ 
    public static final String IN_PROCESS_STATUS = "InProcess";
    public static final String RESOURCE_ERROR_STATUS = "ResourceError";
    public static final String TRANSFER_ERROR_STATUS = "TransferError";
    public static final String POST_PROCESS_ERROR_STATUS = "PostProcessError";
    public static final String POST_ACTION_ERROR_STATUS = "PostActionError";
    public static final String DUPLICATE_ERROR_STATUS = "Duplicate";
    public static final String TRANSFER_SUCCESS_STATUS = "Success";
    public static final String COMPLETE_WITH_ERROR_STATUS = "CompleteWithError";
    public static final String TRANSFER_MODE_ASCII="ASCII";
    public static final String TRANSFER_MODE_BINARY="BINARY";
    
    public static Logger Log=LoggerFactory.getLogger(FileTransferOperation.class);
        
    
    private DataSource dataSource;
    
    private  Connection getConnection() {
    Connection connection = null;
    try {
        InitialContext context = new InitialContext();
        DataSource dataSource = (DataSource) context
            .lookup("ftpds");
         if(dataSource==null){
                Log.debug("LockBox Error, Datasource is null.");
            }
        connection = dataSource.getConnection();
    } catch (NamingException e) {
        e.printStackTrace();
    } catch (SQLException e) {
      Log.debug("LockBox Error in FileTransferOperation:: getConnection() is : " + e.getMessage());
    }
    return connection;
    }

            
    public int insertHeaderFileTransferRecord( FileXferRecord fileProps ) throws Exception
    {
        
        PreparedStatement stmt=null;
        ResultSet resultSet=null;
        Connection db_conn = null;
        StringBuffer sql = new StringBuffer();
        Timestamp ts =new java.sql.Timestamp(new java.util.Date().getTime());


        try{
         //   db_conn = Utility.getConnection("PASDataSource");
            sql = new StringBuffer();
            sql.append("INSERT INTO FILE_XFER_TRACKING_HEADER ");
            sql.append("( ");
            sql.append("transaction_id, status,  file_name, remote_dir, direction, host, post_action, user_id, transfer_mode, encrypted, " );
            sql.append("start_time, end_time, update_time, interface_id");
            sql.append(" ) ");
            sql.append(" VALUES ");
            sql.append(" ( ");
            sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?, ?, ?");
            sql.append(" )");
            db_conn = Util.getConnection("ftpds");
            Log.debug(sql.toString());
            stmt = db_conn.prepareStatement(sql.toString());
            
            stmt.clearParameters();
            

            int counter=1;
            //stmt.setLong(counter++, fileProps.transactionId);
            stmt.setLong(counter++, fileProps.transactionId);
            stmt.setString(counter++, fileProps.status);
            stmt.setString(counter++, fileProps.fileName);
            stmt.setString(counter++, fileProps.remoteDir);
            stmt.setString(counter++, fileProps.fileDirection);
            stmt.setString(counter++, fileProps.host);
            stmt.setString(counter++,fileProps.postAction);
            stmt.setString(counter++, fileProps.userId);
            stmt.setString(counter++, fileProps.transferMode);
            stmt.setString(counter++, fileProps.encrypted);
            stmt.setDate(counter++, fileProps.endTime);
//            stmt.setDate(counter++, fileProps.startTime);
            stmt.setDate(counter++, fileProps.updateTime);
            stmt.setString(counter++, fileProps.interfaceId);
            Log.debug("InterfaceId in insertHeaderFileTransferRecord is: " + fileProps.interfaceId);
            
            return stmt.executeUpdate();
        } catch(Exception ex)
        {
            Log.debug("FileTransferOperation, LockBox Error while executing Sql :" + sql + ex);
            throw ex;
        }
        finally
        {
            
            if ( resultSet != null ) 
            {
                try{
                    
                    resultSet.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            if ( stmt != null ) 
            {
                try{
                    stmt.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            if ( db_conn != null ) 
            {
                try{
                    db_conn.close();
                   Log.debug("Closing connection for insertHeaderFileTransferRecord method."); 
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
                        
    }

    public int insertDetailFileTransferRecord( FileXferRecord fileProps ) throws Exception
    {
        
        PreparedStatement stmt=null;
        ResultSet resultSet=null;
        Connection db_conn = null;
        StringBuffer sql = new StringBuffer();
        Timestamp ts =new java.sql.Timestamp(new java.util.Date().getTime());


        try{
            //db_conn = Utility.getConnection("PASDataSource");
            

            
            sql = new StringBuffer();
            sql.append("INSERT INTO FILE_XFER_TRACKING_DETAIL ");
            sql.append("( ");
            sql.append("file_id, transaction_id, status, file_name, file_size, start_time" );
            sql.append(" ) ");
            sql.append(" VALUES ");
            sql.append(" ( ");
            sql.append("?, ?, ?, ?, ?, NOW()");
            sql.append(" )");
            db_conn = Util.getConnection("ftpds");
            stmt = db_conn.prepareStatement(sql.toString());
            
            stmt.clearParameters();
            
            int counter=1;
            stmt.setLong(counter++, fileProps.fileId);
            stmt.setLong(counter++, fileProps.transactionId);
            stmt.setString(counter++, fileProps.status);
            stmt.setString(counter++, fileProps.fileName);
            stmt.setLong(counter++, fileProps.fileSize);
            
            return stmt.executeUpdate();
        } catch(Exception ex)
        {
            Log.debug("FileTransferOperation:: LockBox Error while executing Sql :" + sql + "insertDetailFileTransferRecord() : " + ex);
            throw ex;
        }
        finally
        {
            if ( resultSet != null ) 
            {
                try{
                    
                    resultSet.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            if ( stmt != null ) 
            {
                try{
                    stmt.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            if ( db_conn != null ) 
            {
                try{
                    db_conn.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
                        
    }


    private int updateFileTransferRecord(FileXferRecord fileProps, boolean updateEndTime, boolean header) throws Exception
    {
        
        PreparedStatement stmt=null;
        ResultSet resultSet=null;
        Connection db_conn = null;
        StringBuffer sql = new StringBuffer();
        Timestamp ts =new java.sql.Timestamp(new java.util.Date().getTime());


        try{
         //   db_conn = Utility.getConnection("PASDataSource");
            
            
            sql = new StringBuffer();
            if ( header )
                sql.append("update FILE_XFER_TRACKING_HEADER ");
            else
                sql.append("update FILE_XFER_TRACKING_DETAIL ");
            sql.append("set ");
            sql.append(" update_time=NOW(), " );
            if (updateEndTime)
                sql.append(" end_time=NOW(), " );
            sql.append(" error_text=?, " );
            if ( !header )
                sql.append(" file_size=?, " );
            sql.append(" status=? " );
            if ( header )
                sql.append(" where transaction_id = ?");
            else
                sql.append(" where file_id = ?");
            
            db_conn = Util.getConnection("ftpds");
            stmt = db_conn.prepareStatement(sql.toString());
            
            stmt.clearParameters();
            
            int counter=1;
            if(fileProps.errorText!=null && fileProps.errorText.length()>44){
               fileProps.errorText= fileProps.errorText.substring(0, 44);
            }
            stmt.setString(counter++, fileProps.errorText);
            if ( !header )
                stmt.setLong(counter++, fileProps.fileSize);
            stmt.setString(counter++, fileProps.status);
            if ( header )
                stmt.setLong(counter++, fileProps.transactionId);
            else
                stmt.setLong(counter++, fileProps.fileId);
            
            
            return stmt.executeUpdate();
        } catch(Exception ex)
        {
            Log.debug("FileTransferOperation:: LockBox Error while executing Sql :" + sql + "updateFileTransferRecord() : " + ex);
            throw ex;
        }
        finally
        {
            if ( resultSet != null ) 
            {
                try{
                    Log.debug("Inside finally, updateFileTransferRecord method");
                    resultSet.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            if ( stmt != null ) 
            {
                try{
                    stmt.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            if ( db_conn != null ) 
            {
                try{
                    db_conn.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    public int updateHeaderFileTransferRecord(FileXferRecord fileProps, boolean updateEndTime) throws Exception
    {
        return updateFileTransferRecord(fileProps, updateEndTime, true);
        
    }

    public int updateDetailFileTransferRecord(FileXferRecord fileProps, boolean updateEndTime) throws Exception
    {
        return updateFileTransferRecord(fileProps, updateEndTime, false);
    }
    
    public static boolean areDuplicatesPresentForRecord(FileXferRecord fileProps) throws Exception
    {
        
        PreparedStatement stmt=null;
        ResultSet resultSet=null;
        Connection db_conn = null;
        StringBuffer sql = new StringBuffer();
        Timestamp ts =new java.sql.Timestamp(new java.util.Date().getTime());


        try{
          //  db_conn = Utility.getConnection("PASDataSource");
            
            sql = new StringBuffer();
            sql.append("select a.file_id from FILE_XFER_TRACKING_DETAIL a,  FILE_XFER_TRACKING_HEADER b");
            sql.append(" where ");
            sql.append(" b.transaction_id = a.transaction_id and " );
            sql.append(" b.interface_id =? and " );
            sql.append(" a.file_name =? and " );
            sql.append(" a.status in ( '"+ TRANSFER_SUCCESS_STATUS +"','"+ POST_ACTION_ERROR_STATUS +"') " );
            sql.append(" and a.file_id <> ? ");
            sql.append(" and b.direction = ?");
            db_conn = Util.getConnection("ftpds");
            stmt = db_conn.prepareStatement(sql.toString());
            
            stmt.clearParameters();
            
            int counter=1;
            stmt.setString(counter++, fileProps.interfaceId);
            stmt.setString(counter++, fileProps.fileName);
            stmt.setLong(counter++, fileProps.fileId);
            stmt.setString(counter++, fileProps.fileDirection);
            
            int dupCount =0;
            
            resultSet = stmt.executeQuery();
            if ( resultSet != null )
            {
                while ( resultSet.next()  )
                {
                    dupCount++;
                }
            }else
                return false;
            
            
            
            if ( dupCount >0 )
                return true;
            else
                return false;
        } catch(Exception ex)
        {
            Log.debug("FileTransferOperation:: LockBox Error while executing Sql :" + sql + "areDuplicatesPresentForRecord() : " + ex);
            throw ex;
        }
        finally
        {
            if ( resultSet != null ) 
            {
                try{
                    
                    resultSet.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            if ( stmt != null ) 
            {
                try{
                    stmt.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            if ( db_conn != null ) 
            {
                try{
                    db_conn.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    public boolean isCurrentRecordBeingProcessed(FileXferRecord fileProps, int proTimeoutMins) throws Exception
    {
        
        PreparedStatement stmt=null;
        ResultSet resultSet=null;
        Connection db_conn = null;
        StringBuffer sql = new StringBuffer();
        Timestamp ts =new java.sql.Timestamp(new java.util.Date().getTime());


        try{
          //  db_conn = Utility.getConnection("PASDataSource");
            
            
            sql = new StringBuffer();
            sql.append("select a.file_id from FILE_XFER_TRACKING_DETAIL a,  FILE_XFER_TRACKING_HEADER b");
            sql.append(" where ");
            sql.append(" b.transaction_id = a.transaction_id and " );
            sql.append(" b.interface_id =? and " );
            sql.append(" a.file_name =? and " );
            sql.append(" a.status = '"+ IN_PROCESS_STATUS +"' and  " );
            double minsToDay  = ((double) proTimeoutMins )/(60.00*24.00);
            sql.append(" a.update_time > NOW()- " + minsToDay);
            sql.append(" and a.file_id <> ?");
            sql.append(" and b.direction = ?");
            Log.debug("Mysql: "+ sql.toString());
            db_conn = Util.getConnection("ftpds");
            stmt = db_conn.prepareStatement(sql.toString());
            
            stmt.clearParameters();
            
            
            int counter=1;
            stmt.setString(counter++, "LOCKBOX_IN");
            stmt.setString(counter++, fileProps.fileName);
            stmt.setLong(counter++, fileProps.fileId);
            stmt.setString(counter++, fileProps.fileDirection);
                        
            int tempCount =0;
            
            resultSet = stmt.executeQuery();
            if ( resultSet != null )
            {
                while ( resultSet.next()  )
                {
                    tempCount++;
                }
            }else
                return false;
            
            
            
            if ( tempCount >0 )
                return true;
            else
                return false;
        } catch(Exception ex)
        {
            Log.debug("FileTransferOperation:: LockBox Error while executing Sql :" + sql + "isCurrentRecordBeingProcessed() : " + ex);
            throw ex;
        }
        finally
        {
            if ( resultSet != null ) 
            {
                try{
                    resultSet.close();
                    Log.debug("Closing connection in isCurrentRecordBeingProcessed method.");
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            if ( stmt != null ) 
            {
                try{
                    stmt.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            if ( db_conn != null ) 
            {
                try{
                    db_conn.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    public boolean isCurrentFileFailedInPostAction(FileXferRecord fileProps, int proTimeoutMins) throws Exception
    {
        
        PreparedStatement stmt=null;
        ResultSet resultSet=null;
        Connection db_conn = null;
        StringBuffer sql = new StringBuffer();
        Timestamp ts =new java.sql.Timestamp(new java.util.Date().getTime());


        try{
          //  db_conn = Utility.getConnection("PASDataSource");
            
            
            sql = new StringBuffer();
            sql.append("select a.file_id from FILE_XFER_TRACKING_DETAIL a,  FILE_XFER_TRACKING_HEADER b");
            sql.append(" where ");
            sql.append(" b.transaction_id = a.transaction_id and " );
            sql.append(" b.interface_id =? and " );
            sql.append(" a.file_name =? and " );
            sql.append(" a.status = '"+ POST_ACTION_ERROR_STATUS  );
            sql.append("'" );
            sql.append(" and a.file_id <> ?");
            sql.append(" and a.ignore_status <> 1");
            sql.append(" and b.direction = ?");
            
            db_conn = Util.getConnection("ftpds");
            stmt = db_conn.prepareStatement(sql.toString());
            
            stmt.clearParameters();
            
            int counter=1;
            stmt.setString(counter++, fileProps.interfaceId);
            stmt.setString(counter++, fileProps.fileName);
            stmt.setLong(counter++, fileProps.fileId);
            stmt.setString(counter++, fileProps.fileDirection);
            
            int tempCount =0;
            
            resultSet = stmt.executeQuery();
            if ( resultSet != null )
            {
                while ( resultSet.next()  )
                {
                    tempCount++;
                }
            }else
                return false;
            
            
            
            if ( tempCount >0 )
                return true;
            else
                return false;
        } catch(Exception ex)
        {
            Log.debug("FileTransferOperation:: LockBox Error while executing Sql :" + sql + "isCurrentFileFailedInPostAction() : " + ex);
            throw ex;
        }
        finally
        {
            if ( resultSet != null ) 
            {
                try{
                    
                    resultSet.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            if ( stmt != null ) 
            {
                try{
                    stmt.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            if ( db_conn != null ) 
            {
                
                try{
                    db_conn.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }
    
    
}
