package com.inv.walletCare.rest.notification;

import com.inv.walletCare.logic.entity.notification.Notification;
import com.inv.walletCare.logic.entity.notification.NotificationService;
import com.inv.walletCare.logic.entity.user.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationRestController {

    private final NotificationService notificationService;

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
        return notificationService.findAllByUserId(user.getId()).get();
    }

    /**
     * Create a new notification. Only allow ADMIN and SUPER_ADMIN roles.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public void createNotification(Notification notification) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
    }

    /**
     * Mark as read a notification by its ID for the currently authenticated user.
     * @param id The ID of the notification to read.
     * @throws RuntimeException if the notification is not found or not owned by the current user.
     */
    @PutMapping("/read/{id}")
    public void readNotification(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        notificationService.markNotificationAsRead(id, currentUser.getId());
    }

    /**
     * Deletes a notification by its ID for the currently authenticated user.
     * @param id The ID of the notification to delete.
     * @throws RuntimeException if the notification is not found or not owned by the current user.
     */
    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        notificationService.markNotificationAsDeleted(id, currentUser.getId());
    }
}
