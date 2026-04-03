package com.nexus.nexuschat.client;

import com.nexus.nexuschat.SQLitedatabase.model.*;
import com.nexus.nexuschat.SQLitedatabase.service.ChatMemberService;
import com.nexus.nexuschat.SQLitedatabase.service.ContactService;
import com.nexus.nexuschat.SQLitedatabase.service.GroupChatService;
import com.nexus.nexuschat.SQLitedatabase.service.IdentityService;
import com.nexus.nexuschat.client.listeners.MessageListener;
import com.nexus.nexuschat.pojo.ChatFriendPayload;
import com.nexus.nexuschat.pojo.GroupRequestPayload;
import com.nexus.nexuschat.pojo.Message;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.*;

public class MyStompSessionHandler extends StompSessionHandlerAdapter {

    private String username;
    private MessageListener messageListener;
    private GroupChatService groupChatService;
    private IdentityService identityService;
    private ChatMemberService chatMemberService;
    private ContactService contactService;
    private StompSession stompSession;
    private MyStompClient myStompClient;

    public MyStompSessionHandler(MessageListener messageListener,
                                 String username,
                                 GroupChatService groupChatService,
                                 IdentityService identityService,
                                 ChatMemberService chatMemberService,
                                 ContactService contactService,
                                 MyStompClient myStompClient) {
        this.username = username;
        this.messageListener = messageListener;
        this.groupChatService = groupChatService;
        this.identityService = identityService;
        this.chatMemberService = chatMemberService;
        this.contactService = contactService;
        this.myStompClient = myStompClient;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        this.stompSession = session;

        System.out.println("Client Connected");
        session.send("/app/connect", username);

        // For every groupChat the client is connected to, connect to that groupChat
        List<GroupChat> allChats = groupChatService.retrieveAllGroupChats();

        // Unique WebSocket endpoint for each
        Identity identity = identityService.readIdentity();

        // Client subscribes to a few routs, i.e. gets messages from these routes
        // 1. For every group chat in the database
        // 2. Friend requests
        // 3. Group requests
        // 4. "Group Created" --> For every new client that joins this group,
        // add them locally as contact and chat member

        // Obsolete --> online users, to be removed in future version, only used during development

        // GroupChats
        for(GroupChat chat : allChats){
            session.subscribe("/topic/messages/" + chat.getChatId(), new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return Message.class;
                }
                // Informs client of the expected payload type ("Message" class)

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {

                    try {
                        if (payload instanceof Message) {
                            Message message = (Message) payload;
                            messageListener.onMessageReceive(message);
                            System.out.println("Received message: " + message.getUser() + ": " + message.getMessage());
                        } else {
                            System.out.println("Received unexpected payload type: " + payload.getClass());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // Checks if of Message type, stores it as Message type if so

            });
            System.out.println("Client Subscribe to /topic/messages/" + chat.getChatId());
            // The two overwritten methods handle the payload received from the subscribed destination "/topic/messages"
        }

        // Friend Requests
        session.subscribe("/topic/friend-requests/" + identity.getUserId(), new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return FriendRequest.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {

                try {
                    if (payload instanceof FriendRequest) {
                        FriendRequest friendRequest = (FriendRequest) payload;
                        messageListener.onFriendRequestReceive(friendRequest);
                        System.out.println("Received FriendRequest: " + friendRequest.getUsername() + ": " + friendRequest.getRequestId());
                    } else {
                        System.out.println("Received unexpected payload type: " + payload.getClass());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Checks if of Message type, stores it as Message type if so

        });
        System.out.println("Client Subscribe to /topic/friend-requests/" + identity.getUserId());


        // Group Chat Requests
        session.subscribe("/topic/group-requests/" + identity.getUserId(), new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return GroupRequestPayload.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {

                try {
                    if (payload instanceof GroupRequestPayload) {
                        GroupRequestPayload groupRequestPayload = (GroupRequestPayload) payload;
                        messageListener.onGroupRequestReceive(groupRequestPayload);
                        System.out.println("Received GroupRequest: " + groupRequestPayload);
                    } else {
                        System.out.println("Received unexpected payload type: " + payload.getClass());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Checks if of Message type, stores it as Message type if so

        });
        System.out.println("Client Subscribe to /topic/group-requests/" + identity.getUserId());

        // Receive active users
        session.subscribe("/topic/users", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return new ArrayList<String>().getClass();
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try{
                    if(payload instanceof ArrayList){
                        ArrayList<String> activeUsers = (ArrayList<String>) payload;
                        messageListener.onActiveUsersUpdated(activeUsers);
                        System.out.println("Received active users: " + activeUsers);
                    } else {
                        System.out.println("Received unexpected payload type: " + payload.getClass());
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        System.out.println("Client Subscribe to /topic/users");

        // Accept all newly joined ChatMembers
        session.subscribe("/topic/group-created/" + identity.getUserId(), new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatFriendPayload.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                if (payload instanceof ChatFriendPayload) {
                    ChatFriendPayload chatFriend = (ChatFriendPayload) payload;

                    // Stores new chat member
                    chatMemberService.addMemberToChat(
                            chatFriend.getChatId(),
                            chatFriend.getSenderId()
                    );
                    System.out.println("Chat member added to group chat: ");

                    System.out.println(chatFriend.getSenderId().equals(identityService.readIdentity().getUserId()));
                    // stores new contact
                    contactService.createContact(
                            chatFriend.getSenderId(),
                            chatFriend.getReceiverName()
                    );
                    System.out.println("Contact processed");


                    // sends automatic friend request so sender also saves as contact
                    myStompClient.sendFriendRequest(new FriendRequest(
                            UUID.randomUUID().toString(),
                            identityService.readIdentity().getUserId(),
                            chatFriend.getSenderId(),                      // Sender will receive this friend request
                            contactService.retrieveContactById(identityService.readIdentity().getUserId()).getUsername(),
                            "ACCEPTED",
                            new Timestamp(System.currentTimeMillis())
                    ));

                    // Notify GUI
                    // messageListener.onGroupCreated(groupChat);
                }
            }
        });
        System.out.println("Client subscribed to " + "/topic/group-created/" + identity.getUserId());

        session.send("/app/request-users", "");
    }

    // Sub to a new group if joined in ClientGUI
    public void subToNewGroup(GroupChat groupChat){
        stompSession.subscribe("/topic/messages/" + groupChat.getChatId(), new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Message.class;
            }
            // Informs client of the expected payload type ("Message" class)

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {

                try {
                    if (payload instanceof Message) {
                        Message message = (Message) payload;
                        messageListener.onMessageReceive(message);
                        System.out.println("Received message: " + message.getUser() + ": " + message.getMessage());
                    } else {
                        System.out.println("Received unexpected payload type: " + payload.getClass());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Checks if of Message type, stores it as Message type if so

        });
        System.out.println("Client Subscribe to /topic/messages/" + groupChat.getChatId());
    }

    // Handle errors
    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        exception.printStackTrace();
    }

}
