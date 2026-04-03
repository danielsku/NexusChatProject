package com.nexus.nexuschat.client;

import com.nexus.nexuschat.SQLitedatabase.model.FriendRequest;
import com.nexus.nexuschat.SQLitedatabase.model.GroupRequest;
import com.nexus.nexuschat.SQLitedatabase.service.ChatMemberService;
import com.nexus.nexuschat.SQLitedatabase.service.ContactService;
import com.nexus.nexuschat.SQLitedatabase.service.GroupChatService;
import com.nexus.nexuschat.SQLitedatabase.service.IdentityService;
import com.nexus.nexuschat.client.listeners.MessageListener;
import com.nexus.nexuschat.pojo.GroupRequestPayload;
import com.nexus.nexuschat.pojo.Message;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyStompClient {

    private final GroupChatService groupChatService;
    private final IdentityService identityService;
    private final ChatMemberService chatMemberService;
    private final ContactService contactService;
    private StompSession session;
    private MyStompSessionHandler sessionHandler;
    // Allows us to connect to STOMP servers
    // Has methods that allows 1. Send messages; 2. Subscribe to Routes; 3. Manage the Connection

    private String username;
    private MessageListener messageListener;

    public MyStompClient(MessageListener messageListener,
                         String username,
                         GroupChatService groupChatService,
                         IdentityService identityService,
                         ChatMemberService chatMemberService,
                         ContactService contactService) throws ExecutionException, InterruptedException {

        this.groupChatService = groupChatService;
        this.messageListener = messageListener;
        this.username = username;
        this.identityService = identityService;
        this.chatMemberService = chatMemberService;
        this.contactService = contactService;

        List<Transport> transports = new ArrayList<>();
        // Transport - web communication - method or protocol used to transfer data between client <-> server

        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        // Allows SockJS ~ to communicate w/ websocket server

        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        // SockJS can have issues w/ websockets, so we need WebSocketStompClient here
        // Now SockJs can use STOMP protocols

        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        // Serialize/Deserialize information to be able to send to/ receive from websockets

        this.sessionHandler = new MyStompSessionHandler(messageListener,
                username,
                groupChatService,
                identityService,
                chatMemberService,
                contactService,
                this
        );
        // Instantiate the StompSessionHandler (pass in the username)

        String url = "ws://4.206.211.43:8080/ws"; // Use ws:// for WebSocket
        // url to connect to our websocket

        session = stompClient.connectAsync(url, this.sessionHandler).get();
    }

    //Send messages
    public void sendMessage(Message message) {
        try {
            session.send("/app/messages", message);
            System.out.println("Message Sent: " + message.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Send friend requests
    public void sendFriendRequest(FriendRequest friendRequest){
        try{
            session.send("/app/friend-requests", friendRequest);
            System.out.println("Sent friend request " + friendRequest);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Send group reqeusts
    public void sendGroupRequest(GroupRequestPayload groupRequestPayload){
        try{
            session.send("/app/group-requests", groupRequestPayload);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Sends to server to indicated disconnected user
    public void disconnectUser(String username) {
        session.send("/app/disconnect", username);
        System.out.println("Disconnect User: " + username);
    }

    // Used in external classes (ClientGUI) to send messages manually when needed
    public MyStompSessionHandler getSessionHandler() {
        return sessionHandler;
    }

    
}
