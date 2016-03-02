package za.co.hellogroup.xmppchat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.bua.xmppasmack.asmack.XMPPServer;

/**
 * Created by rofhiwa.sikhauli on 10/16/2015.
 */
public class ConnectTask extends AsyncTaskLoader<String> {

    private boolean isLoading = false;
    private String serverAddress, serverName, username, password, mainUsername;
    private int serverPort;

    public ConnectTask(Context context, Bundle bundle){
        super(context);
        this.serverAddress = bundle.getString("serverAddress");
        this.serverPort = bundle.getInt("serverPort");
        this.serverName = bundle.getString("serverName");
        this.username = bundle.getString("username");
        this.password = bundle.getString("password");
        this.mainUsername = bundle.getString("mainUsername");
    }

    @Override
    public void forceLoad() {
        if (!isLoading) {
            super.forceLoad();
            isLoading = !isLoading;
        }
    }

    @Override
    public String loadInBackground(){
        XMPPServer.init(serverAddress, serverPort, serverName, username, password, mainUsername);
        return null;
    }

    @Override
    public void deliverResult(String name) {
        super.deliverResult(name);
    }
}
