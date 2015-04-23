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
public class LOCKBOX_TmrBean implements LOCKBOX_TmrBeanLocal{

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    private int id = (int)(Math.random() * 10000);
    private String[] args = null;

    public boolean active = true;
//    @Schedule(minute = "0/15", dayOfMonth = "*", month = "*", year = "*", hour = "*", dayOfWeek = "*")
    //@Schedule(second = "*/20", minute = "*", dayOfMonth = "*", month = "*", year = "*", hour = "*", dayOfWeek = "*")
    @Override
    public void myTimer() {
        if (this.active) {
            try {
//                Logger.getLogger(LOCKBOX_TmrBean.class.getName()).log(Level.INFO, "LOCKBOX_TmrBean Timer for " + id + " is working.");
                
            } catch (Exception ex) {
                Logger.getLogger(LOCKBOX_TmrBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(id+" "+" Timer event: " + new Date());
            com.washpost.main.Main.main(args);
        }
        else System.out.println(id+" "+" Timer event: Not Active");
    }
    
    @Override
    public void setActive(){
        Logger.getLogger(LOCKBOX_TmrBean.class.getName()).log(Level.INFO, "LOCKBOX_TmrBean setActive for id " + id);
        this.active = true;
    }
    
    @Override
    public void setInactive(){
        Logger.getLogger(LOCKBOX_TmrBean.class.getName()).log(Level.INFO, "LOCKBOX_TmrBean setInactive for id " + id);
        this.active = false;
    }
    
    @Override
    public void toggleActive(){
        Logger.getLogger(LOCKBOX_TmrBean.class.getName()).log(Level.INFO, "LOCKBOX_TmrBean Toggle for id " + id);
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
