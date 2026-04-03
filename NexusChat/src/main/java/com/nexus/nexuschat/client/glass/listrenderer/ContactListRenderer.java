package com.nexus.nexuschat.client.glass.listrenderer;

import com.nexus.nexuschat.SQLitedatabase.model.Contact;
import com.nexus.nexuschat.client.glass.Utilities;

import javax.swing.*;
import java.awt.*;

public class ContactListRenderer extends JPanel implements ListCellRenderer<Contact> {
    private JLabel usernameLabel = new JLabel();
    private JLabel userIdLabel = new JLabel();

    public ContactListRenderer() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10,15,10,15));
        //setOpaque(false);

        usernameLabel.setOpaque(false);
        userIdLabel.setOpaque(false);

        add(usernameLabel, BorderLayout.CENTER);
        add(userIdLabel, BorderLayout.SOUTH);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Contact> list,
                                                  Contact value, int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        usernameLabel.setText(value.getUsername());
        userIdLabel.setText(value.getContactId());

        if(isSelected){
            setBackground(new Color(0, 94, 164));

        } else {
            setBackground(Utilities.SECONDARY_COLOR);
        }
        return this;
    }
}
