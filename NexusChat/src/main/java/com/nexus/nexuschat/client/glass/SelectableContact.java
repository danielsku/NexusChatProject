package com.nexus.nexuschat.client.glass;

import com.nexus.nexuschat.SQLitedatabase.model.Contact;

public class SelectableContact {
    private Contact contact;
    private boolean selected;

    public SelectableContact(Contact contact) {
        this.contact = contact;
        this.selected = false;
    }

    public Contact getContact() {
        return contact;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "SelectableContact{" +
                "contact=" + contact +
                ", selected=" + selected +
                '}';
    }
}
