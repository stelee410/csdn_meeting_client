package com.csdn.meeting.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.csdn.meeting.infrastructure.repository")
public class JpaConfig {
}
