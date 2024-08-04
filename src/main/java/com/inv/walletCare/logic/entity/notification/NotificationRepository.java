package com.inv.walletCare.logic.entity.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Notification crud repository.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find all notification by user id.
     * @param userId Long value with the user id to search.
     * @return Optional list of notification received by the user.
     */
    @Query("SELECT n FROM Notification n WHERE n.owner.id= ?1 AND n.isDeleted = false")
    Optional<List<Notification>> findAllByUserId(Long userId);

    /**Find a notification by its id and user id.
     * Prevents other users from seeing notifications they did not receive.
     * @param notificationId Long value with the notification id.
     * @param userId Long value with the user id.
     * @return Optional notification object found.
     */
    @Query("SELECT n FROM Notification n WHERE n.id = ?1 AND n.owner.id = ?2 AND n.isDeleted = false")
    Optional<Notification> findByIdAndUserId(Long notificationId, Long userId);
}
