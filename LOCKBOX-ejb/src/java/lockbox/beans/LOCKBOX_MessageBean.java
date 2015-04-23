/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lockbox.beans;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 *
 * @author seemanapallik
 */
@MessageDriven(mappedName = "jms/LOCKBOX_JmsQueue", activationConfig = {
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class LOCKBOX_MessageBean implements MessageListener {
    
        @EJB
    LOCKBOX_TmrBeanLocal timerSessionBean;
    private int id = (int) (Math.random() * 10000);
    
    public LOCKBOX_MessageBean() {
    }
    
    public int getId() {
      return id;
    }
    
    @Override
    public void onMessage(Message message) {
        
        if (message == null) {
            System.out.println("Received JMS message is null.");
            return;

        }
        if (!(message instanceof TextMessage)) {
            System.out.println("Not a text message. Ignore");
            return;
        }
        try {
            String text = ((TextMessage) message).getText();
            System.out.println(id+": "+text);
            if (text.equals("START")) {
                Logger.getLogger(LOCKBOX_MessageBean.class.getName()).log(Level.INFO, "LOCKBOX_MessageBean Toggle Active");

                timerSessionBean.setActive();
            } else if (text.equals("STOP")) {
                Logger.getLogger(LOCKBOX_MessageBean.class.getName()).log(Level.INFO, "LOCKBOX_MessageBean Toggle Inactive");
                timerSessionBean.setInactive();
            } else {
               Logger.getLogger(LOCKBOX_MessageBean.class.getName()).log(Level.INFO, "LOCKBOX_MessageBean Toggle Active");
                timerSessionBean.toggleActive();
                com.washpost.main.Main.main(null);
            }
        } catch (JMSException ex) {
            Logger.getLogger(LOCKBOX_MessageBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}




