package com.klarna.consumer.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.klarna.consumer.api.Consumer;
import com.klarna.consumer.application.ConsumerApplication;


public class ConsumerApplicationTestIT {

    private static final ConsumerApplication APPLICATION = new ConsumerApplication(8080);
    private final RestTemplate restTemplate = restTemplate();
    private final String invalidData = "{\"email\":\"test@test.com\", \"address\": { \"given_name\": \"  \", \"surname\": \"doe\", \"street\": \" Some   street\", \"street_no\": \"123\", \"zip_code\": \"178 23\", \"city\": \"Stockholm\"}}";
    private final String validData = "{\"email\":\"test@test.com\", \"address\": { \"given_name\": \" John \", \"surname\": \"doe\", \"street\": \" Some   street\", \"street_no\": \"123\", \"zip_code\": \"178 23\", \"city\": \"Stockholm\"}}";
    private final String validData2 = "{\"email\":\"test@test.com\", \"address\": { \"given_name\": \" John \", \"surname\": \"doe\", \"street\": \" Some   street\", \"street_no\": \"123456\", \"zip_code\": \"178 23\", \"city\": \"Stockholm\"}}";
    private final String dataToNormalize = "{\"email\":\"normalize@test.com\", \"address\": { \"given_name\": \" john \", \"surname\": \"doe\", \"street\": \" Some    street\", \"street_no\": \"456\", \"zip_code\": \"178 23\", \"city\": \"SToCKholm\"}}";

    
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
    
    
    @SuppressWarnings({ "unused", "unchecked" })
    @Test
    public void testSaveConsumerInfo() {
          Consumer consumer = null;
		try {
			consumer = objectMapper().readValue(validData, Consumer.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
        Map<String, String> response = new HashMap<String, String>();
        response = restTemplate.postForObject("http://localhost:8080/consumers",consumer, Map.class);
        Consumer response1 = restTemplate.getForObject("http://localhost:8080/consumers/email?email=test@test.com", Consumer.class);
        assertNotNull(response1);
        
    }
    
    @SuppressWarnings("rawtypes")
	@Test
    public void testInvalidConsumer() {
          Consumer consumer = null;
		try {
			consumer = objectMapper().readValue(invalidData, Consumer.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ResponseEntity<Map> response = restTemplate.postForEntity("http://localhost:8080/consumers",consumer, Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
    }
    
    @Test
    public void testInvalidId() {
         
    	ResponseEntity<Consumer> response1 = restTemplate.getForEntity("http://localhost:8080/consumers/test", Consumer.class);
        assertEquals(HttpStatus.NOT_FOUND,response1.getStatusCode());
        
    }
    
    @SuppressWarnings("unchecked")
	@Test
    public void testGetCustomerInfoById() {
          Consumer consumer = null;
		try {
			consumer = objectMapper().readValue(validData, Consumer.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
        Map<String,String> response = restTemplate.postForObject("http://localhost:8080/consumers",consumer, Map.class);
        Consumer response1 = restTemplate.getForObject("http://localhost:8080/consumers/"+response.get("consumer_id"), Consumer.class);
        assertNotNull(response1);
        
    }
    
    @SuppressWarnings({ "unused", "unchecked" })
	@Test
    public void testGetCustomerHistoryById() {
          Consumer consumer = null;
          Consumer consumer1 = null;
		try {
			consumer = objectMapper().readValue(validData, Consumer.class);
			consumer1 = objectMapper().readValue(validData2, Consumer.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String,String> response = restTemplate.postForObject("http://localhost:8080/consumers",consumer, Map.class);
		Map<String,String> response2 = restTemplate.postForObject("http://localhost:8080/consumers",consumer, Map.class);
        Object[] response1 = restTemplate.getForObject("http://localhost:8080/consumers/"+response.get("consumer_id")+ "/history", Object[].class);
        assertEquals(1,response1.length);
        Map<String,String> responseFromPost1 = restTemplate.postForObject("http://localhost:8080/consumers",consumer1, Map.class);
        Map<String,String> responseFromPost2 = restTemplate.postForObject("http://localhost:8080/consumers",consumer1, Map.class);
        Object[] responseObject = restTemplate.getForObject("http://localhost:8080/consumers/"+responseFromPost1.get("consumer_id")+ "/history", Object[].class);
        assertEquals(2,responseObject.length);
        
    }
    
    @SuppressWarnings({ "unused", "unchecked" })
    @Test
    public void testDataNormalization() {
          Consumer consumer = null;
		try {
			consumer = objectMapper().readValue(dataToNormalize, Consumer.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String,String> response = restTemplate.postForObject("http://localhost:8080/consumers",consumer, Map.class);
        Consumer response1 = restTemplate.getForObject("http://localhost:8080/consumers/email?email=normalize@test.com", Consumer.class);
        assertNotNull(response1);
        assertEquals("John", response1.getAddress().getGivenName());
        assertEquals("Doe", response1.getAddress().getSurname());
        assertEquals("Some Street", response1.getAddress().getStreet());
        assertEquals("456", response1.getAddress().getStreetNo());
        assertEquals("178 23", response1.getAddress().getZipCode());
        assertEquals("Stockholm", response1.getAddress().getCity());
        assertEquals(null, response1.getAddress().getCountry());
        assertEquals(null, response1.getAddress().getCareOf());
        
    }
    
}
