package com.hugo.metalbroker.facades;

import com.hugo.metalbroker.exceptions.MetalNotFoundException;
import com.hugo.metalbroker.model.datavalues.historic.HistoricItemsList;
import com.hugo.metalbroker.model.datavalues.spot.SpotItemsList;
import com.hugo.metalbroker.repository.FetchHistoricData;
import com.hugo.metalbroker.repository.FetchSpotData;
import com.hugo.metalbroker.utils.RedisHistoricDataUtils;
import com.hugo.metalbroker.utils.RedisSpotDataUtils;
import org.springframework.stereotype.Component;

@Component
public class FetchDataFacade {
    private final FetchHistoricData historicData;
    private final FetchSpotData spotData;
    private final RedisHistoricDataUtils redisHistoricList;
    private final RedisSpotDataUtils redisSpotList;

    public FetchDataFacade(FetchHistoricData historicData, FetchSpotData spotData, RedisHistoricDataUtils redisHistoricList, RedisSpotDataUtils redisSpotList) {
        this.historicData = historicData;
        this.spotData = spotData;
        this.redisHistoricList = redisHistoricList;
        this.redisSpotList = redisSpotList;
    }

    public HistoricItemsList getHistoricData(String metal) {
        if (metal.equals("gold") || metal.equals("silver")) {
            String key = "historicData::" + metal;
            if (redisHistoricList.getValue(key) != null) {
                return redisHistoricList.getValue(key);
            }
            HistoricItemsList data = historicData.getItems(metal);
            redisHistoricList.setValue(key, data);
            return data;
        } else {
            throw new MetalNotFoundException(metal);
        }
    }

    public SpotItemsList getSpotData(String metal) {
        if (metal.equals("gold") || metal.equals("silver")) {
            String key = "spotData::" + metal;
            if (redisSpotList.getValue(key) != null) {
                return redisSpotList.getValue(key);
            }
            SpotItemsList data = spotData.getItems(metal);
            redisSpotList.setValue(key, data);
            return data;
        } else {
            throw new MetalNotFoundException(metal);
        }
    }
}
