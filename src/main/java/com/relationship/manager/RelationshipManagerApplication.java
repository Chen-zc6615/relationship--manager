package com.relationship.manager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.relationship.manager.mapper")
@SpringBootApplication
public class RelationshipManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RelationshipManagerApplication.class, args);
    }
}
