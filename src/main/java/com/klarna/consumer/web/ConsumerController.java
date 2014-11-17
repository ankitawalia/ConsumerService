package com.klarna.consumer.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klarna.consumer.api.Consumer;
import com.klarna.consumer.service.ConsumerService;

@Controller
@RequestMapping("/consumers")
public class ConsumerController {

	 private final Logger logger = LoggerFactory.getLogger(getClass());
	 
	 @Autowired
	 private ConsumerService consumerservice;
	 
	 @Autowired
	 private ObjectMapper objMapper;
	 
	 Map<Integer, Consumer> consumerData = new HashMap<Integer, Consumer>();
	 
	 @RequestMapping(method = POST,produces="application/json")
	 @ResponseBody
	 public String saveConsumerInfo(@RequestBody Consumer consumer, HttpServletRequest request, HttpServletResponse response) {
	        logger.debug("Saving consumer info for later retrieval " );
	        String conData = null;
	        boolean validRequest = validateConsumerDataBeforeSaving(consumer);
	        if (validRequest){
	            conData = consumerservice.saveConsumerInfo(consumer);
	        } else{
	        	response.setStatus( HttpServletResponse.SC_BAD_REQUEST  );
	        }
	        
	        return conData;
	    }
	 
	 private boolean validateConsumerDataBeforeSaving(Consumer consumer) {
		if(StringUtils.isNotBlank(consumer.getEmail()) && 
				StringUtils.isNotBlank(consumer.getAddress().getCity()) && 
				StringUtils.isNotBlank(consumer.getAddress().getGivenName()) &&
				StringUtils.isNotBlank(consumer.getAddress().getSurname()) &&
				StringUtils.isNotBlank(consumer.getAddress().getStreet()) &&
				StringUtils.isNotBlank(consumer.getAddress().getStreetNo()) &&
			    StringUtils.isNotBlank(consumer.getAddress().getZipCode())){
			return true;
		}
		
		return false;
	}

	@RequestMapping(method = GET, value ="/{id}")
	 @ResponseBody
	 public Consumer getConsumerInfoForId(@PathVariable("id") String consumerId, HttpServletRequest request, HttpServletResponse response) {
		 logger.info("Fetch customer for customer ID: " + consumerId);
         Consumer conData = consumerservice.getConsumerInfoForId(consumerId);
         if (conData == null)
         {
        	 response.setStatus( HttpServletResponse.SC_NOT_FOUND  );
        	 
         }
         return conData;
	 }
	 
	 @RequestMapping(method = GET, value ="/email")
	 @ResponseBody
	 public Consumer getConsumerInfoForEmail(@RequestParam("email") String email, HttpServletRequest request, HttpServletResponse response) {
		 logger.info("Fetch customer for customer email: " + email);
		 System.out.println("Got consumer in controller for consumer Key in thread "+Thread.currentThread().getId());
         Consumer conData = consumerservice.getConsumerInfoForEmail(email);  
         if (conData == null)
         {
        	 response.setStatus( HttpServletResponse.SC_NOT_FOUND  );
        	 
         }
	        return conData;
	 }

	 @RequestMapping(method = GET, value ="/{id}/history")
	 @ResponseBody
	 public Object getConsumerHistoryById(@PathVariable("id") String id) {
		 logger.info("Fetch customer history for customer email: " + id);
		 
		 ConcurrentLinkedDeque<Consumer> conData = consumerservice.getConsumerHistoryById(id);
		 if(conData == null)
		 {
			 return ArrayUtils.EMPTY_OBJECT_ARRAY;
		 }
	        return conData.toArray();
	 }
	     
}
	 
