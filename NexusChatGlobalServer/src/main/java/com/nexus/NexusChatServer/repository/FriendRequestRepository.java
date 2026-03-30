package com.nexus.NexusChatServer.repository;

import com.nexus.NexusChatServer.entity.FriendRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, String> {
    //find all pending requests for a user
    @Query("SELECT fr FROM FriendRequest fr WHERE fr.receiverId = :userId AND fr.stat = 'PENDING'")
    List<FriendRequest> findPendingRequestsByReceiver_id(@Param("userId") String userId);

    //update the status of all requests
    @Modifying
    @Transactional
    @Query("UPDATE FriendRequest fr SET fr.stat = :status WHERE fr.id = :requestId")
    int updateRequestStatus(@Param("requestId") String requestId, @Param("status") String status);

    //delete processed requests
    @Modifying
    @Transactional
    @Query("DELETE FROM FriendRequest fr WHERE fr.receiverId = :userId AND fr.stat != 'PENDING'")
    int deleteRequestsByReceiver_id(@Param("userId") String userId);

}
