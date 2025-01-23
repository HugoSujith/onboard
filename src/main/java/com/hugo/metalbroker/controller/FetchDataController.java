package com.hugo.metalbroker.controller;

import com.hugo.metalbroker.facades.FetchDataFacade;
import com.hugo.metalbroker.model.datavalues.historic.HistoricItemsList;
import com.hugo.metalbroker.model.datavalues.spot.SpotItemsList;
import com.hugo.metalbroker.repository.FetchSpotData;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class FetchDataController {
    private final FetchDataFacade dataFacade;
    private final FetchSpotData spotData;

    public FetchDataController(FetchDataFacade dataFacade, FetchSpotData spotData) {
        this.dataFacade = dataFacade;
        this.spotData = spotData;
    }

    @GetMapping(path = "/historicData/{metal}", produces = MediaType.APPLICATION_JSON_VALUE)
    public HistoricItemsList getHistoricData(@PathVariable String metal) {
        return dataFacade.getHistoricData(metal);
    }

    @GetMapping(path = "/spotData/{metal}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SpotItemsList getSpotData(@PathVariable String metal) {
        return dataFacade.getSpotData(metal);
    }
}
