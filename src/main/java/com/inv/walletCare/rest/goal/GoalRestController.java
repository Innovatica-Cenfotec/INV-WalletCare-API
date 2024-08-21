package com.inv.walletCare.rest.goal;

import com.inv.walletCare.logic.entity.goal.Goal;
import com.inv.walletCare.logic.entity.goal.GoalRepository;
import com.inv.walletCare.logic.entity.goal.GoalService;
import com.inv.walletCare.logic.entity.goal.GoalStatusEnum;
import com.inv.walletCare.logic.entity.report.PiechartDTO;
import com.inv.walletCare.logic.entity.report.ReportService;
import com.inv.walletCare.logic.entity.rol.RoleEnum;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.exceptions.FieldValidationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/goals")
public class GoalRestController {

    private final GoalRepository goalRepository;

    private final GoalService goalService;

    private final ReportService reportService;

    public GoalRestController(GoalRepository goalRepository,
                              GoalService goalService,
                              ReportService reportService) {
        this.goalRepository = goalRepository;
        this.goalService = goalService;
        this.reportService = reportService;
    }

    /***
     * Get all the goals
     * @return List of goals
     */
    @GetMapping
    public List<Goal> getGoals() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        if (user.getRole().getName() == RoleEnum.ADMIN) {
            return goalRepository.findAll();
        } else
            return goalRepository.findAllByOwnerId(user.getId());
    }

    /***
     * Approve or reject a goal
     * @param id Goal id
     * @param accept True to approve, false to reject
     */
    @PutMapping("/accept-or-reject/{id}")
    public Goal acceptOrRejectGoal(@PathVariable long id, @RequestBody boolean accept) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Optional<Goal> goal = goalRepository.findByIdAndOwnerId(id, user.getId());
        if (goal.isEmpty()) {
            throw new FieldValidationException("id", "La meta no existe o no pertenece al usuario");
        }

        if (goal.get().getStatus() != GoalStatusEnum.GOAL_PENDING) {
            throw new FieldValidationException("status", "La meta no está pendiente de aprobación");
        }

        if (accept) {
            goal.get().setStatus(GoalStatusEnum.ACTIVE);
        } else {
            goal.get().setStatus(GoalStatusEnum.GOAL_REJECTED);
        }

        goalRepository.save(goal.get());
        return goal.get();
    }

    /***
     * Delete a goal
     * @param id Goal id
     */
    @DeleteMapping("/{id}")
    public void deleteGoal(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Optional<Goal> goal = goalRepository.findByIdAndOwnerId(id, user.getId());
        if (goal.isEmpty()) {
            throw new FieldValidationException("id", "La meta no existe o no pertenece al usuario");
        }

        goal.get().setDeleted(true);
        goal.get().setDeletedAt(new Date());

        switch (goal.get().getStatus()) {
            case GOAL_PENDING:
                goal.get().setStatus(GoalStatusEnum.GOAL_REJECTED);
                break;
            case ACTIVE:
                goal.get().setStatus(GoalStatusEnum.FAILED);
                break;

        }

        goalRepository.save(goal.get());
    }

    /***
     * Propose a goal
     * @return The proposed goal
     */
    @PostMapping("/propose")
    public Goal proposeGoal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Goal goal = goalService.createGoal(user);
        return goalRepository.save(goal);
    }

    /**
     * Get a report with the count of goals sort by status.
     * @return List of PiechartDTO with the report of expense.
     */
    @GetMapping("/report/progress")
    public List<PiechartDTO> getAnualAmountByCategory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return reportService.getGoalsProgressByStatus(user.getId());
    }
}