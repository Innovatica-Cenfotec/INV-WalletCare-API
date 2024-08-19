package com.inv.walletCare.logic.entity.goal;

import com.inv.walletCare.logic.entity.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class GoalServiceTest {

    @Autowired
    private GoalService goalService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void testCreateGoal() {
        // Validation of environment variables required to run the test
        assertNotNull(System.getenv("PROJECT_ID"), "PROJECT_ID Is not defined");
        assertNotNull(System.getenv("LOCATION"), "LOCATION Is not defined");

        // Create a user
        User user = new User();
        user.setId(2L);

        Goal goal = goalService.createGoal(user);

        assertNotNull(goal, "Goal is null");
        assertNotNull(goal.getName(), "Goal name is null");
        assertNotNull(goal.getDescription(), "Goal description is null");
        assertNotNull(goal.getRecommendation(), "Goal recommendation is null");
        assertNotNull(goal.getType(), "Goal type is null");
    }

    @Test
    public void testRunService() {
        // Validation of environment variables required to run the test
        assertNotNull(System.getenv("PROJECT_ID"), "PROJECT_ID Is not defined");
        assertNotNull(System.getenv("LOCATION"), "LOCATION Is not defined");

        // Validation of the service execution
        assertDoesNotThrow(() -> goalService.runService());
    }
}