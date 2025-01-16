package com.hugo.onboard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.hugo.onboard.model.datavalues.historic.HistoricItems;
import com.hugo.onboard.model.datavalues.spot.SpotItems;
import com.hugo.onboard.repository.HistoricRepository;
import com.hugo.onboard.repository.SpotRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FetchData {
    private final SpotRepository spotRepository;
    private final HistoricRepository historicRepository;

    public FetchData(SpotRepository spotRepository, HistoricRepository historicRepository) {
        this.spotRepository = spotRepository;
        this.historicRepository = historicRepository;
    }

    @Scheduled(fixedRate = 10000)
    public boolean updateDataRegularly() {
        JsonNode histData = storeHistoricData();
        JsonNode spotData = storeSpotData();
        return (histData != null && spotData != null);
    }

    public JsonNode storeSpotData() {
        String url = "https://goldbroker.com/api/spot-prices?metal=XAG&currency=PKR&weight_unit=g";
        try {
            RestTemplate restTemplate = new RestTemplate();
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            if (response != null) {
                ArrayNode embeddedItems = (ArrayNode) response.get("_embedded").get("items");
                for (JsonNode i : embeddedItems) {
                    SpotItems spotItem = new ObjectMapper().treeToValue(i, SpotItems.class);
                    spotRepository.save(spotItem);
                }
            }
            return response;
        } catch (Exception e) {
            System.out.println("No instance available! " + e.getMessage());
            return null;
        }
    }

    public JsonNode storeHistoricData() {
        String url = "https://goldbroker.com/api/historical-spot-prices?metal=XAG&currency=PKR&weight_unit=g";
        try {
            RestTemplate restTemplate = new RestTemplate();
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            if (response != null) {
                ArrayNode embeddedItems = (ArrayNode) response.get("_embedded").get("items");
                for (JsonNode i : embeddedItems) {
                    HistoricItems historicItem = new ObjectMapper().treeToValue(i, HistoricItems.class);
                    historicRepository.save(historicItem);
                }
            }
            return response;
        } catch (Exception e) {
            System.out.println("No instance available! " + e.getMessage());
            return null;
        }
    }
}
