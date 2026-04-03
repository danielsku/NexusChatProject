CREATE TABLE contact(
    contact_id TEXT PRIMARY KEY,
    username VARCHAR(30),
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE group_chat(
    chat_id TEXT PRIMARY KEY,
    created_by TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    group_name TEXT NOT NULL,
    FOREIGN KEY(created_by) REFERENCES contact(contact_id)
);

CREATE TABLE chat_member(
    chat_id TEXT NOT NULL,
    user_id TEXT NOT NULL,
    PRIMARY KEY(chat_id, user_id),
    FOREIGN KEY(chat_id) REFERENCES group_chat(chat_id),
    FOREIGN KEY(user_id) REFERENCES contact(contact_id)
);

CREATE TABLE message(
    m_id TEXT PRIMARY KEY,
    chat_id TEXT NOT NULL,
    user_id TEXT NOT NULL,
    content TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(chat_id, user_id) REFERENCES chat_member(chat_id, user_id)
);

CREATE TABLE friend_requests(
    request_id TEXT PRIMARY KEY,
    sender_id TEXT NOT NULL,
    receiver_id TEXT NOT NULL,
    username TEXT NOT NULL,
    stat TEXT DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(sender_id) REFERENCES contact(contact_id),
    FOREIGN KEY(receiver_id) REFERENCES contact(contact_id)
);