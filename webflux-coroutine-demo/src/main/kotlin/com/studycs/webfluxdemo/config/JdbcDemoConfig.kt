package com.studycs.webfluxdemo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration(proxyBeanMethods = false)
class JdbcDemoConfig {
    @Bean
    fun dataSource(): DataSource =
        DriverManagerDataSource(
            "jdbc:h2:mem:webfluxdemo;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
            "sa",
            "",
        )

    @Bean
    fun jdbcTemplate(dataSource: DataSource): JdbcTemplate =
        JdbcTemplate(dataSource)
}

