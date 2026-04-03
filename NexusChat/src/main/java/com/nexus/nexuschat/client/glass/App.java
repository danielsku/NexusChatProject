package com.nexus.nexuschat.client.glass;



import com.formdev.flatlaf.FlatDarkLaf;
import com.nexus.nexuschat.SQLitedatabase.AppConfig;
import com.nexus.nexuschat.SQLitedatabase.model.Contact;
import com.nexus.nexuschat.SQLitedatabase.model.FriendRequest;
import com.nexus.nexuschat.SQLitedatabase.model.GroupRequest;
import com.nexus.nexuschat.SQLitedatabase.model.Message;
import com.nexus.nexuschat.SQLitedatabase.service.*;
import com.nexus.nexuschat.http.SyncClient;
import com.nexus.nexuschat.pojo.SyncResponse;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class App {

    private static IdentityService identityService;
    private static ContactService contactService;
    private static MessageService messageService;
    private static FriendRequestService friendRequestService;
    private static GroupRequestService groupRequestService;

    public static void main(String[] args){
        //UIManager.put("defaultFont", new Font("Avenir", Font.PLAIN, 14));
        FlatDarkLaf.setup();
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                com.nexus.nexuschat.client.glass.swingacrylic.SwingAcrylic.prepareSwing();

                AnnotationConfigApplicationContext context =
                        new AnnotationConfigApplicationContext(AppConfig.class);

                identityService = context.getBean(IdentityService.class);
                contactService = context.getBean(ContactService.class);
                messageService = context.getBean(MessageService.class);
                friendRequestService = context.getBean(FriendRequestService.class);
                groupRequestService = context.getBean(GroupRequestService.class);

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

                    identityService.createIdentity();
                    contactService.createContact(identityService.readIdentity().getUserId(), username);

                    JOptionPane.showMessageDialog(null,
                            "Your new user id is : " + identityService.readIdentity().getUserId(),
                            "Welcome New User!",
                            JOptionPane.INFORMATION_MESSAGE);

                }


                // Sync Client with all missing Messages, FriendRequests, GroupRequests
                //syncClient();

                ClientGUI frame = null;

                try {
                    frame = new ClientGUI(contactService.retrieveContactById(identityService.readIdentity().getUserId()).getUsername(), context);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

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

    private static void syncClient() {
        SyncClient syncClient = new SyncClient(identityService);

        SyncResponse syncResponse;
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

        // TODO: Create a service method that saves all without chance of duplicates

        // Save all missed messages
        for(Message m : missedMessages) {
            messageService.storeMessage(m);
        }

        // Save all missed friend-requests
        for(FriendRequest f : missedFriendRequests){

            if(f.getStat().equals("ACCEPT")){
                contactService.createContact(f.getReceiverId(), f.getUsername());

                continue;
            }

            friendRequestService.storeFriendRequest(f);
        }

        // Save all missed group-requests
        for(GroupRequest g : missedGroupRequests) {
            groupRequestService.createGroupRequest(g);
        }

        System.out.println("Sync complete: "
                + missedMessages.size() + " messages, "
                + missedFriendRequests.size() + " friend requests, "
                + missedGroupRequests.size() + " group requests");
    }
}
