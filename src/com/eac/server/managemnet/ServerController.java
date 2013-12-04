/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.server.managemnet;

import com.eac.server.managemnet.ServerInfo;
import com.eac.db.dao.ServerDAO;
import com.eac.db.entity.Server;
import java.util.UUID;

/**
 *
 * @author Sijin
 */
public class ServerController {

    ServerInfo info = new ServerInfo();
    String serverid;
    VMControlAPI api = new ICCloudAPI();

    public ServerController(String id) {
        serverid = id;
    }

    public void create(String port, String type) {


        Server server = new Server(serverid);

        String ip = info.getIp();

        server.setIp(ip);
        server.setPort(port);
        server.setStatus("RUNNING");
        server.setType(type);

        ServerDAO sdao = new ServerDAO();

        if (sdao.create(server)) {
            info.writeConfig(server);
        }

    }

    public void start() {

        Server server = new Server(serverid);

        ServerDAO sdao = new ServerDAO();

        server = sdao.fetch(server);

        server.setStatus("RUNNING");

        sdao.modify(server);

        api.start(server);

    }

    public void stop() {

        Server server = new Server(serverid);

        ServerDAO sdao = new ServerDAO();

        server = sdao.fetch(server);

        server.setStatus("STOPPED");

        sdao.modify(server);

        api.stop(server);

    }

    public void delete() {

        Server server = new Server(serverid);

        ServerDAO sdao = new ServerDAO();

        server = sdao.fetch(server);

        if (sdao.delete(server)) {
            info.deleteFile(ServerInfo.propertyPath);
        }

    }

    public void restart() {
        try {

            Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "service tomcat restart"});

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
