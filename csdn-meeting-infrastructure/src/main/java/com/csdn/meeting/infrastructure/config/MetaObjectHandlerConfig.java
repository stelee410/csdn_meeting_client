package com.csdn.meeting.infrastructure.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class MetaObjectHandlerConfig {

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                LocalDateTime now = LocalDateTime.now();
                if (metaObject.hasSetter("createdAt") && getFieldValByName("createdAt", metaObject) == null) {
                    setFieldValByName("createdAt", now, metaObject);
                }
                if (metaObject.hasSetter("updatedAt") && getFieldValByName("updatedAt", metaObject) == null) {
                    setFieldValByName("updatedAt", now, metaObject);
                }
                if (metaObject.hasSetter("registeredAt") && getFieldValByName("registeredAt", metaObject) == null) {
                    setFieldValByName("registeredAt", now, metaObject);
                }
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                if (metaObject.hasSetter("updatedAt")) {
                    setFieldValByName("updatedAt", LocalDateTime.now(), metaObject);
                }
            }
        };
    }
}
