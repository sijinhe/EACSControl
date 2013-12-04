/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eac.server.managemnet;

import com.eac.db.entity.Server;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 *
 * @author Sijin
 */
public class ServerInfo {

    public static String propertyPath = "/config/config.properties";

    public ServerInfo(){
        
    }

    public String getIp(){

        String ip ="";

        try {
            String line;


            Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "ifconfig eth0 | sed '/inet\\ /!d;s/.*r://g;s/\\ .*//g'"});

            /**
             * Create a buffered reader from the Process' input stream.
             */
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            /**
             * Loop through the input stream to print the program output into console.
             */
            while ((line = input.readLine()) != null) {
                //  System.out.println(line);
                ip = line;

            }


            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ip;
    }

    public void writeConfig(Server s){

            Properties prop = new Properties();

            try {
                //set the properties value
                prop.setProperty("serverid", s.getIdServer());

                //save properties to project root folder
                prop.store(new FileOutputStream(propertyPath), null);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        
    }

    public void deleteFile(String path) {
        File bkup = new File(path);
        // Quick, now, delete it immediately:
        bkup.delete();
    }

    public String getServerId() {

        String id = "";

        Properties prop = new Properties();

        try {
            //load a properties file
            prop.load(new FileInputStream(propertyPath));

            id = prop.getProperty("serverid");

        } catch (IOException ex) {
            ex.printStackTrace();
        }


        return id;
    }

    public String getClusterId() {

        String id = "";

        Properties prop = new Properties();

        try {
            //load a properties file
            prop.load(new FileInputStream(propertyPath));

            id = prop.getProperty("clusterid");

        } catch (IOException ex) {
            ex.printStackTrace();
        }


        return id;
    }
}
