package com.hugo.metalbroker.service.implementation;

import java.util.List;

import com.hugo.metalbroker.facades.FetchDataFacade;
import com.hugo.metalbroker.model.datavalues.spot.SpotItems;
import com.hugo.metalbroker.model.datavalues.spot.SpotItemsList;
import com.hugo.metalbroker.service.SpotData;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

@Service
public class SpotDataImpl implements SpotData {
    private final FetchDataFacade dataFacade;

    public SpotDataImpl(FetchDataFacade dataFacade) {
        this.dataFacade = dataFacade;
    }

    @Override
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
