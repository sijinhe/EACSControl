/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.tool.file;

import com.eac.db.dao.ServerDAO;
import com.eac.db.entity.Container;
import com.eac.db.entity.Server;
import com.eac.db.entity.ServerHasContainer;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sijin
 */
public class WorkersProperties {

    public static String path = "C:\\Users\\Sijin\\Desktop\\"; //for windows
    //public static String path = "/conf/";
    PrintWriter output = null;

    public WorkersProperties() {
    }

    private void writeToFile(HashMap map, String fileName) throws Exception {

        Properties properties = new Properties();
        Set set = map.keySet();
        Iterator itr = set.iterator();
        while (itr.hasNext()) {
            String key = (String) itr.next();
            String value = (String) map.get(key);
            properties.setProperty(key, value);
        }

        //We have all the properties, now write them to the file.
        //The second argument is optional. You can use it to identify the file.
        properties.store(new FileOutputStream(fileName), "workers properties");

        //To keep this example simple, I did not include any exception handling
        //code, but in your application you might want to handle exceptions here
        //according to your requirements.

    }

    public void modify(String path) {
        try {

            HashMap map = new HashMap();
            //put applications
            //# Define worker names
            String worklist = "jkstatus";

            ServerDAO sdao = new ServerDAO();
            List<ServerHasContainer> shc = sdao.fetchServersContainerRelation();

            HashSet<String> containerid = new HashSet<String>();

            for(ServerHasContainer s: shc){
                containerid.add(s.getContainer().getIdContainer());
            }

            Iterator it = containerid.iterator();

            while(it.hasNext()){
                
                List<ServerHasContainer> newshc = sdao.fetchServersContainerRelationByContainerId((String)it.next());
               // System.out.println((String)it.next());
                String id = newshc.get(0).getContainer().getAppName();

                worklist += ", " + id;
                map.put("worker." + id + ".type", "lb");
                map.put("worker." + id + ".method", "B");
                map.put("worker." + id + ".sticky_session", "0");

                String catList = "";

                for (int j = 0; j < newshc.size(); j++) {
                    String catname = newshc.get(j).getServer().getIdServer();

                    if (j == 0) {
                        catList += catname;
                    } else {
                        catList += ", " + catname;
                    }
                }

                map.put("worker." + id + ".balance_workers", catList);
            }

            map.put("worker.list", worklist);
            //# Create virtual workers
            map.put("worker.jkstatus.type", "status");

            for (int i = 0; i < shc.size(); i++) {
                Server server = shc.get(i).getServer();
                String catname = server.getIdServer();

                map.put("worker." + catname + ".type", "ajp13");
                map.put("worker." + catname + ".host", server.getIp());
                map.put("worker." + catname + ".port", "8009");
                map.put("worker." + catname + ".lbfactor", "1");
            }

            writeToFile(map, path + "workers.properties");

        } catch (Exception ex) {
            Logger.getLogger(WorkersProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public static void main(String[] args) {
        WorkersProperties wp = new WorkersProperties();
        wp.modify(WorkersProperties.path);

    }
}
