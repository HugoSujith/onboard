package com.hugo.metalbroker.configuration;

import com.google.protobuf.util.JsonFormat;
import com.hugo.metalbroker.facades.FetchDataFacade;
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
    @Bean
    @Primary
    ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        JsonFormat.TypeRegistry typeRegistry = JsonFormat.TypeRegistry
                .newBuilder()
                .add(HistoricItems.getDescriptor())
                .add(HistoricItemsList.getDescriptor())
                .add(HistoricPerformance.getDescriptor())
                .add(SpotItems.getDescriptor())
                .add(SpotItemsList.getDescriptor())
                .add(Transactions.getDescriptor())
                .add(UserDTO.getDescriptor())
                .build();
        JsonFormat.Parser parser = JsonFormat.parser();
        JsonFormat.Printer printer = JsonFormat.printer()
                .usingTypeRegistry(typeRegistry);

        return new ProtobufJsonFormatHttpMessageConverter(parser, printer);
    }
}
