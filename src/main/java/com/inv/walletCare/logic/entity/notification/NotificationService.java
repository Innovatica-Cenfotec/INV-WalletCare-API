package com.inv.walletCare.logic.entity.notification;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Class designed to send notifications.
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Gets all notifications received by user id.
     * @param userId Long value with the user id.
     * @return List of notifications received by user id.
     */
    public Optional<List<Notification>> findAllByUserId(Long userId) {
        return notificationRepository.findAllByUserId(userId);
    }

    /**
     * Get a notifications received by notification id and user id.
     * @param notificationId Long value with the notification id.
     * @param userId Long value with the user id.
     * @return List of notifications received by notification id and user id.
     */
    public Optional<Notification> findByIdAndUserId(Long notificationId, Long userId) {
        return notificationRepository.findByIdAndUserId(notificationId, userId);
    }

    /**
     * Mark a notification as read by its id.
     * @param notificationId Long value with the notification id.
     * @param userId Long value with the user id.
     */
    public void markNotificationAsRead(Long notificationId, Long userId) {
        Notification notification = checkIfNotificationExist(notificationId, userId).get();
        notification.setRead(true);
        notification.setUpdatedAt(new Date());
        notificationRepository.save(notification);
    }

    /**
     * Mark a notification as deleted by its id.
     * @param notificationId Long value with the notification id.
     * @param userId Long value with the user id.
     */
    public void markNotificationAsDeleted(Long notificationId, Long userId) {
        Notification notification = checkIfNotificationExist(notificationId, userId).get();
        notification.setDeleted(true);
        notification.setDeletedAt(new Date());
        notificationRepository.save(notification);
    }

    /**
     * Save a notification with the body indicated.
     * @param notification Body of the notification.
     * @return The body of the notification.
     */
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    /**
     * Check if notification exists and belong to indicated user.
     * @param notificationID Long value with the notification id.
     * @param userID Long value with the user id.
     * @return If there is no exception, return Notification object.
     */
    public Optional<Notification> checkIfNotificationExist(Long notificationID, Long userID) {
        Optional<Notification> notification = findByIdAndUserId(notificationID, userID);
        if (notification.isEmpty()) {
            throw new IllegalArgumentException("La notificación no existe o le faltan permisos para acceder a la información.");
        } else {
            return notification;
        }
    }
}
