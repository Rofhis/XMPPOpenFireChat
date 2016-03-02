package com.bua.xmppasmack.asmack;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by root on 2015/09/28.
 * XMPPServer
 *
 */
public class XMPPServer {

    static String serverAddress;
    static int serverPort;
    static String serverName;
    static String userName;
    static String userPassword;
    static String mainUsername;
    static XMPPTCPConnection mConnection = null;
    static MultiUserChatManager manager;
    static XMPPTCPConnectionConfiguration.Builder serverConnectionConfig;
    static AccountManager accountManager;
    static TextView textView;
    static Activity mContext;
    /**
     * Empty Constructors
     */
    public XMPPServer() {}

    /*
     * Non-empty Constructors
     *
     */
    public XMPPServer(String serverAddress, int serverPort, String serverName, String userName, String userPassword, String mainUser) {

        init(serverAddress, serverPort, serverName, userName, userPassword, mainUser);

    }

    /**
     * createChatroom
     * @Params serverAddress
     * @Params  serverPort
     * @Params  serverName
     * @Params  userName
     * @Params  userPassword
     *
     */
    public static void init(String address, int port, String host, String username, String password, String mainUser) {
        serverAddress = address;
        serverPort = port;
        serverName = host;
        userName = username;
        userPassword = password;
        mainUsername = mainUser;

        serverConnectionConfig = XMPPTCPConnectionConfiguration.builder();
        serverConnectionConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        serverConnectionConfig.setUsernameAndPassword(userName + "@" + serverName, userPassword);
        serverConnectionConfig.setServiceName(serverName);
        serverConnectionConfig.setHost(serverAddress);
        serverConnectionConfig.setPort(serverPort);
        serverConnectionConfig.setDebuggerEnabled(true);
        serverConnectionConfig.setSendPresence(true);
        serverConnectionConfig.setHostnameVerifier((hostname, session) -> true);
        serverConnectionConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.ifpossible);

        SmackConfiguration.DEBUG = true;
        SASLAuthentication.unBlacklistSASLMechanism(SASLMechanism.PLAIN);
        SASLAuthentication.blacklistSASLMechanism(SASLMechanism.DIGESTMD5);

        mConnection = new XMPPTCPConnection(serverConnectionConfig.build());
        mConnection.setPacketReplyTimeout(10000);
        /**
         * Check if connection is already established
         */
        connect();

    }

    /**
     * connect to XMPP server
     *
     */
    private static void connect() {
        if (!mConnection.isConnected()) {
            try{
                mConnection.connect();
                login();
                while (true) {
                    handlePrivateMessages();
                }
            } catch (SmackException | XMPPException | IOException ex) {
                Log.d("XMPP Server", "Connection failed");
                ex.printStackTrace();
                connect();
            }
        }
    }

    /**
     * createAccount
     *
     */
    private static void createAccount() throws SmackException, XMPPException, IOException {
        accountManager = AccountManager.getInstance(mConnection);
        accountManager.sensitiveOperationOverInsecureConnection(true);
        accountManager.createAccount(mainUsername, userPassword);
    }

    /**
     * login to xmpp server
     *
     */
    private static void login() throws SmackException, XMPPException, IOException {
        try{
            mConnection.login(mainUsername, userPassword);
            Presence presence = new Presence(Presence.Type.available);
            presence.setStatus("I'm available");
            mConnection.sendStanza(presence);
        } catch (XMPPException ex){
            createAccount();
            login();
        }

    }

    /**
     * createChatroom
     * @param chatroomName
     *
     */
    public static Boolean createChatroom(String chatroomName) {

        Boolean response = false;
        String chatroom = chatroomName.replaceAll("\\s", "");
        manager = MultiUserChatManager.getInstanceFor(mConnection);
        MultiUserChat multiUserChat = manager.getMultiUserChat(chatroom + "@conference." + serverName);

        try {
            multiUserChat.create(chatroom);
            multiUserChat.sendConfigurationForm(new Form(DataForm.Type.submit));
            joinChatroom(chatroom);
            response = true;
        } catch (SmackException | XMPPException e) {
            e.printStackTrace();
        } finally {
            return response;
        }
    }

    /**
     * sendMessage
     * @param messageTo
     * @param message
     *
     */
    public static void sendMessage(String messageTo, String message) throws SmackException.NotConnectedException {
        Message msg = new Message(messageTo + "@" + serverName, Message.Type.chat);
        msg.setFrom(mainUsername);
        msg.setSubject("Message from " + mainUsername);
        msg.setStanzaId(getTime());
        msg.setBody(message);
        mConnection.sendStanza(msg);
        Log.d("XMPP SERVER", "Sending message: " + message + " to " + messageTo);
    }

    static String getTime() {
        Date date = new Date( );
        SimpleDateFormat ft =  new SimpleDateFormat ("hh:mma", Locale.ENGLISH);
        return ft.format(date);
    }

    /**
     * joinChatroom
     * @param chatroomName
     *
     */
    public static void joinChatroom(String chatroomName) {
        String chatroom = chatroomName.replaceAll("\\s", "");
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(mConnection);
        MultiUserChat multiUserChat = manager.getMultiUserChat(chatroom + "@conference." + serverName);
        Message msg = new Message(chatroom);

        msg.setType(Message.Type.groupchat);
        msg.setSubject("headline");
        msg.setBody(userName + " joined");

        if (!multiUserChat.isJoined()) {
            try {
                multiUserChat.join(userName);
                multiUserChat.sendMessage(msg);
                listenChatroomMessages(chatroom);
            } catch (SmackException | XMPPException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * sendChatroomMessage
     * @param chatroomName
     * @param messageBody
     *
     */
    public static Boolean sendChatroomMessage(String chatroomName, String messageBody) {
        Boolean response = false;
        String chatroom = chatroomName.replaceAll("\\s", "");
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(mConnection);
        MultiUserChat multiUserChat = manager.getMultiUserChat(chatroom + "@conference." + serverName);
        Message msg = new Message(chatroom, Message.Type.groupchat);

        msg.setSubject("00:00");
        msg.setBody(messageBody);

        try {
            multiUserChat.sendMessage(msg);
            response = true; //Success
        } catch (SmackException e) {
            e.printStackTrace();
            Log.i("Not connected error", "Not connected: " + e.getMessage());
        }

        return response;
    }

    public static void setTextView(TextView mTextView){
        textView = mTextView;
    }

    static TextView getTextView(){
        return textView;
    }

    /**
     * handlePrivateMessages
     *
     */
    public static void handlePrivateMessages() {
       if (mConnection != null) {
           ChatManager chatManager = ChatManager.getInstanceFor(mConnection);
           chatManager.addChatListener((chat, createdLocally) -> {
               if (chat != null && !createdLocally) {
                   chat.addMessageListener((i, message) -> {
                       final String[] sender = message.getFrom().split("@");
                       final String time = message.getStanzaId();
                       final String messageBody = message.getBody();
                       Log.i("One on one message", sender[0] + " - " + messageBody + "\n");
                       getContext().runOnUiThread(() -> getTextView().append(time + ": " + sender[0] + " - " + messageBody + "\n"));
                   });
               }
           });
       }
    }


    public static void setContext(Activity context){
        mContext = context;
    }

    public static Activity getContext(){
        return mContext;
    }


    /**
     * static listenChatroomMessages
     * @param chatroomName
     *
     */
    public static void listenChatroomMessages(String chatroomName) {
        String chatroom = chatroomName.replaceAll("\\s", "");
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(mConnection);
        MultiUserChat multiUserChat = manager.getMultiUserChat(chatroom + "@conference." + serverName);

        multiUserChat.addMessageListener((message) -> {
            String sender = message.getFrom().split("@")[0];
            String fileName = message.getTo().split("@")[0];
            String subject = "" + message.getSubject();
            String body = message.getBody();

            if (subject.equals("headline")) {

            } else {
                Log.i("Chat Message: ", body);
            }
        });
    }

    /**
     * getServerAddress
     * @return String server address
     *
     */
    public static String getServerAddress() {
        return serverAddress;
    }

    /**
     * getServerName
     * @return String server name
     *
     */
    public static String getServerName() {
        return serverName;
    }

    /**
     * getUserName
     * @return String username
     *
     */
    public static String getUserName() {
        return userName;
    }

    /**
     * getUserName
     * @return String username
     *
     */
    public static String getUserPassword() {
        return userPassword;
    }

    /**
     * getServerPort
     * @return int server port
     *
     */
    public static int getServerPort() {
        return serverPort;
    }

    /**
     * getConnection
     * @return XMPPTCPConnection XMPP Server Connection
     *
     */
    public static XMPPTCPConnection getConnection() {

        XMPPTCPConnection response = null;

        if (mConnection != null) {
            response = mConnection;
        }

        return response;
    }
}
