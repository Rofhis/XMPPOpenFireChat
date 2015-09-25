package za.co.hellogroup.xmppchat;

import org.jivesoftware.smack.chat.Chat;

/**
 * Created by uFree_Dev_Team on 2015/04/28.
 */
public interface CallBackTask {

    void runCallback(Chat response);

}