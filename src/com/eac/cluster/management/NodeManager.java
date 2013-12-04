/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.cluster.management;

import com.eac.container.scaling.Scaling;
import com.eac.db.dao.ClusterDAO;
import com.eac.db.dao.ContainerDAO;
import com.eac.db.dao.ServerDAO;
import com.eac.db.entity.Cluster;
import com.eac.db.entity.Container;
import com.eac.db.entity.Server;
import com.eac.db.entity.ServerHasContainer;
import com.eac.monitor.management.Monitor;
import com.eac.server.managemnet.ServerInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Sijin
 */
public class NodeManager {

    public NodeManager() {
    }

    public List<Server> placment(String containerid) {

        Container container = new Container(containerid);

        ContainerDAO cdao = new ContainerDAO();

        container = cdao.fetch(container);

        ServerDAO sdao = new ServerDAO();

        List<Server> serverList = sdao.fetchServersByClusterId(container.getCluster().getIdCluster());

        List<ServerHasContainer> shc = sdao.fetchServersContainerRelationByContainerId(containerid);

        List<Server> templist = new ArrayList<Server>();

        for (Server server : serverList) {

            for (int i = 0; i < shc.size(); i++) {

                if (shc.get(i).getServer().getIdServer().equalsIgnoreCase(server.getIdServer())) {
                    break;
                }

                if (i == (shc.size() - 1)) {
                    templist.add(server);
                }

            }

        }


        Random random = new Random();

        List<Server> newList = new ArrayList<Server>();

        newList.add(templist.get(random.nextInt(templist.size())));

        return newList;

    }

    public List<Server> initialPlacment(String containerid) {

        Container container = new Container(containerid);

        ContainerDAO cdao = new ContainerDAO();

        container = cdao.fetch(container);

        ServerDAO sdao = new ServerDAO();

        List<Server> serverList = sdao.fetchServersByClusterId(container.getCluster().getIdCluster());

        Random random = new Random();

        List<Server> newList = new ArrayList<Server>();

        newList.add(serverList.get(random.nextInt(serverList.size())));

        return newList;

    }

    public void monitor() {

        ServerInfo info = new ServerInfo();

        Cluster cluster = new Cluster(info.getClusterId());
        //    Cluster cluster = new Cluster("fba184b1-1693-4990-9640-825158be40ad");

        ServerDAO sdao = new ServerDAO();

        List<ServerHasContainer> shc = sdao.fetchServersContainerRelation();

        for (ServerHasContainer s : shc) {

            //  Server server = s.getServer();
            Container container = s.getContainer();

            if (container.getCluster().getIdCluster().equalsIgnoreCase(cluster.getIdCluster())) {
                Monitor mon = new Monitor(s);
                mon.monitorApp("json");

                //   mon.monitorApp("146.169.35.36", "8080", container.getAppName(), container.getIdContainer(), "json");
            }

        }

    }

    public static void main(String[] args) {
        NodeManager nm = new NodeManager();
        nm.monitor();
    }

    public void scale() {
        ServerInfo info = new ServerInfo();

        Cluster cluster = new Cluster(info.getClusterId());


        ContainerDAO cdao = new ContainerDAO();

        List<Container> containerList = cdao.fetchContainers();


        for (Container container : containerList) {

            if (container.getAppStatus().equalsIgnoreCase("RUNNING")) {

                if (container.getCluster().getIdCluster().equalsIgnoreCase(cluster.getIdCluster())) {
                    Scaling mon = new Scaling(container);
                    mon.appScaling();
                    


                }
            }


        }
    }
}
