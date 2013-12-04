/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.entity.operation;

import com.eac.db.dao.ContainerDAO;
import com.eac.db.dao.StorageDAO;
import com.eac.db.entity.Container;
import com.eac.db.entity.Storage;
import com.eac.tool.file.RandomString;
import com.eac.tool.json.JSONStorage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sijin
 */
public class StorageAction {

    final private String url = "jdbc:mysql://146.169.35.22:3306/";
    final private String driver = "com.mysql.jdbc.Driver";
    final private String userName = "root";
    final private String pw = "sijinsijin";

    public StorageAction() {
    }

    public Container enableDB(Container container, Storage s) {

        String password = createPW();

        container.setDbName(container.getContainerName());
        container.setDbStatus("RUNNING");
        container.setDbPassword(password);
        container.setStorage(s);

        String dbname = container.getDbName();

        if (this.createDatabase(dbname)) {

            if (this.createUser(dbname, password, password)) {

                if (this.modifyMaxUserConnection(dbname, password, container.getMaxConnection())) {

                    this.flushPrivilege();
                    ContainerDAO cdao = new ContainerDAO();

                    if (!cdao.modify(container)) {
                        return null;
                    }

                    return container;

                } else {
                    this.deleteDatabase(dbname);
                    return null;
                }

            } else {
                this.deleteDatabase(dbname);
                return null;
            }

        } else {

            return null;
        }


    }

    public Container disableDB(Container container) {

        if (this.deleteDatabase(container.getDbName())) {

            ContainerDAO cdao = new ContainerDAO();

            container.setDbName("");
            container.setDbStatus("NOT_IN_USE");
            container.setStorage(null);
            container.setDbPassword("");
            container.setMaxConnection(5);

            if (!cdao.modify(container)) {
                return null;
            }

            return container;

        } else {
            return null;
        }


    }

    public String createStorage(String publicip, String publicport, String privateip, String privateport, String status, String type) {

        String idStorage = UUID.randomUUID().toString();

        Storage store = new Storage(idStorage);

        store.setPrivateIp(privateip);
        store.setPrivatePort(privateport);
        store.setPublicIp(publicip);
        store.setPublicPort(publicport);
        store.setStatus(status);
        store.setType(type);

        StorageDAO sdao = new StorageDAO();

        String json = "";

        JSONStorage js = new JSONStorage();

        if (sdao.create(store)) {

            json = js.createJSON(store, true);

        } else {
            json = js.createJSON(store, false);
        }

        return json;
    }

    public boolean createDatabase(String database) {

        Connection con = null;
        boolean output = false;

        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url, userName, pw);
            Statement st = con.createStatement();
            int i = st.executeUpdate("CREATE DATABASE " + database);
            System.out.println(i);
            if (i != 0) {
                output = true;
            }

        } catch (InstantiationException ex) {
            Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.close();

            } catch (SQLException ex) {
                Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return output;

    }

    public boolean createUser(String database, String username, String password) {

        Connection con = null;
        boolean output = false;
        try {

            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url, userName, pw);
            Statement st = con.createStatement();
            int i = st.executeUpdate("GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, REFERENCES, INDEX, ALTER, CREATE TEMPORARY TABLES, LOCK TABLES, EXECUTE, CREATE VIEW, SHOW VIEW, CREATE ROUTINE, ALTER ROUTINE ON " + database + ".* TO '" + username + "'@'%' IDENTIFIED BY '" + password + "' WITH GRANT OPTION;");
            if (i == 0) {
                output = true;
            }

        } catch (InstantiationException ex) {
            Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return output;


    }

    public boolean modifyMaxUserConnection(String database, String username, int maxConnection) {
        Connection con = null;
        boolean output = false;

        try {

            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url, userName, pw);
            Statement st = con.createStatement();
            int i = st.executeUpdate("GRANT USAGE ON " + database + ".* TO '" + username + "'@'%' WITH MAX_USER_CONNECTIONS " + maxConnection + ";");
            if (i == 0) {
                output = true;
            }

        } catch (SQLException ex) {
            Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return output;

    }

    public boolean flushPrivilege() {

        Connection con = null;
        boolean output = false;
        try {

            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url, userName, pw);
            Statement st = con.createStatement();
            int i = st.executeUpdate("FLUSH PRIVILEGES;");
            if (i == 0) {
                output = true;
            }

        } catch (SQLException ex) {
            Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return output;

    }

    public boolean deleteDatabase(String database) {
        Connection con = null;
        boolean output = false;
        try {

            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url, userName, pw);
            Statement st = con.createStatement();
            int i = st.executeUpdate("DROP DATABASE " + database);
            System.out.println(i);
            if (i == 0) {
                output = true;
            }

        } catch (SQLException ex) {
            output = true;
            Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(StorageAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return output;
    }

    public int obtainDiskUsage(String database) {
        int key = 0;
        Connection con = null;
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url, userName, pw);
            try {
                Statement st = con.createStatement();

                ResultSet result = st.executeQuery("select sum(DATA_LENGTH) from information_schema.TABLES where information_schema.TABLES.TABLE_SCHEMA LIKE \"" + database + "\"");


                while (result.next()) { // process results one row at a time
                    key = result.getInt(1);

                }

                //  System.out.println(key/16384);

                System.out.println("1 row(s) affacted");
                con.close();

            } catch (SQLException s) {
                System.out.println("SQL statement is not executed!");
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key / 16384;
    }

    public String registerDatabaseByUser(String database, String username, String password, int max) {

        createDatabase(database);
        createUser(database, username, password);
        modifyMaxUserConnection(database, username, max);
        flushPrivilege();

        return password;
    }

    public static void main(String[] args) {

        Container container = new Container("1b70c6ac-ff83-45bd-96aa-2be505fca567");
        // Container container = new Container("61e87356-a151-491c-9a0f-f887a810d28d");
        ContainerDAO cdao = new ContainerDAO();

        container = cdao.fetch(container);

        StorageAction dact = new StorageAction();

        StorageDAO sdao = new StorageDAO();

        Storage s = sdao.fetchRunningMySQLNode().get(0);

        //  container = dact.enableDB(container, s);

        container = dact.disableDB(container);

    }

    private String createPW() {
        RandomString rs = new RandomString(8);
        String password = rs.nextString();

        ContainerDAO cdao = new ContainerDAO();

        while (cdao.checkdbName(password)) {

            password = rs.nextString();
        }

        return password;
    }

    public boolean changeMaxCONN(String containerid, String maxcon) {
        boolean isChange = false;
        int max = Integer.parseInt(maxcon);

        Container container = new Container(containerid);
        ContainerDAO cdao = new ContainerDAO();
        container = cdao.fetch(container);

        container.setMaxConnection(max);


        if (cdao.modify(container)) {

            if(container.getDbStatus().equalsIgnoreCase("RUNNING")){
                
                if(this.modifyMaxUserConnection(container.getDbName(), container.getDbPassword(), max)){

                    isChange = true;      
                }
                
            } else {
                isChange = true;
            }


        }


        return isChange;
    }
}
