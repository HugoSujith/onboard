package com.hugo.metalbroker.service;

import com.hugo.metalbroker.model.datavalues.historic.HistoricItemsList;

public interface HistoricData {
    HistoricItemsList fetchPaginatedData(String metal, int pageNum);
}
