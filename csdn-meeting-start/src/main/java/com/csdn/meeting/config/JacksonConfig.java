package com.csdn.meeting.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;

/**
 * Jackson 全局配置：
 * 1. LocalDateTime 兼容带时区后缀（"Z" / "+08:00"）的 ISO-8601 字符串
 * 2. LocalTime 同时兼容字符串格式（"12:02:00"）和对象格式（{"hour":12,"minute":2,"second":0,"nano":0}）
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // LocalDateTime：兼容 "2026-03-14T16:00:00.000Z" 等带时区偏移的字符串
        javaTimeModule.addDeserializer(LocalDateTime.class, new FlexibleLocalDateTimeDeserializer());

        // LocalTime：兼容字符串 "12:02:00" 和对象 {"hour":12,"minute":2,"second":0,"nano":0}
        SimpleModule localTimeModule = new SimpleModule();
        localTimeModule.addDeserializer(LocalTime.class, new FlexibleLocalTimeDeserializer());

        mapper.registerModule(javaTimeModule);
        mapper.registerModule(localTimeModule);

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        return mapper;
    }

    /**
     * LocalDateTime 反序列化器：
     * 优先按 ISO_LOCAL_DATE_TIME 解析，失败则尝试带时区的 ISO_OFFSET_DATE_TIME / ISO_ZONED_DATE_TIME，
     * 最终统一转为不含时区的 LocalDateTime（以服务器本地语义存储）。
     */
    static class FlexibleLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

        private static final DateTimeFormatter ISO_FLEX = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .optionalStart()
                .appendOffsetId()
                .optionalEnd()
                .optionalStart()
                .appendLiteral('[')
                .parseCaseSensitive()
                .appendZoneRegionId()
                .appendLiteral(']')
                .optionalEnd()
                .toFormatter();

        FlexibleLocalDateTimeDeserializer() {
            super(LocalDateTime.class);
        }

        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            String text = p.getText().trim();
            try {
                return LocalDateTime.from(ISO_FLEX.parse(text));
            } catch (DateTimeParseException e) {
                throw ctx.weirdStringException(text, LocalDateTime.class,
                        "无法解析时间，请使用 ISO-8601 格式，如 \"2026-03-14T16:00:00\" 或 \"2026-03-14T16:00:00.000Z\"");
            }
        }
    }

    /**
     * LocalTime 反序列化器：
     * 同时支持字符串格式（"12:02"、"12:02:00"）和 Java 对象格式（{"hour":12,"minute":2,...}）。
     */
    static class FlexibleLocalTimeDeserializer extends StdDeserializer<LocalTime> {

        FlexibleLocalTimeDeserializer() {
            super(LocalTime.class);
        }

        @Override
        public LocalTime deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            if (p.currentToken() == JsonToken.VALUE_STRING) {
                String text = p.getText().trim();
                try {
                    return LocalTime.parse(text);
                } catch (DateTimeParseException e) {
                    throw ctx.weirdStringException(text, LocalTime.class,
                            "无法解析时间，请使用 \"HH:mm\" 或 \"HH:mm:ss\" 格式，如 \"12:02\" 或 \"12:02:00\"");
                }
            }

            if (p.currentToken() == JsonToken.START_OBJECT) {
                int hour = 0, minute = 0, second = 0, nano = 0;
                while (p.nextToken() != JsonToken.END_OBJECT) {
                    String field = p.currentName();
                    p.nextToken();
                    switch (field) {
                        case "hour":   hour   = p.getIntValue(); break;
                        case "minute": minute = p.getIntValue(); break;
                        case "second": second = p.getIntValue(); break;
                        case "nano":   nano   = p.getIntValue(); break;
                        default: p.skipChildren(); break;
                    }
                }
                return LocalTime.of(hour, minute, second, nano);
            }

            throw ctx.wrongTokenException(p, LocalTime.class, JsonToken.VALUE_STRING,
                    "LocalTime 需要字符串格式（如 \"12:02:00\"）或对象格式（{\"hour\":12,\"minute\":2,...}）");
        }
    }
}
