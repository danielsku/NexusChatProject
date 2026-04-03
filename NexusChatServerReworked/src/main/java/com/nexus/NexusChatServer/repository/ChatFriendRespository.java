package com.nexus.NexusChatServer.repository;

import com.nexus.NexusChatServer.entity.ChatFriend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatFriendRespository extends JpaRepository<ChatFriend, String> {
    //find all pending requests for a user
    @Query("SELECT cf FROM ChatFriend cf WHERE cf.receiverId = :userId")
    List<ChatFriend> findRequestsByReceiver_id(@Param("userId") String userId);
}
