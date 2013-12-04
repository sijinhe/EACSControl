/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.tool.json;

import com.eac.db.entity.History;
import com.eac.monitor.management.Monitor;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *
 * @author Sijin
 */
public class JSONHistory {

    public JSONHistory() {
    }

    public String createJSON(List<History> historylist, boolean b, String comment) {
        String json = "";

        //     JSONArray returnArray = new JSONArray();

        JSONObject jobj = new JSONObject();

        jobj.put("result", b);
        jobj.put("comment", comment);

        if (b == true) {
            jobj.put("usages", parseHistory(historylist));

        } else {
            jobj.put("usages", "");
        }

        //    returnArray.add(jobj);


        json = jobj.toString();// returnArray.toString();



        return json;
    }

    private String parseHistory(List<History> historylist) {

        String json = "";

        JSONArray returnArray = new JSONArray();

        for (History h : historylist) {

            JSONObject jobj = new JSONObject();

            jobj = createJSONHistory(h);

            returnArray.add(jobj);

        }

        json = returnArray.toString();

        return json;


    }

    private JSONObject createJSONHistory(History h) {
        
        JSONObject jobj = new JSONObject();

        jobj.put("hitrate", h.getHits());
        jobj.put("responsetime", Monitor.getMean(h.getHits(),h.getDurationsSum()));
        jobj.put("sdreponsetime", Monitor.getStandardDeviation(h.getHits(), h.getDurationsSquareSum(), h.getDurationsSum()));
        jobj.put("cpuresponsetime", Monitor.getCpuTimeMean(h.getHits(), h.getCpuTimeSum()));
        jobj.put("errorrate", Monitor.getSystemErrorPercentage(h.getHits(), h.getSystemErrors()));
        jobj.put("datasize", Monitor.getResponseSizeMean(h.getHits(), h.getResponseSizesSum())/1024);
        jobj.put("usedmemory", h.getUsedmemory());
 

        return jobj;
    }
}
