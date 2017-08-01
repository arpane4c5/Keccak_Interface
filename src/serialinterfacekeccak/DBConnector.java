/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package serialinterfacekeccak;

/**
 *
 * @author Bloodhound
 */
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import java.util.Properties;

/**
 *
 * @author Arpan Gupta
 */
public class DBConnector {

    private String dbURL;
    private String dbDriver;
    private Connection dbCon;
    private String username;
    private String password;

    public DBConnector() {
        dbURL = "";
        dbDriver = "";
        username = "";
        password = "";
        dbCon = null;
    }

    public Connection getdbCon() {
        return dbCon;
    }

    public void setdbCon(final Connection con) {
        dbCon = con;
    }

    public void setdbURL(final String url) {
        dbURL = url;
    }

    public void setdbDriver(final String driver) {
        dbDriver = driver;
    }

    public void setUsername(final String user) {
        username = user;
    }

    public void setPassword(final String pass) {
        password = pass;
    }

    public String getdbURL() {
        return dbURL;
    }

    public String getdbDriver() {
        return dbDriver;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void init() throws FileNotFoundException, IOException {

        InputStream inn = this.getClass().getClassLoader().getResourceAsStream("DatabaseConnectionParameters.properties");
        Properties prop = new Properties();
        prop.load(inn);
        dbDriver = prop.getProperty("dbDriver");
        dbURL = prop.getProperty("dbURL");
        username = prop.getProperty("Username");
        password = prop.getProperty("Password");

    }

    public boolean connect() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        if (dbDriver.compareTo("") == 0 || dbURL.compareTo("") == 0) {
            return false;
        } else {
            Class.forName(dbDriver).newInstance();
            if (username.compareTo("") != 0 && password.compareTo("") != 0) {
                dbCon = DriverManager.getConnection(dbURL, username, password);
            } else {
                dbCon = DriverManager.getConnection(dbURL);
            }
            return true;
        }

    }

    public int updateData(final String query) throws SQLException {
        System.err.println("QueryProcessor.java : Received query : " + query);
        Statement s = dbCon.createStatement();
        int num = s.executeUpdate(query);
        s.close();
        return num;
    }

    public ResultSet getRecords(final String query) throws SQLException {
        Statement s = dbCon.createStatement();
        ResultSet rs = s.executeQuery(query);
        return rs;
    }

    public int insertData(final String query)throws SQLException
    {
        System.err.println("QueryProcessor.java : Received query : "+query);
        PreparedStatement pstmt=dbCon.prepareStatement(query);
        int num=pstmt.executeUpdate();
        pstmt.close();
        return num;
    }

    public ResultSet getLoginRecords(final String query)throws SQLException
    {
        PreparedStatement pstmt=dbCon.prepareStatement(query);
        ResultSet rs=pstmt.executeQuery();
        return rs;
    }

    public void close() throws SQLException {
        dbCon.close();
    }

    
    public static void main(String args[]) {
        String pwd = System.getProperty("user.dir");
        System.out.println("Present Working Directory : " + pwd);
        DBConnector qp = new DBConnector();
        try {
            qp.init();
        } catch (FileNotFoundException f) {
            System.err.println("QueryProcessor.java : Could not find the database parameters file\n" + f);
        } catch (IOException ioe) {
            System.err.println("QueryProcessor.java : Could not read from database parameters file\n" + ioe);
        }
        System.err.println(qp.dbDriver + " " + qp.dbURL + " " + qp.username + " " + qp.password);
        try {
            qp.connect();
        } catch (InstantiationException ie) {
            System.err.println("taskProcessor.java : #Could not connect to database # " + ie);
        } catch (ClassNotFoundException c) {
            System.err.println("taskProcessor.java : #Could not connect to database # " + c);
        } catch (SQLException s) {
            System.err.println("taskProcessor.java : #Could not connect to database # " + s);
        } catch (IllegalAccessException i) {
            System.err.println("taskProcessor.java : #Could not connect to database # " + i);
        }
        /*
        try {
            ResultSet rs = qp.getRecords("select * from Servers;");
            rs.close();
        } catch (SQLException s) {
            System.err.println("taskProcessor.java : #Could not connect to database # " + s);
        }

         */
        try {
            qp.close();
        } catch (SQLException s) {
            System.err.println("taskProcessor.java : #Could not connect to database # " + s);
        }
    }
     
     
}
