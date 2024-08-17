package com.inv.walletCare.logic.entity.tools.exchange;

import com.inv.walletCare.logic.entity.helpers.configuration.AppParametersRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExchangeService {

    @Autowired
    private AppParametersRepository appParametersRepository;

    private String API_KEY;
    private String API_URL;

    public List<CurrencyCodesDTO> getCodes() throws IOException {
        this.API_KEY = this.appParametersRepository.findByParamKey("ExchangeApiKey").get().getParamValue();
        this.API_URL = this.appParametersRepository.findByParamKey("ExchangeUrl").get().getParamValue() + API_KEY;

        var conn = (HttpURLConnection) new URL(API_URL + "/codes").openConnection();
        conn.setRequestMethod("GET");
        var in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        }

        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        conn.disconnect();

        JSONObject jsonResponse = new JSONObject(content.toString());
        JSONArray supportedCodes = jsonResponse.getJSONArray("supported_codes");
        List<CurrencyCodesDTO> codes = new ArrayList<CurrencyCodesDTO>();

        for (int i = 0; i < supportedCodes.length(); i++) {
            JSONArray codePair = supportedCodes.getJSONArray(i);
            codes.add(new CurrencyCodesDTO(codePair.getString(0), codePair.getString(1)));
        }
        return codes;
    }

    public CurrencyExchangeDTO getExchangeRate (CurrencyExchangeDTO currencyExchange) throws IOException {
        var exchangeRate = getExchangeRate(currencyExchange.getCurrencyFrom(), currencyExchange.getCurrencyTo());
        currencyExchange.setExchangeValue(exchangeRate * currencyExchange.getAmount());
        return currencyExchange;
    }

    private float getExchangeRate(String currencyFrom, String currencyTo) throws IOException {

        this.API_KEY = this.appParametersRepository.findByParamKey("ExchangeApiKey").get().getParamValue();
        this.API_URL = this.appParametersRepository.findByParamKey("ExchangeUrl").get().getParamValue() + API_KEY;

        var conn = (HttpURLConnection) new URL(API_URL + "/latest/" + currencyFrom).openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        }
        var in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        conn.disconnect();


        JSONObject data = new JSONObject(content.toString());
        var exchange = data.getJSONObject("conversion_rates").getFloat(currencyTo);
        return exchange;


    }
}
