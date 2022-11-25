package com.takirahal.srfgroup;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SrfgroupApplication implements CommandLineRunner {

    public static void main(String[] args) {
        System.out.println("SrfgroupApplication runing ...");
        SpringApplication.run(SrfgroupApplication.class, args);
    }

    @Override
    public void run(String... args){
    }

    @GetMapping("test")
    public String hello(){
        return "Hello world";
    }
}
