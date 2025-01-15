package com.hugo.onboard.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FetchData {
    @Scheduled(fixedRate = 1000)
    public JsonNode getSpotData() {
        String url = "https://goldbroker.com/api/spot-prices?metal=XAG&currency=PKR&weight_unit=g";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, JsonNode.class);
    }

    @Scheduled(fixedRate = 1000)
    public JsonNode getHistoricData() {
        String url = "https://goldbroker.com/api/historical-spot-prices?metal=XAG&currency=PKR&weight_unit=g";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, JsonNode.class);
    }
}
