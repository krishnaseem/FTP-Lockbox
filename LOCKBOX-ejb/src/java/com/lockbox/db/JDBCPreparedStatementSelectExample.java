/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lockbox.db;

/**
 *
 * @author seemanapallik
 */
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCPreparedStatementSelectExample {

    public static Logger logger=LoggerFactory.getLogger(JDBCPreparedStatementSelectExample.class);
   private void closeConnection(Connection connection) throws SQLException{
       connection.close();
   }
    
    private  Connection getConnection() {
    Connection connection = null;
    try {
        InitialContext context = new InitialContext();
        DataSource dataSource = (DataSource) context
            .lookup("ftpds");
         if(dataSource==null){
                logger.debug("LockBox Error, Datasource is null.");
            }
        connection = dataSource.getConnection();
    } catch (NamingException e) {
        e.printStackTrace();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return connection;
    }
        
        
    	public  Map<String,String> selectRecordsFromTable() throws SQLException {
 
             
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
                SFTPConfiguration configuration=new SFTPConfiguration();
 
		String selectSQL = "SELECT * FROM APP_PARAMETER where PARAM_NAME IN( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                Map<String,String> configMap=new HashMap<String,String>();  
		try {
			
                        InitialContext context = new InitialContext();
                        DataSource dataSource = (DataSource) context.lookup("ftpds");
                        if(dataSource==null){
                           logger.debug("LockBox Error, Datasource is null.");
                        }
                        dbConnection=dataSource.getConnection();
			            preparedStatement = dbConnection.prepareStatement(selectSQL);
                        preparedStatement.setString(1, ParamEnum.FTPLOCALDIR.name);
                        preparedStatement.setString(2, ParamEnum.PROTOCOL.name);
                        preparedStatement.setString(3, ParamEnum.FTPHOST.name);
                        preparedStatement.setString(4,ParamEnum.FTPPASSWORD.name);
                        preparedStatement.setString(5, ParamEnum.FILEPATTERN.name);
                        preparedStatement.setString(6, ParamEnum.FTPARCH.name);
                        preparedStatement.setString(7, ParamEnum.FTPDIR.name);
                        preparedStatement.setString(8, ParamEnum.FTPPOSTDELETE.name);
                        preparedStatement.setString(9, ParamEnum.FTPPOSTARCHIVE.name);
                        preparedStatement.setString(10, ParamEnum.FILEARCHIVEDIR.name);
                        preparedStatement.setString(11, ParamEnum.EMAIL_USER.name);
                        preparedStatement.setString(12, ParamEnum.EMAIL_YN.name);
                        preparedStatement.setString(13, ParamEnum.ENCRYPT.name);
                        preparedStatement.setString(14, ParamEnum.LOCALARCHIVE.name);
                        preparedStatement.setString(15, ParamEnum.POSTPROCESS.name);
                        preparedStatement.setString(16, ParamEnum.FILE_PROCESSED_EMAIL_YN.name);
                        preparedStatement.setString(17, ParamEnum.FTPUSER.name);
                        preparedStatement.setString(18, ParamEnum.PORT.name);
                        preparedStatement.setString(19, ParamEnum.SFTPKEY.name);
                        preparedStatement.setString(20, ParamEnum.INPUT_QUEUE.name);
                        preparedStatement.setString(21, ParamEnum.DUPLICATESALLOWED.name);
                        preparedStatement.setString(22, ParamEnum.ASCII_YN.name);
                        preparedStatement.setString(23, ParamEnum.ENV.name);
 
			// execute select SQL stetement
			ResultSet rs = preparedStatement.executeQuery();
 
			while (rs.next()) {
 
			    String param_name =rs.getString("PARAM_NAME");	
                            String param_value = rs.getString("PARAM_VALUE");
			
                            configMap.put(param_name,param_value);
				//logger.debug(param_name +" :" + param_value);
				
 
			}
                    
		} catch (SQLException e) {
 
			logger.error("LockBox Error: " + e.getMessage());
 
		} finally {
 
			if (preparedStatement != null) {
				preparedStatement.close();
			}
 
			if (dbConnection != null) {
				dbConnection.close();
                                logger.debug("dbConnection closed here.");
			}
                     return configMap;
		}
 
	}
 
}

