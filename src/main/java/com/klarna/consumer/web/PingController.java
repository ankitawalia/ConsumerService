package com.klarna.consumer.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PingController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(method = GET, value = "/ping")
    @ResponseBody
    public String ping() {
        logger.debug("Ping received");
        return "pong";
    }
    
    

}
