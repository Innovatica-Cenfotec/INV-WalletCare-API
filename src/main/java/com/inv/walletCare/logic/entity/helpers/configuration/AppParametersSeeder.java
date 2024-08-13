package com.inv.walletCare.logic.entity.helpers.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class AppParametersSeeder implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private AppParametersRepository appParametersRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        insertConfiguration();
    }

    private void insertConfiguration() {

        //Email Configuration
        var baseEmail = appParametersRepository.findByParamKey("BaseEmail");
        if (baseEmail.isEmpty()) {
            appParametersRepository.save(new AppParameters("BaseEmail", "info.walletcare@gmail.com"));
        }

        //Email Templates Configuration
        var templatesRoute = appParametersRepository.findByParamKey("TemplatesRoute");
        if (templatesRoute.isEmpty()) {
            appParametersRepository.save(new AppParameters("TemplatesRoute", "src/main/resources/templates/"));
        }

        //FrontEnd URL
        var frontUrl = appParametersRepository.findByParamKey("FrontUrl");
        if (frontUrl.isEmpty()) {
            appParametersRepository.save(new AppParameters("FrontUrl", "http://localhost:4200"));
        }

        //Exchange URL
        var exchangeURL = appParametersRepository.findByParamKey("ExchangeUrl");
        if (exchangeURL.isEmpty()) {
            appParametersRepository.save(new AppParameters("ExchangeUrl", "https://v6.exchangerate-api.com/v6/"));
        }

        //ExchangeApiKey
        var exchangeApiKey = appParametersRepository.findByParamKey("ExchangeApiKey");
        if (exchangeApiKey.isEmpty()) {
            appParametersRepository.save(new AppParameters("ExchangeApiKey", "8a6ea62c7344bdac099ac5ad"));
        }
    }


}
