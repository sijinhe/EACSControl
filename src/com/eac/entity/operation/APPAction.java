/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.entity.operation;

import com.eac.cluster.management.NodeManager;
import com.eac.container.algorithm.cluster.ClusterAlgorithm;
import com.eac.container.algorithm.cluster.FirstOneAlgorithm;
import com.eac.db.dao.ClusterDAO;
import com.eac.db.dao.ContainerDAO;
import com.eac.db.dao.ServerDAO;
import com.eac.db.entity.Cluster;
import com.eac.db.entity.Container;
import com.eac.db.entity.Server;
import com.eac.db.entity.ServerHasContainer;
import com.eac.server.managemnet.FileAction;
import com.eac.server.managemnet.ServerInfo;
import com.eac.tool.file.Logging;
import com.eac.tool.file.WebXML;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 *
 * @author Sijin
 */
public class APPAction {

    public APPAction() {
    }

    public Container getAPP(String containerid) {

        Container container = new Container(containerid);

        ContainerDAO cdao = new ContainerDAO();

        container = cdao.fetch(container);

        return container;

    }

    public Container enableAPP(Container container) {

        ContainerDAO cdao = new ContainerDAO();

        container.setAppName(container.getContainerName());
        container.setAppStatus("VACANT");

        Cluster cluster = chooseCluster();

        if (cluster == null) {
            return null;
        }

        container.setCluster(cluster);

        if (!cdao.modify(container)) {
            return null;
        }

        return container;
    }

    public Container disableAPP(Container container) {

        ContainerDAO cdao = new ContainerDAO();

        container.setAppName("");
        container.setAppStatus("NOT_IN_USE");

        container.setCluster(null);

        if (!cdao.modify(container)) {
            return null;
        }

        return container;
    }

    public Container startAPP(Container container) {

        ContainerDAO cdao = new ContainerDAO();

        container.setAppStatus("RUNNING");

        if (!cdao.modify(container)) {
            return null;
        }

        return container;
    }

    public Container stopAPP(Container container) {

        ContainerDAO cdao = new ContainerDAO();

        container.setAppStatus("STOPPED");

        if (!cdao.modify(container)) {
            return null;
        }

        return container;
    }

    public Container undelpoyAPP(Container container) {

        ContainerDAO cdao = new ContainerDAO();

        container.setAppStatus("VACANT");

        if (!cdao.modify(container)) {
            return null;
        }

        return container;
    }

    private Cluster chooseCluster() {

        ClusterAction cact = new ClusterAction();

        List<Cluster> clusterlist = cact.listClusters();

        ClusterAlgorithm ca = new FirstOneAlgorithm();

        Cluster cluster = ca.run(clusterlist);

        return cluster;
    }

    public Boolean uploadAPP(String containerid) {

        Container container = new Container(containerid);

        ContainerDAO cdao = new ContainerDAO();

        container = cdao.fetch(container);

        container.setUpload("TRUE");

        if (!cdao.modify(container)) {

            return false;
        }

        return true;

    }

    public boolean notifyDownload(String containerid) throws MalformedURLException, IOException {
        NodeManager nm = new NodeManager();

        List<Server> serverList = nm.initialPlacment(containerid);

        return notifyNodes(serverList, containerid);
    }

    public boolean notifyReDownload(String containerid) throws MalformedURLException, IOException {
        NodeManager nm = new NodeManager();

        List<Server> serverList = nm.placment(containerid);

        return notifyNodes(serverList, containerid);
    }

    private boolean notifyNodes(List<Server> serverList, String id) throws MalformedURLException, IOException {

        for (Server s : serverList) {

            if (!callbackRequest(s, id)) {
                return false;
            }
        }

        return true;

    }

    private boolean callbackRequest(Server s, String id) {

        Container container = new Container(id);

        ContainerDAO cdao = new ContainerDAO();

        container = cdao.fetch(container);

        Cluster cluster = new Cluster(container.getCluster().getIdCluster());

        ClusterDAO clusterdao = new ClusterDAO();

        cluster = clusterdao.fetch(cluster);

        String url = "http://" + s.getIp() + ":" + s.getPort() + "/EACNodeController/";
        // String url = "http://" + s.getIp() + ":" + s.getPort() + "/EACNodeController/";
        String servlet = "DownloadAPP";
        String callbackURL = "http://" + cluster.getPrivateIp() + "/appbase/" + id + "/" + container.getAppName() + ".war";
        String parameter = "callback=" + callbackURL;
        String homePath = "homePath=" + s.getHomePath();
        String appname = "appname=" + container.getAppName();
        url = url + servlet + "?" + parameter + "&" + homePath + "&" + appname;

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

            if (inputLine.equalsIgnoreCase("true")) {

                isValid = true;

                ServerDAO sdao = new ServerDAO();
                sdao.createServerContainerRelation(s.getIdServer(), id);
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


        Container container = new Container("61e87356-a151-491c-9a0f-f887a810d28d");
        ContainerDAO cdao = new ContainerDAO();
        container = cdao.fetch(container);

        APPAction aact = new APPAction();
        System.out.println(aact.notifyUndeploy(container));

    }

    public boolean notifyUndeploy(Container container) {

        Cluster cluster = new Cluster(container.getCluster().getIdCluster());

        ClusterDAO clusterdao = new ClusterDAO();

        cluster = clusterdao.fetch(cluster);

        String url = "http://" + cluster.getPublicIp() + ":8080/EACClusterControl/";
        // String url = "http://" + s.getIp() + ":" + s.getPort() + "/EACNodeController/";
        String servlet = "UndeployNotifier";
        String parameter = "containerid=" + container.getIdContainer();

        url = url + servlet + "?" + parameter;

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

            String inputLine = "";

            inputLine = in.readLine();

            JSONObject json = (JSONObject) JSONSerializer.toJSON(inputLine);

            isValid = json.getBoolean("result");

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

    public boolean notifyStop(Container container) {
        Cluster cluster = new Cluster(container.getCluster().getIdCluster());

        ClusterDAO clusterdao = new ClusterDAO();

        cluster = clusterdao.fetch(cluster);

        String url = "http://" + cluster.getPublicIp() + ":8080/EACClusterControl/";
        // String url = "http://" + s.getIp() + ":" + s.getPort() + "/EACNodeController/";
        String servlet = "StopNotifier";
        String parameter = "containerid=" + container.getIdContainer();

        url = url + servlet + "?" + parameter;

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

            String inputLine = "";

            inputLine = in.readLine();

            JSONObject json = (JSONObject) JSONSerializer.toJSON(inputLine);

            isValid = json.getBoolean("result");

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

    public boolean notifyReload(Container container) {
        Cluster cluster = new Cluster(container.getCluster().getIdCluster());

        ClusterDAO clusterdao = new ClusterDAO();

        cluster = clusterdao.fetch(cluster);

        String url = "http://" + cluster.getPublicIp() + ":8080/EACClusterControl/";
        // String url = "http://" + s.getIp() + ":" + s.getPort() + "/EACNodeController/";
        String servlet = "ReloadNotifier";
        String parameter = "containerid=" + container.getIdContainer();

        url = url + servlet + "?" + parameter;

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

            String inputLine = "";

            inputLine = in.readLine();

            JSONObject json = (JSONObject) JSONSerializer.toJSON(inputLine);

            isValid = json.getBoolean("result");

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

    public boolean notifyStart(Container container) {
        Cluster cluster = new Cluster(container.getCluster().getIdCluster());

        ClusterDAO clusterdao = new ClusterDAO();

        cluster = clusterdao.fetch(cluster);

        String url = "http://" + cluster.getPublicIp() + ":8080/EACClusterControl/";
        // String url = "http://" + s.getIp() + ":" + s.getPort() + "/EACNodeController/";
        String servlet = "StartNotifier";
        String parameter = "containerid=" + container.getIdContainer();

        url = url + servlet + "?" + parameter;

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

            String inputLine = "";

            inputLine = in.readLine();

            JSONObject json = (JSONObject) JSONSerializer.toJSON(inputLine);

            isValid = json.getBoolean("result");

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

    public boolean modifyWAR(String containerid) {
        boolean isValid = false;

        ServerInfo info = new ServerInfo();

        Cluster cluster = new Cluster(info.getClusterId());

        ClusterDAO cdao = new ClusterDAO();

        cluster = cdao.fetch(cluster);

        String appname = getAPP(containerid).getAppName();

        FileAction fa = new FileAction();

        String path = cluster.getHomePath() + "appbase/" + containerid + "/";

        if (fa.unPackWar(appname, path)) {
            //   System.out.println(path);
            if (fa.deleteWar(appname, path)) {
                String configPath = "/config/file/";

                File folderExisting = new File(path + "WEB-INF/lib/");

                if (!folderExisting.exists()) {
                    folderExisting.mkdir();
                }

                folderExisting = new File(path + "WEB-INF/classes/");

                if (!folderExisting.exists()) {
                    folderExisting.mkdir();
                }

                Logging log = new Logging();

                String target = "/tomcat/logs/app/" + appname + "/";

                log.write(appname, target, path + "WEB-INF/classes/");

                fa.deleteRelevantFile("xstream", path + "WEB-INF/lib/");
                fa.deleteRelevantFile("jrobin", path + "WEB-INF/lib/");
                fa.deleteRelevantFile("javamelody", path + "WEB-INF/lib/");

                fa.copy(configPath + "xstream-1.3.1.jar", path + "WEB-INF/lib/xstream-1.3.1.jar");

                fa.copy(configPath + "jrobin-1.5.9.1.jar", path + "WEB-INF/lib/jrobin-1.5.9.1.jar");

                fa.copy(configPath + "javamelody.jar", path + "WEB-INF/lib/javamelody.jar");

                WebXML wx = new WebXML();

                wx.addJavamelodyListener(path + "WEB-INF/web.xml");

                if (fa.packWar(appname, path)) {
                    isValid = true;
                }

            }

        }

        return isValid;
    }
}
