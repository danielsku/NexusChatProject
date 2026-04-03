package com.nexus.nexuschat.client.glass;



import com.formdev.flatlaf.FlatDarkLaf;
import com.nexus.nexuschat.SQLitedatabase.AppConfig;
import com.nexus.nexuschat.SQLitedatabase.model.FriendRequest;
import com.nexus.nexuschat.SQLitedatabase.model.GroupRequest;
import com.nexus.nexuschat.SQLitedatabase.model.Message;
import com.nexus.nexuschat.SQLitedatabase.service.*;
import com.nexus.nexuschat.client.MyStompClient;
import com.nexus.nexuschat.http.SyncClient;
import com.nexus.nexuschat.pojo.ChatFriendPayload;
import com.nexus.nexuschat.pojo.GroupRequestPayload;
import com.nexus.nexuschat.pojo.SyncResponse;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class App {

    private static IdentityService identityService;
    private static ContactService contactService;
    private static MessageService messageService;
    private static FriendRequestService friendRequestService;
    private static GroupRequestService groupRequestService;
    private static ChatMemberService chatMemberService;
    private static GroupChatService groupChatService;

    public static void main(String[] args){
        //UIManager.put("defaultFont", new Font("Avenir", Font.PLAIN, 14));

        // Modern look and feel set up
        FlatDarkLaf.setup();
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                com.nexus.nexuschat.client.glass.swingacrylic.SwingAcrylic.prepareSwing();

                // Get all beans
                AnnotationConfigApplicationContext context =
                        new AnnotationConfigApplicationContext(AppConfig.class);

                identityService = context.getBean(IdentityService.class);
                contactService = context.getBean(ContactService.class);
                messageService = context.getBean(MessageService.class);
                friendRequestService = context.getBean(FriendRequestService.class);
                groupRequestService = context.getBean(GroupRequestService.class);
                chatMemberService = context.getBean(ChatMemberService.class);
                groupChatService = context.getBean(GroupChatService.class);

                // Add loginGUI

                // if there is no entry in Identity table --> prompt login
                if(identityService.readIdentity() == null) {
                    String username = JOptionPane.showInputDialog(null,
                            "Enter username (Max: 16 Characters)",
                            "Chat Application",
                            JOptionPane.QUESTION_MESSAGE);

                    if(username == null || username.isEmpty() || username.length() > 16){
                        JOptionPane.showMessageDialog(null,
                                "Invalid Username",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Create a new identity for future logins
                    identityService.createIdentity();
                    contactService.createContact(identityService.readIdentity().getUserId(), username);

                    // Show new user id
                    JOptionPane.showMessageDialog(null,
                            "Your new user id is : " + identityService.readIdentity().getUserId(),
                            "Welcome New User!",
                            JOptionPane.INFORMATION_MESSAGE);

                }


                ClientGUI frame = null;

                // Create GUI
                try {
                    frame = new ClientGUI(identityService.readIdentity().getUserId(), context);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // Get StompClient for syncing
                MyStompClient myStompClient = frame.getMyStompClient();

                // Sync Client with all missing Messages, FriendRequests, GroupRequests, ChatFriends
                syncClient(myStompClient);

                // Load GUI and make it visible
                frame.loadGui();
                frame.setVisible(true);
                frame.applyShape();
                ClientGUI finalFrame = frame;
                SwingUtilities.invokeLater(() -> {
                    com.nexus.nexuschat.client.glass.swingacrylic.SwingAcrylic.processFrame(finalFrame, 120, 0xFFFFFF);
                    finalFrame.applyShape();
                });
            }
        });
    }

    // Sync client with missed messages/requests
    private static void syncClient(MyStompClient myStompClient) {
        // New client that will call REST API to sync according to id
        SyncClient syncClient = new SyncClient(identityService);

        SyncResponse syncResponse;

        // Call the REST API
        try {
            syncResponse = syncClient.findAllSyncInformation();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Sync failed", e);
        }

        if (syncResponse == null) {
            return;
        }

        // Retrieve all information
        List<Message> missedMessages = syncResponse.getMessages();
        List<FriendRequest> missedFriendRequests= syncResponse.getFriendRequests();
        List<GroupRequest> missedGroupRequests = syncResponse.getGroupRequests();
        List<ChatFriendPayload> missedChatFriends = syncResponse.getChatFriends();

        // Save all missed messages
        for(Message m : missedMessages) {
            messageService.storeMessage(m);
        }

        // Save all missed friend-requests
        for(FriendRequest f : missedFriendRequests){

            // ACCEPT --> you sent the friend request, friend accepted, you are saving it as contact
            if(f.getStat().equals("ACCEPT")){
                contactService.createContact(f.getReceiverId(), f.getUsername());

                continue;
            }
            // You received friend request --> save as friend request locally
            friendRequestService.storeFriendRequest(f);
        }


        // Save all missed group-requests
        for(GroupRequest g : missedGroupRequests) {
            groupRequestService.createGroupRequest(g);
        }

        // Save all chat friends
        for(ChatFriendPayload cfp : missedChatFriends) {
            // 1. Add user to chat (duplicates handled in service layer)
            chatMemberService.addMemberToChat(
                    cfp.getChatId(),
                    cfp.getSenderId()
            );

            // 2. Add contact (duplicates handled in service layer)
            contactService.createContact(
                    cfp.getSenderId(),
                    cfp.getReceiverName()
            );

            // 3. Send automatic friend request (same as WebSocket)
            myStompClient.sendFriendRequest(new FriendRequest(
                    UUID.randomUUID().toString(),
                    identityService.readIdentity().getUserId(),
                    cfp.getSenderId(),
                    contactService.retrieveContactById(identityService.readIdentity().getUserId()).getUsername(),
                    "ACCEPTED",
                    new Timestamp(System.currentTimeMillis())
            ));
        }

        // Logging information
        System.out.println("Sync complete: "
                + missedMessages.size() + " messages, "
                + missedFriendRequests.size() + " friend requests, "
                + missedGroupRequests.size() + " group requests");
    }
}
