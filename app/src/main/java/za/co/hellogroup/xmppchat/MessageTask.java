package za.co.hellogroup.xmppchat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.bua.xmppasmack.asmack.XMPPServer;

import org.jivesoftware.smack.SmackException;

/**
 * Created by rofhiwa.sikhauli on 10/16/2015.
 */
public class MessageTask extends AsyncTaskLoader<String> {

    private boolean isLoading = false;
    private String messageTo, message;

    public MessageTask(Context context, Bundle bundle){
        super(context);
        this.messageTo = bundle.getString("messageTo");
        this.message = bundle.getString("message");
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
        try{
            XMPPServer.sendMessage(messageTo, message);
        } catch (SmackException.NotConnectedException ex){
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void deliverResult(String name) {
        super.deliverResult(name);
    }
}
