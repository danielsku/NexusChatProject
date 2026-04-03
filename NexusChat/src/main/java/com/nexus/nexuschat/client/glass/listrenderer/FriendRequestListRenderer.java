package com.nexus.nexuschat.client.glass.listrenderer;

import com.nexus.nexuschat.SQLitedatabase.model.FriendRequest;
import com.nexus.nexuschat.client.glass.Utilities;

import javax.swing.*;
import java.awt.*;

public class FriendRequestListRenderer extends JPanel implements ListCellRenderer<FriendRequest> {
    private JLabel usernameLabel = new JLabel();
    private JLabel userIdLabel = new JLabel();
    private JButton acceptButton = new JButton("Accept");
    private JButton declineButton = new JButton("Decline");

    public FriendRequestListRenderer() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10,15,10,15));
        //setOpaque(false);

        usernameLabel.setOpaque(false);
        userIdLabel.setOpaque(false);

        JPanel idPanel = new JPanel();
        idPanel.setLayout(new BorderLayout());
        idPanel.setBackground(Utilities.TRANSPARENT_COLOR);
        idPanel.add(usernameLabel, BorderLayout.CENTER);
        idPanel.add(userIdLabel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Utilities.TRANSPARENT_COLOR);
        buttonPanel.add(acceptButton);
        buttonPanel.add(declineButton);

        add(idPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);

    }

    @Override
    public Component getListCellRendererComponent(JList<? extends FriendRequest> list, FriendRequest value, int index, boolean isSelected, boolean cellHasFocus) {
        usernameLabel.setText(value.getUsername());
        userIdLabel.setText(value.getSenderId());

        if(isSelected){
            setBackground(new Color(0, 94, 164));

        } else {
            setBackground(Utilities.SECONDARY_COLOR);
        }

        return this;
    }

    public JButton getAcceptButton() { return acceptButton; }
    public JButton getDeclineButton() { return declineButton; }
}
