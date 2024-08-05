package com.inv.walletCare.logic.entity.notification;

import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.entity.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Class designed to send notifications.
 */
@Service
public class NotificationService {
    /**
     * Notification repository interface.
     */
    private final NotificationRepository notificationRepository;
    /**
     * User repository interface.
     */
    private final UserRepository userRepository;

    /**
     * Service constructor in charge of initializing required repositories. Replace @autowire.
     * @param notificationRepository Notification repository interface.
     * @param userRepository User repository interface.
     */
    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
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
    @Transactional
    public void markNotificationAsRead(Long notificationId, Long userId) {
        Notification notification = checkIfNotificationExist(notificationId, userId).orElse(null);
        if (notification != null) {
            notification.setRead(true);
            notification.setUpdatedAt(new Date());
            notificationRepository.save(notification);
        }
    }

    /**
     * Mark a notification as not read by its id.
     * @param notificationId Long value with the notification id.
     * @param userId Long value with the user id.
     */
    @Transactional
    public void markNotificationAsNotRead(Long notificationId, Long userId) {
        Notification notification = checkIfNotificationExist(notificationId, userId).orElse(null);
        if (notification != null) {
            notification.setRead(false);
            notification.setUpdatedAt(new Date());
            notificationRepository.save(notification);
        }
    }

    /**
     * Mark a notification as deleted by its id.
     * @param notificationId Long value with the notification id.
     * @param userId Long value with the user id.
     */
    @Transactional
    public void markNotificationAsDeleted(Long notificationId, Long userId) {
        Notification notification = checkIfNotificationExist(notificationId, userId).orElse(null);
        if (notification != null) {
            notification.setDeleted(true);
            notification.setDeletedAt(new Date());
            notificationRepository.save(notification);
        }
    }

    /**
     * Send a notification to a user search by email. This method set isRead = false.
     * @param notificationBody Notification body and receiver email.
     * @return The body of the notification.
     * @throws Exception Message indication that the user email is not registered.
     */
    @Transactional
    public Optional<Notification> sendNotificationByUserEmail(NotificationDTO notificationBody)
            throws Exception {
        User receiver = userRepository.findByEmail(notificationBody.getReceiverEmail())
                .orElseThrow(() -> new Exception("El email del usuario no esta registrado en la aplicación."));

        Notification notification = new Notification();
        notification.setOwner(receiver);
        notification.setType(notificationBody.getType());
        notification.setTitle(notificationBody.getTitle());
        notification.setMessage(notificationBody.getMessage());
        notification.setRead(false);
        notification.setCreatedAt(new Date());
        notification.setUpdatedAt(new Date());
        notification.setDeleted(false);
        notificationRepository.save(notification);
        return Optional.of(notification);
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
