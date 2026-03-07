package com.csdn.meeting.infrastructure.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatisPlus配置类
 * 配置分页插件和自动填充字段
 */
@Configuration
@MapperScan("com.csdn.meeting.infrastructure.repository.mapper")
public class MybatisPlusConfig {

    /**
     * 分页插件配置
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件，指定数据库类型为MySQL
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 自动填充字段处理器
     * 统一处理 createTime/createdAt、updateTime/updatedAt、isDeleted、registeredAt 等字段
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                LocalDateTime now = LocalDateTime.now();
                fillIfNull(metaObject, "createTime", now);
                fillIfNull(metaObject, "createdAt", now);
                fillIfNull(metaObject, "updateTime", now);
                fillIfNull(metaObject, "updatedAt", now);
                fillIfNull(metaObject, "registeredAt", now);
                fillIfNull(metaObject, "isDeleted", 0);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                LocalDateTime now = LocalDateTime.now();
                if (metaObject.hasSetter("updateTime")) {
                    setFieldValByName("updateTime", now, metaObject);
                }
                if (metaObject.hasSetter("updatedAt")) {
                    setFieldValByName("updatedAt", now, metaObject);
                }
            }

            private void fillIfNull(MetaObject metaObject, String field, Object value) {
                if (metaObject.hasSetter(field) && getFieldValByName(field, metaObject) == null) {
                    setFieldValByName(field, value, metaObject);
                }
            }
        };
    }
}