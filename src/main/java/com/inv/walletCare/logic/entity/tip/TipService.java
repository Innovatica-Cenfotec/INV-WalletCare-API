package com.inv.walletCare.logic.entity.tip;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.inv.walletCare.logic.entity.account.AccountRepository;
import com.inv.walletCare.logic.entity.notification.NotificationDTO;
import com.inv.walletCare.logic.entity.notification.NotificationService;
import com.inv.walletCare.logic.entity.notification.NotificationType;
import com.inv.walletCare.logic.entity.recurrence.RecurrenceRepository;
import com.inv.walletCare.logic.entity.rol.RoleEnum;
import com.inv.walletCare.logic.entity.transaction.TransactionRepository;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.entity.user.UserRepository;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class TipService {
    private final AccountRepository accountRepository;
    private final RecurrenceRepository recurrenceRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public TipService(AccountRepository accountRepository, RecurrenceRepository recurrenceRepository,
                      TransactionRepository transactionRepository, UserRepository userRepository,
                      NotificationService notificationService) {
        this.accountRepository = accountRepository;
        this.recurrenceRepository = recurrenceRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    interface TipExtractor {
        @UserMessage(fromResource = "/prompts/create_tip.txt")
        TipProposal extractTip(@V("data_user") String data, @V("current_date") String currentDate);
    }

    /**
     * Run the service to send a notification with a new tip to all registered users.
     */
    public void runService() throws Exception {
        var users = userRepository.findAll();
        for (var user : users.stream().filter(u -> u.getRole().getName() == RoleEnum.USER).toList()) {
            TipProposal tip = createTip(user);
            if (tip != null) {
                NotificationDTO notification = new NotificationDTO();
                notification.setReceiverEmail(user.getEmail());
                notification.setType(NotificationType.TIP);
                notification.setTitle(tip.getName());
                notification.setMessage(tip.getDescription());
                notificationService.sendNotificationByUserEmail(notification);
            }
        }
    }

    /**
     * Build the JSON object for the given user
     * @param user The user to build the JSON object for
     * @return The JSON object
     */
    private String BuilderJson(User user) {
        var recurrences = recurrenceRepository.findAllByOwner(user.getId()).get();
        var accounts = accountRepository.findAllByOwnerId(user.getId());
        var transactions = transactionRepository.findAllbyOwner(user.getId());

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
            accountObject.add("savings", savingsArray);
            accountObject.add("transactions", transactionsArray);
            accountsArray.add(accountObject);
        }

        jsonObject.add("accounts", accountsArray);
        return new Gson().toJson(jsonObject);
    }

    /**
     * Create a tip for the given user
     * @param user The user to create the tip for
     * @return The created tip
     */
    public TipProposal createTip(User user) {
        // AI config
        ChatLanguageModel model = VertexAiGeminiChatModel.builder()
                .project(System.getenv("PROJECT_ID"))
                .location(System.getenv("LOCATION"))
                .modelName("gemini-1.5-flash-001")
                .temperature(0f)
                .topK(1)
                .build();

        String current_date = Date.from(Instant.now()).toString();
        String data = BuilderJson(user);

        TipExtractor extractor = AiServices.create(TipExtractor.class, model);
        var tip = extractor.extractTip(data, current_date);

        TipProposal newTip = new TipProposal();
        newTip.setName(tip.getName());
        newTip.setDescription(tip.getDescription());

        return newTip;
    }
}