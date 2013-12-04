/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.cluster.management;

import com.eac.tool.file.AppConf;
import com.eac.tool.file.WorkersProperties;

/**
 *
 * @author Sijin
 */
public class Apache {

    public static final String CONFIGPATH = "/config/"; //running on linux

    public Apache() {
    }

    public void modifyAppConfig() {

        AppConf app = new AppConf();

        app.modify(Apache.CONFIGPATH);

    }

    public void modifyWorkersProperties() {

        WorkersProperties wp = new WorkersProperties();

        wp.modify(Apache.CONFIGPATH);

    }

    public boolean reload() {

        boolean isWork = false;

        try {

            String command = "/etc/init.d/httpd reload";

            Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});

            if (p.waitFor() == 0) {
                isWork = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return isWork;
        }



    }
}
