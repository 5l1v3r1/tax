package com.softactive.spring;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.softactive.security.ActiveUserStore;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class AppConfig {
    // beans

    @Bean
    public ActiveUserStore activeUserStore() {
        return new ActiveUserStore();
    }
    
    @Bean
    public DataSource dataSource() {
    	HikariConfig config = new HikariConfig();
    	config.setUsername("root");
    	config.setPassword("selvaggiaa1988");
    	config.setJdbcUrl("jdbc:mysql://localhost:3306/tax?autoReconnect=true&useSSL=false");
    	config.setConnectionTimeout(600000);
    	config.setMaximumPoolSize(10);
    	config.setMinimumIdle(2);
    	return new HikariDataSource(config);
    }

}