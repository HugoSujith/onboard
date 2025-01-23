package com.hugo.metalbroker.service.implementation;

import java.util.List;

import com.hugo.metalbroker.facades.FetchDataFacade;
import com.hugo.metalbroker.model.datavalues.historic.HistoricItems;
import com.hugo.metalbroker.model.datavalues.historic.HistoricItemsList;
import com.hugo.metalbroker.service.HistoricData;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

@Service
public class HistoricDataImpl implements HistoricData {
    private final FetchDataFacade dataFacade;

    public HistoricDataImpl(FetchDataFacade dataFacade) {
        this.dataFacade = dataFacade;
    }

    @Override
    public HistoricItemsList fetchPaginatedData(String metal, int pageNum) {
        HistoricItemsList historicData = dataFacade.getHistoricData(metal);
        List<HistoricItems> itemsList = historicData.getItemsList();

        int entriesPerPage = Integer.parseInt(Dotenv.load().get("ENTRIES_PER_PAGE"));

        int fromIndex = entriesPerPage * (pageNum - 1);
        int toIndex = Math.min(fromIndex + entriesPerPage, itemsList.size());

        if (fromIndex >= itemsList.size() || fromIndex < 0) {
            return HistoricItemsList.newBuilder().build();
        }

        List<HistoricItems> paginatedList = itemsList.subList(fromIndex, toIndex);

        return HistoricItemsList.newBuilder().addAllItems(paginatedList).build();
    }
}
