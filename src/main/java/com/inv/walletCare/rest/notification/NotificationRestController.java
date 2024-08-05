package com.inv.walletCare.rest.notification;

import com.inv.walletCare.logic.entity.notification.Notification;
import com.inv.walletCare.logic.entity.notification.NotificationDTO;
import com.inv.walletCare.logic.entity.notification.NotificationService;
import com.inv.walletCare.logic.entity.user.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Rest controller for notification service. Url: "/notifications".
 */
@RestController
@RequestMapping("/notifications")
public class NotificationRestController {
    /**
     * Notification service class.
     */
    private final NotificationService notificationService;

    /**
     * Rest controller constructor in charge of initializing required repositories and services.
     * Replace @autowire.
     * @param notificationService Notification service class.
     */
    public NotificationRestController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Gets all notifications received by the logged user.
     * @return List of notifications received by logged user.
     */
    @GetMapping
    public List<Notification> getNotificationsByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return notificationService.findAllByUserId(user.getId()).orElse(new ArrayList<>());
    }

    /**
     * Send a notification to user search by id. Only allow ADMIN and SUPER_ADMIN roles.
     * @return The notification body.
     */
    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Notification sendNotification( @RequestBody NotificationDTO notification) throws Exception {
        return notificationService.sendNotificationByUserEmail(notification)
                .orElseThrow(() -> new Exception("No se pudo mandar la notificación."));
    }

    /**
     * Mark as read a notification by its ID for the currently authenticated user.
     * @param id The ID of the notification to read.
     * @throws RuntimeException if the notification is not found or not owned by the current user.
     */
    @PutMapping("/read/{id}")
    public Notification readNotification(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return notificationService.markNotificationAsRead(id, currentUser.getId());
    }

    /**
     * Deletes a notification by its ID for the currently authenticated user.
     * @param id The ID of the notification to delete.
     * @throws RuntimeException if the notification is not found or not owned by the current user.
     */
    @DeleteMapping("/{id}")
    public Notification deleteNotification(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return notificationService.markNotificationAsDeleted(id, currentUser.getId());
    }
}
