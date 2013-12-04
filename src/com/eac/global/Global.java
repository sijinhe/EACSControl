/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eac.global;

import com.eac.db.dao.ServerDAO;

/**
 *
 * @author Sijin
 */
public class Global {

    public static final String TOMCAT_DIR = "/tomcat/";
    public static final long INTERVAL = 30;
    public static int maxInstance(){

        int max = 0;

        ServerDAO sdao = new ServerDAO();

        max = sdao.fetchServers().size();

        return max;
    }

    public static int maxConnection(){

        int max = 40;

        return max;

    }



}
