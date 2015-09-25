package za.co.hellogroup.xmppchat;

import android.os.AsyncTask;
import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

/**
 * Created by rofhiwa.sikhauli on 9/25/2015.
 */
public class IncomingMessageTask extends AsyncTask<String, Void, Chat> {

    private Exception exception;
    private XMPPTCPConnection mConnection;
    private Chat response;
    private CallBackTask callBackTask = null;

    IncomingMessageTask(CallBackTask callBackTask, XMPPTCPConnection mConnection){
        this.mConnection = mConnection;
        this.callBackTask = callBackTask;
    }

    protected Chat doInBackground(String... params) {

        response = null;

        ChatManager chatManager = ChatManager.getInstanceFor(mConnection);
        chatManager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {

                if (!createdLocally) {
                    response = chat;
                }

            }

        });

        return response;

    }

    @Override
    protected void onPostExecute(Chat chat) {
        super.onPostExecute(chat);
        callBackTask.runCallback(chat);
    }



}

