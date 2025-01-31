package com.hugo.metalbroker.controller;

import com.hugo.metalbroker.model.datavalues.historic.HistoricItemsList;
import com.hugo.metalbroker.model.datavalues.spot.SpotItemsList;
import com.hugo.metalbroker.service.HistoricData;
import com.hugo.metalbroker.service.SpotData;
import com.hugo.metalbroker.utils.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final JWTUtils jwtUtils;

    public FetchDataController(SpotData spotData, HistoricData historicData, JWTUtils jwtUtils) {
        this.spotData = spotData;
        this.historicData = historicData;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(path = "/historicData/{metal}", produces = MediaType.APPLICATION_JSON_VALUE)
    public HistoricItemsList getHistoricDataPage(@PathVariable String metal, @RequestParam(defaultValue = "1") Integer pageNum, HttpServletRequest request, HttpServletResponse response) {
        HistoricItemsList items = historicData.fetchPaginatedData(metal, pageNum, request);
        jwtUtils.generateRefreshToken(request, response);
        return items;
    }

    @GetMapping(path = "/spotData/{metal}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SpotItemsList getSpotDataPage(@PathVariable String metal, @RequestParam(defaultValue = "1") Integer pageNum, HttpServletRequest request, HttpServletResponse response) {
        SpotItemsList items = spotData.fetchPaginatedData(metal, pageNum, request);
        jwtUtils.generateRefreshToken(request, response);
        return items;
    }
}
