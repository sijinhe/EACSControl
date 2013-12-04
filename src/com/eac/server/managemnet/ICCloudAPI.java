/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.server.managemnet;

import com.eac.db.entity.Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sijin
 */
public class ICCloudAPI implements VMControlAPI {

    public String ip = "http://192.168.100.1:8080"; //"http://barking01.doc.ic.ac.uk:8080"; //

    private void command(String vmid, String execute) {
        try {

            String url = ip;
            url += "/RedDragonEnterprise/InstanceCtrlServlet";
            url += "?";
            url += "methodtype=executecommand";
            url += "&";
            url += "loginuser=sijinsijin";
            url += "&";
            url += "password=sijinsijin";
            url += "&";
            url += "executecommand=" + execute;
            url += "&";
            url += "zone=HUXLEY";
            url += "&";
            url += "vmid=" + vmid;

            URL oracle = new URL(url);
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(ICCloudAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        ICCloudAPI api = new ICCloudAPI();
      
    }

    public void start(Server s) {

        command(s.getIdServer(), "poweron");
       
    }

    public void stop(Server s) {
        command(s.getIdServer(), "poweroff");
       
    }
}
