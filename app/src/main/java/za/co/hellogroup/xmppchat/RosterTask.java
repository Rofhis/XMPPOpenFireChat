package za.co.hellogroup.xmppchat;

import android.os.AsyncTask;
import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

/**
 * Created by rofhiwa.sikhauli on 9/25/2015.
 */
public class RosterTask extends AsyncTask<String, Void, String> {

    private Exception exception;
    private XMPPTCPConnection mConnection;

    RosterTask(XMPPTCPConnection mConnection){
        this.mConnection = mConnection;
    }

    protected String doInBackground(String... params) {

        String retStr = "Not connected";

        ChatManager chatmanager = ChatManager.getInstanceFor(mConnection);
        Chat newChat = chatmanager.createChat("admin@localhost", new ChatMessageListener() {

            public void processMessage(Chat chat, Message message) {
                Log.i("Message Process", "Received message: " + message);
            }
        });

        try {
            newChat.sendMessage("Hows you my man!");

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.i("Not connected error", "Not connected: " + e.getMessage());
        }

        return retStr;

    }


    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}

