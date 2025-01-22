package com.hugo.metalbroker.service;

import java.util.List;

import com.hugo.metalbroker.model.datavalues.spot.SpotItems;
import org.springframework.scheduling.annotation.Scheduled;

public interface FetchSpotData {
    @Scheduled(fixedRate = 10000)
    boolean data();

    boolean updateData(String url);

    boolean storeData(String url);

    List<SpotItems> getItems(String metal);
}
