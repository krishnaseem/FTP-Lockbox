///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.washpost.ft.util;
//
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
////import java.util.*;
//import java.util.Hashtable;
//import javax.jms.*;
//import com.washpost.ft.util.Util;
//import java.util.HashMap;
//import java.util.Iterator;
//import javax.naming.Context;
//import javax.jms.Connection;
//import javax.jms.ConnectionFactory;
//import javax.jms.JMSException;
//import javax.jms.MessageProducer;
//import javax.jms.Queue;
//import javax.jms.Session;
//import javax.jms.TextMessage;
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//
///**
// *
// * @author SEEMANAPALLIK
// */
//public class Queue_Send {
//    
//      // Defines the JMS context factory.
//  public final static String JMS_FACTORY="javax.jms.ConnectionFactory";
//
//  // Defines the queue.
//
//  private ConnectionFactory qconFactory;
//  private Connection qcon;
//  private Session qsession;
//  private QueueSender qsender;
//  private Queue queue;
//  private TextMessage txtMsg;
//  private BytesMessage bytesMsg;
//    private String text;
//  
//
//  public void init(Context ctx, String queueName,int birthTime,
//                   boolean transactionFlag,boolean persistenceFlag)
//    throws NamingException, JMSException
//  {
//      
//      MessageProducer producer = null;
//      Connection connection = null;
//      Session session = null;
//      try {
//          
//         //  qconFactory = (QueueConnectionFactory) ctx.lookup(JMS_FACTORY);
//        
////    qcon = qconFactory.createConnection();
////    
////    
////    qsession = qcon.createSession(transactionFlag, Session.AUTO_ACKNOWLEDGE);
//        
//    //((WLSession)qsession).setRedeliveryDelay(delay);
//    
////    queue = (Queue) ctx.lookup("jms/LOCKBOX_SAP_JmsQueue");
////    qsender = qsession.createSender(queue);
//    
//            
//            qconFactory = (ConnectionFactory) ctx.lookup("javax.jms.ConnectionFactory");
//            connection = qconFactory.createConnection();
//            session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
//            Object temp = ctx.lookup("jms/LOCKBOX_SAP_JmsQueue");
//            if (temp.getClass().getName().endsWith("Topic")) {
//                producer = session.createProducer((Topic)temp);
//            } else {
//                producer = session.createProducer((Queue) temp);
//            }
//            TextMessage message = session.createTextMessage(text);
//            producer.send(message);
//    
//    if(birthTime > 0)
//       ((javax.jms.MessageProducer)qsender).setDeliveryMode(birthTime);   // changed old code here.
//
//    txtMsg =   qsession.createTextMessage();
//    bytesMsg = qsession.createBytesMessage();
//            
//    if(persistenceFlag)
//       qsender.setDeliveryMode(DeliveryMode.PERSISTENT);
//    else   
//       qsender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);    
//    
//    qcon.start();
//      }catch (Exception e) {
//                     System.out.println(e.getMessage());
//                     throw e;
//               }
//      
//   
//  }
//
//  /**
//   * Closes JMS objects.
//   * @exception JMSException if JMS fails to close objects due to internal error
//   */
//  public void close() throws JMSException {
//
//    //SJ 2007.12.10, added conditional close to avoid null pointer
//    if ( qsender != null )
//        qsender.close();
//    if ( qsession != null )
//        qsession.close();
//    if ( qcon != null )
//        qcon.close();
//    //END SJ 2007.12.10, added code to handle exception properly
//
//  }
//
//  public static void sendMessageToQueue(String queue,String txtData,byte [] byteData,
//                                        String dataType,int birthTime,
//                                        boolean transactionFlag,boolean persistenceFlag,
//                                        String param1Name , String param1Value,
//                                        String param2Name , String param2Value,
//                                        String param3Name , String param3Value
//                                        ) throws Exception {
//
//    InitialContext ic = Util.getInitialContext();
//    
//    //SJ 2007.12.10, added code to handle exception properly
//    Queue_Send qs = null;
//    try{
//        qs = new Queue_Send();
//        qs.init(ic, queue,birthTime,transactionFlag,persistenceFlag);
//        qs.sendMessage(txtData,byteData,dataType,
//                        param1Name,param1Value,
//                        param2Name,param2Value,
//                        param3Name,param3Value                    
//                        );
//        if(transactionFlag)                
//           qs.qsession.commit();
//        qs.close();
//        qs = null;
//    }catch(Exception ex)
//    {
//        throw ex;
//    }
//    finally
//    {
//        if ( qs != null )
//            qs.close();
//    }
//    //END SJ 2007.12.10, added code to handle exception properly
//    
//  }
//
//
//  public static void sendMessageToQueue(String queue,String textData,byte [] byteData,
//                                 String dataType,int birthTime,
//                                 boolean transactionFlag,boolean persistenceFlag,
//                                 HashMap map
//                                 ) throws Exception {
//
//    InitialContext ic = Util.getInitialContext();
//    
//    //SJ 2007.12.10, added code to handle exception properly
//    Queue_Send qs = null;
//    try{
//        qs = new Queue_Send();
//        qs.init(ic, queue,birthTime,transactionFlag,persistenceFlag);
//        qs.sendMessage(textData,byteData,dataType,map);
//        if(transactionFlag)                
//           qs.qsession.commit();
//        qs.close();
//        qs = null;
//    }catch(Exception ex)
//    {
//        throw ex;
//    }
//    finally
//    {
//        if ( qs != null )
//            qs.close();
//    }
//    //END SJ 2007.12.10, added code to handle exception properly
//  }
//
//
//  public static void sendMessageToQueue(String queue,Message message,int birthTime,
//                                        boolean transactionFlag,boolean persistenceFlag
//                                        ) throws Exception {
//
//    InitialContext ic = Util.getInitialContext();
//    
//    //SJ 2007.12.10, added code to handle exception properly
//    Queue_Send qs = null;
//    try{
//        qs = new Queue_Send();
//        qs.init(ic, queue,birthTime,transactionFlag,persistenceFlag);
//        qs.qsender.send(message);
//        if(transactionFlag)                
//           qs.qsession.commit();
//        qs.close();
//        qs = null;
//    }catch(Exception ex)
//    {
//        throw ex;
//    }
//    finally
//    {
//        if ( qs != null )
//            qs.close();
//    }
//    //END SJ 2007.12.10, added code to handle exception properly
//    
//  }
//
//
//
//  void sendMessage(String textData,byte [] byteData,String dataType,
//                   String param1Name , String param1Value,
//                   String param2Name , String param2Value,
//                   String param3Name , String param3Value
//                   ) throws Exception
//  {
//       Message message;
//    
//        if(Util.isTextMessage(dataType))  {
//            //txtMsg =   qsession.createTextMessage();
//            txtMsg.setText(textData);
//            message = txtMsg;
//        }    
//        else {    
//            //bytesMsg = qsession.createBytesMessage();
//            //bytesMsg.set   
//            bytesMsg.writeBytes(byteData);        
//            message = bytesMsg;
//        }        
//        
//        message.setStringProperty("DataType",dataType);
//
//        int messageSize = 0;
//        if(dataType.equals(Constants.BINARY_DATA)) {
//            if(byteData != null)
//               messageSize = byteData.length;
//        }
//        else {
//            if(textData == null)
//              messageSize = textData.length(); 
//        }         
//
//        message.setIntProperty("MessageSize",messageSize);
//
//        if(!param1Name.equals("")) 
//           message.setStringProperty(param1Name,param1Value);
//
//        if(!param2Name.equals("")) 
//            message.setStringProperty(param2Name,param2Value);
//            
//        if(!param3Name.equals(""))                   
//            message.setStringProperty(param3Name,param3Value);
//
//        qsender.send(message);                         
//  }  
//  
//  
//
//  void sendMessage(String textData,byte [] byteData,String dataType,HashMap map
//                   ) throws Exception
//  {
//    
//       String key;
//       String value;
//
//       Message message;
//    
//        if(Util.isTextMessage(dataType))  {
//            //txtMsg =   qsession.createTextMessage();
//            txtMsg.setText(textData);
//            message = txtMsg;
//        }    
//        else {    
//            //bytesMsg = qsession.createBytesMessage();
//            //bytesMsg.set   
//            bytesMsg.writeBytes(byteData);        
//            message = bytesMsg;
//        }        
//        
//        message.setStringProperty("DataType",dataType);
//       
//        int messageSize = 0;
//        if(dataType.equals(Constants.BINARY_DATA)) {
//            if(byteData != null)
//               messageSize = byteData.length;
//        }
//        else {
//            if(textData == null)
//              messageSize = textData.length(); 
//        }         
//
//        message.setIntProperty("MessageSize",messageSize);
//              
//       for(Iterator it =  map.keySet().iterator();it.hasNext();) {
//            key = (String) it.next();
//            value = (String)map.get(key);
//            message.setStringProperty(key,value);        
//       } 
//       
//       qsender.send(message);                         
//       
//  }  
//
//
//
//
//  public static void main(String []args) throws Exception  {
//    
//         Queue_Send.sendMessageToQueue("Queue1","How are u:" ,new byte[0],
//                                      Constants.TEXT_DATA,0,false,false,
//                                      "Param1" ,"Value1",
//                                      "Param2" ,"Value2",
//                                      "Param3" ,"Value3"
//                                     );
//         HashMap map = new HashMap();
//         
//         map.put("Company","BEA");
//         map.put("Address","13381 Sir Ramsey Way");
//          
//
//         Queue_Send.sendMessageToQueue("RawDataQueue","" ,Util.serializeObject(map),
//                                      Constants.BINARY_DATA,0,false,false,
//                                      "Param1" ,"Value1",
//                                      "Param2" ,"Value2",
//                                      "Param3" ,"Value3"
//                                     );
//
//  }  
//    
//}
