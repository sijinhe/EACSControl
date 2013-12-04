/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eac.tool.file;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Sijin
 */
public class Logging {
    public boolean write(String appname, String path, String location) {

        boolean isDone = false;

        Properties prop = new Properties();

        try {
            //set the properties value
            prop.setProperty("handlers", "org.apache.juli.FileHandler, java.util.logging.ConsoleHandler");
            prop.setProperty("org.apache.juli.FileHandler.level", "ALL");
            prop.setProperty("org.apache.juli.FileHandler.directory", path);
            prop.setProperty("org.apache.juli.FileHandler.prefix", appname + ".");

            prop.setProperty("java.util.logging.ConsoleHandler.level", "ALL");
            prop.setProperty("java.util.logging.ConsoleHandler.formatter", "java.util.logging.SimpleFormatter");
            //save properties to project root folder
            prop.store(new FileOutputStream(location + "logging.properties"), null);

        } catch (IOException ex) {
            ex.printStackTrace();
        }


        return isDone;
    }

    public static void main(String[] args) {
        Logging log = new Logging();
        String appname = "abc";
        String path = "/tomcat/logs/app/" + appname + "/";
        String location = "/var/www/html/appbase/";

        log.write(appname, path, location);
    }
}
