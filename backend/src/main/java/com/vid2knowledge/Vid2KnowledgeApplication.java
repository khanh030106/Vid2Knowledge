package com.vid2knowledge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Vid2KnowledgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(Vid2KnowledgeApplication.class, args);
    }

}
