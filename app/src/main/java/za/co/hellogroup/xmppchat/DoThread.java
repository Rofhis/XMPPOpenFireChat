package za.co.hellogroup.xmppchat;

import android.content.Context;
import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

import javax.net.ssl.SSLSocketFactory;

/**
 * Created by rofhiwa.sikhauli on 9/25/2015.
 */

class DoThread implements Runnable {


    String serverAddress = "";
    int serverPort;
    String serverName = "";
    String userName = "";
    String strPassword = "";

    public String retStr = "";

    private XMPPTCPConnection mConnection;

    DoThread(String serverAddress, int serverPort, String serverName, String userName, String pPassword) {

        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.serverName = serverName;
        this.userName = userName;
        this.strPassword = pPassword;

    }


    public void run() {


        String retStr = "";


        XMPPTCPConnectionConfiguration.Builder connconfig = XMPPTCPConnectionConfiguration.builder(); //Crash here, because the rest of codes are commented out below - if line this is commented out no crash takes place
        connconfig.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        connconfig.setUsernameAndPassword(userName + "@" + serverName, strPassword);
        connconfig.setServiceName(serverName);
        connconfig.setHost(serverAddress);
        connconfig.setPort(serverPort);
        connconfig.setDebuggerEnabled(true);
        connconfig.setSocketFactory(SSLSocketFactory.getDefault());

        mConnection = new XMPPTCPConnection(connconfig.build());
        try {
            mConnection.connect();
            Log.i("Success Connect", "Connect to the server");
        } catch (SmackException | IOException | XMPPException e) {
            e.printStackTrace();
            Log.i("Error Connect", "Connect error = " + e.getMessage());
        }

        // Most servers require you to login before performing other tasks.
        try {
            mConnection.login(userName, strPassword);
            Log.i("Success Login", "Login to the server");
        } catch (SmackException | IOException | XMPPException e) {
            e.printStackTrace();
            Log.i("Error Login", "Login error = " + e.getMessage());

        }
    }

}