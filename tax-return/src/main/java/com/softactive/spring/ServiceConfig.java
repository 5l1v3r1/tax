package com.softactive.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({ "com.softactive.service" })
public class ServiceConfig {
}
