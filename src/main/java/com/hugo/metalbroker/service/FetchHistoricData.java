package com.hugo.metalbroker.service;

import org.springframework.scheduling.annotation.Scheduled;

public interface FetchHistoricData {
    @Scheduled(fixedRate = 10000)
    boolean data();

    boolean updateData(String url);

    boolean storeData(String url);
}
