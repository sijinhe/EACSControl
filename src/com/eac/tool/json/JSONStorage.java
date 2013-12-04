/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eac.tool.json;

import com.eac.db.entity.Storage;
import net.sf.json.JSONObject;

/**
 *
 * @author Sijin
 */
public class JSONStorage {

    public JSONStorage(){

    }

    public String createJSON(Storage store, boolean b) {

        String json = "";

   //     JSONArray returnArray = new JSONArray();

        JSONObject jobj = new JSONObject();

        jobj.put("result", b);

        if (b == true) {
            jobj.put("storage", parseStorage(store));

        } else {
            jobj.put("storage", "");
        }

    //    returnArray.add(jobj);


        json = jobj.toString();// returnArray.toString();


        return json;
    }

    public String parseStorage(Storage store) {

        String json = "";

      //  JSONArray returnArray = new JSONArray();

        JSONObject jobj = new JSONObject();

        jobj.put("storageid", store.getIdStorage());
        jobj.put("publicip", store.getPublicIp());
        jobj.put("publicport", store.getPublicPort());
        jobj.put("privateip", store.getPrivateIp());
        jobj.put("privateport", store.getPrivatePort());
        jobj.put("status", store.getStatus());
        jobj.put("type", store.getType());


    //    returnArray.add(jobj);

        json = jobj.toString();//returnArray.toString();

        return json;
    }



    public String createSimpleJSON(boolean b) {
        String json = "";

        JSONObject jobj = new JSONObject();

        jobj.put("result", b);

        json = jobj.toString();

        return json;
    }
}
