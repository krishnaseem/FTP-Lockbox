/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lockbox.db;

/**
 *
 * @author seemanapallik
 */
public class SFTPConfiguration {
    String ftpPattern;
    String ftphost;
    int ftpPort=22;

    public String getFtpPattern() {
        return ftpPattern;
    }

    public void setFtpPattern(String ftpPattern) {
        this.ftpPattern = ftpPattern;
    }
    
    public String getFtpHost(){
        return ftphost;
    }
    
    public void setFtpHost(String ftphost) {
        this.ftphost = ftphost;
    }
    
    public int getftpPort(){
        return 0;
    }
    
    public void setftpPort(int ftpPort) {
        this.ftpPort = ftpPort;
    }                             
    
}
