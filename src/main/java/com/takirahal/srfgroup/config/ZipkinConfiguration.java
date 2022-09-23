package com.takirahal.srfgroup.config;

import brave.sampler.Sampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ZipkinConfiguration {

    private final Logger log = LoggerFactory.getLogger(ZipkinConfiguration.class);

    @Autowired
    RestTemplate restTemplate;

    @RestController
    class ZipkinController{

        @Autowired
        RestTemplate restTemplate;

        @Bean
        public Sampler alwaysSampler() {
            return Sampler.ALWAYS_SAMPLE;
        }

        @Bean
        public RestTemplate getRestTemplate() {
            return new RestTemplate();
        }

        private final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(ZipkinController.class.getName());

        @GetMapping(value="/api/zipkin-server")
        public String zipkinService()
        {
            LOG.info("Inside zipkinService ...");
            String response = (String) restTemplate.exchange("http://127.0.0.1:9411/zipkin",
                    HttpMethod.GET, null, new ParameterizedTypeReference<String>() {}).getBody();

            return response;
        }
    }
}
