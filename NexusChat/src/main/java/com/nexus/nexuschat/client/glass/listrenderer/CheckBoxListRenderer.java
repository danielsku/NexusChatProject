package com.nexus.nexuschat.client.glass.listrenderer;

import com.nexus.nexuschat.client.glass.SelectableContact;

import javax.swing.*;
import java.awt.*;

public class CheckBoxListRenderer extends JCheckBox implements ListCellRenderer<SelectableContact> {
    @Override
    public Component getListCellRendererComponent(JList<? extends SelectableContact> list,
                                                  SelectableContact value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        setText(value.getContact().getUsername());
        setSelected(value.isSelected());
        setBackground(list.getBackground());
        setForeground(list.getForeground());
        return this;
    }
}
