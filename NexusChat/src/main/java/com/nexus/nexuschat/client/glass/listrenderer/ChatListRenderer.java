package com.nexus.nexuschat.client.glass.listrenderer;

import com.nexus.nexuschat.SQLitedatabase.model.GroupChat;
import com.nexus.nexuschat.client.glass.Utilities;

import javax.swing.*;
import java.awt.*;

public class ChatListRenderer extends JPanel implements ListCellRenderer<GroupChat> {

    private JLabel nameLabel;

    public ChatListRenderer(){
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10,15,10,15));
        setOpaque(true);

        nameLabel = new JLabel();
        nameLabel.setFont(new Font("Roboto", Font.BOLD, 14));
        nameLabel.setForeground(Utilities.TEXT_COLOR);

        add(nameLabel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends GroupChat> list, GroupChat value, int index, boolean isSelected, boolean cellHasFocus) {
        nameLabel.setText(value.getGroupName());

        if(isSelected){
            setBackground(new Color(255, 255, 255, 40));
        } else {
            setBackground(Utilities.TRANSPARENT_COLOR);
        }

        return this;
    }
}
