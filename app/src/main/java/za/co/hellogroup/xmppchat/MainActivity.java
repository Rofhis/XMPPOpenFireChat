package za.co.hellogroup.xmppchat;

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
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;


public class MainActivity extends ActionBarActivity {

    private final String  SERVER_ADDRESS = "192.168.168.37";
    private final int  SERVER_PORT = 5222;
    private final String  SERVER_NAME = "localhost";
    private final String  USER_NAME = "rofhiwa";
    private final String  USER_PASSWORD = "rofhiwa";

    private EditText message_to, message_body;
    private Button sendBtn;
    private TextView showMessage;

    private XMPPTCPConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        message_to = (EditText) findViewById(R.id.message_to);
        message_body = (EditText) findViewById(R.id.message_body);
        sendBtn = (Button) findViewById(R.id.sendButton);
        showMessage = (TextView) findViewById(R.id.showMessage);

        XMPPTCPConnectionConfiguration.Builder connconfig = XMPPTCPConnectionConfiguration.builder(); //Crash here, because the rest of codes are commented out below - if line this is commented out no crash takes place
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

        new IncomingMessageTask(new CallBackTask() {

            @Override
            public void runCallback(Chat chat){
                if(chat != null){
                    handleMessage(chat);
                }
            }

        }, mConnection).execute();

    }


    void handleMessage(Chat chat){

        chat.addMessageListener(new ChatMessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                Log.i("Sender", message.getFrom());
                Log.i("Receiver", message.getTo());
                Log.i("Body", message.getBody());

                String[] sender = message.getFrom().split("@");
                showMessage.setText(sender[0] + " - " + message.getBody());
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
