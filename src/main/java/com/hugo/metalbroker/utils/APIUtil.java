package com.hugo.metalbroker.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.hugo.metalbroker.exceptions.ApiFetchingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class APIUtil {
    public JsonNode getResponse(String url) {
        JsonNode response = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            response = restTemplate.getForObject(url, JsonNode.class);
        } catch (Exception e) {
            throw new ApiFetchingFailureException(this.getClass().getName());
        }

        if (response != null) {
            return response.get("_embedded").get("items");
        }
        return null;
    }
}
