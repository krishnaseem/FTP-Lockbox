/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lockbox.beans;

import javax.ejb.Local;

/**
 *
 * @author seemanapallik
 */
@Local
public interface LOCKBOX_TmrBeanLocal {
    
    public void myTimer();

    public void setActive();

    public void setInactive();

    public void toggleActive();

    public int getId();

    public boolean isActive();
    
}
