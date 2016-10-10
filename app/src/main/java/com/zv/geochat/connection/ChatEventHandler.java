package com.zv.geochat.connection;

import android.util.Log;

import com.zv.geochat.broadcast.BroadcastSender;
import com.zv.geochat.model.ChatMessage;
import com.zv.geochat.notification.NotificationDecorator;
import com.zv.geochat.provider.ChatMessageStore;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatEventHandler {
    private static final String TAG = "ChatEventHandler";
    private final BroadcastSender broadcastSender;
    private final ChatMessageStore chatMessageStore;
    private final NotificationDecorator notificationDecorator;

    public ChatEventHandler(BroadcastSender broadcastSender,
                            ChatMessageStore chatMessageStore,
                            NotificationDecorator notificationDecorator) {
        this.broadcastSender = broadcastSender;
        this.chatMessageStore = chatMessageStore;
        this.notificationDecorator = notificationDecorator;
    }

    public void onLogin(String userName, JSONObject data) {
        Log.e(TAG, "onLogin");
        try {
            int numUsers = data.getInt(ChatMessageKey.NUM_USERS);
            broadcastSender.sendUserJoined(userName, numUsers);
            notificationDecorator.displaySimpleNotification("Joined  Chat...", "Number of users: " + numUsers);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void onUserJoined(JSONObject data) {
        Log.e(TAG, "onUserJoined");
        try {
            String userName = data.getString(ChatMessageKey.USER_NAME);
            int numUsers = data.getInt(ChatMessageKey.NUM_USERS);
            broadcastSender.sendUserJoined(userName, numUsers);
            notificationDecorator.displaySimpleNotification("Joined Chat...", "User: " + userName);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void onUserLeft(JSONObject data) {
        Log.e(TAG, "onUserLeft");
        try {
            String userName = data.getString(ChatMessageKey.USER_NAME);
            int numUsers = data.getInt(ChatMessageKey.NUM_USERS);
            broadcastSender.sendUserLeft(userName, numUsers);
            notificationDecorator.displaySimpleNotification("Left Chat...", "User: " + userName);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void onNewMessage(JSONObject data) {
        Log.e(TAG, "onNewMessage");
        try {
            String userName = data.getString(ChatMessageKey.USER_NAME);
            String message = data.getString(ChatMessageKey.MESSAGE);
            notificationDecorator.displayExpandableNotification("New message: " + userName , message);
            broadcastSender.sendNewMessage(userName, message);
            chatMessageStore.insert(new ChatMessage(userName, message));
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void onConnected() {
        Log.e(TAG, "onConnected");
        notificationDecorator.displaySimpleNotification("Connected to Chat...", "");
        broadcastSender.sendConnected();
    }

    public void onDisconnected() {
        Log.e(TAG, "onDisconnected");
        notificationDecorator.displaySimpleNotification("Disconnected from Chat...", "");
        broadcastSender.sendNotConnected();
    }


    public void onAttemptSend(String userName, String message) {
        Log.e(TAG, "onAttemptSend");
        notificationDecorator.displayExpandableNotification("Sending message...", message);
        broadcastSender.sendNewMessage(userName, message);
        chatMessageStore.insert(new ChatMessage(userName, message));
    }
}
