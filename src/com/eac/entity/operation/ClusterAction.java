/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.entity.operation;

import com.eac.cluster.management.Apache;
import com.eac.db.dao.ClusterDAO;
import com.eac.db.dao.ContainerDAO;
import com.eac.db.dao.ServerDAO;
import com.eac.db.entity.Cluster;
import com.eac.db.entity.Container;
import com.eac.db.entity.Server;
import com.eac.db.entity.ServerHasContainer;
import com.eac.tool.json.JSONCluster;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 *
 * @author Sijin
 */
public class ClusterAction {

    static final String kuser = "sijin"; // your account name
    static final String kpass = "sijin"; // your password for the account

    public boolean startAllInstances(String containerid) {
        boolean isStart = true;

        ServerDAO sdao = new ServerDAO();

        List<ServerHasContainer> shclist = sdao.fetchServersContainerRelationByContainerId(containerid);

        for (ServerHasContainer s : shclist) {

            if (!startInstance(s)) {
                isStart = false;
                break;
            }

        }

        return isStart;
    }

    private boolean startInstance(ServerHasContainer s) {
        Container container = s.getContainer();
        Server server = s.getServer();

        Authenticator.setDefault(new MyAuthenticator());
        String url = "http://" + server.getIp() + ":" + server.getPort() + "/manager/start";
        //      String url = "http://146.169.35.36:8080/manager/stop";

        String parameter = "path=/" + container.getAppName();
        url = url + "?" + parameter;

        System.out.println(url);

        boolean isValid = false;

        URL yahoo = null;
        try {
            yahoo = new URL(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);

            return isValid;
        }

        URLConnection yc = null;
        try {
            yc = yahoo.openConnection();
        } catch (IOException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);
            return isValid;
        }

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);
            return isValid;
        }

        try {

            String inputLine = in.readLine();

            if (inputLine.substring(0, 2).equals("OK")) {
                isValid = true;

            }


        } catch (IOException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);

            return isValid;

        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return isValid;
    }

    public boolean reloadAllInstances(String containerid) {
        boolean isReload = true;

        ServerDAO sdao = new ServerDAO();

        List<ServerHasContainer> shclist = sdao.fetchServersContainerRelationByContainerId(containerid);

        for (ServerHasContainer s : shclist) {

            if (!reloadInstance(s)) {
                isReload = false;
                break;
            }

        }

        return isReload;
    }

    private boolean reloadInstance(ServerHasContainer s) {
        Container container = s.getContainer();
        Server server = s.getServer();

        Authenticator.setDefault(new MyAuthenticator());
        String url = "http://" + server.getIp() + ":" + server.getPort() + "/manager/reload";
        //      String url = "http://146.169.35.36:8080/manager/stop";

        String parameter = "path=/" + container.getAppName();
        url = url + "?" + parameter;

        System.out.println(url);

        boolean isValid = false;

        URL yahoo = null;
        try {
            yahoo = new URL(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);

            return isValid;
        }

        URLConnection yc = null;
        try {
            yc = yahoo.openConnection();
        } catch (IOException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);
            return isValid;
        }

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);
            return isValid;
        }

        try {

            String inputLine = in.readLine();

            if (inputLine.substring(0, 2).equals("OK")) {
                isValid = true;

            }


        } catch (IOException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);

            return isValid;

        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return isValid;
    }

    public boolean undeployAllInstances(String containerid) {
        boolean isUndeploy = true;

        ServerDAO sdao = new ServerDAO();

        List<ServerHasContainer> shclist = sdao.fetchServersContainerRelationByContainerId(containerid);

        for (ServerHasContainer s : shclist) {

            if (!undeployInstance(s)) {
                isUndeploy = false;
                break;
            }

        }

        return isUndeploy;
    }

    public boolean undeployOneInstance(String containerid) {

        boolean isUndeploy = true;

        ServerDAO sdao = new ServerDAO();

        List<ServerHasContainer> shclist = sdao.fetchServersContainerRelationByContainerId(containerid);

        Random random = new Random();

        ServerHasContainer s = shclist.get(random.nextInt(shclist.size()));

        if (!undeployInstance(s)) {
            isUndeploy = false;

        }

        return isUndeploy;

    }

    public boolean undeployInstance(ServerHasContainer s) {
        Container container = s.getContainer();
        Server server = s.getServer();

        Authenticator.setDefault(new MyAuthenticator());
        String url = "http://" + server.getIp() + ":" + server.getPort() + "/manager/undeploy";
        //       String url = "http://146.169.35.36:8080/manager/undeploy";

        String parameter = "path=/" + container.getAppName();
        url = url + "?" + parameter;

        System.out.println(url);

        boolean isValid = false;

        URL yahoo = null;
        try {
            yahoo = new URL(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);

            return isValid;
        }

        URLConnection yc = null;
        try {
            yc = yahoo.openConnection();
        } catch (IOException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);
            return isValid;
        }

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);
            return isValid;
        }

        try {

            String inputLine = in.readLine();

            if (inputLine.substring(0, 2).equals("OK")) {
                isValid = true;
                cleanUp(s);
            }

            if (inputLine.substring(0, 24).equals("FAIL - No context exists")) {
                isValid = true;
                cleanUp(s);
            }


        } catch (IOException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);

            return isValid;

        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return isValid;
    }

    private boolean cleanUp(ServerHasContainer s) {
        Container container = s.getContainer();
        Server server = s.getServer();

        String url = "http://" + server.getIp() + ":" + server.getPort() + "/EACNodeController/CleanUp";
        //       String url = "http://146.169.35.36:8080/manager/undeploy";

        String parameter = "appname=" + container.getAppName();
        url = url + "?" + parameter;

        System.out.println(url);

        boolean isValid = false;

        URL yahoo = null;
        try {
            yahoo = new URL(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);

            return isValid;
        }

        URLConnection yc = null;
        try {
            yc = yahoo.openConnection();
        } catch (IOException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);
            return isValid;
        }

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);
            return isValid;
        }

        try {

            String inputLine = in.readLine();

            if (inputLine.equals("true")) {
                isValid = true;
            }


        } catch (IOException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);

            return isValid;

        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return isValid;
    }

    public String getLoggingfromServers(String containerid, String date) {

        String json = "";

        ServerDAO sdao = new ServerDAO();

        List<ServerHasContainer> shc = sdao.fetchServersContainerRelationByContainerId(containerid);

        JSONCluster jc = new JSONCluster();

        String array = "";

        JSONArray returnArray = new JSONArray();

        for (ServerHasContainer s : shc) {

            JSONObject jobj = (JSONObject) JSONSerializer.toJSON(getLogging(s, date));

            returnArray.add(jobj);

        }

        array = returnArray.toString();

        json = jc.createLoggingJSON(array, true);

        return json;
    }

    private String getLogging(ServerHasContainer s, String date) {
        String json = "";

        JSONObject jobj = new JSONObject();

        Container container = s.getContainer();
        Server server = s.getServer();

        jobj.put("appname", container.getAppName());
        jobj.put("serverid", s.getServer().getIdServer());
        jobj.put("date", date);
        jobj.put("log", "");

        json = jobj.toString();

        String url = "http://" + server.getIp() + ":" + server.getPort() + "/EACNodeController/";
        String servlet = "GetLogging";
        String parameter1 = "appname=" + container.getAppName();
        String parameter2 = "date=" + date;

        url = url + servlet + "?" + parameter1 + "&" + parameter2;

        URL yahoo = null;
        try {
            yahoo = new URL(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);

            return json;
        }

        URLConnection yc = null;
        try {
            yc = yahoo.openConnection();
        } catch (IOException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);
            return json;
        }

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);
            return json;
        }

        try {

            String inputLine = "";
            String temp = "";

            while ((inputLine = in.readLine()) != null) {
                // Print the content on the console
                temp += inputLine + " \n ";
            }


            //  JSONObject jsonp = (JSONObject) JSONSerializer.toJSON(inputLine);

            jobj.put("log", temp);


        } catch (IOException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);

            return json;

        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        json = jobj.toString();

        return json;
    }

    static class MyAuthenticator extends Authenticator {

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            // I haven't checked getRequestingScheme() here, since for NTLM
            // and Negotiate, the usrname and password are all the same.
            System.err.println("Feeding username and password for " + getRequestingScheme());
            return (new PasswordAuthentication(kuser, kpass.toCharArray()));
        }
    }

    public ClusterAction() {
    }

    public Cluster createCluster(String publicIp, String publicPort, String privateIp, String privatePort, String homePath, String type) {

        String idCluster = UUID.randomUUID().toString();

        Cluster cluster = new Cluster(idCluster);

        cluster.setPublicIp(publicIp);
        cluster.setPublicPort(publicPort);
        cluster.setPrivateIp(privateIp);
        cluster.setPrivatePort(privatePort);
        cluster.setStatus("NOT_IN_USE");
        cluster.setHomePath(homePath);
        cluster.setType(type);

        ClusterDAO cdao = new ClusterDAO();

        if (!cdao.create(cluster)) {
            return null;
        }

        return cluster;
    }

    public Cluster startCluster(Cluster cluster) {

        ClusterDAO cdao = new ClusterDAO();

        cluster.setStatus("RUNNING");

        if (!cdao.modify(cluster)) {
            return null;
        }

        return cluster;

    }

    public Cluster stopCluster(Cluster cluster) {

        ClusterDAO cdao = new ClusterDAO();

        cluster.setStatus("STOPPED");

        if (!cdao.modify(cluster)) {
            return null;
        }

        return cluster;
    }

    List<Cluster> listClusters() {

        ClusterDAO cdao = new ClusterDAO();

        List<Cluster> clist = cdao.fetchClusters();

        return clist;
    }

    public boolean changeClusterSetting() {

        Apache apache = new Apache();

        apache.modifyAppConfig();

        apache.modifyWorkersProperties();

        return apache.reload();

    }

    public boolean stopAllInstances(String containerid) {

        boolean isStop = true;

        ServerDAO sdao = new ServerDAO();

        List<ServerHasContainer> shclist = sdao.fetchServersContainerRelationByContainerId(containerid);

        for (ServerHasContainer s : shclist) {

            if (!stopInstance(s)) {
                isStop = false;
                break;
            }

        }

        return isStop;

    }

    private boolean stopInstance(ServerHasContainer s) {

        Container container = s.getContainer();
        Server server = s.getServer();

        Authenticator.setDefault(new MyAuthenticator());
        String url = "http://" + server.getIp() + ":" + server.getPort() + "/manager/stop";
        //      String url = "http://146.169.35.36:8080/manager/stop";

        String parameter = "path=/" + container.getAppName();
        url = url + "?" + parameter;

        System.out.println(url);

        boolean isValid = false;

        URL yahoo = null;
        try {
            yahoo = new URL(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);

            return isValid;
        }

        URLConnection yc = null;
        try {
            yc = yahoo.openConnection();
        } catch (IOException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);
            return isValid;
        }

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);
            return isValid;
        }

        try {

            String inputLine = in.readLine();

            if (inputLine.substring(0, 2).equals("OK")) {
                isValid = true;

            }


        } catch (IOException ex) {
            Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);

            return isValid;

        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(APPAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return isValid;

    }

    public static void main(String[] args) {
        ClusterAction ca = new ClusterAction();

        ca.undeployAllInstances("61e87356-a151-491c-9a0f-f887a810d28d");
    }
}
