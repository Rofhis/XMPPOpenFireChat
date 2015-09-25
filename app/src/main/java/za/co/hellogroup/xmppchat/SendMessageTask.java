package za.co.hellogroup.xmppchat;

import android.os.AsyncTask;
import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

/**
 * Created by rofhiwa.sikhauli on 9/25/2015.
 */
public class SendMessageTask extends AsyncTask<String, Void, String> {

    private Exception exception;
    private XMPPTCPConnection mConnection;

    SendMessageTask(XMPPTCPConnection mConnection){
        this.mConnection = mConnection;
    }

    protected String doInBackground(String... params) {

        String retStr = "";
        String messageTo = params[0];
        String messageBody = params[1];

        ChatManager chatmanager = ChatManager.getInstanceFor(mConnection);
        Chat newChat = chatmanager.createChat(messageTo, new ChatMessageListener() {

            public void processMessage(Chat chat, Message message) {
                Log.i("Message Process", message.getBody());
            }
        });

        try {
            newChat.sendMessage(messageBody);

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

