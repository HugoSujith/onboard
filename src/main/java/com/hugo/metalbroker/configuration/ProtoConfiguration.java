package com.hugo.metalbroker.configuration;

import com.google.protobuf.util.JsonFormat;
import com.hugo.metalbroker.model.datavalues.historic.HistoricItems;
import com.hugo.metalbroker.model.datavalues.historic.HistoricItemsList;
import com.hugo.metalbroker.model.datavalues.historic.HistoricPerformance;
import com.hugo.metalbroker.model.datavalues.spot.SpotItems;
import com.hugo.metalbroker.model.datavalues.spot.SpotItemsList;
import com.hugo.metalbroker.model.transactions.Transactions;
import com.hugo.metalbroker.model.user.UserDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;

@Configuration
public class ProtoConfiguration {

    private static JsonFormat.TypeRegistry createTypeRegistry() {
        return JsonFormat.TypeRegistry
                .newBuilder()
                .add(HistoricItems.getDescriptor())
                .add(HistoricItemsList.getDescriptor())
                .add(HistoricPerformance.getDescriptor())
                .add(SpotItems.getDescriptor())
                .add(SpotItemsList.getDescriptor())
                .add(Transactions.getDescriptor())
                .add(UserDTO.getDescriptor())
                .build();
    }

    private static JsonFormat.Parser createParser() {
        return JsonFormat.parser();
    }

    private static JsonFormat.Printer createPrinter(JsonFormat.TypeRegistry typeRegistry) {
        return JsonFormat.printer().usingTypeRegistry(typeRegistry);
    }

    @Bean
    @Primary
    public ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        JsonFormat.TypeRegistry typeRegistry = createTypeRegistry();
        JsonFormat.Parser parser = createParser();
        JsonFormat.Printer printer = createPrinter(typeRegistry);

        return new ProtobufJsonFormatHttpMessageConverter(parser, printer);
    }
}
