/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.monitor.management;

import com.eac.db.dao.HistoryDAO;
import com.eac.db.entity.Container;
import com.eac.db.entity.Server;
import com.eac.db.entity.ServerHasContainer;
import com.eac.entity.operation.HistoryAction;
import com.eac.tool.file.MonitorDataStorage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Sijin
 */
public class Monitor {

    Server server;
    Container container;
    ServerHasContainer s;

    public ServerHasContainer getS() {
        return s;
    }

    public void setS(ServerHasContainer s) {
        this.s = s;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Monitor(ServerHasContainer s) {
        this.s = s;
        this.server = s.getServer();
        this.container = s.getContainer();
    }

    public boolean monitorApp(String format) {

        String period = "tout";

        String url = "http://" + server.getIp() + ":" + server.getPort() + "/" + container.getAppName() + "/monitoring?" + "format=" + format + "&" + "period=" + period;
     //   String url = "http://146.169.35.36:" + server.getPort() + "/" + container.getAppName() + "/monitoring?" + "format=" + format + "&" + "period=" + period;

        URL yahoo = null;
        try {
            yahoo = new URL(url);
          //  System.out.println(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        URLConnection yc = null;
        try {
            yc = yahoo.openConnection();
        } catch (IOException ex) {
            Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        BufferedReader in = null;
        try {

            InputStream stream = yc.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));

        } catch (IOException ex) {
            Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = in.readLine()) != null) {
                sb.append(line + "\n");
            }
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);

        }

        String result = sb.toString();

        return analysis(result);
    }

    private boolean analysis(String inputLine) {

        JSONObject json = (JSONObject) JSONSerializer.toJSON(inputLine);

        JSONArray list = json.getJSONArray("list");

        JSONObject http = (JSONObject) list.get(0);

        JSONArray requests = http.getJSONArray("requests");

        List<AppHttpInfo> infoList = new ArrayList<AppHttpInfo>();

        for (int i = 0; i < requests.size(); i++) {

            String str = requests.get(i).toString();

            JSONArray array = (JSONArray) JSONSerializer.toJSON(str);

            JSONObject a = (JSONObject) array.get(1);

            AppHttpInfo info = new AppHttpInfo();//for each individual json


            info.setId(a.getString("id"));
            info.setName(a.getString("name"));
            info.setHits(a.getLong("hits"));
            info.setDurationsSum(a.getLong("durationsSum"));
            info.setDurationsSquareSum(a.getLong("durationsSquareSum"));
            info.setCpuTimeSum(a.getLong("cpuTimeSum"));
            info.setMaximum(a.getLong("maximum"));
            info.setResponseSizesSum(a.getLong("responseSizesSum"));
            info.setSystemErrors(a.getLong("systemErrors"));

            infoList.add(info);

        }

        MonitorDataStorage mds = null;

        AppHttpInfo old = new AppHttpInfo(container.getIdContainer(), server.getIdServer());

        mds = new MonitorDataStorage(old);

        if (mds.ifExist()) {
            //exist
            old = mds.read();

        } else {

            old.setCpuTimeSum(0);
            old.setDurationsSquareSum(0);
            old.setDurationsSum(0);
            old.setHits(0);
            old.setResponseSizesSum(0);
            old.setSystemErrors(0);
        }

        //////////below write to db
        long hits = hitsPerTime(infoList, old.getHits());

        long durationsSum = durationSumPerTime(infoList, old.getDurationsSum());

        long durationsSquareSum = durationsSquareSumPerTime(infoList, old.getDurationsSquareSum());

        long cpuTimeSum = cpuTimeSumPerTime(infoList, old.getCpuTimeSum());

        long responseSizesSum = responseSizesSumPerTime(infoList, old.getResponseSizesSum());

        long systemErrors = systemErrorsPerTime(infoList, old.getSystemErrors());

        JSONObject mem = (JSONObject) list.get(11);

        JSONObject memoryInfo = mem.getJSONObject("memoryInformations");

        long usedMemory = memoryInfo.getLong("usedMemory")/(1024*1024);

        long maxMemory = memoryInfo.getLong("maxMemory")/(1024*1024);

        //above write to db
        HistoryAction hact = new HistoryAction();

        hact.createHistory((int)cpuTimeSum, (int) durationsSquareSum, (int) durationsSum, (int) hits, (int) responseSizesSum, s, (int) systemErrors, (int) usedMemory, (int) maxMemory);

       // System.out.println(hits);

        AppHttpInfo newinfo = new AppHttpInfo(container.getIdContainer(), server.getIdServer());

        newinfo.setCpuTimeSum(cpuTimeSum + old.getCpuTimeSum());
        newinfo.setDurationsSquareSum(durationsSquareSum + old.getDurationsSquareSum());
        newinfo.setDurationsSum(durationsSum + old.getDurationsSum());
        newinfo.setHits(hits + old.getHits());
        newinfo.setResponseSizesSum(responseSizesSum + old.getResponseSizesSum());
        newinfo.setSystemErrors(systemErrors + old.getSystemErrors());
        newinfo.setUsedMemory(usedMemory);
        newinfo.setMaxMemory(maxMemory);

        mds = new MonitorDataStorage(newinfo);
        mds.write();
        
        return true;
    }

    private long hitsPerTime(List<AppHttpInfo> infoList, long old) {

        long lastTotalHits = old;
        long hits = 0;
        long acc = 0;

        for (int i = 0; i < infoList.size(); i++) {
            acc += infoList.get(i).getHits();
        }

        hits = acc - lastTotalHits;

        return hits;
    }

    private long durationSumPerTime(List<AppHttpInfo> infoList, long old) {

        long lastTotalDurationSum = old;
        long durationSum = 0;
        long acc = 0;

        for (int i = 0; i < infoList.size(); i++) {
            acc += infoList.get(i).getDurationsSum();
        }

        durationSum = acc - lastTotalDurationSum;

        return durationSum;

    }

    static public long getMean(long hits, long durationsSum) {
        if (hits > 0) {
            return (int) (durationsSum / hits);
        }
        return 0;
    }

    static public Double getMeanDouble(long hits, long durationsSum) {
        if (hits > 0) {
           
            return (Double) (durationsSum /(double) hits);
        }
        return 0.0;
    }

    static public int getStandardDeviation(long hits, long durationsSquareSum, long durationsSum) {

        if (hits > 0) {
            return (int) Math.sqrt((durationsSquareSum - (double) durationsSum * durationsSum
                    / hits)
                    / (hits - 1));
        }
        return 0;
    }

    static public int getCpuTimeMean(long hits, long cpuTimeSum) {
        if (hits > 0) {
            return (int) (cpuTimeSum / hits);
        }
        return 0;
    }

    static public float getSystemErrorPercentage(long hits, long systemErrors) {

        if (hits > 0) {
            return Math.min(100f * systemErrors / hits, 100f);
        }
        return 0;
    }

    static public int getResponseSizeMean(long hits, long responseSizesSum) {
        if (hits > 0) {
            return (int) (responseSizesSum / hits);
        }
        return 0;
    }

    private long durationsSquareSumPerTime(List<AppHttpInfo> infoList, long old) {
        long lastTotalDurationSum = old;
        long durationSum = 0;
        long acc = 0;

        for (int i = 0; i < infoList.size(); i++) {
            acc += infoList.get(i).getDurationsSquareSum();
        }

        durationSum = acc - lastTotalDurationSum;

        return durationSum;
    }

    private long cpuTimeSumPerTime(List<AppHttpInfo> infoList, long old) {
        long lastTotalDurationSum = old;
        long durationSum = 0;
        long acc = 0;

        for (int i = 0; i < infoList.size(); i++) {
            acc += infoList.get(i).getCpuTimeSum();
        }

        durationSum = acc - lastTotalDurationSum;

        return durationSum;
    }

    private long responseSizesSumPerTime(List<AppHttpInfo> infoList, long old) {
        long lastTotalDurationSum = old;
        long durationSum = 0;
        long acc = 0;

        for (int i = 0; i < infoList.size(); i++) {
            acc += infoList.get(i).getResponseSizesSum();
        }

        durationSum = acc - lastTotalDurationSum;

        return durationSum;
    }

    private long systemErrorsPerTime(List<AppHttpInfo> infoList, long old) {
        long lastTotalDurationSum = old;
        long durationSum = 0;
        long acc = 0;

        for (int i = 0; i < infoList.size(); i++) {
            acc += infoList.get(i).getSystemErrors();
        }

        durationSum = acc - lastTotalDurationSum;

        return durationSum;
    }
}
