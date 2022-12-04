package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItServer {

    public static void main(String[] args) {
        System.getProperties().put("datasource.url", "jdbc:postgresql://localhost:5432/shareit");
        System.getProperties().put("datasource.username", "root");
        System.getProperties().put("datasource.password", "root");
        System.getProperties().put("server.port", 9090);
        SpringApplication.run(ShareItServer.class, args);
    }

}
