package com.hugo.metalbroker.service;

import java.util.List;

import com.hugo.metalbroker.facades.FetchDataFacade;
import com.hugo.metalbroker.model.datavalues.spot.SpotItems;
import com.hugo.metalbroker.model.datavalues.spot.SpotItemsList;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

@Service
public class SpotData {
    private final FetchDataFacade dataFacade;

    public SpotData(FetchDataFacade dataFacade) {
        this.dataFacade = dataFacade;
    }

    public SpotItemsList fetchPaginatedData(String metal, int pageNum) {
        SpotItemsList spotData = dataFacade.getSpotData(metal);
        List<SpotItems> itemsList = spotData.getItemsList();

        int entriesPerPage = Integer.parseInt(Dotenv.load().get("ENTRIES_PER_PAGE"));

        int fromIndex = entriesPerPage * (pageNum - 1);
        int toIndex = Math.min(fromIndex + entriesPerPage, itemsList.size());

        if (fromIndex >= itemsList.size() || fromIndex < 0) {
            return SpotItemsList.newBuilder().build();
        }

        List<SpotItems> paginatedList = itemsList.subList(fromIndex, toIndex);

        return SpotItemsList.newBuilder().addAllItems(paginatedList).build();
    }

}
