package com.inv.walletCare.logic.entity.goal;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Represents a goal proposal record.
 */
public class GoalProposal {
    /**
     * The name of the goal.
     */
    private String name;

    /**
     * The description of the goal.
     */
    private String description;

    /**
     * The recommendation for the goal.
     */
    private String recommendation;

    /**
     * The type of the goal.
     */
    private GoalTypeEnum type;

    /**
     * Id reference to the account.
     */
    private long RefIdAccount;

    /**
     * Id reference to the saving.
     */
    private long RefIdSaving;

    /**
     * The target amount of the goal.
     */
    private BigDecimal targetAmount;

    /**
     * The target date of the goal.
     */
    private Date targetDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public long getRefIdSaving() {
        return RefIdSaving;
    }

    public void setRefIdSaving(long refIdSaving) {
        RefIdSaving = refIdSaving;
    }

    public long getRefIdAccount() {
        return RefIdAccount;
    }

    public void setRefIdAccount(long refIdAccount) {
        RefIdAccount = refIdAccount;
    }

    public GoalTypeEnum getType() {
        return type;
    }

    public void setType(GoalTypeEnum type) {
        this.type = type;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}