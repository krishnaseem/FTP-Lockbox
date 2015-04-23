package com.washpost.ft.util;


import com.washpost.integration.exception.AppException;
import com.washpost.integration.exception.CodeMappingException;
import com.washpost.integration.exception.DupKeyException;
import com.washpost.integration.exception.MessageOutOfOrderException;
import com.washpost.integration.exception.SQLDataException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.logging.Logger;
import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.xml.namespace.QName;


public class Util
{
  public static void log(String type, String message)
  {
    System.out.println(new java.util.Date() + "::" + type + "::" + message);
  }
  
  public static void log(String type, String message, Exception e)
  {
    System.out.print(new java.util.Date() + "::" + type + "::" + message);
    if (e != null) {
      System.out.print("::Exception::" + e.getMessage());
    }
  }
  
  
  
  public static byte[] readFile(String name)
    throws Exception
  {
    try
    {
      File file = new File(name);
      
      long length = file.length();
      byte[] data = new byte[(int)length];
      
      FileInputStream inStream = new FileInputStream(file);
      inStream.read(data);
      
      inStream.close();
      
      return data;
    }
    catch (Exception e)
    {
      log("ERROR", "********** Error Reading File *******::" + e.getMessage(), e);
      throw e;
    }
  }
  
  public static void createFile(String name, byte[] data)
    throws Exception
  {
    try
    {
      File file = new File(name);
      
      FileOutputStream outStream = new FileOutputStream(file);
      
      outStream.write(data);
      
      outStream.close();
      
      return;
    }
    catch (Exception e)
    {
      log("ERROR", "********** Error Writing File *******::" + e.getMessage(), e);
      throw e;
    }
  }
  
  public static void renameFile(String src, String dest)
    throws Exception
  {
    try
    {
      File renamedFile = new File(dest);
      File srcFile = new File(src);
      if (renamedFile.exists())
      {
        log("ERROR", "File already exists in directory :" + dest, null);
        dest = dest + "." + System.currentTimeMillis();
        log("INFO", "File renamed to :" + dest, null);
        renamedFile = new File(dest);
      }
      srcFile.renameTo(renamedFile);
      return;
    }
    catch (Exception e)
    {
      log("ERROR", e.getMessage(), e);
      throw e;
    }
  }
  
  public static InitialContext getInitialContext()
    throws Exception
  {
    Hashtable env = new Hashtable();
    
//    env.put("java.naming.factory.initial", "weblogic.jndi.WLInitialContextFactory");
    




    return new InitialContext(env);
  }
  
  public static Connection getConnection(String dataSource)
throws Exception
  {
    Connection connection = null;
//    Statement stmt = null;
    Statement statement = null;
    ResultSet resultSet = null;
    try
    {
      Context ctx = null;
      
      ctx = getInitialContext();
      
      DataSource ds = (DataSource)ctx.lookup(dataSource);
      
      return ds.getConnection();
    }
    catch (Exception e)
    {
      log("ERROR", e.getMessage(), e);
      throw e;
    }
finally {
      closeAll(resultSet, statement, connection);
      System.out.println("closed resultset, statement, connection");
    }
  }
  
    private static void closeAll(ResultSet resultSet, Statement statement, Connection connection) {
    if (resultSet != null) {
      try {
        resultSet.close();
      } catch (SQLException e) {
      } // nothing we can do
    }
    if (statement != null) {
      try {
        statement.close();
      } catch (SQLException e) {
      } // nothing we can do
    }
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
      } // nothing we can do
    }
  }
  
  public static void release(Connection db_conn, PreparedStatement stmt, ResultSet rset)
  {
    if (rset != null) {
      try
      {
        rset.close();
      }
      catch (Exception e)
      {
        log("ERROR", e.getMessage(), e);
      }
    }
    if (stmt != null) {
      try
      {
        stmt.close();
      }
      catch (Exception e)
      {
        log("ERROR", e.getMessage(), e);
      }
    }
    if (db_conn != null) {
      try
      {
        db_conn.close();
        return;
      }
      catch (Exception e)
      {
        log("ERROR", e.getMessage(), e);
      }
    }
  }
  
  public static void release(Connection db_conn, Statement stmt, ResultSet rset)
  {
    if (rset != null) {
      try
      {
        rset.close();
      }
      catch (Exception e)
      {
        log("ERROR", e.getMessage(), e);
      }
    }
    if (stmt != null) {
      try
      {
        stmt.close();
      }
      catch (Exception e)
      {
        log("ERROR", e.getMessage(), e);
      }
    }
    if (db_conn != null) {
      try
      {
        db_conn.close();
        return;
      }
      catch (Exception e)
      {
        log("ERROR", e.getMessage(), e);
      }
    }
  }
  
  public static String nullToStr(String input)
  {
    if (input == null) {
      return "";
    }
    return input;
  }
  
  public static java.util.Date formatDate(String date, String dateFormat, String time, String timeFormat)
    throws Exception
  {
    if (date == null) {
      return null;
    }
    SimpleDateFormat formatter = new SimpleDateFormat(dateFormat + timeFormat);
    
    return formatter.parse(date + time);
  }
  
  public static String formatDate(java.util.Date inputDate, String outputFormat)
    throws Exception
  {
    if (inputDate == null) {
      return null;
    }
    SimpleDateFormat formatter = new SimpleDateFormat(outputFormat);
    
    return formatter.format(inputDate);
  }
  
  public static java.util.Date parseDate(String inputDate, String outputFormat)
    throws Exception
  {
    if (inputDate == null) {
      return null;
    }
    SimpleDateFormat formatter = new SimpleDateFormat(outputFormat);
    
    return formatter.parse(inputDate);
  }
  
  public static java.util.Date getTodaysDate()
  {
    java.util.Date today = null;
    GregorianCalendar cal = new GregorianCalendar();
    
    cal.set(10, cal.getActualMaximum(10));
    cal.set(12, cal.getActualMaximum(12));
    cal.set(14, cal.getActualMaximum(14));
    
    return cal.getTime();
  }
  
  public static double getDoubleValue(String data)
    throws Exception
  {
    if ((data == null) || (data.equals(""))) {
      data = "0";
    }
    return Double.parseDouble(data);
  }
  
  public static int getIntValue(String data)
    throws Exception
  {
    if ((data == null) || (data.equals(""))) {
      data = "0";
    }
    return Integer.parseInt(data);
  }
  
  public static void setStringParam(PreparedStatement stmt, int position, String data, int size)
    throws Exception
  {
    if (data == null)
    {
      stmt.setNull(position, 0);return;
    }
    if (data.length() > size)
    {
      stmt.setString(position, data.substring(0, size));return;
    }
    stmt.setString(position, data);
  }
  
  public static void setTimestampParam(PreparedStatement stmt, int position, java.util.Date date)
    throws Exception
  {
    if (date == null)
    {
      stmt.setNull(position, 91);return;
    }
    stmt.setTimestamp(position, new Timestamp(date.getTime()));
  }
  
  public static void setDateParam(PreparedStatement stmt, int position, java.util.Date date)
    throws Exception
  {
    if (date == null)
    {
      stmt.setNull(position, 91);return;
    }
    stmt.setDate(position, new java.sql.Date(date.getTime()));
  }
  
  public static void setDataValue(HashMap map, String key, Object value)
  {
    if (value != null) {
      map.put(key, value);
    }
  }
  
  
  
  public static boolean isTextMessage(String dataType)
  {
    if ((dataType.equals("xml")) || (dataType.equals("txt"))) {
      return true;
    }
    return false;
  }
  
  public static byte[] readBytesMesage(BytesMessage bytesMsg)
    throws Exception
  {
    byte[] byteBuf = new byte[0];
    byte[] retBuf = new byte[0];
    byte[] backupBuf;
    int len = 0;
    int totalLen = 0;
    int BUF_SIZE = 5120000;
    

    int messageSize = bytesMsg.getIntProperty("MessageSize");
    if (messageSize != 0) {
      BUF_SIZE = messageSize;
    }
    byteBuf = new byte[BUF_SIZE];
    len = bytesMsg.readBytes(byteBuf);
    
    retBuf = new byte[len];
    System.arraycopy(byteBuf, 0, retBuf, 0, len);
    
    return retBuf;
  }
  
  public static String getRandom(long seed1, int len)
  {
    StringBuffer buf = new StringBuffer();
    for (int ind = 0; ind < len; ind++) {
      buf.append((int)(Math.random() * 10.0D));
    }
    return buf.toString();
  }
  
  public static String createUniqueFilename(String prefix, String extension)
    throws Exception
  {
    long ts = System.currentTimeMillis();
    
    return prefix + "_" + ts + "_" + getRandom(ts, 8) + "." + extension;
  }
  
  public static String createProcessFileName(String prefix, String processName, long processId, long parentProcessId, String extension)
  {
    return prefix + processName + "_" + System.currentTimeMillis() + "_" + processId + "_" + parentProcessId + "_" + getRandom(processId, 4) + "." + extension;
  }
  
     public  static byte [] serializeObject(Object obj) throws Exception {
     
           long size = 0;
           ByteArrayOutputStream bStream = null;
           
           ObjectOutputStream oStream = null;
           
           if(!(obj instanceof java.io.Serializable)) {                        
               Util.log(Constants.WARNING,"Object not Serializable ::" + obj.getClass());                        
               throw new Exception("Not a Serializable Object !");
           }   
           try {
               bStream = new  ByteArrayOutputStream();
           
               oStream = new ObjectOutputStream(bStream);
           
               oStream.writeObject(obj);
               
               return bStream.toByteArray();
                              
           }
           catch(Exception e) {
               Util.log(Constants.ERROR,"Object not Serializable ::" + obj.getClass());                        
               throw e;
           }          
           finally {
                         
              try { if(oStream != null) oStream.close();} catch(Exception e){}
              try { if(bStream != null) bStream.close();} catch(Exception e){}
            
           }                                                
    } 
  
}