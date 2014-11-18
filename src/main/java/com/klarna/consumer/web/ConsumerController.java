package com.klarna.consumer.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.klarna.consumer.api.Consumer;
import com.klarna.consumer.service.ConsumerService;

/**
 * @author ankita walia
 * Controller class to save and retrieve consumer info using restful api 
 *
 */
@Controller
@RequestMapping("/consumers")
public class ConsumerController {

	 private final Logger logger = LoggerFactory.getLogger(getClass());
	 
	 @Autowired
	 private ConsumerService consumerservice;
	 
	 Map<Integer, Consumer> consumerData = new HashMap<Integer, Consumer>();
	 
	 
	 /**
	  * Method to save consumer info.
	  * 
	  * It validates the incoming request to check if all fields except "mobile_phone",
	  * "care_of" and "country" are present and contain a non whitespace character.
	  * 
	  * If request is valid then returns status code 200OK
	  * If request is invalid then returns status code 400 Bad Request
	  * 
	  * @return Map of String
	  * 
	  */
	
	 @RequestMapping(method = RequestMethod.POST)
	 @ResponseBody
	 public Map<String, String> saveConsumerInfo(@RequestBody Consumer consumer, HttpServletRequest request, HttpServletResponse response){
	        logger.info("Saving consumer info for later retrieval " );
	        Map<String, String> conData = null;
	        boolean validRequest = validateConsumerDataBeforeSaving(consumer);
	        if (validRequest){
	            conData = consumerservice.saveConsumerInfo(consumer);
	        } else{
	        	response.setStatus( HttpServletResponse.SC_BAD_REQUEST  );
	        }
	        
	        return conData;
	    
	 }
	 
	 private boolean validateConsumerDataBeforeSaving(final Consumer consumer) {
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

	 /**
	  * Method to get consumer info by giving Id in the request.
	  * 
	  * It retrieves the latest data stored for that customer
	  * and if no customer exist with the given Id then returns 404 Not Found
	  * 
	  * @PathVariable Consumer Id
	  * @return Consumer object
	  * 
	  */
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
	 
	/**
	  * Method to get consumer info by giving emailId in the request.
	  * 
	  * It retrieves the latest data stored for that customer
	  * and if no customer exist with the given emailId then returns 404 Not Found
	  * 
	  * @RequestParam email
	  * @return Consumer object
	  * 
	  */
	 @RequestMapping(method = GET, value ="/email")
	 @ResponseBody
	 public Consumer getConsumerInfoForEmail(@RequestParam("email") String email, HttpServletRequest request, HttpServletResponse response) {
		 logger.info("Fetch customer for customer email: " + email);
         Consumer conData = consumerservice.getConsumerInfoForEmail(email);  
         if (conData == null)
         {
        	 response.setStatus( HttpServletResponse.SC_NOT_FOUND  );
        	 
         }
	        return conData;
	 }

	 /**
	  * Method to get consumer info history by giving Id in the request.
	  * 
	  * It retrieves all the data stored for that customer and orders the data with the most recent one first and the oldest one last 
	  * and if no matching consumer found then returns an empty array
	  * 
	  * @PathVariable Consumer Id
	  * @return Object array
	  * 
	  */
	 @RequestMapping(method = GET, value ="/{id}/history")
	 @ResponseBody
	 public Object[] getConsumerHistoryById(@PathVariable("id") String id) {
		 logger.info("Fetch customer history for customer id: " + id);
		 ConcurrentLinkedDeque<Consumer> conData = consumerservice.getConsumerHistoryById(id);
		 if(conData == null)
		 {
			 return ArrayUtils.EMPTY_OBJECT_ARRAY;
		 }
	        return conData.toArray();
	 }
	     
}
	 
