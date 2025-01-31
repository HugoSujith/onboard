package com.hugo.metalbroker.service;

import com.hugo.metalbroker.model.datavalues.historic.HistoricItemsList;
import org.springframework.scheduling.annotation.Scheduled;

public interface HistoricData {

    HistoricItemsList fetchPaginatedData(String metal, int pageNum);

    @Scheduled(fixedRate = 10000)
    boolean data() throws Exception;

    boolean updateData(String url);

    boolean storeData(String url) throws Exception;
}
