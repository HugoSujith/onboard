package com.hugo.metalbroker.service;

import java.util.List;

import com.hugo.metalbroker.model.datavalues.historic.HistoricItems;
import org.springframework.scheduling.annotation.Scheduled;

public interface FetchHistoricData {
    @Scheduled(fixedRate = 10000)
    boolean data();

    boolean updateData(String url);

    boolean storeData(String url);

    List<HistoricItems> getItems(String metal);
}
