/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lockbox.beans;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;


/**
 *
 * @author seemanapallik
 */
@Stateless
public class LOCKBOX_SessionBean implements LOCKBOX_SessionBeanLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    //@Resource(lookup = "jms/SCP2PCD_RT_JMSPool")
   @Resource(lookup = "jms/SAP_LockBox_ConnectionFactory")
    private ConnectionFactory connectionFactory;
    //@Resource(lookup = "jms/SCP2PCD_RT_JmsQueue")
    @Resource(lookup = "jms/LOCKBOX_JmsQueue")
    private Queue jmsQueue;

    private int id = (int)(Math.random() * 10000);
    
    @Override
    public String sendMessage(String text) {
        String resultOut;
        try {
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(
                    false,
                    Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(jmsQueue);
            TextMessage message = session.createTextMessage(text);
            producer.send(message);
            resultOut = id+": Message Sent: " + text;
            producer.close();
            session.close();
            connection.close();
        } catch (JMSException ex) {
            resultOut = ex.getMessage();
            System.out.println(id+": "+ex.getMessage());
        }
        return resultOut;
    }

}
