/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lockbox.beans;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Schedule;
import javax.ejb.Stateless;

/**
 *
 * @author seemanapallik
 */
@Stateless
public class SAP_LOCKBOX_TmrBean implements SAP_LOCKBOX_TmrBeanLocal{
    
    private int id = (int)(Math.random() * 10000);

    public boolean active = true;
//    @Schedule(minute = "0/5", dayOfMonth = "*", month = "*", year = "*", hour = "*", dayOfWeek = "*")
   // @Schedule(minute = "*", second = "0", dayOfMonth = "*", month = "*", year = "*", hour = "9-17", dayOfWeek = "Mon-Fri")
    @Override
    public void myTimer() {
        
            if (this.active) {
            try {
//                Logger.getLogger(SAP_LOCKBOX_TmrBean.class.getName()).log(Level.INFO, "SAP_LOCKBOX_TmrBean Timer for " + id + " is working.");
                
            } catch (Exception ex) {
                Logger.getLogger(SAP_LOCKBOX_TmrBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(id+" "+" Timer event: " + new Date());
            
        }
        else System.out.println(id+" "+" Timer event: Not Active");
        
    }
    
    @Override
    public void setActive(){
        Logger.getLogger(SAP_LOCKBOX_TmrBean.class.getName()).log(Level.INFO, "SAP_LOCKBOX_TmrBean setActive for id " + id);
        this.active = true;
    }
    
    @Override
    public void setInactive(){
        Logger.getLogger(SAP_LOCKBOX_TmrBean.class.getName()).log(Level.INFO, "SAP_LOCKBOX_TmrBean setInactive for id " + id);
        this.active = false;
    }
    
    @Override
    public void toggleActive(){
        Logger.getLogger(SAP_LOCKBOX_TmrBean.class.getName()).log(Level.INFO, "SAP_LOCKBOX_TmrBean Toggle for id " + id);
        this.active = !this.active;
    }
    
    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean isActive() {
        return active;
    }
}
