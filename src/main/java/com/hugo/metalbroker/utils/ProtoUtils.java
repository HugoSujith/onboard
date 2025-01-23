package com.hugo.metalbroker.utils;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.protobuf.Struct;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.JsonFormat;
import org.springframework.stereotype.Component;

@Component
public class ProtoUtils {
    public Struct parseJsonToProto(JsonNode jsonData) throws Exception {
        String jsonToStr = jsonData.toString();
        Struct.Builder structBuilder = Struct.newBuilder();
        JsonFormat.parser().ignoringUnknownFields().merge(jsonToStr, structBuilder);
        return structBuilder.build();
    }

    public double getFieldValue(Struct historicData, String fieldName) {
        if (historicData.getFieldsMap().get(fieldName) != null) {
            return historicData.getFieldsMap().get(fieldName).getNumberValue();
        }
        return -1000;
    }

    public Timestamp localDateTimeToGoogleTimestamp(LocalDateTime localDateTime) {
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);

        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    public Timestamp sqlDateToGoogleTimestamp(Date date) {
        LocalDate localDate = date.toLocalDate();

        return Timestamp.newBuilder()
                .setSeconds(localDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC))
                .setNanos(0)
                .build();
    }
}
