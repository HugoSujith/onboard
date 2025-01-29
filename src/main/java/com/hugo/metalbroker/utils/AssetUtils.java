package com.hugo.metalbroker.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.hugo.metalbroker.exceptions.ApiFetchingFailureException;
import com.hugo.metalbroker.exceptions.CurrencyConversionException;
import com.hugo.metalbroker.model.datavalues.spot.SpotItems;
import com.hugo.metalbroker.model.transactions.TradeAssets;
import com.hugo.metalbroker.repository.FetchSpotData;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AssetUtils {
    private final FetchSpotData fetchSpotData;

    public AssetUtils(FetchSpotData fetchSpotData) {
        this.fetchSpotData = fetchSpotData;
    }

    public double getAssetValue(TradeAssets assets, String userCurrencyCode, boolean buy) {
        String url = "https://v6.exchangerate-api.com/v6/59841edd63cf8ac80453f82c/latest/" + userCurrencyCode;
        try {
            RestTemplate template = new RestTemplate();
            JsonNode jsonResponse = template.getForObject(url, JsonNode.class);
            double exchangeRate = 1;
            if (jsonResponse != null && jsonResponse.has("conversion_rates") && jsonResponse.get("conversion_rates").has("INR")) {
                exchangeRate = jsonResponse.get("conversion_rates").get("INR").asDouble();
            }
            SpotItems currentPrices = fetchSpotData.fetchCurrentPrices();
            if (currentPrices != null) {
                if (buy) {
                    return assets.getGrams() * currentPrices.getAsk() * exchangeRate;
                }
                return assets.getGrams() * currentPrices.getBid() * exchangeRate;
            }
            throw new CurrencyConversionException("");
        } catch (Exception e) {
            throw new ApiFetchingFailureException(this.getClass().getName());
        }
    }
}
