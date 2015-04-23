/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.washpost.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processes.FTPInbound;

/**
 *
 * @author seemanapallik
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        Logger Log=LoggerFactory.getLogger(Main.class);
        
        FTPInbound inbound=new FTPInbound();
        try{
//           inbound.initialize();
        //System.out.println("----------------------Start Processing--------------------------");
        inbound.connectAndGetListOfFiles();
        inbound.getFileAndProcessIt();
        }catch(Exception e){
           Log.error(e.getMessage());
        }
    }
    
}
