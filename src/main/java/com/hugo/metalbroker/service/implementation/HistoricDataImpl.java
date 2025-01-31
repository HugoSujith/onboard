package com.hugo.metalbroker.service.implementation;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.protobuf.Struct;
import com.hugo.metalbroker.exceptions.DataUpdationFailureException;
import com.hugo.metalbroker.facades.FetchDataFacade;
import com.hugo.metalbroker.model.datavalues.historic.HistoricItems;
import com.hugo.metalbroker.model.datavalues.historic.HistoricItemsList;
import com.hugo.metalbroker.repository.HistoricDataRepo;
import com.hugo.metalbroker.service.HistoricData;
import com.hugo.metalbroker.utils.APIUtil;
import com.hugo.metalbroker.utils.ProtoUtils;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class HistoricDataImpl implements HistoricData {
    private final FetchDataFacade dataFacade;
    private int checker = 0;
    private final ProtoUtils protoUtils;
    private final Logger log;
    private final HistoricDataRepo historicDataRepo;
    private final APIUtil apiUtil;

    public HistoricDataImpl(FetchDataFacade dataFacade, ProtoUtils protoUtils, HistoricDataRepo historicDataRepo, APIUtil apiUtil) {
        this.dataFacade = dataFacade;
        this.protoUtils = protoUtils;
        this.apiUtil = apiUtil;
        this.log = Logger.getLogger(this.getClass().getName());
        this.historicDataRepo = historicDataRepo;
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

    @Scheduled(fixedRate = 10000)
    @Override
    public boolean data() throws Exception {
        boolean historicDataSilver = false;
        boolean historicDataGold = false;
        if (checker == 0) {
            historicDataSilver = storeData(Dotenv.load().get("SILVER_HISTORIC_URL"));
            if (historicDataSilver) {
                log.info("Historic Prices of Silver has been inserted to database");
            }
            historicDataGold = storeData(Dotenv.load().get("GOLD_HISTORIC_URL"));
            if (historicDataGold) {
                log.info("Historic Prices of Gold has been inserted to database");
            }
        } else {
            historicDataSilver = updateData(Dotenv.load().get("SILVER_HISTORIC_URL"));
            if (historicDataSilver) {
                log.info("Historic Prices of Silver has been updated to database");
            }
            historicDataGold = updateData(Dotenv.load().get("GOLD_HISTORIC_URL"));
            if (historicDataGold) {
                log.info("Historic Prices of Gold has been updated to database");
            }
        }
        checker++;
        return (historicDataSilver && historicDataGold);
    }

    @Override
    public boolean updateData(String url) {
        String metal = url.equals(Dotenv.load().get("SILVER_HISTORIC_URL")) ? "silver" : "gold";
        ArrayNode embeddedItems = (ArrayNode) apiUtil.getResponse(url);

        if (embeddedItems != null && !embeddedItems.isEmpty()) {
            JsonNode historicDataJson = embeddedItems.get(embeddedItems.size() - 1);

            try {
                Struct historicData = protoUtils.parseJsonToProto(historicDataJson);
                LocalDate date = LocalDate.parse(historicData.getFieldsMap().get("date").getStringValue());
                Date sqlDate = Date.valueOf(date);
                if (!historicDataRepo.checkIfDataPresent(date, metal)) {
                    return historicDataRepo.insertIntoDB(metal, historicData, sqlDate) > 0;
                }
            } catch (Exception e) {
                throw new DataUpdationFailureException(this.getClass().getName());
            }
        }

        return false;
    }

    @Override
    public boolean storeData(String url) throws Exception {
        String metal = url.equals(Dotenv.load().get("SILVER_HISTORIC_URL")) ? "silver" : "gold";
        ArrayNode embeddedItems = (ArrayNode) apiUtil.getResponse(url);

        JsonNode historicDataJsonForCheck = embeddedItems.get(0);
        Struct historicDataForCheck = protoUtils.parseJsonToProto(historicDataJsonForCheck);
        LocalDate dateForCheck = LocalDate.parse(historicDataForCheck.getFieldsMap().get("date").getStringValue());

        if (!historicDataRepo.checkIfDataPresent(dateForCheck, metal)) {
            for (JsonNode historicDataJson : embeddedItems) {
                Struct historicData = protoUtils.parseJsonToProto(historicDataJson);
                LocalDate date = LocalDate.parse(historicData.getFieldsMap().get("date").getStringValue());
                Date sqlDate = Date.valueOf(date);

                int value = historicDataRepo.insertIntoDB(metal, historicData, sqlDate);
                if (value <= 0) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }
}
