package com.example.DAO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.Entitiy.*;

import jakarta.transaction.Transactional;

@Repository
public interface GroupChatRequestRepository extends JpaRepository<GroupChatRequest, String>{
    //find all group chat requests for a user
    @Query("SELECT gcr FROM GroupChatRequest gcr WHERE gcr.receiver_id = :userId AND gcr.stat = 'PENDING'")
    List<GroupChatRequest> findRequestsByReceiver_id(@Param("userId") String userId);

    //update requests status
    @Modifying
    @Transactional
    @Query("UPDATE GroupChatRequest gcr SET gcr.stat = :status WHERE gcr.id = :requestId")
    int updateRequestStatus(@Param("requestId") String requestId, @Param("status") String status);

    //delete processed requests
    @Modifying
    @Transactional
    @Query("DELETE FROM GroupChatRequest gcr WHERE gcr.receiver_id = :userId AND gcr.stat != 'PENDING'")
    int deleteRequestsByReceiver_id(@Param("userId") String userId);
}
