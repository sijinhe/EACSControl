/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.entity.operation;

import com.eac.db.dao.ContainerDAO;
import com.eac.db.entity.Container;
import com.eac.tool.json.JSONContainer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 *
 * @author Sijin
 */
public class ContainerAction {

    public ContainerAction() {
    }

    public Container createContainer(String login, String containerName, int maxcon, int inst) {


        String idContainer = UUID.randomUUID().toString();

        Container container = new Container(idContainer);
        container.setContainerName(containerName);
        container.setLogin(login);

        container.setAppName("");
        container.setAppStatus("NOT_IN_USE");
        container.setUpload("FALSE");
        container.setHitRate(0);

        container.setDbName("");
        container.setDbStatus("NOT_IN_USE");
        container.setMaxConnection(maxcon);
        container.setInstanceNo(inst);
        container.setDbPassword("");

        java.util.Date date = new java.util.Date();
        container.setCreationDate(date);

        ContainerDAO cdao = new ContainerDAO();

        if (!cdao.create(container)) {
            return null;
        }

        return container;
    }

    public boolean checkContainerName(String containerName) {

        ContainerDAO cdao = new ContainerDAO();

        return cdao.checkContainerName(containerName);
    }

    public boolean checkContainerDeletable(Container container) {

        if (container.getAppStatus().equalsIgnoreCase("NOT_IN_USE") && container.getDbStatus().equalsIgnoreCase("NOT_IN_USE")) {
            return true;
        }

        return false;
    }

    public Container deleteContainer(Container container) {

        ContainerDAO cdao = new ContainerDAO();


        if (!cdao.delete(container)) {
            return null;
        }


        return container;

    }

    public String getLogging(String containerid, String clusterip, String date) {

        String json = "";

        JSONContainer jc = new JSONContainer();
        json = jc.createSimpleJSON(false, "Error");

        String url = "http://" + clusterip + ":8080/EACClusterControl/";
        String servlet = "GetLogging";
        String parameter1 = "containerid=" + containerid;
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

            inputLine = in.readLine();

          //  JSONObject jsonp = (JSONObject) JSONSerializer.toJSON(inputLine);

            json = inputLine;//jsonp.toString();

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

        

        return json;
    }

    private static class increasingContainer implements Comparator<Container> {

        public increasingContainer() {
        }

        public int compare(Container o1, Container o2) {
            return (o1.getCreationDate().getTime() < o2.getCreationDate().getTime() ? -1 : (o1.getCreationDate().getTime() == o2.getCreationDate().getTime() ? 0 : 1));
        }
    }

    public List<Container> listContainers(String login) {

        ContainerDAO cdao = new ContainerDAO();

        List<Container> clist = cdao.fetchEACsByLogin(login);

        Collections.sort(clist, new increasingContainer());

        return clist;

    }

    public static void main(String[] args) {

        ContainerAction aact = new ContainerAction();

        aact.listContainers("sijinsijin");


    }

    public boolean changeInstance(String containerid, String inst) {
        boolean isChange = false;
        int max = Integer.parseInt(inst);

        Container container = new Container(containerid);
        ContainerDAO cdao = new ContainerDAO();
        container = cdao.fetch(container);

        container.setInstanceNo(max);


        if (cdao.modify(container)) {


            isChange = true;



        }


        return isChange;
    }
}
