package com.example.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
//    public class SecurityExampleApplication extends SpringBootServletInitializer {
public class SecurityExampleApplication{
    //date to log file with log4j
    static {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            System.setProperty("current.date.time", dateFormat.format(new Date()));
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SecurityExampleApplication.class, args);
    }

}
