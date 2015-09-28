package za.co.hellogroup.xmppchat;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;


public class MainActivity extends ActionBarActivity {

    private final String  SERVER_ADDRESS = "41.185.29.87";
    private final int  SERVER_PORT = 5222;
    private final String  SERVER_NAME = "ufree.com";
    private final String  USER_NAME = "rofhiwa";
    private final String  USER_PASSWORD = "rofhiwa";

    private EditText message_to, message_body;
    private Button sendBtn;
    private TextView showMessage;
    private Context context;
    private XMPPTCPConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        message_to = (EditText) findViewById(R.id.message_to);
        message_body = (EditText) findViewById(R.id.message_body);
        sendBtn = (Button) findViewById(R.id.sendButton);
        showMessage = (TextView) findViewById(R.id.showMessage);
        context = this;

        XMPPTCPConnectionConfiguration.Builder connconfig = XMPPTCPConnectionConfiguration.builder();
        connconfig.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        connconfig.setUsernameAndPassword(USER_NAME + "@" + SERVER_NAME, USER_PASSWORD);
        connconfig.setServiceName(SERVER_NAME);
        connconfig.setHost(SERVER_ADDRESS);
        connconfig.setPort(SERVER_PORT);
        connconfig.setDebuggerEnabled(true);

        mConnection = new XMPPTCPConnection(connconfig.build());

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String to = message_to.getText().toString();
                String body = message_body.getText().toString();

                sendMessage(to, body);

            }
        });

    }

    @Override
    public void onResume(){



        super.onResume();
        if(!mConnection.isConnected()) {
            new LoginTask(mConnection).execute(SERVER_ADDRESS, SERVER_NAME, USER_NAME, USER_PASSWORD);
        }

        ChatManager chatManager = ChatManager.getInstanceFor(mConnection);
        chatManager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {

                if (chat != null && !createdLocally) {
                    handleMessage(chat);
                }

            }

        });



//
//        new IncomingMessageTask(new CallBackTask() {
//
//            @Override
//            public void runCallback(Chat chat){
//                if(chat != null){
//                    handleMessage(chat);
//                    Toast.makeText(context, "Messages found", Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(context, "No messages found", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//        }, mConnection).execute();

    }


    void handleMessage(Chat chat){
        Log.i("Handle messages", "Got new messages");
        chat.addMessageListener(new ChatMessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                Log.i("Sender", message.getFrom());
                Log.i("Receiver", message.getTo());
                Log.i("Body", message.getBody());

                final String[] sender = message.getFrom().split("@");
                final String messageBody = message.getBody();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showMessage.append(sender[0] + " - " + messageBody + "\n");
                    }
                });



            }
        });

    }




    //Send message
    void sendMessage(String to, String message){

        new SendMessageTask(mConnection).execute(to + "@" + SERVER_NAME, message);
        Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
