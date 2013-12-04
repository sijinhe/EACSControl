/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.entity.operation;

import com.eac.db.dao.HistoryDAO;
import com.eac.db.entity.History;
import com.eac.db.entity.ServerHasContainer;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Sijin
 */
public class HistoryAction {

    private static class decreasingHistory implements Comparator<History> {

        public decreasingHistory() {
        }

        public int compare(History o1, History o2) {
            return (o1.getTimestamp().getTime() > o2.getTimestamp().getTime() ? -1 : (o1.getTimestamp().getTime() == o2.getTimestamp().getTime() ? 0 : 1));
        }
    }

    private static class increasingHistory implements Comparator<History> {

        public increasingHistory() {
        }

        public int compare(History o1, History o2) {
            return (o1.getTimestamp().getTime() < o2.getTimestamp().getTime() ? -1 : (o1.getTimestamp().getTime() == o2.getTimestamp().getTime() ? 0 : 1));
        }
    }

    public HistoryAction() {
    }

    public History createHistory(int cpuTimeSum, int durationSquareSume,
            int durationSum, int hits, int responseSizeSum, ServerHasContainer shc, int systemErrors, int usedmem, int maxmem) {


        String idHistory = UUID.randomUUID().toString();

        History history = new History(idHistory);

        history.setCpuTimeSum(cpuTimeSum);
        history.setDurationsSquareSum(durationSquareSume);
        history.setDurationsSum(durationSum);
        history.setHits(hits);
        history.setResponseSizesSum(responseSizeSum);
        history.setIdContainer(shc.getContainer().getIdContainer());
        history.setIdServer(shc.getServer().getIdServer());
        history.setSystemErrors(systemErrors);
        history.setUsedmemory(usedmem);
        history.setMaxmemory(maxmem);

        Timestamp currentTimestamp = new Timestamp(Calendar.getInstance().getTime().getTime());

        history.setTimestamp(currentTimestamp);

        HistoryDAO cdao = new HistoryDAO();

        if (!cdao.create(history)) {
            return null;
        }

        return history;
    }

    public List<History> fetchHistoryList(String containerid, String startDate, String endDate, String timeInterval) {

        List<History> historylist = new ArrayList<History>();

        long start = Long.parseLong(startDate);
        long end = Long.parseLong(endDate);
        long interval = Long.parseLong(timeInterval);

        HistoryDAO hdao = new HistoryDAO();

        historylist = hdao.getHistoryList(containerid);

   

        ArrayList<History> templist = new ArrayList<History>();

        for (History h : historylist) {

            long historytime = h.getTimestamp().getTime();

   

            if (historytime >= start && historytime <= end) {
                templist.add(h);

            }

        }


//        Collections.sort(templist, new decreasingHistory());// from big to small

        List<History> newlist = new ArrayList<History>();


        for (long tempend = end; tempend > start; tempend = tempend - interval) {

            History temp = new History();
            Date d = new Date(tempend);
            temp.setTimestamp(d);
            temp.setCpuTimeSum(0);
            temp.setDurationsSquareSum(0);
            temp.setDurationsSum(0);
            temp.setHits(0);
            temp.setResponseSizesSum(0);
            temp.setSystemErrors(0);
            temp.setUsedmemory(0);

            int counter = 0;
            long usedmen = 0;
            for (int i = 0; i < templist.size(); i++) {


                History h = templist.get(i);
                long time = h.getTimestamp().getTime();

//                System.out.println(templist.size());
//                System.out.println(i);
              //  System.out.println(time);
                
                if ((time >= (tempend - interval)) && time <= tempend) {
                    temp.setCpuTimeSum(temp.getCpuTimeSum() + h.getCpuTimeSum());
                    temp.setDurationsSquareSum(temp.getDurationsSquareSum() + h.getDurationsSquareSum());
                    temp.setDurationsSum(temp.getDurationsSum() + h.getDurationsSum());
                    temp.setHits(temp.getHits() + h.getHits());
                    temp.setResponseSizesSum(temp.getResponseSizesSum() + h.getResponseSizesSum());
                    temp.setSystemErrors(temp.getSystemErrors() + h.getSystemErrors());
                    usedmen += h.getUsedmemory();
                //    System.out.println(usedmen);

//                    temp.setUsedmemory(temp.getUsedmemory() + h.getUsedmemory());
                    
                    counter ++;
                }

            }

            if(counter!=0){
                temp.setUsedmemory((int) usedmen/counter);
            }

            System.out.println(temp.getUsedmemory());

            //temp.setUsedmemory(temp.getUsedmemory() / templist.size());

            newlist.add(temp);
          //  System.out.println("end1");


        }

        Collections.sort(newlist, new increasingHistory());

   //     System.out.println("end2kkkkkkkkkkkkkkkk");


        return newlist;

    }

    public static void main(String[] args) {

        HistoryAction ha = new HistoryAction();

        List<History> list = ha.fetchHistoryList("61e87356-a151-491c-9a0f-f887a810d28d", "1334329660000", "1334330380000", "60000"); //case 1 wihin range
        //case 2  end outside
        //case 3 start outside
        // both outside

        for (History h : list) {
            System.out.println(h.getHits());
        }

        System.out.println(list.size());
    }
}
