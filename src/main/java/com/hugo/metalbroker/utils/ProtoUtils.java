package com.hugo.metalbroker.utils;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Struct;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.JsonFormat;
import org.springframework.stereotype.Component;

@Component
public class ProtoUtils {
    public Struct parseJsonToProto(JsonNode jsonData) throws InvalidProtocolBufferException {
        String jsonToStr = jsonData.toString();
        Struct.Builder structBuilder = Struct.newBuilder();
        JsonFormat.parser().ignoringUnknownFields().merge(jsonToStr, structBuilder);
        return structBuilder.build();
    }

    public double getFieldValue(Struct data, String fieldName) {
        if (data.getFieldsMap().get(fieldName) != null) {
            return data.getFieldsMap().get(fieldName).getNumberValue();
        }
        return -1000;
    }

    public Timestamp localDateTimeToGoogleTimestamp(Instant instant) {
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
