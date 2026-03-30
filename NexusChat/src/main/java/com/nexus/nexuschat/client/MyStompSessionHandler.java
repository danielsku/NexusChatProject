package com.nexus.nexuschat.client;

import com.nexus.nexuschat.SQLitedatabase.model.FriendRequest;
import com.nexus.nexuschat.SQLitedatabase.model.GroupChat;
import com.nexus.nexuschat.SQLitedatabase.model.GroupRequest;
import com.nexus.nexuschat.SQLitedatabase.model.Identity;
import com.nexus.nexuschat.SQLitedatabase.service.GroupChatService;
import com.nexus.nexuschat.SQLitedatabase.service.IdentityService;
import com.nexus.nexuschat.client.listeners.MessageListener;
import com.nexus.nexuschat.pojo.GroupRequestPayload;
import com.nexus.nexuschat.pojo.Message;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MyStompSessionHandler extends StompSessionHandlerAdapter {

    private String username;
    private MessageListener messageListener;
    private GroupChatService groupChatService;
    private IdentityService identityService;
    private StompSession stompSession;

    public MyStompSessionHandler(MessageListener messageListener, String username, GroupChatService groupChatService, IdentityService identityService) {
        this.username = username;
        this.messageListener = messageListener;
        this.groupChatService = groupChatService;
        this.identityService = identityService;
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

        // Messages
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
            // Informs client of the expected payload type ("Message" class)

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
            // Informs client of the expected payload type ("Message" class)

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

        session.subscribe("/topic/group-created/" + identity.getUserId(), new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return GroupChat.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                if (payload instanceof GroupChat) {
                    GroupChat groupChat = (GroupChat) payload;

                    // Save locally
                    groupChatService.createGroupChat(
                            new HashSet<>(),
                            groupChat.getCreatedBy(),
                            groupChat.getGroupName(),
                            groupChat.getChatId()
                            );

                    // Subscribe to messages
                    subToNewGroup(groupChat);

                    // Notify GUI
                    messageListener.onGroupCreated(groupChat);
                }
            }
        });

        session.send("/app/request-users", "");
    }

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

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        exception.printStackTrace();
    }

}
