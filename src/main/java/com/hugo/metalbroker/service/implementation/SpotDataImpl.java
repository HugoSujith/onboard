package com.hugo.metalbroker.service.implementation;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.protobuf.Struct;
import com.hugo.metalbroker.exceptions.DataUpdationFailureException;
import com.hugo.metalbroker.facades.FetchDataFacade;
import com.hugo.metalbroker.model.datavalues.spot.SpotItems;
import com.hugo.metalbroker.model.datavalues.spot.SpotItemsList;
import com.hugo.metalbroker.repository.SpotDataRepo;
import com.hugo.metalbroker.service.SpotData;
import com.hugo.metalbroker.utils.APIUtil;
import com.hugo.metalbroker.utils.ProtoUtils;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SpotDataImpl implements SpotData {
    private final FetchDataFacade dataFacade;
    private int checker = 0;
    private final Logger log;
    private final SpotDataRepo spotDataRepo;
    private final ProtoUtils protoUtils;
    private final APIUtil apiUtil;

    public SpotDataImpl(FetchDataFacade dataFacade, SpotDataRepo spotDataRepo, ProtoUtils protoUtils, APIUtil apiUtil) {
        this.dataFacade = dataFacade;
        this.protoUtils = protoUtils;
        this.apiUtil = apiUtil;
        this.log = Logger.getLogger(this.getClass().getName());
        this.spotDataRepo = spotDataRepo;
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

    @Scheduled(fixedRate = 10000)
    @Override
    public boolean data() throws Exception {
        boolean spotDataSilver = false;
        boolean spotDataGold = false;
        if (checker == 0) {
            spotDataSilver = storeData(Dotenv.load().get("SILVER_SPOT_URL"));
            if (spotDataSilver) {
                log.info("Spot Prices of Silver has been inserted to database");
            }
            spotDataGold = storeData(Dotenv.load().get("GOLD_SPOT_URL"));
            if (spotDataGold) {
                log.info("Spot Prices of Gold has been inserted to database");
            }
        } else {
            spotDataSilver = updateData(Dotenv.load().get("SILVER_SPOT_URL"));
            if (spotDataSilver) {
                log.info("Spot Prices of Silver has been updated to database");
            }
            spotDataGold = updateData(Dotenv.load().get("GOLD_SPOT_URL"));
            if (spotDataGold) {
                log.info("Spot Prices of Gold has been updated to database");
            }
        }
        checker++;
        return (spotDataSilver && spotDataGold);
    }

    @Override
    public boolean updateData(String url) {
        String metal = url.equals(Dotenv.load().get("SILVER_SPOT_URL")) ? "silver" : "gold";
        ArrayNode embeddedItems = (ArrayNode) apiUtil.getResponse(url);

        if (embeddedItems != null && !embeddedItems.isEmpty()) {
            JsonNode spotDataJson = embeddedItems.get(embeddedItems.size() - 1);

            try {
                Struct spotData = protoUtils.parseJsonToProto(spotDataJson);
                String date = spotData.getFieldsMap().get("date").getStringValue();

                OffsetDateTime offsetDateTime = OffsetDateTime.parse(date);

                Instant instant = offsetDateTime.toInstant();

                Timestamp timestamp = Timestamp.from(instant);

                if (!spotDataRepo.checkIfDataPresent(timestamp, metal)) {
                    return spotDataRepo.insertIntoDB(metal, spotData, timestamp) > 0;
                }
            } catch (Exception e) {
                throw new DataUpdationFailureException(this.getClass().getName());
            }
        }

        return false;
    }

    @Override
    public boolean storeData(String url) throws Exception {
        String metal = url.equals(Dotenv.load().get("SILVER_SPOT_URL")) ? "silver" : "gold";
        ArrayNode embeddedItems = (ArrayNode) apiUtil.getResponse(url);

        JsonNode spotDataJsonForCheck = embeddedItems.get(0);
        Struct spotDataForCheck = protoUtils.parseJsonToProto(spotDataJsonForCheck);

        OffsetDateTime offsetDateTime = OffsetDateTime.parse(spotDataForCheck.getFieldsMap().get("date").getStringValue());
        Timestamp timestamp = Timestamp.from(offsetDateTime.toInstant());


        if (!spotDataRepo.checkIfDataPresent(timestamp, metal)) {
            for (JsonNode spotDataJson : embeddedItems) {
                Struct spotData = protoUtils.parseJsonToProto(spotDataJson);

                offsetDateTime = OffsetDateTime.parse(spotData.getFieldsMap().get("date").getStringValue());
                timestamp = Timestamp.from(offsetDateTime.toInstant());

                int value = spotDataRepo.insertIntoDB(metal, spotData, timestamp);
                if (value <= 0) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
