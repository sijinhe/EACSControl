/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.container.scaling;

import com.eac.db.dao.ServerDAO;
import com.eac.db.entity.Container;
import com.eac.db.entity.History;
import com.eac.db.entity.Server;
import com.eac.db.entity.ServerHasContainer;
import com.eac.entity.operation.APPAction;
import com.eac.entity.operation.ClusterAction;
import com.eac.entity.operation.HistoryAction;
import com.eac.global.Global;
import com.eac.monitor.management.Monitor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sijin
 */
public class Scaling {

    Container container;

    public Scaling(Container s) {

        this.container = s;
    }

    public void trackApp() {

        HistoryAction ha = new HistoryAction();

        Calendar cal = Calendar.getInstance();

        long endDate = cal.getTimeInMillis();

        long period = 2 * 60 * 1000;

        long startDate = endDate - period;


        List<History> historyList = ha.fetchHistoryList(container.getIdContainer(), String.valueOf(startDate), String.valueOf(endDate), String.valueOf(period));

        int totalHit = 0;

        for (History history : historyList) {

            totalHit += history.getHits();

        }

        ServerDAO sdao = new ServerDAO();

        List<ServerHasContainer> shc = sdao.fetchServersContainerRelationByContainerId(container.getIdContainer());

        int counter = shc.size();//number of instances

        int averageHit = totalHit / counter;

        System.out.println("average hit " + averageHit);

        List<Server> serverlist = sdao.fetchServersByClusterId(container.getCluster().getIdCluster());

        if (averageHit > container.getInstanceNo() && counter < serverlist.size()) {
            //Scaling up by 1
            scaleUp();
        }

        if (averageHit <= container.getInstanceNo() && counter > 1) {
            //scalling down by 1
            scaleDown();
        }

    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    private void scaleUp() {

        try {

            String containerid = container.getIdContainer();
            APPAction aact = new APPAction();

            if (aact.notifyReDownload(containerid)) {

                Container tempcontainer = aact.getAPP(containerid);
                ClusterAction ca = new ClusterAction();

                if (ca.changeClusterSetting()) {

                    tempcontainer = aact.startAPP(tempcontainer);

                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(Scaling.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Scaling.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    private void scaleDown() {

        String containerid = container.getIdContainer();

        ClusterAction ca = new ClusterAction();

        ServerDAO sdao = new ServerDAO();

        List<ServerHasContainer> shclist = sdao.fetchServersContainerRelationByContainerId(containerid);

        Random random = new Random();

        ServerHasContainer serverRemoval = shclist.get(random.nextInt(shclist.size()));

        if (ca.undeployInstance(serverRemoval)) {

            sdao.deleteServerContainerRelation(serverRemoval.getServerHasContainerPK());

            ca.changeClusterSetting();

        }

    }

    public void appScaling() {


        ServerDAO sdao = new ServerDAO();

        List<ServerHasContainer> shc = sdao.fetchServersContainerRelationByContainerId(container.getIdContainer());

        int counter = shc.size();//number of instances

        writeDataToFile(container.getIdContainer());

        int detect = detectOutlier(container.getIdContainer());
        if (detect == 1) {


            if (counter < container.getInstanceNo()) {

                //    int temp = container.getInstanceNo() - counter;

                //    for (int i = 0; i < temp; i++) {
                scaleUp();
                System.out.println("scale up");
                //   }

            }
        }

        if (detect == -1) {


            if (counter > 1) {
                //     int temp = counter - container.getInstanceNo();

                //    for (int i = 0; i < temp; i++) {
                scaleDown();
                System.out.println("scale down");
                //    }
            }

        }
    }

    private int detectOutlier(String idContainer) {

        int detect = 0;


//           ArrayList<Double> responsetimelist = getData(idContainer, 5);
//
//           detect = dixon(responsetimelist, idContainer);

        ArrayList<Double> responsetimelist = getData(idContainer, 10);

        detect = grubb(responsetimelist, idContainer);

        return detect;

    }

    private int dixon(ArrayList<Double> responsetimelist, String idContainer) {
        int detect = 0;

        for (int i = 0; i < responsetimelist.size(); i++) {
            System.out.println(responsetimelist.get(i));
        }

        System.out.println("********real**");

        double last = responsetimelist.get(responsetimelist.size() - 1);

        Collections.sort(responsetimelist);

        if (responsetimelist.size() <= 1) {
            return detect;
        }

        Double qtable = 0.412;
        Double bigq = 0.0;

        if (last == responsetimelist.get(0)) {

            bigq = (responsetimelist.get(1) - responsetimelist.get(0)) / (responsetimelist.get(responsetimelist.size() - 1) - responsetimelist.get(0));

        } else if (last == responsetimelist.get(responsetimelist.size() - 1)) {

            bigq = (responsetimelist.get(responsetimelist.size() - 1) - responsetimelist.get(responsetimelist.size() - 2)) / (responsetimelist.get(responsetimelist.size() - 1) - responsetimelist.get(0));

        } else {
            for (int i = 1; i < responsetimelist.size() - 1; i++) {

                Double gap1 = responsetimelist.get(i) - responsetimelist.get(i - 1);
                Double gap2 = responsetimelist.get(i + 1) - responsetimelist.get(i);

                if (gap1 < gap2) {
                    bigq = gap1 / (responsetimelist.get(responsetimelist.size() - 1) - responsetimelist.get(0));
                } else {
                    bigq = gap2 / (responsetimelist.get(responsetimelist.size() - 1) - responsetimelist.get(0));
                }

            }
        }

        if (bigq > qtable) {

            System.out.println("outlier");
            System.out.println("last" + last);
            System.out.println("*****new*****");

            ArrayList<Double> expectedresponsetimelist = getData(idContainer, 100);

            if (getMean(expectedresponsetimelist) < last) {
                System.out.println("should scale up");
                detect = 1;
            } else {
                System.out.println("should scale down");
                detect = -1;
            }

            return detect;

        }


        return detect;
    }

    public static void main(String[] args) {

        ArrayList<Double> responsetimelist = new ArrayList<Double>();

        responsetimelist.add(11.0);
        responsetimelist.add(12.0);
        responsetimelist.add(13.0);
        responsetimelist.add(14.0);

        System.out.println(getMean(responsetimelist));
        System.out.println(Scaling.getSD(responsetimelist, getMean(responsetimelist)));

//        Collections.sort(responsetimelist);
//
//        for (int i = 0; i < responsetimelist.size(); i++) {
//
//            if (i == responsetimelist.size() - 1) {
//                break;
//            }
//            Double qtable = 0.412;
//
//            Double bigq = (responsetimelist.get(i + 1) - responsetimelist.get(i)) / (responsetimelist.get(responsetimelist.size() - 1) - responsetimelist.get(0));
//
//            if (bigq > qtable) {
//                System.out.println("outlier");
//            } else {
//                System.out.println("not outlier");
//            }
//
//        }

    }

    static private Double getMean(ArrayList<Double> responsetimelist) {
        Double mean = 0.0;
        Double acc = 0.0;

        for (int i = 0; i < responsetimelist.size(); i++) {
            acc += responsetimelist.get(i);
        }

        mean = acc / (double) responsetimelist.size();

        return mean;
    }

    private ArrayList<Double> getData(String idContainer, int numberData) {

        HistoryAction ha = new HistoryAction();

        List<History> historyList = null;

        long endDate = System.currentTimeMillis();
        long timeInterval = Global.INTERVAL * 1000;

        long startDate = endDate - timeInterval * numberData;

        historyList = ha.fetchHistoryList(idContainer, String.valueOf(startDate), String.valueOf(endDate), String.valueOf(timeInterval));

        ArrayList<Double> responsetimelist = new ArrayList<Double>();

        for (int i = 0; i < historyList.size(); i++) {

            History h = historyList.get(i);

            double mean = Monitor.getMeanDouble(h.getHits(), h.getDurationsSum());

            responsetimelist.add(mean);


        }

        return responsetimelist;
    }

    private void writeDataToFile(String idContainer) {
        HistoryAction ha = new HistoryAction();

        List<History> historyList = null;

        long endDate = System.currentTimeMillis();
        long timeInterval = 20 * 1000;

        long startDate = endDate - timeInterval * 1;

        historyList = ha.fetchHistoryList(idContainer, String.valueOf(startDate), String.valueOf(endDate), String.valueOf(timeInterval));

        History h = historyList.get(historyList.size() - 1);

        int hit = h.getHits();
        double responseTime = Monitor.getMeanDouble(h.getHits(), h.getDurationsSum());
        double CPU = Monitor.getMeanDouble(h.getHits(), h.getCpuTimeSum());
        int datasize = Monitor.getResponseSizeMean(h.getHits(), h.getResponseSizesSum()) / 1024;
        int memory = h.getUsedmemory();
        float error = Monitor.getSystemErrorPercentage(h.getHits(), h.getSystemErrors());

        ServerDAO sdao = new ServerDAO();

        List<ServerHasContainer> shc = sdao.fetchServersContainerRelationByContainerId(idContainer);

        int numberEAC = shc.size();

        boolean writeTitle = false;

        try {
            File f = new File("/config/appdata.csv");
            if (!f.exists()) {
                writeTitle = true;
            }

            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/config/appdata.csv", true)));

            if (writeTitle) {
                out.println("Time, ContainerId, Hit Rate, Response Time, CPU Response Time, Data Transfer, Used Memory, EAC Number, Error Rate");
            }

            out.println(endDate + ", " + idContainer + ", " + hit + ", " + responseTime + ", " + CPU + ", " + datasize + ", " + memory + ", " + numberEAC + ", " + error);

            out.close();

        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private int grubb(ArrayList<Double> responsetimelist, String idContainer) {
        int detect = 0;

        for (int i = 0; i < responsetimelist.size(); i++) {
            System.out.println(responsetimelist.get(i));
        }

        System.out.println("********real**");

        double last = responsetimelist.get(responsetimelist.size() - 1);

        Collections.sort(responsetimelist);

        if (responsetimelist.size() <= 1) {
            return detect;
        }

        Double gtable = 2.18;
        Double g = 0.0;

        Double mean = getMean(responsetimelist);

        Double sd = getSD(responsetimelist, mean);

        g = Math.abs(last - mean)/sd;
   
        if (g > gtable) {

            System.out.println("outlier");
            System.out.println("last" + last);
            System.out.println("*****new*****");

            ArrayList<Double> expectedresponsetimelist = getData(idContainer, 100);

            if (getMean(expectedresponsetimelist) < getMean(responsetimelist)) {
                System.out.println("should scale up");
                detect = 1;
            } else {
                System.out.println("should scale down");
                detect = -1;
            }

            return detect;

        }


        return detect;
    }

    static public Double getSD(ArrayList<Double> responsetimelist, Double mean) {
        double sum = 0;

        for (Double i : responsetimelist) {

            sum += Math.pow((i - mean), 2);
        }
        return Math.sqrt(sum / (responsetimelist.size() - 1)); // sample

    }
}
