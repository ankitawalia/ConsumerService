package com.klarna.consumer.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.klarna.consumer.api.Consumer;
import com.klarna.consumer.service.ConsumerService;

@Controller
@RequestMapping("/consumers")
public class ConsumerController {

	 private final Logger logger = LoggerFactory.getLogger(getClass());
	 
	 @Autowired
	 ConsumerService consumerservice;
	 
	 Map<Integer, Consumer> consumerData = new HashMap<Integer, Consumer>();
	 
	 @RequestMapping(method = POST)
	 @ResponseBody
	 public Consumer saveConsumerInfo(@RequestBody Consumer consumer) {
	        logger.debug("Saving consumer info for later retrieval " );
	        Consumer conData = consumerservice.saveConsumerInfo(consumer);
	        return conData;
	    }
	 
	 @RequestMapping(method = GET, value ="/{id}")
	 @ResponseBody
	 public Consumer getConsumerInfoForId(@PathVariable("id") String consumerId) {
		 logger.info("Fetch customer for customer ID: " + consumerId);
         Consumer conData = consumerservice.getConsumerInfoForId(consumerId);
	        return conData;
	 }
	 
	 @RequestMapping(method = GET, value ="/email/{email}")
	 @ResponseBody
	 public Consumer getConsumerInfoForEmail(@RequestParam("email") String email) {
		 logger.info("Fetch customer for customer email: " + email);
         Consumer conData = consumerservice.getConsumerInfoForEmail(email);
	        return conData;
	 }

	     
}
	 
