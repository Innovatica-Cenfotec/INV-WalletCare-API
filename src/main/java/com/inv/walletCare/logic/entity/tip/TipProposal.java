package com.inv.walletCare.logic.entity.tip;

public class TipProposal {
    /**
     * The name of the tip.
     */
    private String name;

    /**
     * The description of the tip.
     */
    private String description;

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
