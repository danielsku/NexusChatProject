package com.example.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.Entitiy.SentTo;
import com.example.Entitiy.SentToId;
import java.util.List;

@Repository
public interface SentToRepository extends JpaRepository<SentTo, SentToId> {

    @Query("SELECT st FROM SentTo st WHERE st.message.message_id = :messageId")
    List<SentTo> findByMessageId(@Param("messageId") String messageId);

    @Query("SELECT st FROM SentTo st WHERE st.receiver.receiverId = :receiverId")
    List<SentTo> findByReceiverId(@Param("receiverId") String receiverId);
}