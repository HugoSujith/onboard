package com.hugo.metalbroker.controller;

import java.util.List;
import java.util.logging.Logger;

import com.hugo.metalbroker.model.datavalues.historic.HistoricItems;
import com.hugo.metalbroker.model.datavalues.historic.HistoricItemsList;
import com.hugo.metalbroker.model.datavalues.spot.SpotItems;
import com.hugo.metalbroker.model.datavalues.spot.SpotItemsList;
import com.hugo.metalbroker.service.implementations.FetchHistoricDataImpl;
import com.hugo.metalbroker.service.implementations.FetchSpotDataImpl;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class FetchDataController {
    private final FetchHistoricDataImpl historicData;
    private final FetchSpotDataImpl spotData;
    Logger log = Logger.getLogger("FetchHistoricData.class");

    public FetchDataController(FetchHistoricDataImpl historicData, FetchSpotDataImpl spotData) {
        this.historicData = historicData;
        this.spotData = spotData;
    }

    @GetMapping(path = "/historicData/{metal}", produces = MediaType.APPLICATION_JSON_VALUE)
    public HistoricItemsList getHistoricData(@PathVariable String metal) {
        if (metal.equals("gold") || metal.equals("silver")) {
            List<HistoricItems> data = historicData.getItems(metal);
            HistoricItemsList.Builder historicItemsListBuilder = HistoricItemsList.newBuilder();
            historicItemsListBuilder.addAllItems(data);
            return historicItemsListBuilder.build();
        } else {
            throw new RuntimeException("Metal not found");
        }
    }

    @GetMapping(path = "/spotData/{metal}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SpotItemsList getSpotData(@PathVariable String metal) {
        if (metal.equals("gold") || metal.equals("silver")) {
            List<SpotItems> data = spotData.getItems(metal);
            SpotItemsList.Builder spotItemsListBuilder = SpotItemsList.newBuilder();
            spotItemsListBuilder.addAllItems(data);
            return spotItemsListBuilder.build();
        } else {
            throw new RuntimeException("Metal not found");
        }
    }
}
