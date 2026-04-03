
package com.nexus.nexuschat.client.glass;

import com.nexus.nexuschat.SQLitedatabase.model.*;
import com.nexus.nexuschat.SQLitedatabase.service.*;
import com.nexus.nexuschat.client.MyStompSessionHandler;
import com.nexus.nexuschat.client.listeners.MessageListener;
import com.nexus.nexuschat.client.MyStompClient;
import com.nexus.nexuschat.client.glass.listrenderer.ChatListRenderer;
import com.nexus.nexuschat.client.glass.listrenderer.CheckBoxListRenderer;
import com.nexus.nexuschat.client.glass.listrenderer.ContactListRenderer;
import com.nexus.nexuschat.http.FriendRequestClient;
import com.nexus.nexuschat.http.GroupRequestClient;
import com.nexus.nexuschat.http.GroupRequestMemberClient;
import com.nexus.nexuschat.pojo.GroupMember;
import com.nexus.nexuschat.pojo.GroupRequestPayload;
import com.nexus.nexuschat.pojo.Message;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class ClientGUI extends JFrame implements MessageListener {

    private PanelTransparent messagePanel;

    private MyStompClient myStompClient;
    private String username;

    private Message receivedMessage;
    private JScrollPane scrollPane;

    private GroupChatService groupChatService;
    private ContactService contactService;
    private IdentityService identityService;
    private MessageService messageService;
    private FriendRequestService friendRequestService;
    private ChatMemberService chatMemberService;
    private GroupRequestService groupRequestService;

    private PanelTransparent chatListPanel;
    private JPanel leftPanel;

    private CardLayout infoCards;
    private PanelTransparent infoPanel;

    private CardLayout centerCards;
    private JPanel centerPanel;

    private DefaultListModel<GroupChat> chatListModel;
    private JList<GroupChat> chatList;


    private String currentChatId;

    private DefaultListModel<Contact> contactListModel;
    private JList<Contact> contactList;

    private Timestamp loadedChatsTime;
    private String loadedLastMessageId;
    private boolean isLoadingOlderMessages;

    private FriendRequestClient frClient;
    private GroupRequestClient grClient;
    private GroupRequestMemberClient grmClient;

    private MyStompSessionHandler myStompSessionHandler;

    private JPanel friendRequestPanel;
    private DefaultListModel<SelectableContact> memberListModel;

    private JPanel groupRequestPanel;

    public ClientGUI(String username, ApplicationContext context) throws ExecutionException, InterruptedException {

        super("Nexus Chat Messenger: " + username);

        // This is NOT the username per se, App.java passes the userId instead
        this.username = username;
        this.groupChatService = context.getBean(GroupChatService.class);
        this.contactService = context.getBean(ContactService.class);
        this.identityService = context.getBean(IdentityService.class);
        this.messageService = context.getBean(MessageService.class);
        this.friendRequestService = context.getBean(FriendRequestService.class);
        this.chatMemberService = context.getBean(ChatMemberService.class);
        this.groupRequestService = context.getBean(GroupRequestService.class);

        // Get myStompClient to manually sned
        myStompClient = new MyStompClient(
                this,
                identityService.readIdentity().getUserId(),
                groupChatService,
                identityService,
                chatMemberService,
                contactService
        );

        // get session handler to subscribe to webscokets manually
        myStompSessionHandler = myStompClient.getSessionHandler();

        // Create and configure all clients that will communicate via REST API
        String baseUrl = "http://4.206.211.43:8080";
        frClient = new FriendRequestClient(identityService, baseUrl);
        grClient = new GroupRequestClient(identityService, baseUrl);
        grmClient = new GroupRequestMemberClient(identityService, baseUrl);
        // Read from the Relay Server all missed messages via REST APIs
    }

    // Load the GUI
    public void loadGui(){

        // Window closes, notify server
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                myStompClient.disconnectUser(username); // Actually disconnects the userId, not the username
            }
        });

        // For Swing Acrylic (glass morphism) must be undecorated
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1218, 685);
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0));

        setLayout(new BorderLayout());

        // update shape on resize
        addWindowStateListener(e -> {
            if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                setShape(null); // remove rounding when maximized
            } else {
                applyShape(); // restore rounding
            }
        });

        // When component is resized, redraw the rounded corneds
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 40, 40));
            }
        });

        // New resize mechanic with Acrylic
        FixedEdgeResizeListener resizeListener = new FixedEdgeResizeListener(this);
        addMouseListener(resizeListener);
        addMouseMotionListener(resizeListener);

        // Add title bar
        JPanel titleBar = addTitleBar(resizeListener);


        add(titleBar, BorderLayout.NORTH);

        // Add components
        addGuiComponents();
    }

    private JPanel addTitleBar(FixedEdgeResizeListener resizeListener) {

        // Create title bar JPanel
        JPanel titleBar = new JPanel();
        titleBar.setLayout(new BorderLayout());
        titleBar.setOpaque(false); // optional if you want transparency
        titleBar.setPreferredSize(new Dimension(getWidth(), 40)); // semi-transparent

        JLabel title = new JLabel("  My App");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleBar.add(title, BorderLayout.WEST);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttons.setOpaque(false);

        HoverButton close = new HoverButton("✕", () -> {
            int option = JOptionPane.showConfirmDialog(
                    ClientGUI.this,
                    "Are you sure?",
                    "Exit",
                    JOptionPane.YES_NO_OPTION
            );

            if(option == JOptionPane.YES_OPTION){
                myStompClient.disconnectUser(username); // Actually disconnects the userId, not the username
                ClientGUI.this.dispose(); // Could do it explicitly w/ ClientGUI.this.dispose();
            }
        }, new Color(255, 0, 0, 150));

        HoverButton minimize = new HoverButton("—", () -> setState(JFrame.ICONIFIED), new Color(128, 128, 128, 50));

        HoverButton maximize = new HoverButton("☐", () -> {
            if (getExtendedState() == JFrame.MAXIMIZED_BOTH) setExtendedState(JFrame.NORMAL);
            else setExtendedState(JFrame.MAXIMIZED_BOTH);
        }, new Color(128, 128, 128, 50));

        buttons.add(minimize);
        buttons.add(maximize);
        buttons.add(close);

        titleBar.add(buttons, BorderLayout.EAST);


        final Point[] dragStart = {new Point()};

        titleBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // Only start moving if cursor is default (not over an edge/corner)
                if (getCursor().getType() == Cursor.DEFAULT_CURSOR) {
                    dragStart[0] = e.getPoint();
                }
            }
        });

        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                // Only move frame if cursor is default (not over an edge/corner)
                if (getCursor().getType() == Cursor.DEFAULT_CURSOR && dragStart[0] != null) {
                    Point curr = e.getLocationOnScreen();
                    setLocation(curr.x - dragStart[0].x, curr.y - dragStart[0].y);
                }
            }
        });

        titleBar.addMouseListener(resizeListener);
        titleBar.addMouseMotionListener(resizeListener);
        return titleBar;
    }

    private void addGuiComponents(){
        addLeftPanel();

        addLeftControlPanel();
        addChatList();

        addCenterPanel();
        addMessageFeed();
        addFriendList();
        addGroupRequests();

        addChatUserList();
    }

    private void addLeftPanel() {
        leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setBackground(Utilities.TRANSPARENT_COLOR);

        add(leftPanel, BorderLayout.WEST);
    }

    private void addLeftControlPanel() {
        PanelTransparent controlPanel = new PanelTransparent(Utilities.SECONDARY_COLOR, true);
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setPreferredSize(new Dimension(50, getHeight()));

        ImageIcon friendImage = new ImageIcon(getClass().getResource("/friend.png"));
        Image scaledImage = friendImage.getImage().getScaledInstance(40,40, Image.SCALE_SMOOTH);
        ImageIcon friendIcon = new ImageIcon(scaledImage);

        JButton friendButton = new JButton(friendIcon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (getModel().isPressed()) {
                    g2.setColor(new Color(100, 100, 100, 200));
                } else if (getModel().isRollover()) {
                    g2.setColor(Utilities.TRANSPARENT_COLOR);
                }else {
                    g2.setColor(new Color(Utilities.SECONDARY_COLOR.getRed(),
                            Utilities.SECONDARY_COLOR.getGreen(),
                            Utilities.SECONDARY_COLOR.getBlue(),
                            0));
                }
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        friendButton.setBorderPainted(false);
        friendButton.setFocusPainted(false);
        friendButton.setContentAreaFilled(false);

        friendButton.addActionListener(e -> {
            System.out.println("Button clicked!");
            centerCards.show(centerPanel, "FriendList");
            infoCards.show(infoPanel, "FriendButtons");
            revalidate();
            repaint();
        });

        ImageIcon groupImage = new ImageIcon(getClass().getResource("/group.png"));
        Image scaledImageB = groupImage.getImage().getScaledInstance(40,40, Image.SCALE_SMOOTH);
        ImageIcon groupIcon = new ImageIcon(scaledImageB);

        JButton groupButton = new JButton(groupIcon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (getModel().isPressed()) {
                    g2.setColor(new Color(100, 100, 100, 200));
                } else if (getModel().isRollover()) {
                    g2.setColor(Utilities.TRANSPARENT_COLOR);
                } else {
                    g2.setColor(new Color(Utilities.SECONDARY_COLOR.getRed(),
                            Utilities.SECONDARY_COLOR.getGreen(),
                            Utilities.SECONDARY_COLOR.getBlue(),
                            0));
                }
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        groupButton.setBorderPainted(false);
        groupButton.setFocusPainted(false);
        groupButton.setContentAreaFilled(false);

        groupButton.addActionListener(e -> {
            System.out.println("Button clicked!");
            centerCards.show(centerPanel, "GroupRequest");
            infoCards.show(infoPanel, "GroupButtons");
            revalidate();
            repaint();
        });

        friendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                revalidate();
                repaint();
            }
        });

        groupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                revalidate();
                repaint();
            }
        });



        controlPanel.add(friendButton);
        controlPanel.add(groupButton);

        leftPanel.add(controlPanel, BorderLayout.WEST);
    }

    private void onChatSelected(GroupChat chat) {
        boolean isSameChat = chat.getChatId().equals(currentChatId);

        centerCards.show(centerPanel, "MessageFeed");

        // TODO: Modify this to show specific group chat information
        infoCards.show(infoPanel, "MessageInfo");
        revalidate();
        repaint();

        if(isSameChat){
            return;
        }

        currentChatId = chat.getChatId();

        System.out.println("Switched to: " + chat.getGroupName());

        messagePanel.removeAll();

        // NEXT: load messages for this chat
        loadMessages(new Timestamp(System.currentTimeMillis()), null);
        revalidate();
        repaint();

        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        });
    }

    private void addChatList() {
        chatListPanel = new PanelTransparent(Utilities.SECONDARY_COLOR, true);
        chatListPanel.setLayout(new BorderLayout());
        chatListPanel.setPreferredSize(new Dimension(200, getHeight()));

        chatListModel = new DefaultListModel<>();
        chatList = new JList<>(chatListModel);

        chatList.setCellRenderer(new ChatListRenderer());
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chatList.setBackground(Utilities.TRANSPARENT_COLOR);

        chatList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = chatList.locationToIndex(e.getPoint());
                if (index != -1) {
                    chatList.setSelectedIndex(index); // keep highlight
                    GroupChat selected = chatListModel.get(index);
                    onChatSelected(selected);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(chatList);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        chatListPanel.add(scrollPane, BorderLayout.CENTER);

        leftPanel.add(chatListPanel, BorderLayout.CENTER);

        loadChats();
    }

    private void loadChats() {
        List<GroupChat> chats = groupChatService.retrieveAllGroupChats();

        chatListModel.clear();

        for(GroupChat chat : chats){
            chatListModel.addElement(chat);
        }
    }

    private void addCenterPanel() {

        // Add mainCenterPanel
        JPanel mainCenterPanel = new JPanel();
        mainCenterPanel.setLayout(new BorderLayout());
        mainCenterPanel.setBackground(Utilities.TRANSPARENT_COLOR);

        // Add infoPanel
        infoPanel = new PanelTransparent(Utilities.SECONDARY_COLOR, true);
        infoCards = new CardLayout();
        infoPanel.setLayout(infoCards);
        infoPanel.setPreferredSize(new Dimension(mainCenterPanel.getWidth(), 100));
        mainCenterPanel.add(infoPanel, BorderLayout.NORTH);

        // Add centerPanel
        centerPanel = new JPanel();
        centerCards = new CardLayout();
        centerPanel.setLayout(centerCards);
        centerPanel.setBackground(Utilities.TRANSPARENT_COLOR);
        mainCenterPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainCenterPanel, BorderLayout.CENTER);
    }

    private void addMessageFeed() {
        PanelTransparent infoMessage = new PanelTransparent(Utilities.TRANSPARENT_COLOR, false);
        infoPanel.add(infoMessage, "MessageInfo");

        PanelTransparent messageFeedPanel = new PanelTransparent(new Color(0,0,0,255), false);
        messageFeedPanel.setLayout(new BorderLayout());

        messagePanel = new PanelTransparent(Utilities.TRANSPARENT_COLOR, false);
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(messagePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            if (isLoadingOlderMessages) return;

            // User scrolls up to load latest messages in the group chat
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            int max = vertical.getMaximum();
            int extent = vertical.getModel().getExtent();
            int value = vertical.getValue();

            if (!isLoadingOlderMessages && currentChatId != null && value < extent/4) {
                isLoadingOlderMessages = true;

                int oldMax = vertical.getMaximum();

                loadMessages(loadedChatsTime, loadedLastMessageId);

                SwingUtilities.invokeLater(() -> {
                    int newMax = vertical.getMaximum();
                    int delta = newMax - oldMax;
                    //vertical.setValue(newMax - oldMax);
                    vertical.setValue(value + delta);

                    isLoadingOlderMessages = false;
                });
            }

        });
        scrollPane.getViewport().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                revalidate();
                repaint();
            }
        });
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        messageFeedPanel.add(scrollPane, BorderLayout.CENTER);



        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(Utilities.TRANSPARENT_COLOR);
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField inputField = new JTextField(){
            private final int padding = 12;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setComposite(AlphaComposite.Clear);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setComposite(AlphaComposite.SrcOver);

                Shape round = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(58, 108, 108, 85)); // semi-transparent fill
                g2.fill(round);

                g2.draw(round);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public Insets getInsets() {
                // Return insets for the caret and text
                return new Insets(padding, padding, padding, padding);
            }
        };
        inputField.setOpaque(false);
        inputField.setBorder(BorderFactory.createEmptyBorder());
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if((e.getKeyChar() == KeyEvent.VK_ENTER) && (!inputField.getText().isEmpty())){
                    String input = inputField.getText();

                    inputField.setText("");

                    // Save the message into the database

                    // 1. Create message body

                    Timestamp currentTime = new Timestamp(System.currentTimeMillis());

                    // 2. Get the list of all users in the group chat to be sent to

                    List<ChatMember> membersList = chatMemberService.retrieveMembersByChat(currentChatId);

                    // Remove self from list (CHECK THIS)
                    membersList.remove(new ChatMember(
                            currentChatId,
                            identityService.readIdentity().getUserId()
                    ));

                    // Get list of all memberIds (used to check if online/offline later)
                    List<String> memberIdList = new ArrayList<>();

                    for(ChatMember cm : membersList){
                        memberIdList.add(cm.getUserId());
                    }

                    // When message is sent, if websocket doesn't work -> save to MySQL database
                    myStompClient.sendMessage(new Message(
                            contactService.retrieveContactById(identityService.readIdentity().getUserId()).getUsername(),
                            input,
                            identityService.readIdentity().getUserId(),
                            currentChatId,
                            currentTime,
                            memberIdList
                    ));
                }
            }
        });
        inputField.setBackground(Utilities.SECONDARY_COLOR);
        inputField.setForeground(Utilities.TEXT_COLOR);
        inputField.setFont(new Font("Lato", Font.PLAIN, 16 ));
        inputField.setPreferredSize(new Dimension(inputPanel.getWidth(), 50));
        inputPanel.add(inputField, BorderLayout.CENTER);
        messageFeedPanel.add(inputPanel, BorderLayout.SOUTH);;

        centerPanel.add("MessageFeed", messageFeedPanel);
    }

    private JPanel createChatMessage(Message message, String userId){
        boolean isSender = contactIsSender(userId);

        PanelTransparent wrapper = new PanelTransparent(new FlowLayout(isSender ? FlowLayout.RIGHT : FlowLayout.LEFT));
        wrapper.setOpaque(false);
        wrapper.setBackground(Utilities.TRANSPARENT_COLOR);
        wrapper.setTransparent(0);
        wrapper.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        BubblePanel bubble = new BubblePanel(
                isSender ? new Color(0, 132, 255, 200) : new Color(150, 150, 150, 200));
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));

        JLabel usernameLabel = new JLabel(message.getUser());
        usernameLabel.setFont(new Font("Lato", Font.BOLD, 12));
        usernameLabel.setForeground(Color.WHITE);

        JLabel messageBody = new JLabel(
                "<html><body style='width: 200px'>" + message.getMessage() + "</body></html>");
        messageBody.setFont(new Font("Lato", Font.PLAIN, 14));
        messageBody.setForeground(Color.WHITE);

        bubble.add(usernameLabel);
        bubble.add(Box.createVerticalStrut(5));
        bubble.add(messageBody);

        wrapper.add(bubble);

        return wrapper;
    }

    private void loadMessages(Timestamp timeloaded, String lastMessageId) {
        List<com.nexus.nexuschat.SQLitedatabase.model.Message> latestMessages = messageService.loadLatestMessages(currentChatId, 20, timeloaded, lastMessageId);

        //Collections.reverse(latestMessages);

        loadedChatsTime = latestMessages.get(latestMessages.size() - 1).getSentAt();
        loadedLastMessageId = latestMessages.get(latestMessages.size() - 1).getmId();

        for(com.nexus.nexuschat.SQLitedatabase.model.Message message : latestMessages){
            // Convert to the other message type
            Message entry = new Message(
                    contactService.retrieveContactById(message.getUserId()).getUsername(),
                    message.getContent(),
                    contactService.retrieveContactById(message.getUserId()).getContactId(),
                    currentChatId,
                    message.getSentAt(),
                    null        // List of all users that the message needs to send to, not needed for GUI
            );

            System.out.println(entry);
            // load it into the screen
            messagePanel.add(createChatMessage(entry, message.getUserId()), 0);

        }
    }

    private void addGroupRequests() {
        // Add the menu for Group Chats (Approve/Decline and create requests)

        PanelTransparent mainGroupPanel = new PanelTransparent(Utilities.TRANSPARENT_COLOR, false);
        CardLayout groupCards = new CardLayout();
        mainGroupPanel.setLayout(groupCards);

        // Pending Panel
        PanelTransparent pendingPanel = new PanelTransparent(Utilities.TRANSPARENT_COLOR, false);
        pendingPanel.setLayout(new BorderLayout());
            // Set up scrollpane and load all group requests from database
        JScrollPane groupRequestScrollPane = new JScrollPane(loadGroupRequests());
        groupRequestScrollPane.setBorder(null);
        groupRequestScrollPane.setOpaque(false);
        groupRequestScrollPane.getViewport().setOpaque(false);
        groupRequestScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        groupRequestScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        groupRequestScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        groupRequestScrollPane.getVerticalScrollBar().addAdjustmentListener(e ->{
            revalidate();
            repaint();
        });
            // Add to main group panel
        pendingPanel.add(groupRequestScrollPane, BorderLayout.CENTER);
        mainGroupPanel.add("Pending", pendingPanel);

        // Create Panel
        PanelTransparent createPanel = new PanelTransparent(Utilities.TRANSPARENT_COLOR, false);
        createPanel.setLayout(new BorderLayout());
            // Input field to create group chat name
        JTextField groupNameInput = new JTextField();
            // ListModel for all members to add to new group chat
        memberListModel = new DefaultListModel<>();
            // JList to select potential members
        JList<SelectableContact> memberList = new JList<SelectableContact>(memberListModel);

        memberList.setCellRenderer(new CheckBoxListRenderer());
            // Add the function to select members
        memberList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = memberList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    SelectableContact sc = memberListModel.get(index);
                    sc.setSelected(!sc.isSelected());
                    memberList.repaint(memberList.getCellBounds(index, index));
                }
            }
        });

            // Add a JScrollPane for all potential member
        JScrollPane memberScrollPane = new JScrollPane(memberList);
        memberScrollPane.setBorder(null);
        memberScrollPane.setOpaque(false);
        memberScrollPane.getViewport().setOpaque(false);
        memberScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        memberScrollPane.getVerticalScrollBar().addAdjustmentListener(e ->{
            revalidate();
            repaint();
        });
            // Load all potential members into the model
        loadSelectableContacts(memberListModel);
            // Button to create a new group chat
        JButton createChatBtn = new JButton("Create Group Chat");
        createChatBtn.addActionListener(e -> {
            String chatName = groupNameInput.getText().trim();

            // Repeat prompting input until group chat name is not empty
            if(chatName.isEmpty()){
                JOptionPane.showMessageDialog(
                        null,
                        "Group Chat name can't be empty",
                        "Error : Empty Group Chat name",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            groupNameInput.setText("");

            // Get list of all selected contacts, excluding local user
            List<Contact> selectedContacts = new ArrayList<>();
            String localUserId = identityService.readIdentity().getUserId();
            String localUsername = contactService.retrieveContactById(identityService.readIdentity().getUserId()).getUsername();

            for (int i = 0; i < memberListModel.getSize(); i++) {
                SelectableContact sc = memberListModel.getElementAt(i);
                if (sc.isSelected() && !sc.getContact().getContactId().equals(localUserId)) {
                    selectedContacts.add(sc.getContact());
                }
            }

            // If no member is selected, can't create group chat
            if (selectedContacts.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Select at least one member");
                return;
            }

            // Create GroupMember list
            List<GroupMember> selectedMembers = new ArrayList<>();

            // Add selected contacts with stat = PENDING
            String chatId = UUID.randomUUID().toString(); // generate chatId

            // Every selected contact is added to the list
            for (Contact c : selectedContacts) {
                selectedMembers.add(new GroupMember(
                        chatId,
                        c.getContactId(),
                        c.getUsername(),
                        "PENDING"
                ));
            }

            // Add local user, stat = ACCEPTED
            selectedMembers.add(new GroupMember(
                    chatId,
                    localUserId,
                    localUsername,
                    "ACCEPTED"
            ));

            // Create a set of all members to send via payload
            Set<Contact> initialMembers = new HashSet<>();

            // Add all selected members
            for(GroupMember gm : selectedMembers){
                Contact member = contactService.retrieveContactById(gm.getReceiverId());
                initialMembers.add(member);
            }

            // Create group chat locally
            GroupChat joinedGroup = groupChatService.createGroupChat(
                    initialMembers, // This is decoupled
                    identityService.readIdentity().getUserId(),
                    chatName,
                    chatId
            );

            // Not needed anymore, every member will add themselves to every member via WebSocket now
//            for(Contact c : initialMembers){
//                chatMemberService.addMemberToChat(joinedGroup.getChatId(), c.getContactId());
//            }

            // Add self to database
            chatMemberService.addMemberToChat(joinedGroup.getChatId(), identityService.readIdentity().getUserId());

            // Now create the payload with members
            GroupRequestPayload myGroupRequest = new GroupRequestPayload(
                    localUserId,    // Sender
                    chatId,         // The chat ID
                    chatName,       // The chat name
                    selectedMembers // All members of the group
            );

            // Connect self to this new group chat
            myStompClient.getSessionHandler().subToNewGroup(joinedGroup);

            // Update GUI to add this new group
            chatListModel.addElement(joinedGroup);
            chatList.revalidate();
            chatList.repaint();

            // Uncheckmark all members
            for (int i = 0; i < memberListModel.size(); i++) {
                memberListModel.get(i).setSelected(false);
            }
            memberList.repaint();

            // Send to server so that all group members receive it
            myStompClient.sendGroupRequest(myGroupRequest);
        });

        createPanel.add(memberScrollPane, BorderLayout.CENTER);
        createPanel.add(createChatBtn, BorderLayout.SOUTH);
        createPanel.add(groupNameInput, BorderLayout.NORTH);

        mainGroupPanel.add("Create", createPanel);

        // add buttons
        JButton pendingBtn = new JButton("Pending");
        pendingBtn.addActionListener(e->{
            groupCards.show(mainGroupPanel, "Pending");
        });

        JButton createBtn = new JButton("Create");
        createBtn.addActionListener(e->{
            groupCards.show(mainGroupPanel, "Create");
        });

        PanelTransparent groupBtnPanel = new PanelTransparent(Utilities.TRANSPARENT_COLOR, true);
        groupBtnPanel.add(pendingBtn);
        groupBtnPanel.add(createBtn);
        infoPanel.add("GroupButtons", groupBtnPanel);

        centerPanel.add("GroupRequest", mainGroupPanel);
    }

    private JPanel loadGroupRequests() {
        groupRequestPanel = new JPanel();
        groupRequestPanel.setLayout(new BoxLayout(groupRequestPanel, BoxLayout.Y_AXIS));

        List<GroupRequest> allGroupRequests = groupRequestService.getAllGroupRequests();

        for(GroupRequest f : allGroupRequests){
            JPanel groupRequestEntry = createGroupRequestEntry(f);
            groupRequestPanel.add(groupRequestEntry);
        }

        return groupRequestPanel;
    }

    private JPanel createGroupRequestEntry(GroupRequest f) {
        JPanel groupRequestEntry = new JPanel();
        groupRequestEntry.setLayout(new BorderLayout());

        JLabel chatNameLabel = new JLabel(f.getChatName());
        JLabel userIdLabel = new JLabel(f.getSenderId());

        JButton acceptButton = new JButton("Accept");
        JButton declineButton = new JButton("Decline");

        // When user accepts a group Chat
        acceptButton.addActionListener(e -> {

            List<GroupMember> allMembers;
            // Get all members of a group chat
            try {
                allMembers = grmClient.getAll(f);
                System.out.println("members : " + allMembers);
            } catch (IOException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }

            // Update status to ACCEPTED, call REST API to server to update that
            try {
                grmClient.update(new GroupMember(
                        f.getRequestId(),
                        f.getReceiverId(),
                        contactService.retrieveContactById(identityService.readIdentity().getUserId()).getUsername(),
                        "ACCEPTED"
                ));
            } catch (IOException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }

            // All new group members to save into contacts
            Set<Contact> groupMembersToSave = new HashSet<>();

            // Add all members who have accepted locally
            for (GroupMember g : allMembers) {
                System.out.println(g.getStat().equals("ACCEPTED"));
                if(g.getStat().equals("ACCEPTED")) {
                    contactService.createContact(
                            g.getReceiverId(),
                            g.getUsername()
                    );
                    groupMembersToSave.add(new Contact(g.getReceiverId(), g.getUsername(), new Timestamp(System.currentTimeMillis())));
                }
            }
            System.out.println("All group members : " + groupMembersToSave);

            // Save this new group chat to the database
            GroupChat joinedGroup = groupChatService.createGroupChat(
                    groupMembersToSave, // Obsolete, to be refactored
                    f.getSenderId(),
                    f.getChatName(),
                    f.getRequestId() // <--- This is it, my destiny (This fix saved my group chats, LET IT BE KNOW!)
            );

            // Save all chat members to this group
            for(Contact c : groupMembersToSave){
                chatMemberService.addMemberToChat(joinedGroup.getChatId(), c.getContactId());
            }

            // Add self to the new group chat
            chatMemberService.addMemberToChat(joinedGroup.getChatId(), identityService.readIdentity().getUserId());

            // Subscribe to this new group
            myStompClient.getSessionHandler().subToNewGroup(joinedGroup);

            groupRequestService.deleteGroupRequest(f.getRequestId());

            chatListModel.addElement(joinedGroup);
            chatList.setSelectedValue(joinedGroup, true);

            // Force Swing to update everything
            chatList.revalidate();
            chatList.repaint();
            if(chatList.getParent() != null){
                chatList.getParent().revalidate();
                chatList.getParent().repaint();
            }

        });

        // Decline group chat
        declineButton.addActionListener(e -> {
            try {
                grmClient.update(new GroupMember(
                        f.getRequestId(),
                        f.getReceiverId(),
                        contactService.retrieveContactById(identityService.readIdentity().getUserId()).getUsername(),
                        "DECLINED"
                ));
            } catch (IOException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }

            groupRequestService.deleteGroupRequest(f.getRequestId());

            groupRequestPanel.remove(groupRequestEntry);
            groupRequestPanel.revalidate();
            groupRequestPanel.repaint();
        });

        JPanel idPanel = new JPanel(new BorderLayout());
        idPanel.setBackground(Utilities.TRANSPARENT_COLOR);
        idPanel.add(chatNameLabel, BorderLayout.CENTER);
        idPanel.add(userIdLabel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Utilities.TRANSPARENT_COLOR);
        buttonPanel.add(acceptButton);
        buttonPanel.add(declineButton);

        groupRequestEntry.add(idPanel, BorderLayout.CENTER);
        groupRequestEntry.add(buttonPanel, BorderLayout.EAST);

        return groupRequestEntry;
    }

    private void loadSelectableContacts(DefaultListModel<SelectableContact> memberListModel) {
        List<Contact> allContacts = contactService.retrieveAllContacts();

        memberListModel.clear();

        for(Contact c : allContacts){
            memberListModel.addElement(new SelectableContact(c));
        }
    }

    private void addFriendList() {
        // Create main container JPanel
        JPanel mainFriendPanel = new JPanel();
        CardLayout friendCard = new CardLayout();
        mainFriendPanel.setLayout(friendCard);
        mainFriendPanel.setBackground(Utilities.TRANSPARENT_COLOR);

        // 1. allPanel : Retrieve a list of all friends
        PanelTransparent allPanel = new PanelTransparent(Utilities.TRANSPARENT_COLOR, false);
        allPanel.setLayout(new BorderLayout());
        contactListModel = new DefaultListModel<>();
        contactList = new JList<>(contactListModel);

        contactList.setCellRenderer(new ContactListRenderer());
        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactList.setOpaque(false);

        JScrollPane contactScrollPane = new JScrollPane(contactList);
        contactScrollPane.setBorder(null);
        contactScrollPane.setOpaque(false);
        contactScrollPane.getViewport().setOpaque(false);
        contactScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contactScrollPane.getVerticalScrollBar().addAdjustmentListener(e ->{
            revalidate();
            repaint();
        });

        loadContact(contactListModel);

        allPanel.add(contactScrollPane, BorderLayout.CENTER);

        mainFriendPanel.add("All", allPanel);


        // 2. pendingPanel : retrieve list of pending friend requests
        PanelTransparent pendingPanel = new PanelTransparent(Utilities.SECONDARY_COLOR, false);
        pendingPanel.setLayout(new BorderLayout());

        JScrollPane friendRequestScrollPane = new JScrollPane(loadFriendRequests());
        friendRequestScrollPane.setBorder(null);
        friendRequestScrollPane.setOpaque(false);
        friendRequestScrollPane.getViewport().setOpaque(false);
        friendRequestScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        friendRequestScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        friendRequestScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        friendRequestScrollPane.getVerticalScrollBar().addAdjustmentListener(e ->{
            revalidate();
            repaint();
        });

        pendingPanel.add(friendRequestScrollPane, BorderLayout.CENTER);
        mainFriendPanel.add("Pending", pendingPanel);

        // 3. addPanel
        PanelTransparent addPanel = new PanelTransparent(Utilities.TRANSPARENT_COLOR, false);
        addPanel.setLayout(new BorderLayout());
        JTextField inputFriendField = new JTextField();

        JPanel formPanel = new JPanel();
        //formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        inputFriendField.setMaximumSize(new Dimension(300, 35));

        JButton addFriendButton = new JButton("Add Friend");
        addFriendButton.addActionListener(e -> {
            String friendId = inputFriendField.getText();
            if(friendId.isEmpty()){
                return;
            }

            inputFriendField.setText("");

            // Sending friend request via WebSocket

            FriendRequest fr = new FriendRequest(
                    UUID.randomUUID().toString(),
                    identityService.readIdentity().getUserId(),
                    friendId,
                    contactService.retrieveContactById(identityService.readIdentity().getUserId()).getUsername(),
                    "PENDING",
                    new Timestamp(System.currentTimeMillis())
            );

            System.out.println("Created request " + fr);

            // TODO: Modify MySQL db logic
            myStompClient.sendFriendRequest(fr);

            // --> Later down the road, REST API to check if such a user exists
        });

        addFriendButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        formPanel.add(inputFriendField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(addFriendButton);
        addPanel.add(formPanel, BorderLayout.CENTER);

        mainFriendPanel.add("Add", addPanel);

        // Add buttons to the top
        PanelTransparent friendBtnPanel = new PanelTransparent(Utilities.TRANSPARENT_COLOR, false);
        JButton allBtn = new JButton("All");
        allBtn.addActionListener(e -> {
            friendCard.show(mainFriendPanel, "All");
        });

        JButton pendingBtn = new JButton("Pending");
        pendingBtn.addActionListener(e -> {
            friendCard.show(mainFriendPanel, "Pending");
        });

        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> {
            friendCard.show(mainFriendPanel, "Add");
        });

        friendBtnPanel.add(allBtn);
        friendBtnPanel.add(pendingBtn);
        friendBtnPanel.add(addBtn);

        infoPanel.add("FriendButtons" ,friendBtnPanel);

        // Add to centerPanel
        centerPanel.add("FriendList", mainFriendPanel);
    }

    private JPanel loadFriendRequests() {
        friendRequestPanel = new JPanel();
        friendRequestPanel.setLayout(new BoxLayout(friendRequestPanel, BoxLayout.Y_AXIS));
        friendRequestPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        friendRequestPanel.setBackground(Utilities.TRANSPARENT_COLOR);

        List<FriendRequest> allFriendRequests = friendRequestService.retrieveAllFriendRequests();

        for (FriendRequest f : allFriendRequests) {
            JPanel entry = createFriendRequestEntry(f);
            friendRequestPanel.add(entry);
        }

        return friendRequestPanel;
    }

    private void loadContact(DefaultListModel<Contact> contactListModel) {
        List<Contact> allContacts = contactService.retrieveAllContacts();

        contactListModel.clear();

        for(Contact c : allContacts){
            contactListModel.addElement(c);
        }

    }

    private void addChatUserList() {
        PanelTransparent userListPanel = new PanelTransparent(Utilities.SECONDARY_COLOR, true);
        userListPanel.setPreferredSize(new Dimension(200, getHeight()));
        // Load all members of a group chat in here

        add(userListPanel, BorderLayout.EAST);
    }

    public void applyShape() {
        setShape(new RoundRectangle2D.Double(
                0, 0,
                getWidth(),
                getHeight(),
                40, 40
        ));
    }

    private boolean contactIsSender(String userId){
        return userId.equals(identityService.readIdentity().getUserId());
    }

    @Override
    public void onMessageReceive(Message message) {

        // Store messages from all the chats
        messageService.storeMessage(new com.nexus.nexuschat.SQLitedatabase.model.Message(
                UUID.randomUUID().toString(),
                message.getChat_id(),
                message.getUser_id(),
                message.getMessage(),
                message.getSent_at() )
        );

        // Update UI if it's the active chat
        if(message.getChat_id().equals(currentChatId)) {
            messagePanel.add(createChatMessage(message, message.getUser_id()));
            revalidate();
            repaint();

            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            int max = vertical.getMaximum();
            int extent = vertical.getModel().getExtent();
            int value = vertical.getValue();

            if (value + extent > max - extent / 4) {
                vertical.setValue(max);
            }
        }
        // Mark chat as having unread messages (later)

    }

    public void onFriendRequestReceive(FriendRequest friendRequest) {

        SwingUtilities.invokeLater(() -> {

            if(friendRequest.getStat().equals("PENDING")) {
                friendRequestService.storeFriendRequest(friendRequest);

                // create contact for sender if this is our own outgoing request
                if(friendRequest.getSenderId().equals(identityService.readIdentity().getUserId())) {
                    contactService.createContact(friendRequest.getReceiverId(), friendRequest.getUsername());
                }

                if(friendRequestPanel != null) {
                    friendRequestPanel.add(createFriendRequestEntry(friendRequest));
                    friendRequestPanel.revalidate();
                    friendRequestPanel.repaint();
                }
            }

            if(friendRequest.getStat().equals("ACCEPTED")) {
                // Ensure contact exists, create if missing

                // Ensures user doesn't save himself, different payloads may change the receive/sender ids
                String contactId = friendRequest.getSenderId().equals(identityService.readIdentity().getUserId())
                        ? friendRequest.getReceiverId()
                        : friendRequest.getSenderId();

                // Get username
                String contactName = friendRequest.getUsername();

                // Create new contact if it doesn't exist already
                Contact newContact = contactService.retrieveContactById(contactId);
                if(newContact == null) {
                    newContact = contactService.createContact(contactId, contactName);
                }

                // Check if that contact exists in the list model
                boolean exists = false;
                for (int i = 0; i < contactListModel.size(); i++) {
                    if (contactListModel.get(i).getContactId().equals(newContact.getContactId())) {
                        exists = true;
                        break;
                    }
                }

                // If it doesn't exist in the list model, add it
                if (!exists) {
                    contactListModel.addElement(newContact);
                }

                // Refresh the GUI
                refreshSelectableContacts();
            }
        });
    }

    private JPanel createFriendRequestEntry(FriendRequest friendRequest) {

        JPanel entry = new JPanel(new BorderLayout());
        entry.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        entry.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel usernameLabel = new JLabel(friendRequest.getUsername());
        JLabel userIdLabel = new JLabel(friendRequest.getSenderId());

        JButton acceptButton = new JButton("Accept");
        JButton declineButton = new JButton("Decline");

        acceptButton.addActionListener(e -> {
            String senderUsername = friendRequest.getUsername();

            friendRequest.setStat("ACCEPTED");
            friendRequest.setUsername(
                    contactService.retrieveContactById(identityService.readIdentity().getUserId()).getUsername()
            );

            try {
                frClient.update(friendRequest); // REST
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            myStompClient.sendFriendRequest(friendRequest); // WebSocket notify sender

            Contact contact = contactService.createContact(
                    friendRequest.getSenderId(),
                    senderUsername
            );

            contactListModel.addElement(contact);

            friendRequestService.deleteFriendRequest(friendRequest);

            refreshFriendRequestsUI(entry);
        });

        declineButton.addActionListener(e -> {
            try {
                frClient.delete(friendRequest.getRequestId());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            friendRequestService.deleteFriendRequest(friendRequest);

            refreshFriendRequestsUI(entry);
        });

        JPanel idPanel = new JPanel(new BorderLayout());
        idPanel.setBackground(Utilities.TRANSPARENT_COLOR);
        idPanel.add(usernameLabel, BorderLayout.CENTER);
        idPanel.add(userIdLabel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Utilities.TRANSPARENT_COLOR);
        buttonPanel.add(acceptButton);
        buttonPanel.add(declineButton);

        entry.add(idPanel, BorderLayout.CENTER);
        entry.add(buttonPanel, BorderLayout.EAST);

        return entry;
    }

    private void refreshFriendRequestsUI() {
        friendRequestPanel.removeAll();

        for (FriendRequest f : friendRequestService.retrieveAllFriendRequests()) {
            friendRequestPanel.add(createFriendRequestEntry(f));
        }

        friendRequestPanel.revalidate();
        friendRequestPanel.repaint();
    }

    private void refreshFriendRequestsUI(JPanel entry) {
        friendRequestPanel.removeAll();

        for (FriendRequest f : friendRequestService.retrieveAllFriendRequests()) {
            friendRequestPanel.add(createFriendRequestEntry(f));
        }

        friendRequestPanel.remove(entry);
        friendRequestPanel.revalidate();
        friendRequestPanel.repaint();
    }

    private void refreshSelectableContacts() {
        if (memberListModel != null) {
            loadSelectableContacts(memberListModel);
        }
    }

    @Override
    public void onGroupRequestReceive(GroupRequestPayload groupRequestPayload) {
        GroupRequest newRequest = new GroupRequest(
                groupRequestPayload.getChatId(),
                groupRequestPayload.getSenderId(),
                identityService.readIdentity().getUserId(),
                groupRequestPayload.getChatName(),
                "PENDING",
                new Timestamp(System.currentTimeMillis())
        );

        groupRequestService.createGroupRequest(newRequest);

        SwingUtilities.invokeLater(() -> {
            if (groupRequestPanel != null) {
                groupRequestPanel.add(createGroupRequestEntry(newRequest));
                groupRequestPanel.revalidate();
                groupRequestPanel.repaint();
            }
        });
    }


    @Override
    public void onActiveUsersUpdated(ArrayList<String> users) {

        // More efficient way to implement this could be
        // JLabels in a Hashset (Since every user is unique)
        // Access the JLabel to be deleted (the user that was disconnected)
        // .remove(JLabel), revalidate(), repaint()

//        if(connectedUsersPanel.getComponents().length >= 2) {
//            connectedUsersPanel.remove(1);
//        }
//
//        JPanel userListPanel = new JPanel();
//        userListPanel.setBackground(com.nexus.nexuschat.client.Utilities.TRANSPARENT_COLOR);
//        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));
//
//        for(String user : users) {
//            JLabel username = new JLabel();
//            username.setText(user);
//            username.setForeground(com.nexus.nexuschat.client.Utilities.TEXT_COLOR);
//            username.setFont(new Font("Inter", Font.BOLD, 16));
//            userListPanel.add(username);
//        }
//
//        connectedUsersPanel.add(userListPanel);
//        revalidate();
//        repaint();
    }

    // Most likely not needed anymore
    @Override
    public void onGroupCreated(GroupChat groupChat) {
        SwingUtilities.invokeLater(() -> {
            chatListModel.addElement(groupChat);
            chatList.revalidate();
            chatList.repaint();
        });
    }

    public MyStompClient getMyStompClient(){
        return myStompClient;
    }
}
