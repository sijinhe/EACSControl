/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eac.tool.file;

import com.eac.db.dao.ServerDAO;
import com.eac.db.entity.ServerHasContainer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author Sijin
 */
public class AppConf {

   // public static String path = "C:\\Users\\Sijin\\Desktop\\"; //for windows
    //public static String path = "/conf/";

    public AppConf() {
    }

    public void modify(String path) {
        try {
            // Create file
            FileWriter fstream = new FileWriter(path + "app.conf");
            BufferedWriter out = new BufferedWriter(fstream);

            ServerDAO sdao = new ServerDAO();
            List<ServerHasContainer> shc = sdao.fetchServersContainerRelation();

            List<String> oldList = new ArrayList<String>();

            for(ServerHasContainer s: shc){
                oldList.add(s.getContainer().getAppName());
            }

            List<String> newList = new ArrayList<String>(new HashSet<String>(oldList));

            for (int i = 0; i < newList.size(); i++) {
                String name = newList.get(i);
               
                out.write("JkMount /" + name + "/* " + name);
                out.newLine();
                out.write("JkMount /" + name + " " + name);
                out.newLine();
            }

            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
            AppConf appConf = new AppConf();

           // appConf.modify(AppConf.path);
      
    }
}
