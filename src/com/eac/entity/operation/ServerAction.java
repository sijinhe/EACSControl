/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.entity.operation;

import com.eac.db.dao.ClusterDAO;
import com.eac.db.dao.ServerDAO;
import com.eac.db.entity.Cluster;
import com.eac.db.entity.Server;
import com.eac.server.managemnet.ServerController;
import com.eac.server.managemnet.ServerInfo;
import com.eac.server.managemnet.TomcatPatch;
import com.eac.tool.file.BytesStreamsAndFiles;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sijin
 */
public class ServerAction {

    ServerController con;

    public ServerAction() {
    }

    public boolean initialis() {
        String idtomcat = UUID.randomUUID().toString();
        con = new ServerController(idtomcat);
        con.create("8080", "TOMCAT");
        TomcatPatch cat = new TomcatPatch();
        cat.ModifyServerXML();

        return true;
    }

    public boolean start() {

        ServerInfo info = new ServerInfo();
        con = new ServerController(info.getServerId());
        con.start();

        return true;
    }

    public boolean stop() {

        ServerInfo info = new ServerInfo();
        con = new ServerController(info.getServerId());
        con.stop();

        return true;
    }

    public boolean destroy() {
        ServerInfo info = new ServerInfo();
        con = new ServerController(info.getServerId());
        con.delete();
        return true;
    }

    public String getLog(String appname, String date) {

        String json = "";
        String path = "/tomcat/logs/app/" + appname + "/";

        String filename = path + appname + "." + date + ".log";
        File file = new File(filename);

        boolean exists = file.exists();
        if (exists) {
//            BytesStreamsAndFiles test = new BytesStreamsAndFiles();
//            //read in the bytes
//            byte[] fileContents = test.read(filename);
//            //test.readAlternateImpl(INPUT_FILE_NAME);
//            //write it back out to a different file name
//            fileContents.toString();
//            test.write(fileContents, OUTPUT_FILE_NAME);

            FileInputStream fstream = null;
            try {
                fstream = new FileInputStream(filename);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ServerAction.class.getName()).log(Level.SEVERE, null, ex);
            }
            DataInputStream in = new DataInputStream(fstream);
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;


                while ((strLine = br.readLine()) != null) {
                    // Print the content on the console

                    json += strLine + "\n";
                }

                //Read File Line By Line


            } catch (Exception e) {//Catch exception if any
                System.err.println("Error: " + e.getMessage());
            } finally {
                try {
                    //Close the input stream
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(ServerAction.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } else {
            json = "No logging avaialbe";
        }

        return json;
    }

    public Server createServer(String serverid, String ip, String port, String type, String homePath, String clusterid) {

        Server server = new Server(serverid);

        Cluster cluster = new Cluster(clusterid);

        ClusterDAO cdao = new ClusterDAO();

        cluster = cdao.fetch(cluster);

        server.setCluster(cluster);
        server.setHomePath(homePath);
        server.setIp(ip);
        server.setPort(port);
        server.setStatus("STOPPED");
        server.setType(type);

        ServerDAO sdao = new ServerDAO();

        if (!sdao.create(server)) {
            return null;
        }

        return server;
    }

    public Server startServer(Server server) {

        ServerDAO cdao = new ServerDAO();

        server.setStatus("RUNNING");

        if (!cdao.modify(server)) {
            return null;
        }

        return server;
    }
}
