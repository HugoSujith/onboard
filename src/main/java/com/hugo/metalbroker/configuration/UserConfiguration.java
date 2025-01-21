package com.hugo.metalbroker.configuration;

import com.google.protobuf.util.JsonFormat;

import com.hugo.metalbroker.model.user.UserDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;

@Configuration
public class UserConfiguration {
    @Bean
    @Primary
    ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        JsonFormat.TypeRegistry typeRegistry = JsonFormat.TypeRegistry
                .newBuilder()
                .add(UserDTO.getDescriptor())
                .build();
        JsonFormat.Parser parser = JsonFormat.parser();
        JsonFormat.Printer printer = JsonFormat.printer()
                .usingTypeRegistry(typeRegistry);

        return new ProtobufJsonFormatHttpMessageConverter(parser, printer);
    }
}
