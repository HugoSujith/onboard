package com.hugo.metalbroker.service;

import org.springframework.scheduling.annotation.Scheduled;

public interface FetchSpotData {
    @Scheduled(fixedRate = 10000)
    boolean data();

    boolean updateData(String url);

    boolean storeData(String url);

}
