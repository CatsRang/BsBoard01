package com.bs.bsboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppMain {
    private final static Logger log = LoggerFactory.getLogger(AppMain.class);

    public static void main(String[] args) {
        SpringApplication.run(AppMain.class, args);
    }
}
