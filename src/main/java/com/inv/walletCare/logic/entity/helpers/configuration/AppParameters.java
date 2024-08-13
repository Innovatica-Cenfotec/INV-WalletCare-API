package com.inv.walletCare.logic.entity.helpers.configuration;

import jakarta.persistence.*;

@Entity
@Table(name="app_parameters")
public class AppParameters {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "param_key", length = 50,  unique = true, nullable = false)
    private String paramKey;

    @Column(name = "param_value", length = 50, nullable = false)
    private String paramValue;

    public AppParameters() {
    }

    public AppParameters(String paramKey, String paramValue) {
        this.paramKey = paramKey;
        this.paramValue = paramValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }
}
