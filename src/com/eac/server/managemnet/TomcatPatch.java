/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.server.managemnet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sijin
 */
public class TomcatPatch {

    public static String path = "/tomcat/conf/server.xml";
    ServerInfo info = new ServerInfo();
    ServerController ctrl = new ServerController(info.getServerId());

    public TomcatPatch() {
    }

    public void ModifyServerXML() {

        File f = new File(path);

        if (f.exists()) {
            info.deleteFile(path);
        }

        createServerXML(info.getServerId());

        ctrl.restart();

    }

    public static void createServerXML(String id) {

        FileWriter fstream = null;
        try {
            fstream = new FileWriter(path);
        } catch (IOException ex) {
            Logger.getLogger(TomcatPatch.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedWriter out = new BufferedWriter(fstream);

        try {
            // Create file
            out.write("<Server port=\"8005\" shutdown=\"SHUTDOWN\">\n");
            out.write("\t<Listener className=\"org.apache.catalina.core.AprLifecycleListener\" SSLEngine=\"on\" />\n");
            out.write("\t<Listener className=\"org.apache.catalina.core.JasperListener\" />\n");
            out.write("\t<Listener className=\"org.apache.catalina.core.JreMemoryLeakPreventionListener\" />\n");
            out.write("\t<Listener className=\"org.apache.catalina.mbeans.GlobalResourcesLifecycleListener\" />\n");
            out.write("\t<Listener className=\"org.apache.catalina.core.ThreadLocalLeakPreventionListener\" />\n");
            out.write("\t<GlobalNamingResources>\n");
            out.write("\t\t<Resource name=\"UserDatabase\" auth=\"Container\" type=\"org.apache.catalina.UserDatabase\" description=\"User database that can be updated and saved\" factory=\"org.apache.catalina.users.MemoryUserDatabaseFactory\" pathname=\"conf/tomcat-users.xml\" />\n");
            out.write("\t</GlobalNamingResources>\n");
            out.write("\t<Service name=\"Catalina\">\n");
            out.write("\t\t<Connector port=\"8080\" protocol=\"HTTP/1.1\" connectionTimeout=\"20000\" redirectPort=\"8443\" />\n");
            out.write("\t\t<Connector port=\"8009\" protocol=\"AJP/1.3\" redirectPort=\"8443\" />\n");
            out.write("\t\t<Engine name=\"Catalina\" defaultHost=\"localhost\"  jvmRoute=\"" + id + "\" >\n");
            out.write("\t\t\t<Cluster className=\"org.apache.catalina.ha.tcp.SimpleTcpCluster\" channelSendOptions=\"8\">\n");
            out.write("\t\t\t\t<Manager className=\"org.apache.catalina.ha.session.DeltaManager\" expireSessionsOnShutdown=\"false\" notifyListenersOnReplication=\"true\"/>\n");
            out.write("\t\t\t\t<Channel className=\"org.apache.catalina.tribes.group.GroupChannel\">\n ");
            out.write("\t\t\t\t\t<Membership className=\"org.apache.catalina.tribes.membership.McastService\" address=\"228.0.0.4\" port=\"45564\" frequency=\"500\" dropTime=\"3000\"/>\n");
            out.write("\t\t\t\t\t<Sender className=\"org.apache.catalina.tribes.transport.ReplicationTransmitter\">\n");
            out.write("\t\t\t\t\t\t<Transport className=\"org.apache.catalina.tribes.transport.nio.PooledParallelSender\"/>\n");
            out.write("\t\t\t\t\t</Sender>\n");
            out.write("\t\t\t\t\t<Receiver className=\"org.apache.catalina.tribes.transport.nio.NioReceiver\" address=\"auto\" port=\"4000\" autoBind=\"100\" selectorTimeout=\"5000\" maxThreads=\"6\"/>\n");
            out.write("\t\t\t\t\t<Interceptor className=\"org.apache.catalina.tribes.group.interceptors.TcpFailureDetector\"/>\n");
            out.write("\t\t\t\t\t<Interceptor className=\"org.apache.catalina.tribes.group.interceptors.MessageDispatch15Interceptor\"/>\n");
            out.write("\t\t\t\t</Channel>\n");
            out.write("\t\t\t\t<Valve className=\"org.apache.catalina.ha.tcp.ReplicationValve\" filter=\"\"/>\n");
            out.write("\t\t\t\t<Valve className=\"org.apache.catalina.ha.session.JvmRouteBinderValve\"/>\n");
            out.write("\t\t\t\t<ClusterListener className=\"org.apache.catalina.ha.session.JvmRouteSessionIDBinderListener\"/>\n");
            out.write("\t\t\t\t<ClusterListener className=\"org.apache.catalina.ha.session.ClusterSessionListener\"/>\n");
            out.write("\t\t\t</Cluster>\n");
            out.write("\t\t\t<Realm className=\"org.apache.catalina.realm.LockOutRealm\">\n");
            out.write("\t\t\t<Realm className=\"org.apache.catalina.realm.UserDatabaseRealm\" resourceName=\"UserDatabase\"/>\n");
            out.write("\t\t\t</Realm>\n");
            out.write("\t\t\t<Host name=\"localhost\"  appBase=\"webapps\" unpackWARs=\"true\" autoDeploy=\"true\">\n");
            out.write("\t\t\t<Valve className=\"org.apache.catalina.valves.AccessLogValve\" directory=\"logs\" prefix=\"localhost_access_log.\" suffix=\".txt\" pattern=\"%h %l %u %t &quot;%r&quot; %s %b\" />\n");
            out.write("\t\t\t</Host>\n");
            out.write("\t\t</Engine>\n");
            out.write("\t</Service>\n");
            out.write("</Server>\n");
            //Close the output stream
            
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(TomcatPatch.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
//    public static void main(String[] args) {
//
//    }
}
