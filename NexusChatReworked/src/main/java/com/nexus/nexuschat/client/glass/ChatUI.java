//package com.nexus.nexuschat.client.glass;
//
//import com.nexus.nexuschat.pojo.Message;
//
//import javax.swing.*;
//import java.awt.*;
//
////sample message class to act as our message entity, this logic would be implmented in our gui portion and the
////different data in this example would be taken from out database using message_ids, sender_ids, receiver_ids
////and so on
//
//
////class to create a bubble panel for each message using paint component from labs, not exactly sure whats going on
////but all i know is that these specific values create the rounded edges
//
//public class ChatUI {
//
//    //create panel and stuff
//    private JPanel chatContainer;
//
//    public ChatUI() {
//        JFrame frame = new JFrame("Chat Bubble Demo");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(400, 500);
//
//        chatContainer = new JPanel();
//        chatContainer.setLayout(new BoxLayout(chatContainer, BoxLayout.Y_AXIS));
//        chatContainer.setBackground(Color.WHITE);
//
//        JScrollPane scrollPane = new JScrollPane(chatContainer);
//        scrollPane.setBorder(null);
//
//        frame.add(scrollPane);
//
//        addMessage(new Message("Matthew", "This is a message!."), true);
//        addMessage(new Message("Daniel", "Nice, looks really cool :)"), false);
//        addMessage(new Message("Matthew",
//                "Yep, and it wraps long text automatically. Try making this message really long to see how it wraps the message!"),
//                true);
//
//        frame.setVisible(true);
//    }
//
//    //updated version of create message you gave me to invoke the bubble message
//    private JPanel createChatMessage(Message message, boolean isSender) {
//
//        JPanel wrapper = new JPanel(new FlowLayout(isSender ? FlowLayout.RIGHT : FlowLayout.LEFT));
//        wrapper.setOpaque(false);
//        wrapper.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
//
//        BubblePanel bubble = new BubblePanel(
//                isSender ? new Color(0, 132, 255) : new Color(230, 230, 230));
//        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
//
//        JLabel usernameLabel = new JLabel(message.getUser());
//        usernameLabel.setFont(new Font("Lato", Font.BOLD, 12));
//        usernameLabel.setForeground(isSender ? Color.WHITE : Color.BLACK);
//
//        JLabel messageBody = new JLabel(
//                "<html><body style='width: 200px'>" + message.getMessage() + "</body></html>");
//        messageBody.setFont(new Font("Lato", Font.PLAIN, 14));
//        messageBody.setForeground(isSender ? Color.WHITE : Color.BLACK);
//
//        bubble.add(usernameLabel);
//        bubble.add(Box.createVerticalStrut(5));
//        bubble.add(messageBody);
//
//        wrapper.add(bubble);
//
//        return wrapper;
//    }
//
//    //add message to gui
//    private void addMessage(Message message, boolean isSender) {
//        chatContainer.add(createChatMessage(message, isSender));
//        chatContainer.revalidate();
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(ChatUI::new);
//    }
//}