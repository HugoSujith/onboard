package com.hugo.metalbroker.service;

import com.hugo.metalbroker.model.datavalues.spot.SpotItemsList;

public interface SpotData {
    SpotItemsList fetchPaginatedData(String metal, int pageNum);
}
