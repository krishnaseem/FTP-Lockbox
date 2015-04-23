/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.washpost.ft.util;

/**
 *
 * @author SEEMANAPALLIK
 */

import java.io.*;
import java.util.*;
import java.sql.*;

import java.util.logging.*;


public class KeyGenerator {
    
  private static HashMap keyTbl = new HashMap();
  private static Vector  keyLock = new Vector();
  private final static   long  MAX_KEY_COUNT = 999999999;
  private final static   int   KEY_INC_COUNTER = 1;
  
  

  public KeyGenerator() {
  }

  public static long generateKey(String sequenceName)
                                throws Exception {
           long keyValue;

           synchronized(keyLock) {

                  String key =  sequenceName;

                  Key data = (Key) keyTbl.get(key);

                  if(data == null || (data.currentValue == data.maxValue)) {

                           // Need to put retry logic
                           keyValue =  generateKey(sequenceName,KEY_INC_COUNTER);

                           if(keyValue == -1) {
                               throw new Exception ("SequenceName not in SEQUENCE_GENERATOR table :" + sequenceName );
                           }

                           data = new Key(keyValue,keyValue,keyValue + KEY_INC_COUNTER);

                           keyTbl.put(key,data);
                  }
                  keyValue = data.currentValue;

                  data.currentValue++;

                  return keyValue;
           }
  }


  public static synchronized long generateKey(String sequenceName,int count)
                                throws Exception {

             PreparedStatement stmt=null;
             ResultSet rset=null;
             Connection db_conn = null;
             StringBuffer sql = new StringBuffer();
             long key=-1;
             long maxKeyCount;
             String sqlSeqNum="";

             try {

              //     db_conn = Utility.getConnection("PASDataSource");
                     db_conn = Util.getConnection("ftpds");

                   sql.append("SELECT SEQ_NUM ");
                   sql.append("FROM SEQUENCE_GENERATOR ");
                   //SJ 2009.02.11, commented and added line below
                   //sql.append("WHERE sequence_name = ? ");
                    sql.append("WHERE SEQUENCE_NAME = ? for update");

                   System.out.println(sql + "::" + sequenceName);
                   stmt = db_conn.prepareStatement(sql.toString());

                   stmt.clearParameters();

                   stmt.setString(1,sequenceName);

                   rset = stmt.executeQuery ();
				   db_conn.commit();

                    // Iterate through the result set
                    while(rset.next()) {
                          key =   rset.getInt(1);

                          // Update the key
                    }
                    rset.close();
                    stmt.close();
                    stmt = null;
                    rset = null;

                    if(key != -1) {

                          if((key + count) > MAX_KEY_COUNT) {
                              // reset key count
                              // key = 1;
                              sqlSeqNum = "";
                          }
                          else
                              sqlSeqNum = " seq_num + ";

                          sql = new StringBuffer();
                          sql.append("UPDATE SEQUENCE_GENERATOR SET SEQ_NUM = " + sqlSeqNum  + count );
                          sql.append(" WHERE SEQUENCE_NAME = ? ");
                          sql.append(" AND   SEQ_NUM = ? ");
                          stmt = db_conn.prepareStatement(sql.toString());

                          stmt.clearParameters();

                          stmt.setString(1,sequenceName);
                          stmt.setLong(2,key);

                          if(stmt.executeUpdate() != 1) {
                               System.out.println(
                                           ":Not able to generate a new UNIQUE key for sequence :" + sequenceName);
                               throw new Exception("Not able to generate a new UNIQUE key for sequence :" + sequenceName );
                          }
						  db_conn.commit();
                          return key;
                    }

                    return -1;
               }
               catch (Exception e) {
                     System.out.println(e.getMessage());
                     throw e;
               }
             finally { 
                 Util.release(db_conn, stmt, rset);
                 System.out.println("Closing Connection in KeyGenerator(generateKey).");  
             }}
  }
 
  
     
    
    
