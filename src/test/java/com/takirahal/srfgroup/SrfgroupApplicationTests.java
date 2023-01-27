package com.takirahal.srfgroup;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SrfgroupApplicationTests {

    @Value("${takieddinerahal}")
    String value;

    @Test
    void contextLoads() {
        System.out.println("Current value == "+value);
    }

}
