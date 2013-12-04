/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.tool.json;

import com.eac.db.entity.Container;
import com.eac.global.Global;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *
 * @author Sijin
 */
public class JSONContainer {

    public JSONContainer() {
    }

    public String createJSONList(List<Container> clist) {
        String json = "";

        JSONArray returnArray = new JSONArray();

        for (Container a : clist) {

            JSONObject jobj = new JSONObject();

            jobj = createJSONContainer(a);

            returnArray.add(jobj);
        }


        json = returnArray.toString();

        return json;

    }

    private JSONObject createJSONContainer(Container container) {

        JSONObject jobj = new JSONObject();

        jobj.put("containerid", container.getIdContainer());
        jobj.put("containername", container.getContainerName());
        jobj.put("login", container.getLogin());
        jobj.put("appstatus", container.getAppStatus());
        jobj.put("inst", container.getInstanceNo());
        jobj.put("maxcon", container.getMaxConnection());
        jobj.put("maxinst", Global.maxInstance());
        jobj.put("maxallowcons", Global.maxConnection());
        
        if (!container.getAppStatus().equalsIgnoreCase("NOT_IN_USE")) {
            jobj.put("appname", container.getAppName());
            jobj.put("hitrate", container.getHitRate());
            jobj.put("upload", container.getUpload());
            jobj.put("clusterip", container.getCluster().getPublicIp());

            

        }

        jobj.put("dbstatus", container.getDbStatus());


        if (!container.getDbStatus().equalsIgnoreCase("NOT_IN_USE")) {
            jobj.put("dbaccesspoint", container.getStorage().getPublicIp());
            jobj.put("dbport", container.getStorage().getPublicPort());

            jobj.put("privatedbaccesspoint", container.getStorage().getPrivateIp());
            jobj.put("privatedbport", container.getStorage().getPrivatePort());
            jobj.put("dbusername", container.getDbPassword());
            jobj.put("dbpassword", container.getDbPassword());

            jobj.put("dbname", container.getDbName());

            


        }


        return jobj;

    }

    public String createJSON(Container container, boolean b) {

        String json = "";

        JSONObject jobj = new JSONObject();

        jobj.put("result", b);


        if (b == true) {
            jobj.put("container", parseContainer(container));

        } else {
            jobj.put("container", "");
        }

        json = jobj.toString();


        return json;
    }

    public String createJSON(Container container, boolean b, String comment) {

        String json = "";

        //     JSONArray returnArray = new JSONArray();

        JSONObject jobj = new JSONObject();

        jobj.put("result", b);
        jobj.put("comment", comment);

        if (b == true) {
            jobj.put("container", parseContainer(container));

        } else {
            jobj.put("container", "");
        }

        //    returnArray.add(jobj);


        json = jobj.toString();// returnArray.toString();


        return json;
    }

    private String parseContainer(Container container) {

        String json = "";

        JSONObject jobj = new JSONObject();

        jobj = createJSONContainer(container);

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

    public String createSimpleJSON(boolean b, String string) {
        String json = "";

        JSONObject jobj = new JSONObject();

        jobj.put("result", b);
        jobj.put("comment", string);

        json = jobj.toString();

        return json;
    }
}
