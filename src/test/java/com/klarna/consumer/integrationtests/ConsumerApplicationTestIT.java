package com.klarna.consumer.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.klarna.consumer.api.Address;
import com.klarna.consumer.api.Consumer;
import com.klarna.consumer.application.ConsumerApplication;

public class ConsumerApplicationTestIT {

    private static final ConsumerApplication APPLICATION = new ConsumerApplication(8080);
    private final RestTemplate restTemplate = restTemplate();

    @BeforeClass
    public static void init() throws Exception {
        APPLICATION.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        APPLICATION.stop();
    }

    private HttpMessageConverter<?> jacksonConverter() {
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
        jacksonConverter.setObjectMapper(objectMapper());
        return jacksonConverter;
    }

    private ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        return objectMapper;
    }

    private RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(jacksonConverter());
        restTemplate.setMessageConverters(converters);

        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                // Let the errors pass and get handled by the caller
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
            }
        });

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        restTemplate.setRequestFactory(requestFactory);
        return restTemplate;
    }

    @Test
    public void pingTest() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/ping", String.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("pong", response.getBody());
    }
    
    Address address;
    
    @Test
    private void testSaveConsumerInfo() {
        RestTemplate restTemplate = new RestTemplate();
        
        Consumer consumer = new Consumer();
        consumer.withConsumerId("1");
        consumer.withEmail("test@test.com");
        consumer.withMobilePhone("070-1234567");
        consumer.withAddress(address.withSurname("doe"));
        consumer.withAddress(address.withGivenName("John"));
        consumer.withAddress(address.withCity("Stockholm"));
        consumer.withAddress(address.withCountry("sweden"));
        consumer.withAddress(address.withStreet("Some sTREET"));
        consumer.withAddress(address.withStreetNo("123"));
        consumer.withAddress(address.withZipCode("178 23"));
        consumer.withAddress(address.withCareOf("someoNE"));
        ResponseEntity<Consumer> response = restTemplate.postForEntity("http://localhost:8080/consumer/data", consumer, Consumer.class);
        assertNotNull(response);
        System.out.println(response);
    }
}
