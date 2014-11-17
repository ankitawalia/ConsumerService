package com.klarna.consumer.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @org.testng.annotations.BeforeClass
    public static void init() throws Exception {
        APPLICATION.start();
    }

    @org.testng.annotations.AfterClass
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

    @org.testng.annotations.Test(invocationCount=10000,threadPoolSize=20)
    public void pingTest() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/ping", String.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("pong", response.getBody());
    }
    
    
    @org.testng.annotations.Test(invocationCount=10000,threadPoolSize=10)
    public void testSaveConsumerInfo() {
        RestTemplate rest = new RestTemplate();
        String email = Thread.currentThread().getId()+"@test.com";
        String a = "{\"email\":\""+email+"\", \"address\": { \"given_name\": \"john\", \"surname\": \"doe\", \"street\": \" Some   street\", \"street_no\": \"123\", \"zip_code\": \"178 23\", \"city\": \"Stockholm\"}}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> http = new HttpEntity<String>(a, headers);
        String response = rest.postForObject("http://localhost:8080/consumers",http, String.class);
        Consumer response1 = rest.getForObject("http://localhost:8080/consumers/email?email="+Thread.currentThread().getId()+"@test.com", Consumer.class);
        System.out.println("Response "+response+ " in thread"+Thread.currentThread().getId());
        System.out.println("Response "+response1.toString()+ " in thread"+Thread.currentThread().getId());
      //  assertNotNull(res);
    }
    

   /* @Test
    public void testUniqueIdGeneration()
    {
             int threadCount = 1000;
            
               Callable<Boolean> task = new Callable<Boolean>() {
                   @Override
                   public Boolean call() {
                	   RestTemplate restTemplate = new RestTemplate();
                	   Consumer consumer = new Consumer();
                        // String sessionId = "testGetQueueSize-sessionId"+ Thread.currentThread().getId();
                       // String callId = "testGetQueueSize-callId"+ Thread.currentThread().getId();
                        ResponseEntity<Consumer> response = restTemplate.postForEntity("http://localhost:8080/consumers", consumer, Consumer.class);
                        assertNotNull(response);
                       
                   }
               };
               List<Callable<Boolean>> tasks = Collections.nCopies(threadCount, task);
               ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
               List<Future<Boolean>> futures = new ArrayList<>(threadCount);
               int exceptionCount = 0;
                               try {
                                     futures = executorService.invokeAll(tasks);
                               } catch (Exception e) {
                                     System.out.println("exception in invokeall");
                                    
                               }
                              
                               for (Future<Boolean> future : futures) {
                                    
                                     try {
                                            future.get();
                                     } catch (Exception e) {
                                            exceptionCount++;
                                     }
                               }
               // Check for exceptions
                        //int expectedQueueSize = theQueue.getQueueSize(serviceEndpointId);
                       // System.out.println("printing total queue size" + expectedQueueSize);
                       // System.out.println("printing total exceptionCount" + exceptionCount);
               //assertEquals(threadCount-exceptionCount, expectedQueueSize);
                               
        }*/
}
