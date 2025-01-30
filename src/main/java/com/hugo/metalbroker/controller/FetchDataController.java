package com.hugo.metalbroker.controller;

import java.util.Optional;

import com.hugo.metalbroker.model.datavalues.historic.HistoricItemsList;
import com.hugo.metalbroker.model.datavalues.spot.SpotItemsList;
import com.hugo.metalbroker.service.HistoricData;
import com.hugo.metalbroker.service.SpotData;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class FetchDataController {
    private final SpotData spotData;
    private final HistoricData historicData;

    public FetchDataController(SpotData spotData, HistoricData historicData) {
        this.spotData = spotData;
        this.historicData = historicData;
    }

    @GetMapping(path = "/historicData/{metal}", produces = MediaType.APPLICATION_JSON_VALUE)
    public HistoricItemsList getHistoricDataPage(@PathVariable String metal, @RequestParam(defaultValue = "1") Integer pageNum) {
        return historicData.fetchPaginatedData(metal, pageNum);
    }

    @GetMapping(path = "/spotData/{metal}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SpotItemsList getSpotDataPage(@PathVariable String metal, @RequestParam(defaultValue = "1") Integer pageNum) {
        return spotData.fetchPaginatedData(metal, pageNum);
    }
}
