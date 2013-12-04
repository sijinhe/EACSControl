/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.server.managemnet;

import com.eac.tool.file.WebXML;
import com.eac.tool.file.WebXML;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sijin
 */
public class FileAction {

    public final static String SOURCE_PATH = "";
    public final static String TARGET_PATH = "";

    public FileAction() {
    }

    public boolean renameWARtoZip(String appname, String location){

        boolean isDownload = false;

        try {

            String command = "mv " + location + appname + ".war " + location + appname + ".zip";
            System.out.println(command);

            Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});

            int valid = p.waitFor();

            if (valid == 0) {

                isDownload = true;
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            return isDownload;
        }
    }

    public boolean renameZiptoWAR(String appname, String location){

        boolean isDownload = false;

        try {

            String command = "mv " + location + appname + ".zip " + location + appname + ".war";
            System.out.println(command);

            Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});

            int valid = p.waitFor();

            if (valid == 0) {

                isDownload = true;
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            return isDownload;
        }
    }

    public boolean unPackZip(String appname, String location) {
        boolean isDownload = false;

        try {

            String command = "unzip " + location + appname + ".zip";
            System.out.println(command);

            Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});

            int valid = p.waitFor();

            if (valid == 0) {

                isDownload = true;
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            return isDownload;
        }
    }

    public boolean packZip(String appname, String location) {
        boolean isDownload = false;

        try {

            String command = "zip " + location + appname + ".zip";
            System.out.println(command);

            Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});

            int valid = p.waitFor();

            if (valid == 0) {

                isDownload = true;
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            return isDownload;
        }
    }



    public boolean unPackWar(String appname, String location) {
        boolean isDownload = false;

        try {

            String command = "cd " + location + " && jar xf " + appname + ".war";
            System.out.println(command);

            Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});

            int valid = p.waitFor();

            if (valid == 0) {

                isDownload = true;
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            return isDownload;
        }
    }

    public boolean packWar(String appname, String location) {
        boolean isDownload = false;

        try {

            String command = "cd " + location + " && jar cf " + location + appname + ".war *";
                System.out.println(command);

            Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});

            int valid = p.waitFor();

            if (valid == 0) {

                isDownload = true;
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            return isDownload;
        }
    }

    public boolean download(String url, String location) {

        boolean isDownload = false;

        try {


            Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "wget -P " + location + " " + url});

            int valid = p.waitFor();

            if (valid == 0) {

                isDownload = true;
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            return isDownload;
        }

    }

    public void copy(String source, String target) {

        InputStream in = null;
        try {
            in = new FileInputStream(source);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileAction.class.getName()).log(Level.SEVERE, null, ex);
        }
        OutputStream out = null;
        try {
            out = new FileOutputStream(target);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileAction.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte[] buf = new byte[1024];
        int len;
        try {
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (IOException ex) {
            Logger.getLogger(FileAction.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(FileAction.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(FileAction.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) {
    }

    public boolean deleteWar(String appname, String location) {

        boolean isDownload = false;

        try {

            String command = "rm " + location + appname + ".war";
            //     System.out.println(command);

            Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});

            int valid = p.waitFor();

            if (valid == 0) {

                isDownload = true;
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            return isDownload;
        }
    }

    public boolean deleteAllByAppname(String appname, String location) {

        boolean isDownload = false;

        try {

            String command = "rm -r " + location + appname + "*";
            //     System.out.println(command);

            Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});

            int valid = p.waitFor();

            if (valid == 0) {

                isDownload = true;
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            return isDownload;
        }
    }

    public boolean deleteRelevantFile(String appname, String location) {
        boolean isDownload = false;

        try {

            String command = "rm " + location + appname + "*";
            //     System.out.println(command);

            Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});

            int valid = p.waitFor();

            if (valid == 0) {

                isDownload = true;
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            return isDownload;
        }
    }
}
