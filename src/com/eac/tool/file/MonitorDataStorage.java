/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.tool.file;

import com.eac.monitor.management.AppHttpInfo;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Sijin
 */
public class MonitorDataStorage {

 //   public static final String PATH = "C:\\Users\\Sijin\\Desktop\\monitordata\\";
    public static final String PATH = "/config/monitor/";
    AppHttpInfo info;
    private String dir;

    public MonitorDataStorage(AppHttpInfo info) {
       this.info = info;
       this.dir = MonitorDataStorage.PATH + info.getId() + "_" + info.getServerid() + ".ser";

    }

    public AppHttpInfo getInfo() {
        return info;
    }

    public void setInfo(AppHttpInfo info) {
        this.info = info;
    }

    

    public boolean ifExist() {
        boolean exists = (new File(dir)).exists();

        return exists;
    }

    public boolean  delete() {
        
        File f1 = new File(dir);
        
        return f1.delete();

    }

    public void write() {

        try {

            FileOutputStream fs = new FileOutputStream(dir);
            ObjectOutputStream os = new ObjectOutputStream(fs);

            os.writeObject(info);
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public AppHttpInfo read() {

        AppHttpInfo temp = null;

        try {

            FileInputStream fs = new FileInputStream(dir);
            ObjectInputStream os = new ObjectInputStream(fs);

            temp = (AppHttpInfo) os.readObject();

            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            return temp;
        }

    }

    public static void main(String[] args) {




   //     mds.delete("ddfdfddd");
    }
}
