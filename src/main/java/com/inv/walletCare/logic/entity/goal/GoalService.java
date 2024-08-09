package com.inv.walletCare.logic.entity.goal;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.account.AccountRepository;
import com.inv.walletCare.logic.entity.recurrence.RecurrenceRepository;
import com.inv.walletCare.logic.entity.rol.RoleEnum;
import com.inv.walletCare.logic.entity.saving.Saving;
import com.inv.walletCare.logic.entity.saving.SavingRepository;
import com.inv.walletCare.logic.entity.transaction.TransactionRepository;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.entity.user.UserRepository;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class GoalService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SavingRepository savingRepository;

    @Autowired
    private RecurrenceRepository recurrenceRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;

    interface GoalExtrator {
        @UserMessage(fromResource = "/prompts/create_goal.txt")
        GoalProposal extractGoal(@V("data_user") String data, @V("current_date") String data_user);
    }

    /**
     * Run the service to create goals for all users
     */
    public void runService() {
        var users = userRepository.findAll();
        for (var user : users.stream().filter(u -> u.getRole().getName() == RoleEnum.USER).toList()) {
            Goal goal = createGoal(user);
            if (goal != null) {
                goalRepository.save(goal);
            }
        }
    }

    /**
     * Build the JSON object for the given user
     * @param user The user to build the JSON object for
     * @return The JSON object
     */
    private String BuilderJson(User user) {
        var recurrences = recurrenceRepository.findAllByOwner(user.getId());
        var accounts = accountRepository.findAllByOwnerId(user.getId());
        var transactions = transactionRepository.findAllbyOwner(user.getId());
        var goals = goalRepository.findAllByOwnerId(user.getId());

        // Construct JSON object
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", user.getName());
        jsonObject.addProperty("lastname", user.getLastname());
        JsonArray accountsArray = new JsonArray();

        // Add accounts array
        for (var account : accounts.get()) {
            // Info for each account
            JsonObject accountObject = new JsonObject();
            accountObject.addProperty("id", account.getId());
            accountObject.addProperty("name", account.getName());
            accountObject.addProperty("description", account.getDescription());
            accountObject.addProperty("balance", account.getBalance());
            accountObject.addProperty("type", account.getType().name());

            // Add recurring_incomes array
            JsonArray recurringIncomesArray = new JsonArray();
            for (var recurrence : recurrences.stream().filter(r -> r.getIncome() != null).toList()) {
                JsonObject incomeObject = new JsonObject();
                incomeObject.addProperty("amount", recurrence.getIncome().getAmount());
                incomeObject.addProperty("frequency", recurrence.getIncome().getFrequency().name());
                incomeObject.addProperty("scheduledDay", recurrence.getIncome().getScheduledDay());
                recurringIncomesArray.add(incomeObject);
            }

            // Add recurring_expenses array
            JsonArray recurringExpensesArray = new JsonArray();
            for (var recurrence : recurrences.stream().filter(r -> r.getExpense() != null).toList()) {
                JsonObject expenseObject = new JsonObject();
                expenseObject.addProperty("amount", recurrence.getExpense().getAmount());
                expenseObject.addProperty("frequency", recurrence.getExpense().getFrequency().name());
                expenseObject.addProperty("scheduledDay", recurrence.getExpense().getScheduledDay());
                recurringExpensesArray.add(expenseObject);
            }

            // Add savings array
            JsonArray savingsArray = new JsonArray();
            for (var recurrence : recurrences.stream().filter(r -> r.getSaving() != null).toList()) {
                JsonObject savingObject = new JsonObject();
                savingObject.addProperty("balance", recurrence.getSaving().getAmount());
                savingsArray.add(savingObject);
            }

            // Add transactions array
            JsonArray transactionsArray = new JsonArray();
            for (var transaction : transactions.get()) {
                JsonObject transactionObject = new JsonObject();
                transactionObject.addProperty("type", transaction.getType().name());
                transactionObject.addProperty("amount", transaction.getAmount());
                transactionObject.addProperty("description", transaction.getDescription());
                transactionsArray.add(transactionObject);
            }

            accountObject.add("recurring_incomes", recurringIncomesArray);
            accountObject.add("recurring_expenses", recurringExpensesArray);
            //accountObject.add("savings", savingsArray);
            accountObject.add("transactions", transactionsArray);
            accountsArray.add(accountObject);
        }

        // Add Goals array
        JsonArray goalsArray = new JsonArray();
        for (var goal : goals) {
            JsonObject goalObject = new JsonObject();
            goalObject.addProperty("name", goal.getName());
            goalObject.addProperty("description", goal.getDescription());
            goalObject.addProperty("recommendation", goal.getRecommendation());
            goalObject.addProperty("type", goal.getType().name());
            goalObject.addProperty("target_amount", goal.getTargetAmount());
            goalObject.addProperty("initialAmount", goal.getInitialAmount());

            goalsArray.add(goalObject);
        }

        jsonObject.add("accounts", accountsArray);
        jsonObject.add("goals", goalsArray);

        return new Gson().toJson(jsonObject);
    }

    /**
     * Create a goal for the given user
     * @param user The user to create the goal for
     * @return The created goal
     */
    public Goal createGoal(User user) {
        ChatLanguageModel model = VertexAiGeminiChatModel.builder()
                .project(System.getenv("PROJECT_ID"))
                .location(System.getenv("LOCATION"))
                .modelName("gemini-1.5-flash-001")
                .temperature(0f)
                .topK(1)
                .build();

        String current_date = Date.from(Instant.now()).toString();
        String data = BuilderJson(user);

        GoalExtrator extractor = AiServices.create(GoalExtrator.class, model);
        var goal = extractor.extractGoal(data, current_date);





        Optional<Account> account = accountRepository.findByIdAndOwnerId(goal.getRefIdAccount(), user.getId());
        if (account.isEmpty()) {
            // No account found for the given ID, continue with the next goal
            return null;
        }

        Goal newGoal = new Goal();
        newGoal.setName(goal.getName());
        newGoal.setDescription(goal.getDescription());
        newGoal.setRecommendation(goal.getRecommendation());
        newGoal.setType(goal.getType());
        newGoal.setTargetAmount(goal.getTargetAmount());
        newGoal.setTargetDate(goal.getTargetDate());
        newGoal.setOwner(user);
        newGoal.setCreatedAt(new Date());
        newGoal.setStatus(GoalStatusEnum.GOAL_PENDING);
        newGoal.setDeleted(false);
        newGoal.setAccount(account.get());

        if (goal.getType() == GoalTypeEnum.EXPENSE_REDUCTION) {
            newGoal.setInitialAmount(account.get().getBalance());
        }
        else {
            newGoal.setInitialAmount(BigDecimal.ZERO);
        }

        return newGoal;
    }
}
