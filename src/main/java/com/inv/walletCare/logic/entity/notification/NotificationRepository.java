package com.inv.walletCare.logic.entity.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE n.owner.id= ?1 AND n.isDeleted = false")
    Optional<List<Notification>> findAllByUserId(Long userId);

    @Query("SELECT n FROM Notification n WHERE n.id = ?1 AND n.owner.id = ?2 AND n.isDeleted = false")
    Optional<Notification> findByIdAndUserId(Long notificationId, Long userId);
}
