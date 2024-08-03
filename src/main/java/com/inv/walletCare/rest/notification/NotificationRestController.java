package com.inv.walletCare.rest.notification;

import com.inv.walletCare.logic.entity.notification.Notification;
import com.inv.walletCare.logic.entity.notification.NotificationRepository;
import com.inv.walletCare.logic.entity.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationRestController {

    private final NotificationRepository notificationRepository;

    public NotificationRestController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Gets all notifications received by the logged user.
     * @return List of notifications received by logged user.
     */
    @GetMapping
    public List<Notification> getNotificationsByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return notificationRepository.findAllByUserId(user.getId()).get();
    }
}
