package za.co.hellogroup.xmppchat;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bua.xmppasmack.asmack.XMPPServer;

import org.jivesoftware.smack.SmackException;

import java.util.Random;


public class MainActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<String>{
    private EditText message_to, message_body;
    private Button sendBtn;
    private TextView showMessage;
    private Context context;
    private MyApplication appValue;
    Bundle data;
    Loader<String> loaderTask;
    Random rand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        message_to   = (EditText) findViewById(R.id.message_to);
        message_body = (EditText) findViewById(R.id.message_body);
        sendBtn      = (Button) findViewById(R.id.sendButton);
        showMessage  = (TextView) findViewById(R.id.showMessage);
        context      = this;
        appValue     = (MyApplication) getApplicationContext();
        data         = new Bundle();

        data.putString("serverAddress", appValue.getServerAddress());
        data.putInt("serverPort", appValue.getServerPort());
        data.putString("serverName", appValue.getServerName());
        data.putString("username", appValue.getUserName());
        data.putString("password", appValue.getUserPassword());
        data.putString("mainUsername", "rofhiwa");

        sendBtn.setOnClickListener(v -> {
            String to = message_to.getText().toString();
            String body = message_body.getText().toString();
            data.putString("messageTo", to);
            data.putString("message", body);
            loaderTask = new MessageTask(this, data);
            startAsyncLoader(getLoaderID());
            showMessage.append(appValue.getTime() + ": Me - " + body + "\n");
        });
    }

    public int getLoaderID() {
        rand = new Random();
        return rand.nextInt((999999999) + 1);
    }


    void startAsyncLoader(int id) {
        getSupportLoaderManager().initLoader(id, null, this).forceLoad();
    }

    @Override
    public void onResume(){
        super.onResume();
        XMPPServer.setContext(this);
        XMPPServer.setTextView(showMessage);
        loaderTask = new ConnectTask(this, data);
        startAsyncLoader(0);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle bundle) {
        return loaderTask;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        Toast.makeText(this, loader.getId() + " - " + data, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
