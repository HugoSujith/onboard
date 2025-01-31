package com.hugo.metalbroker.service;

import org.springframework.scheduling.annotation.Scheduled;

public interface HistoricPerformanceService {
    @Scheduled(fixedRate = 7200000)
    boolean data();

    boolean insertHistoricPerformanceDataToDB(String url);
}
