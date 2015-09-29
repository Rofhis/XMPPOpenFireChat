package za.co.hellogroup.xmppchat;

import android.os.AsyncTask;
import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

import javax.net.ssl.SSLSocketFactory;

/**
 * Created by rofhiwa.sikhauli on 9/25/2015.
 */
public class LoginTask extends AsyncTask<String, Void, String> {

    private Exception exception;
    private XMPPTCPConnection mConnection;

    LoginTask(XMPPTCPConnection mConnection){
        this.mConnection = mConnection;
    }

    protected String doInBackground(String... params) {

        String serverAddress = params[0];
        String serverName = params[1];
        String userName = params[2];
        String strPassword = params[3];
        String retStr = "Not connected";

        try {

            mConnection.connect();
            retStr = "Connected";

        } catch (SmackException | IOException | XMPPException e) {
            e.printStackTrace();
            Log.i("Error Connect", "Connect error = " + e.getMessage());
        }

        try {
            mConnection.login(userName, strPassword);
            Presence presence = new Presence(Presence.Type.available);
            presence.setStatus("I’m available");
            mConnection.sendStanza(presence);

            Log.i("Success Login", "Login to the server");
        } catch (SmackException | IOException | XMPPException e) {
            e.printStackTrace();
            Log.i("Error Login", "Login error = " + e.getMessage());
        }

        return retStr;

    }


    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}

