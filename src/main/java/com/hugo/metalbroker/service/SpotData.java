package com.hugo.metalbroker.service;

import com.hugo.metalbroker.model.datavalues.spot.SpotItemsList;
import org.springframework.scheduling.annotation.Scheduled;

public interface SpotData {
    SpotItemsList fetchPaginatedData(String metal, int pageNum);

    @Scheduled(fixedRate = 10000)
    boolean data() throws Exception;

    boolean updateData(String url);

    boolean storeData(String url) throws Exception;
}
