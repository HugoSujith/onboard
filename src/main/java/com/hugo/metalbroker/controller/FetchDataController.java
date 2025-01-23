package com.hugo.metalbroker.controller;

import com.hugo.metalbroker.facades.FetchDataFacade;
import com.hugo.metalbroker.model.datavalues.historic.HistoricItemsList;
import com.hugo.metalbroker.model.datavalues.spot.SpotItemsList;
import com.hugo.metalbroker.repository.FetchSpotData;
import com.hugo.metalbroker.service.HistoricData;
import com.hugo.metalbroker.service.SpotData;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class FetchDataController {
    private final FetchDataFacade dataFacade;
    private final SpotData spotData;
    private final HistoricData historicData;

    public FetchDataController(FetchDataFacade dataFacade, SpotData spotData, HistoricData historicData) {
        this.dataFacade = dataFacade;
        this.spotData = spotData;
        this.historicData = historicData;
    }

    @GetMapping(path = "/historicData/{metal}", produces = MediaType.APPLICATION_JSON_VALUE)
    public HistoricItemsList getHistoricData(@PathVariable String metal) {
        return dataFacade.getHistoricData(metal);
    }

    @GetMapping(path = "/spotData/{metal}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SpotItemsList getSpotData(@PathVariable String metal) {
        return dataFacade.getSpotData(metal);
    }

    @GetMapping(path = "/historicData/{metal}/{pageNum}", produces = MediaType.APPLICATION_JSON_VALUE)
    public HistoricItemsList getHistoricDataPage(@PathVariable String metal, @PathVariable int pageNum) {
        return historicData.fetchPaginatedData(metal, pageNum);
    }

    @GetMapping(path = "/spotData/{metal}/{pageNum}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SpotItemsList getSpotDataPage(@PathVariable String metal, @PathVariable int pageNum) {
        return spotData.fetchPaginatedData(metal, pageNum);
    }
}
