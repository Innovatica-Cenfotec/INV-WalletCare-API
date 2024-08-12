package com.inv.walletCare.rest.tip;

import com.inv.walletCare.logic.entity.goal.Goal;
import com.inv.walletCare.logic.entity.goal.GoalService;
import com.inv.walletCare.logic.entity.notification.NotificationDTO;
import com.inv.walletCare.logic.entity.notification.NotificationService;
import com.inv.walletCare.logic.entity.notification.NotificationType;
import com.inv.walletCare.logic.entity.tip.TipProposal;
import com.inv.walletCare.logic.entity.tip.TipService;
import com.inv.walletCare.logic.entity.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tips")
public class TipRestController {
    private final TipService tipService;
    private final NotificationService notificationService;

    public TipRestController(TipService tipService, NotificationService notificationService) {
        this.tipService = tipService;
        this.notificationService = notificationService;
    }

    /***
     * Propose a goal
     * @return The proposed goal
     */
    @PostMapping("/propose")
    public TipProposal proposeGoal() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        TipProposal tip = tipService.createTip(user);

        NotificationDTO tipNotification = new NotificationDTO();
        tipNotification.setReceiverEmail(user.getEmail());
        tipNotification.setType(NotificationType.TIP);
        tipNotification.setTitle(tip.getName());
        tipNotification.setMessage(tip.getDescription());
        notificationService.sendNotificationByUserEmail(tipNotification);
        return tip;
    }
}
