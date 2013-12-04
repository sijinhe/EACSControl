/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.tool.json;

import com.eac.db.entity.Cluster;
import net.sf.json.JSONObject;

/**
 *
 * @author Sijin
 */
public class JSONCluster {

    public JSONCluster() {
    }

    public String createJSON(Cluster cluster, boolean b, String comment) {

        String json = "";

        JSONObject jobj = new JSONObject();

        jobj.put("result", b);
        jobj.put("comment", comment);

        if (b == true) {
            jobj.put("cluster", parseCluster(cluster));

        } else {
            jobj.put("cluster", "");
        }


        json = jobj.toString();


        return json;
    }

    public String createJSON(Cluster cluster, boolean b) {

        String json = "";

        JSONObject jobj = new JSONObject();

        jobj.put("result", b);

        if (b == true) {
            jobj.put("cluster", parseCluster(cluster));

        } else {
            jobj.put("cluster", "");
        }

        json = jobj.toString();


        return json;
    }

    public String parseCluster(Cluster cluster) {

        String json = "";

        JSONObject jobj = new JSONObject();

        jobj.put("clusterid", cluster.getIdCluster());
        jobj.put("publicip", cluster.getPublicIp());
        jobj.put("publicport", cluster.getPublicPort());
        jobj.put("privateip", cluster.getPrivateIp());
        jobj.put("privateport", cluster.getPrivatePort());
        jobj.put("status", cluster.getStatus());
        jobj.put("homepath", cluster.getHomePath());
        jobj.put("type", cluster.getType());

        json = jobj.toString();

        return json;
    }

    public String createSimpleJSON(boolean b) {
        String json = "";

        JSONObject jobj = new JSONObject();

        jobj.put("result", b);

        json = jobj.toString();

        return json;
    }

    public String createLoggingJSON(String array, boolean b) {
        String json = "";

        JSONObject jobj = new JSONObject();

        jobj.put("result", b);

        if (b == true) {
            jobj.put("log", array);

        } else {
            jobj.put("log", "");
        }

        json = jobj.toString();


        return json;
    }
}
